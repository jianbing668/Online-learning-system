package com.xuecheng.govern.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @ClassName : GovernCenterApplication
 * @Description : Eureka服务启动类
 * @Author : JB
 * @Date: 2020-01-30 10:51
 */
@EnableEurekaServer //标识这是一个Eureka服务
@SpringBootApplication
public class GovernCenterApplication {
    public static  void main(String[] args){
        SpringApplication.run(GovernCenterApplication.class,args);
    }
}
