package com.xxl.task.sample.springboot;

import com.xxl.task.client.XxlTaskClientsPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackageClasses = XxlTaskClientsPackage.class)
public class XxlTaskSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(XxlTaskSampleApplication.class, args);
    }
}
