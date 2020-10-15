package com.goldencis.osa.core.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by limingchao on 2018/9/27.
 */
@Configuration
public class MybatisPlusConfig {

    /***
     * plus 的性能优化
     */
    /*@Bean
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        *//*<!-- SQL 执行性能分析，开发环境使用，线上不推荐。 maxTime 指的是 sql 最大执行时长 -->*//*
        performanceInterceptor.setMaxTime(1000);
        *//*<!--SQL是否格式化 默认false-->*//*
        performanceInterceptor.setFormat(true);
        return performanceInterceptor;
    }*/

    /**
     * mybatis-plus 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType("mysql");
        return page;
    }

//    /**
//     *  druid注入
//     */
//    @Bean
//    @ConfigurationProperties("spring.datasource" )
//    public DataSource dataSource() {
//        return new DruidDataSource();
//    }

//    /**
//     * 配置事物管理器
//     */
//    @Autowired
//    @Bean(name="transactionManager")
//    public DataSourceTransactionManager transactionManager(DataSource dataSource){
//        return new DataSourceTransactionManager(dataSource);
//    }
}
