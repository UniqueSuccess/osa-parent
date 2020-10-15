package com.goldencis.osa.approval.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.approval.entity.ApprovalDefinition;
import com.goldencis.osa.approval.entity.ApprovalDetail;
import com.goldencis.osa.approval.entity.ApprovalFlow;
import com.goldencis.osa.approval.entity.ApprovalFlowInfoGranted;
import com.goldencis.osa.approval.handle.ApprovalType;
import com.goldencis.osa.approval.mapper.*;
import com.goldencis.osa.approval.service.IApprovalDetailService;
import com.goldencis.osa.approval.service.IApprovalFlowService;
import com.goldencis.osa.approval.utils.ApprovalConstants;
import com.goldencis.osa.approval.utils.ApprovalExpireUtils;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.RedisUtil;
import com.goldencis.osa.core.entity.Approval;
import com.goldencis.osa.core.entity.Platform;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.mapper.UserMapper;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.core.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 具体审批流程主表--定义审批公共信息 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-16
 */
@Service
public class ApprovalFlowServiceImpl extends ServiceImpl<ApprovalFlowMapper, ApprovalFlow> implements IApprovalFlowService {

    @Autowired
    ApprovalFlowMapper approvalFlowMapper;

    @Autowired
    ApprovalFlowInfoGrantedMapper approvalFlowInfoGrantedMapper;

    @Autowired
    ApprovalFlowInfoCommandMapper approvalFlowInfoCommandMapper;

    @Autowired
    ApprovalDefinitionMapper approvalDefinitionMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ApprovalDetailMapper approvalDetailMapper;

    @Autowired
    IApprovalDetailService approvalDetailService;

    /**
     * 分页获取授权审批列表
     */
    @Override
    public IPage<ApprovalFlow> getApprovalFlowGrantedsInPage(Map<String, String> params) {
        Page page = new Page();
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        if (Objects.isNull(paramMap.get("approvalType"))) {
            throw new IllegalArgumentException("获取审批类型不正确");
        }

        Set<Integer> definitionSets = new HashSet<>();
        if (paramMap.containsKey("definitionType") && !StringUtils.isEmpty(paramMap.get("definitionType"))) {
            //添加类型的查询条件  （1添加； 2删除）
            String conditionStr = (String) paramMap.get("definitionType");
            Set<Integer> finalDefinitionSets = definitionSets;
            Arrays.stream(conditionStr.split(";")).forEach(s -> {
                if ("1".equals(s)){
                    finalDefinitionSets.addAll(ApprovalType.getTypeMarkByDiffer(ApprovalType.Differ.ADD));
                } else if ("2".equals(s)){
                    finalDefinitionSets.addAll(getApprovalDefIdsByDefType(ApprovalType.getTypeMarkByDiffer(ApprovalType.Differ.DELETE)));
                }
            });
        } else {
            //默认类型
             definitionSets = getApprovalGrantedDefIds();
        }
        paramMap.put("definitionTypeSet", definitionSets);

        //添加 审批列表 按 登录 人员过滤
        if ((! ConstantsDto.USER_SYSTEM_ID.equals(SecurityUtils.getCurrentUser().getGuid())) &&  (! ConstantsDto.USER_AUDITOR_ID.equals(SecurityUtils.getCurrentUser().getGuid()))){
            paramMap.put("curUserId", SecurityUtils.getCurrentUser().getGuid());
        }
        //验证未授权的待审批列表【验证是否有超时】
        checkApprovalFlowPendingGranteds();

        //添加授权方式的查询条件
        QueryUtils.addQueryConditionBySplitString4Str(paramMap, "grantedMethod", "grantedMethodList", ";");

        int total = approvalFlowMapper.countApprovalFlowGrantedsInPage(paramMap);
        List<ApprovalFlow> approvalFlows = approvalFlowMapper.getApprovalFlowGrantedsInPage(paramMap);
        int approvalType = Integer.valueOf((String) paramMap.get("approvalType"));
        if (ConstantsDto.APPROVAL_APPROVALTYPE_PENDING == approvalType && !ListUtils.isEmpty(approvalFlows)) {
            //当前时间
            LocalDateTime now = LocalDateTime.now();
            approvalFlows.stream().forEach(approvalFlow -> {
                if ( approvalFlow.getApprovalExpireTime() == ApprovalConstants.APPROVAL_EXPIRE_TIME_UNLIMITED){
                    //不限时、无限期
                    approvalFlow.setEffectiveTime(-1L);
                }else{
                    //待审批状态，设置有效时间
                    long effectiveTime = (approvalFlow.getApprovalExpireTime() - Duration.between(approvalFlow.getApplyTime(), now).getSeconds());
                    approvalFlow.setEffectiveTime(effectiveTime > 0 ? effectiveTime : 0L);
                }
            });
        }
        page.setTotal(total);
        page.setRecords(approvalFlows);
        return page;
    }

    /**
     * 获取自定义审批类型集合
     * @param integers 自定义审批类型集合
     */
    Set<Integer> getApprovalDefIdsByDefType( List<Integer> integers) {
        Set<Integer> definitionSets = new HashSet<>();
        List<Integer> ids = integers;
        List<ApprovalDefinition> list = approvalDefinitionMapper.selectList(new QueryWrapper<ApprovalDefinition>().in("id", ids));
        if (ListUtils.isEmpty(list) || list.size() != ids.size()) {
            throw new IllegalArgumentException("审批授权类型不存在");
        }
        definitionSets.addAll(list.stream().map(ApprovalDefinition::getId).collect(Collectors.toList()));
        return definitionSets;
    }

    /**
     * 获取授权审批的类型【2添加授权，3 删除授权，4删除含授权的设备,5删除设备账号】
     */
    private Set<Integer> getApprovalGrantedDefIds() {
        Set<Integer> definitionSets = new HashSet<>();
        //授权添加
        definitionSets.addAll(getApprovalDefIdsByDefType(ApprovalType.getAllTypeMark()));
        return definitionSets;
    }

    /**
     * 分页获取命令审批列表
     */
    @Override
    public IPage<ApprovalFlow> getApprovalFlowCommandsInPage(Map<String, String> params) {

        Page page = new Page();
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        if (Objects.isNull(paramMap.get("approvalType"))) {
            throw new IllegalArgumentException("获取审批类型不正确");
        }
        Set<Integer> definitionSets = getApprovalCommandDefIds();
        paramMap.put("definitionTypeSet", definitionSets);

        int total = approvalFlowMapper.countApprovalFlowComandsInPage(paramMap);
        List<ApprovalFlow> approvalFlows = approvalFlowMapper.getApprovalFlowComandsInPage(paramMap);
        if (!ListUtils.isEmpty(approvalFlows)) {
            approvalFlows.stream().map((Function<ApprovalFlow, Object>) approvalFlow -> {
                approvalFlow.setCommandContent(approvalFlowInfoCommandMapper.getCommandContentInFlowCommand(approvalFlow.getId()));
                return approvalFlow;
            }).collect(Collectors.toSet());
        }
        page.setTotal(total);
        page.setRecords(approvalFlows);
        return page;
    }

    /**
     * 获取命令审批的类型【1添加需审核命令】
     */
    private Set<Integer> getApprovalCommandDefIds() {
        Set<Integer> definitionSets = new HashSet<>();
        ApprovalDefinition approvalDefCommandDelete = approvalDefinitionMapper.selectById(ApprovalType.COMMAND_ADD.getMark());
        if (Objects.isNull(approvalDefCommandDelete)) {
            throw new IllegalArgumentException("审批授权类型不存在");
        }
        definitionSets.add(approvalDefCommandDelete.getId());
        return definitionSets;
    }

    /**
     * 授权审批保存
     */
    @Override
    public void saveApprovalFlowGranted(ApprovalFlow approvalFlow) {
        if (Objects.isNull(approvalFlow)) {
            throw new IllegalArgumentException("审批内容为空");
        }
        if (Objects.isNull(approvalFlow.getDefinitionId())) {
            throw new IllegalArgumentException("审批模板id为空");
        }
        if (ListUtils.isEmpty(approvalFlow.getApprovalFlowInfoGrantedList())) {
            throw new IllegalArgumentException("审批授权内容为空");
        }
        User currentUser = SecurityUtils.getCurrentUser();
        //根据操作员的guid，查找对应的审批员集合
        List<User> list = userMapper.findAuditorByOperatorGuid(currentUser.getGuid());

        if (ListUtils.isEmpty(list)) {
            throw new IllegalArgumentException("当前操作员没有对应的审批员，请先添加审批员！");
        }

        approvalFlow.setStatus(ConstantsDto.APPROVAL_PENDING);
        //插入审批详情记录
        approvalFlow.getApprovalFlowInfoGrantedList().stream().forEach(approvalFlowInfoGranted -> {
            approvalFlowInfoGranted.setFlowId(approvalFlow.getId());
            approvalFlowInfoGrantedMapper.insert(approvalFlowInfoGranted);
        });
//        //插入审批环节
//        StringBuffer sb = new StringBuffer();
//        list.forEach(user -> sb.append(user.getGuid() + ConstantsDto.SEPARATOR_SEMICOLON));
//        ApprovalDetail approvalDetail = new ApprovalDetail(approvalFlow.getName(),approvalFlow.getId(),0,0,sb.deleteCharAt(sb.length() - 1).toString(), 0, "",1, LocalDateTime.now());
//        approvalDetailMapper.insert(approvalDetail);

        //设置超时时间、超时结果
        approvalFlow.setApprovalExpireTime(ApprovalExpireUtils.getApprovalTime() );
        approvalFlow.setApprovalExpireResult(ApprovalExpireUtils.getApprovalResult());
        //插入审批记录
        baseMapper.insert(approvalFlow);

        //不限时,不放入redis 处理
        if (ApprovalExpireUtils.getApprovalTime() != ApprovalConstants.APPROVAL_EXPIRE_TIME_UNLIMITED){
            //添加到redis服务器，30分钟有效期
            String rediskey = approvalFlow.getId() + ConstantsDto.REDIS_KEY_APPROVAL;
            redisUtil.set(rediskey, approvalFlow, ApprovalExpireUtils.getApprovalTime());//30*60  ConstantsDto.REDIS_APPROVAL_DEFAULT_EXPIRE
        }
    }

    /**
     * 命令审批保存
     */
    @Override
    public void saveApprovalFlowCommand(ApprovalFlow approvalFlow) {
        if (Objects.isNull(approvalFlow)) {
            throw new IllegalArgumentException("审批内容为空");
        }
        if (ListUtils.isEmpty(approvalFlow.getApprovalFlowInfoCommands())) {
            throw new IllegalArgumentException("审批命令内容为空");
        }
        approvalFlow.getApprovalFlowInfoCommands().stream().forEach(approvalFlowInfoCommand -> {
            approvalFlowInfoCommand.setFlowId(approvalFlow.getId());
            approvalFlowInfoCommandMapper.insert(approvalFlowInfoCommand);
        });
        baseMapper.insert(approvalFlow);
    }

    @Override
    public IPage<ApprovalFlowInfoGranted> getApprovalFlowGrantedsDetailInPage(Map<String, String> params) {
        Page page = new Page();
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        if (Objects.isNull(paramMap.get("flowId"))) {
            throw new IllegalArgumentException("授权审批id为空");
        }
        ApprovalFlow approvalFlow = baseMapper.selectOne(new QueryWrapper<ApprovalFlow>().eq("id", (String) paramMap.get("flowId")));
        if (Objects.isNull(approvalFlow)) {
            throw new IllegalArgumentException("授权审批id不存在");
        }

        int total = approvalFlowInfoGrantedMapper.countApprovalFlowGrantedsDetailInPage(paramMap);
        List<ApprovalFlowInfoGranted> approvalFlows = approvalFlowInfoGrantedMapper.getApprovalFlowGrantedsDetailInPage(paramMap);
        page.setTotal(total);
        page.setRecords(approvalFlows);
        return page;
    }

    /**
     * 获取所有的授权数据
     */
    @Override
    public List<ApprovalFlow> getApprovalFlowGranteds() {
        Map<String, Object> paramMap = new HashMap<>();
        Set<Integer> definitionSets = getApprovalGrantedDefIds();
        paramMap.put("definitionTypeSet", definitionSets);
        //只查询待审批授权
        paramMap.put("approvalType", 0);
        return approvalFlowMapper.getApprovalFlowGranteds(paramMap);
    }

    /**
     * 验证待审批的授权数据是否超时
     */
    @Override
    public void checkApprovalFlowPendingGranteds() {
        List<ApprovalFlow> approvalFlows = getApprovalFlowGranteds();
        if (!ListUtils.isEmpty(approvalFlows)) {
            //当前时间
            LocalDateTime now = LocalDateTime.now();
            approvalFlows.stream().forEach(approvalFlow -> {
                if (!Objects.isNull(approvalFlow.getApprovalExpireTime())){
                    //不是无限期
                    if (approvalFlow.getApprovalExpireTime() != ApprovalConstants.APPROVAL_EXPIRE_TIME_UNLIMITED){
                        long seconds = Duration.between(approvalFlow.getApplyTime(), now).getSeconds();
                        if (approvalFlow.getApprovalExpireTime()- seconds <= 0) {
                            approvalDetailService.approvalResult(approvalFlow.getId(), approvalFlow.getApprovalExpireResult(), "审批超时", ApprovalConstants.APPROVAL_APPROVALTYPE_OVERTIME);
                        }
                    }
                }
            });
        }
    }

    /**
     * 根据授权id 删除 待审批记录
     *
     * @param grantedId 授权id
     */
    @Override
    public void deleteApprovalFlowByGrantedId(Integer grantedId) {
        List<ApprovalFlowInfoGranted> approvalFlowInfoGranteds = approvalFlowInfoGrantedMapper.selectList(new QueryWrapper<ApprovalFlowInfoGranted>().eq("granted_id", grantedId));
        if (ListUtils.isEmpty(approvalFlowInfoGranteds)) {
            throw new IllegalArgumentException("授权id没有形成记录");
        }
        //通过授权id 返回 审批id集合
        Set<String> flowIds = approvalFlowInfoGranteds.stream().map(approvalFlowInfoGranted -> approvalFlowInfoGranted.getFlowId()).collect(Collectors.toSet());

        //1）通过 审批id 集合 获取 审批； 2）通过审批状态做后续处理
        flowIds.stream().map(flowId -> approvalFlowMapper.selectById(flowId)).forEach(approvalFlow -> {
            if (approvalFlow.getStatus() == ConstantsDto.APPROVAL_PENDING) {
                //处于待审批状态 1)删除授权子集 2)删除审批详细 3)删除审批 4)删除redis 定时机制
                approvalFlowInfoGrantedMapper.delete(new QueryWrapper<ApprovalFlowInfoGranted>().eq("flow_id", approvalFlow.getId()));
                approvalDetailMapper.delete(new QueryWrapper<ApprovalDetail>().eq("flow_id", approvalFlow.getId()));
                approvalFlowMapper.deleteById(approvalFlow.getId());
                //删除redis key
                redisUtil.del(approvalFlow.getId() + ConstantsDto.REDIS_KEY_APPROVAL);
            }
        });
    }

    @Override
    public boolean isInApprovalByAssetIdAndApprovalType(Integer id, Integer definitionId) {
        if (id == null) {
            return false;
        }
        //判断对应id的设备，是否存在正在进行的审批
        List<ApprovalFlow> approvalFlows = approvalFlowMapper.findUnfinishedApprovalListByAssetIdAndDefinitionId(id, definitionId);
        if (!ListUtils.isEmpty(approvalFlows)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isInApprovalByAssetAccountIdAndApprovalType(Integer assetAccountId, Integer definitionId) {
        if (assetAccountId == null) {
            return false;
        }
        //判断对应id的设备，是否存在正在进行的审批
        List<ApprovalFlow> approvalFlows = approvalFlowMapper.findUnfinishedApprovalListByAssetAccountIdAndDefinitionId(assetAccountId, definitionId);
        if (!ListUtils.isEmpty(approvalFlows)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isInApprovalByAssetGroupIdAndApprovalType(Integer assetGroupId, Integer definitionId) {
        if (assetGroupId == null) {
            return false;
        }
        //判断对应id的设备，是否存在正在进行的审批
        List<ApprovalFlow> approvalFlows = approvalFlowMapper.findUnfinishedApprovalListByAssetGroupIdAndDefinitionId(assetGroupId, definitionId);
        return !ListUtils.isEmpty(approvalFlows);
    }

    /**
     * 同时授权集合
     *
     * @param grantedId 授权id
     */
    @Override
    public Set<Integer> getGrantedIdsFromFlowByGrantedId(Integer grantedId) {
        List<ApprovalFlowInfoGranted> approvalFlowInfoGranteds = approvalFlowInfoGrantedMapper.selectList(new QueryWrapper<ApprovalFlowInfoGranted>().eq("granted_id", grantedId));
        if (ListUtils.isEmpty(approvalFlowInfoGranteds)) {
            throw new IllegalArgumentException("授权id没有形成记录");
        }

        //通过授权id 返回 审批id集合
        Set<Integer> grantedIds = new HashSet<>();
        //1、审批infogranted 获取flow id   2、过滤 审批状态是 待审批  3、通过审批id 获取授权审批子表  授权id集合
        approvalFlowInfoGranteds.stream()
                .map(approvalFlowInfoGranted -> approvalFlowMapper.selectById(approvalFlowInfoGranted.getFlowId()))
                .filter(approvalFlow -> approvalFlow.getStatus() == ConstantsDto.APPROVAL_PENDING)
                .forEach(approvalFlow ->
                        approvalFlowInfoGrantedMapper.selectList(new QueryWrapper<ApprovalFlowInfoGranted>().eq("flow_id", approvalFlow.getId()))
                        .stream()
                        .map(approvalFlowInfoGranted -> approvalFlowInfoGranted.getGrantedId())
                        .forEach(grantedId1 -> grantedIds.add(grantedId1)));
        return grantedIds;
    }

    /**
     * 根据flow id获取flow 和 审批结果备注
     * @param flowId 审批id
     */
    @Override
    public ApprovalFlow getApprovalFlowDetailByFlowId(String flowId) {
        return approvalFlowMapper.getApprovalFlowDetailByFlowId(flowId);
    }

    /**
     * 获取审批配置
     */
    @Override
    public Approval getApprovalExpireSettings() {
        Approval approval = null;
        String plateFormContent = approvalFlowMapper.getApprovalExpireSettings();
        Platform platform = JSON.parseObject(plateFormContent,Platform.class);
        if (! Objects.isNull(platform)) {
            approval = platform.getApproval();
        }
        return approval ;
    }
}
