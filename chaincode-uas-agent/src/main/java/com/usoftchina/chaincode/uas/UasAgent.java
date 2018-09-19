package com.usoftchina.chaincode.uas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yingp
 * @date 2018/9/19
 */
@SpringBootApplication(scanBasePackages = {"com.usoftchina.chaincode"})
public class UasAgent {

    public static void main(String[] args) {
        SpringApplication.run(UasAgent.class, args);
    }
}
