package com.goldencis.osa.system.access;

import com.goldencis.osa.system.domain.AccessControl;
import com.goldencis.osa.system.service.ISystemSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 初始化准入控制参数
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-28 10:12
 **/
@Component
public class AccessControlInit implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccessControlManager manager;
    @Autowired
    private ISystemSetService service;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        AccessControl config = service.getAccessControlConfig();
        if (Objects.isNull(config)) {
            logger.debug("准入控制配置为null,不能初始化参数!!!!!!!");
            return;
        }
        if (Objects.nonNull(config.getStatus()) && config.getStatus()) {
            manager.turnOn();
        } else {
            manager.turnOff();
        }
//        manager.local();
        manager.servers(config.getIps());
        manager.ports(config.getPorts());
        logger.debug("初始化准入控制配置成功!!!!!!!!!!!!!");
    }
}
