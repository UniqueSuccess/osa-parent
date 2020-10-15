package com.goldencis.osa.common.mq;

import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by limingchao on 2018/11/27.
 */
@Component
public class Subscribe extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        super.onMessage(channel, message);
        System.out.println(channel + ":" + message);
    }
}
