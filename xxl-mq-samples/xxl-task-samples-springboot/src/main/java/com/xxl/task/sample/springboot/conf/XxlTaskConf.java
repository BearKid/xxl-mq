package com.xxl.task.sample.springboot.conf;

import com.xxl.task.client.consumer.XxlTaskHandlerSpringFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XxlTaskConf {
    @Value("${carhouse.service-url.xxl-task}")
    private String adminAddress;

    @Bean
    public XxlTaskHandlerSpringFactory xxlTaskHandlerSpringFactory() {
        return new XxlTaskHandlerSpringFactory(adminAddress);
    }
}