package com.goldencis.osa.approval;

import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.mq.MQClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * sub订阅
 */
@Component
public class ApprovalSub {
    @Autowired
    MQClient mqClient;

    @PostConstruct
    public void init() {
        System.out.println("PostConstruct-=-注解");
        Thread thread = new Thread(() -> {
            mqClient.subscribe(ConstantsDto.MQCLIENT_SUBSCRIBE_CHANNEL);
        });
        thread.start();
    }
}
