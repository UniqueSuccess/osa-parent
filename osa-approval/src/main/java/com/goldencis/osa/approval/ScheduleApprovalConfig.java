package com.goldencis.osa.approval;

import com.goldencis.osa.approval.service.IApprovalFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 整合任务定时机制
 */
@Configuration
@EnableScheduling
public class ScheduleApprovalConfig {

    @Autowired
    IApprovalFlowService approvalFlowService;

    @Scheduled(cron="30 30 * * * ?")    // 0/20 每隔20s执行一次； 每小时的30分30秒触发任务(30 30 )
    public void scheduler() {
        System.out.println("ScheduleApprovalConfig定时任务执行了");
        approvalFlowService.checkApprovalFlowPendingGranteds();
    }
}
