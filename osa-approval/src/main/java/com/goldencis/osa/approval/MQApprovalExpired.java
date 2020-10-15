package com.goldencis.osa.approval;

import com.goldencis.osa.approval.entity.ApprovalFlow;
import com.goldencis.osa.approval.service.IApprovalDetailService;
import com.goldencis.osa.approval.service.IApprovalFlowService;
import com.goldencis.osa.approval.utils.ApprovalConstants;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.mq.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * MQ 过期时间设置
 */
@Component
@Primary
public class MQApprovalExpired extends Subscribe {

    @Autowired
    IApprovalFlowService approvalFlowService;

    @Autowired
    IApprovalDetailService approvalDetailService;

    /**
     * @param channel  __keyevent@0__:expired
     * @param message  5684e37f-f2c1-4eef-8ce2-71dfd035f781:approval
     */
    @Override
    public void onMessage(String channel, String message) {
        super.onMessage(channel, message);
        System.out.println("数据过期了--:" + channel + "\n" + message);
        String flowId =  message.replace(ConstantsDto.REDIS_KEY_APPROVAL,"") ;

        ApprovalFlow approvalFlow = approvalFlowService.getById(flowId);
         if (! Objects.isNull(approvalFlow )){
            if (approvalFlow.getApprovalExpireTime() != ApprovalConstants.APPROVAL_EXPIRE_TIME_UNLIMITED){
                approvalDetailService.approvalResult(flowId, approvalFlow.getApprovalExpireResult(), "审批超时", ApprovalConstants.APPROVAL_APPROVALTYPE_OVERTIME);
            }
         }
    }
}
