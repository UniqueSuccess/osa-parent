package com.goldencis.osa.approval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.approval.entity.ApprovalDetail;
import com.goldencis.osa.approval.entity.ApprovalFlow;
import com.goldencis.osa.approval.entity.ApprovalFlowInfoGranted;
import com.goldencis.osa.approval.handle.ApprovalHandlerFactory;
import com.goldencis.osa.approval.handle.IApprovalHandler;
import com.goldencis.osa.approval.mapper.ApprovalDetailMapper;
import com.goldencis.osa.approval.mapper.ApprovalFlowMapper;
import com.goldencis.osa.approval.mapper.ApprovalModelMapper;
import com.goldencis.osa.approval.service.IApprovalDetailService;
import com.goldencis.osa.approval.service.IApprovalFlowInfoGrantedService;
import com.goldencis.osa.approval.utils.ApprovalConstants;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.RedisUtil;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <p>
 * 具体审批流程审批结果关联表--定义审批结果信息 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Service
public class ApprovalDetailServiceImpl extends ServiceImpl<ApprovalDetailMapper, ApprovalDetail> implements IApprovalDetailService {

    private final Logger logger = LoggerFactory.getLogger(ApprovalDetailServiceImpl.class);
    @Autowired
    ApprovalDetailMapper approvalDetailMapper;

    @Autowired
    ApprovalFlowMapper approvalFlowMapper;

    @Autowired
    ApprovalModelMapper approvalModelMapper;

    @Autowired
    IApprovalFlowInfoGrantedService approvalFlowInfoGrantedService;
    @Autowired
    private ApprovalHandlerFactory approvalHandlerFactory;
    @Autowired
    RedisUtil redisUtil;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String approvalResult(String flowId, Integer approvalResult, String approvalRemark, Integer typeFrom) {
        if (StringUtils.isEmpty(flowId)){
            throw new IllegalArgumentException("审批id为空");
        }
        if (Objects.isNull(approvalResult)){
            throw new IllegalArgumentException("审批结果为空");
        }
        if (Objects.isNull(typeFrom)){
            throw new IllegalArgumentException("审批操作类型为空");
        }
        ApprovalFlow approvalFlow = approvalFlowMapper.selectOne(new QueryWrapper<ApprovalFlow>().eq("id",flowId));
        if (Objects.isNull(approvalFlow)){
            throw new IllegalArgumentException("审批id不存在");
        }
//        ApprovalDetail approvalDetail = approvalDetailMapper.selectOne(new QueryWrapper<ApprovalDetail>().eq("flow_id", approvalFlow.getId()));
//        if (Objects.isNull(approvalDetail)){
//            throw new IllegalArgumentException("审批环节id不存在");
//        }

        IApprovalHandler handler = approvalHandlerFactory.getHandlerByApprovalType(approvalFlow.getDefinitionId());
        if (Objects.nonNull(handler)) {
            handler.handle(approvalFlow, approvalResult);
        } else {
            logger.warn("没有对应的IApprovalHandler: {}", approvalFlow);
        }

        //手动审批，删除redis缓存
        if (ApprovalConstants.APPROVAL_APPROVALTYPE_OPERATION == typeFrom){
            //删除redis key
            redisUtil.del(approvalFlow.getId() + ConstantsDto.REDIS_KEY_APPROVAL);
        }

        //更改审批状态
        approvalFlow.setStatus(approvalResult);
        approvalFlow.setFinishTime(LocalDateTime.now());
        approvalFlowMapper.updateById(approvalFlow);
        //添加审批结果
        ApprovalDetail approvalDetail = new ApprovalDetail(approvalFlow.getName(),approvalFlow.getId(),0,0,ApprovalConstants.APPROVAL_APPROVALTYPE_OPERATION == typeFrom ? SecurityUtils.getCurrentUser().getGuid() :"" ,approvalResult,approvalRemark,1, LocalDateTime.now());
        approvalDetailMapper.insert(approvalDetail);

        //审批日志
        StringBuffer infoGrantedContent = new StringBuffer();
        List<ApprovalFlowInfoGranted> approvalFlowInfoGranteds = approvalFlowInfoGrantedService.list(new QueryWrapper<ApprovalFlowInfoGranted>().eq("flow_id", approvalFlow.getId()));
        if (! ListUtils.isEmpty(approvalFlowInfoGranteds)){
            infoGrantedContent.append(ConstantsDto.APPROVAL_AUTHORIZED == approvalResult ? "通过": "拒绝");
            //遍历拼接授权详细
            approvalFlowInfoGranteds.stream().forEach(approvalFlowInfoGranted -> infoGrantedContent.append(getApprovalContentLog(approvalFlowInfoGranted)+"\n"));
            infoGrantedContent.deleteCharAt(infoGrantedContent.length() - 1);
        }
        return infoGrantedContent.toString();
    }

    /**
     * 获取审批日志
     */
    String getApprovalContentLog(ApprovalFlowInfoGranted approvalFlowInfoGranted) {
        String grantedContent = "";
        if (Objects.isNull(approvalFlowInfoGranted.getAssetId())){
            grantedContent =  "设备组："+  approvalFlowInfoGranted.getAssetgroupName();
        }else  {
            grantedContent =   "设备：" +  approvalFlowInfoGranted.getAssetName() + "; 设备账号："+  approvalFlowInfoGranted.getAssetAccountName();
        }
        grantedContent += (Objects.isNull(approvalFlowInfoGranted.getUserId()) ? "; 被授权用户组：" + approvalFlowInfoGranted.getUsergroupNames():"; 被授权用户：" + approvalFlowInfoGranted.getUserUsername()) ;
        return grantedContent;
    }

}
