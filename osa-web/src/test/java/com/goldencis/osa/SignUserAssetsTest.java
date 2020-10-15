package com.goldencis.osa;

import com.goldencis.osa.asset.service.IGrantedService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SignUserAssetsTest {

    @Autowired
    IGrantedService grantedService;

    @Test
    public void testGrantedsByCurrentUser4SSOInPage(){
//        grantedService.getGrantedsByCurrentUser4SSOInPage();
//        boolean flag =  grantedService.checkCurrentUser4AssetAccout(4,9);
//        System.out.println("授权用户结果：" + flag);
    }
}
