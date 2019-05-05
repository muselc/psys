package org.ye.psys.adminapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"org.ye.psys.db","org.ye.psys.core","org.ye.psys.adminapi"})
@MapperScan("org.ye.psys.db.mapper")
@EnableTransactionManagement
@EnableScheduling
public class AdminApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApiApplication.class, args);

    }

}
