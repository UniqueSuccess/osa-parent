package com.goldencis.osa.common.mq;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Component
public class MQClient {
    public static final int MSG_REALTIME = 1;
    public static final int MSG_CACHED = 2;
    public static final int MSG_SERIALIZABLE = 3;
    public static final String NOTIFY_CHANNEL = "ClientNotify";

    @Autowired
    private JedisPool pool;

    @Autowired
    private JedisPubSub pubsub;

    private boolean exit = false;

    public MQClient() {}

    public MQClient(JedisPubSub pubsub, String ip, int port) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxWaitMillis(10 * 1000);
        config.setMaxTotal(50);
        config.setMaxIdle(1000);
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, ip, port, Protocol.DEFAULT_TIMEOUT, null);
        exit = false;
        this.pubsub = pubsub;
    }

    public boolean publish(String channels, String message, String content) {
        JSONObject obj = new JSONObject();
        boolean ret = false;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            if (message != null) {
                obj.put("message", message);
            }
            try {
                JSONObject objCon = JSONObject.parseObject(content);
                obj.put("content", objCon);
            } catch (JSONException e) {
                obj.put("content", content);
            }
            String[] tmp = channels.split(";");
            for (String channel : tmp) {
                try {
                    if (jedis.publish(channel, obj.toString()) > 0) {
                        ret = true;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        } catch (JSONException e) {
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return ret;
    }

    public boolean clientNotify(String clients, String message, String content, int type) {
        if (type == MSG_REALTIME) {
            return publish(clients, message, content);
        }

        boolean ret = false;
        Jedis jedis = null;

        try {
            JSONObject obj = new JSONObject();
            obj.put("clients", clients);
            obj.put("type", type);
            if (message != null) {
                obj.put("message", message);
            }
            try {
                JSONObject objCon = JSONObject.parseObject(content);
                obj.put("content", objCon);
            } catch (JSONException e) {
                obj.put("content", content);
            }

            jedis = pool.getResource();
            if (jedis.publish(NOTIFY_CHANNEL, obj.toString()) > 0) {
                ret = true;
            }
        } catch (JSONException e) {
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return ret;
    }

    public boolean setValue(String key, String value) {
        try {
            String response = pool.getResource().set(key, value);
            if (response != null && response.equals("OK")) {
                return true;
            }
        } catch (JedisConnectionException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getValue(String key) {
        return pool.getResource().get(key);
    }

    public void subscribe(String... channels) {
        while (!exit) {
            try {
                pool.getResource().subscribe(pubsub, channels);
            } catch (JedisConnectionException e) {
                e.printStackTrace();
                System.out.println("try reconnect");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
