package com.goldencis.osa.approval.utils;

import com.goldencis.osa.approval.service.IApprovalFlowService;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.SpringUtil;
import com.goldencis.osa.core.entity.Approval;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class ApprovalExpireUtils {

    @Autowired
    static IApprovalFlowService approvalFlowService;



    /**
     * APPROVAL_EXPIRE_TIME: 1, 30分钟;  2 ,1小时;  3, 1天;  4,7天; -1,不限时
     */
    public static long getApprovalTime(){
      long exprireTime = -1L;
      if (Objects.isNull(approvalFlowService) ){
          approvalFlowService = SpringUtil.getBean(IApprovalFlowService.class);
      }
      Approval approval =  approvalFlowService.getApprovalExpireSettings();
      if (! Objects.isNull(approval)){
          switch (approval.getExpireTime()){
              case 1:
                  //30分钟
                  exprireTime = 30 * 60;
                  break;
              case 2:
                  //1小时
                  exprireTime = 60 * 60;
                  break;
              case 3:
                  //1天
                  exprireTime = 24 * 60 * 60;
                  break;
              case 4:
                  //7天
                  exprireTime = 7 * 24 * 60 * 60;
                  break;
              case -1:
                  //不限时
                  break;
          }
      } else {
          exprireTime = ConstantsDto.REDIS_APPROVAL_DEFAULT_EXPIRE;
      }
        return exprireTime;
    }

    /**
     * APPROVAL_EXPIRE_TIME: 1同意 ，-1 拒绝
     */
    public static  Integer getApprovalResult(){
        Integer apprvalResult = -1;
        if (Objects.isNull(approvalFlowService) ){
            approvalFlowService = SpringUtil.getBean(IApprovalFlowService.class);
        }
        Approval approval =  approvalFlowService.getApprovalExpireSettings();
        if (! Objects.isNull(approval)){
            apprvalResult =  approval.getExpireResult();
        } else {
            apprvalResult = ConstantsDto.APPROVAL_REJECTED;
        }
        return apprvalResult;
    }
}
