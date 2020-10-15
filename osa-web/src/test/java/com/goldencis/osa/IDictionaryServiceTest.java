package com.goldencis.osa;

import com.goldencis.osa.asset.entity.SsoRuleAttr;
import com.goldencis.osa.asset.sso.SSORuleParser;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.core.service.IDictionaryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class IDictionaryServiceTest {

    @Autowired
    private IDictionaryService dictionaryService;
    @Autowired
    private SSORuleParser ssoRuleParser;

    @Test
    public void getAllAssetEncode() {
        assertNotNull(dictionaryService.getAllAssetEncode());
    }

    @Test
    public void getDictionaryListByType() {
        assertNotNull(dictionaryService.getDictionaryListByType(null));
    }

    @Test
    public void getSSORule() {
        assertNotNull(dictionaryService.getDictionaryListByType(ConstantsDto.TYPE_SSO_RULE_TYPE));
        assertNotNull(dictionaryService.getDictionaryListByType(ConstantsDto.TYPE_SSO_RULE_ATTR));
    }

    @Test
    public void testSSOAttr() {
        String rule = "tmo=11111;prg=22222;fwt==33333;fwc=44=44444;fwct=55555;locw=66666;fncc=7777";
        List<SsoRuleAttr> list = ssoRuleParser.parse(rule);
        System.out.println(list);
        assertEquals(list.size(), 6);
    }
}