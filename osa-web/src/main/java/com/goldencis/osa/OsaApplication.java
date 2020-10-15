package com.goldencis.osa;

import com.goldencis.osa.common.config.Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by limingchao on 2018/9/29.
 */
@SpringBootApplication
@EnableTransactionManagement()
@EnableConfigurationProperties({Config.class})
//扫描dao或者是Mapper接口
@MapperScan(value = "com.goldencis.osa.*.mapper")
public class OsaApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(OsaApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(OsaApplication.class);
    }
}
/*public class OsaApplication {
    public static void main(String[] args) {
        SpringApplication.run(OsaApplication.class, args);
    }
}*/
