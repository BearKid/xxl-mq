package com.xxl.task.sample.springboot.conf;

import com.xxl.mq.client.factory.impl.XxlMqSpringClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XxlTaskConf {
    @Value("${carhouse.service-url.xxl-task}")
    private String adminAddress;

    @Bean
    public XxlMqSpringClientFactory getXxlMqConsumer(){
        XxlMqSpringClientFactory xxlMqSpringClientFactory = new XxlMqSpringClientFactory();
        xxlMqSpringClientFactory.setAdminAddress(adminAddress);

        return xxlMqSpringClientFactory;
    }
}
