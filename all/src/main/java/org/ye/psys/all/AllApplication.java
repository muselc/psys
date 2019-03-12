package org.ye.psys.all;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"org.ye.psys"})
@MapperScan("org.ye.psys.db.dao")
@EnableScheduling
@EnableTransactionManagement
public class AllApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllApplication.class, args);
    }

}
