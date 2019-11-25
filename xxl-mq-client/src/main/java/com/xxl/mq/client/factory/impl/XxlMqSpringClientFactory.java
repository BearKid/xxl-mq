package com.xxl.mq.client.factory.impl;

import com.xxl.mq.client.consumer.IMqConsumer;
import com.xxl.mq.client.consumer.MqConsumerWrapper;
import com.xxl.mq.client.consumer.annotation.MqConsumer;
import com.xxl.mq.client.factory.XxlMqClientFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2018-11-18 21:18:10
 */
public class XxlMqSpringClientFactory implements ApplicationContextAware, DisposableBean {

    // ---------------------- param  ----------------------

    private String adminAddress;
    private String accessToken;

    public void setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // XxlMqClientFactory
    private XxlMqClientFactory xxlMqClientFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        // load consumer from spring
        final List<MqConsumerWrapper> consumerList;

        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(MqConsumer.class);
        if (serviceMap != null && serviceMap.size() > 0) {
            consumerList = new ArrayList<>(serviceMap.size());
            for (Object serviceBeanObj : serviceMap.values()) {
                if (serviceBeanObj instanceof IMqConsumer) {
                    final IMqConsumer serviceBean = (IMqConsumer) serviceBeanObj;
                    final MqConsumer annotation = AnnotatedElementUtils.findMergedAnnotation(serviceBean.getClass(), MqConsumer.class);
//                        AnnotationUtils.findAnnotation(serviceBean.getClass(), MqConsumer.class);
                    consumerList.add(new MqConsumerWrapper(serviceBean, annotation));
                }
            }
        } else {
            consumerList = new ArrayList<>(0);
        }

        // init
        xxlMqClientFactory = new XxlMqClientFactory();

        xxlMqClientFactory.setAdminAddress(adminAddress);
        xxlMqClientFactory.setAccessToken(accessToken);
        xxlMqClientFactory.setConsumerList(consumerList);

        xxlMqClientFactory.init();
    }

    @Override
    public void destroy() throws Exception {
        xxlMqClientFactory.destroy();
    }

}
