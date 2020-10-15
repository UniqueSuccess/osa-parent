package com.goldencis.osa.core.config;

import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.ResourceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

/**
 * Created by limingchao on 2018/9/20.
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandlerImpl;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandlerImpl;

    @Autowired
    private LogoutHandler logoutHandlerImpl;

    @Bean
    public PasswordEncoder passwordEncoder() {
//         return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //动态配置所有静态资源均可访问
        List<String> list = ResourceUtils.queryXmlResourceDataList(ConstantsDto.SECURITY_FILE_PATH, ConstantsDto.SECURITY_NONE);
        if (!ListUtils.isEmpty(list)) {
            http.authorizeRequests()
                    .antMatchers(list.toArray(new String[list.size()])).permitAll()
                    .antMatchers(HttpMethod.GET, "/strategy/strategy/**").permitAll();
        } else {
            throw new RuntimeException("Security配置文件异常");
        }

        http
                .formLogin()
                .loginPage("/login")            //配置登录页面的url，是/login
                .loginProcessingUrl("/login")   //配置登录页面的表单 action 必须是 '/login'
                .usernameParameter("username")  //用户名的参数名称
                .passwordParameter("password")  //密码的参数名称
                .successHandler(authenticationSuccessHandlerImpl)
                .failureHandler(authenticationFailureHandlerImpl)
                .permitAll()                    //登录相关均可访问
                .and()
                .logout()
                //配置登出路径，由于默认打开CSRF，所以将logout改为POST请求。
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/login?logout")  //退出登录后的默认网址是”/login?logout”
                .addLogoutHandler(logoutHandlerImpl)
                .permitAll()
                .invalidateHttpSession(true)       //使session失效
                .and()
                .authorizeRequests()
                .anyRequest().access("@rbacService.hasPermission(request, authentication)")    //配置所有请求均需认证
                .and()
                .csrf()                             //关闭csrf防护
                .disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(authenticationProvider());
//                .userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder());
    }

}
