package com.goldencis.osa;

import org.junit.Test;
import org.springframework.util.AntPathMatcher;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-10-29 16:36
 **/
public class MatcherTest {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Test
    public void test() {
        String url = "/osa/asset/**";
        String requestURI = "/asset/getList";
        boolean match = antPathMatcher.match(url, requestURI);
        System.out.println(match);
    }

}
