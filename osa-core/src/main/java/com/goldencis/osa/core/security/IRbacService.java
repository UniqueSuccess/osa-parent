package com.goldencis.osa.core.security;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by limingchao on 2018/10/15.
 */
public interface IRbacService {

    boolean hasPermission(HttpServletRequest request, Authentication authentication);

}
