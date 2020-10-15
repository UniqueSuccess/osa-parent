package com.goldencis.osa;

import com.goldencis.osa.strategy.service.IStrategyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class StrategyTest {

    @Autowired
    IStrategyService strategyService;

    @Test
    public void testIsUserLoginTime(){
        boolean flag =  strategyService.isUserLoginTime("wt001","2018-11-13 16:55:33");
        System.out.println("用户登录时间：" + flag);
    }

    @Test
    public void testGetCommandTypeByUsername(){
        int flag =  strategyService.getCommandTypeByUsername("wt001","2");
        System.out.println("用户命令类型：" + flag);
    }

}
