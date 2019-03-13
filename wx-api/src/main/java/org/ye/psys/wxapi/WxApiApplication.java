package org.ye.psys.wxapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"org.ye.psys.db", "org.ye.psys.core", "org.ye.psys.wxapi"})
@MapperScan("org.ye.psys.db.mapper")
@EnableTransactionManagement
@EnableScheduling
@EnableCaching
public class WxApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxApiApplication.class, args);
    }

}
