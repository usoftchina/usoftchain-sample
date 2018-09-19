package com.usoftchina.chain.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yingp
 * @date 2018/9/5
 */
@SpringBootApplication(scanBasePackages = {"com.usoftchina.chaincode", "com.usoftchina.chain"})
@EnableScheduling
public class DashboardApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DashboardApplication.class, args);
    }
}
