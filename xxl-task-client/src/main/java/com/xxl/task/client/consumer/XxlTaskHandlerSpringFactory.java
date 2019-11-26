package com.xxl.task.client.consumer;

import com.xxl.mq.client.factory.impl.XxlMqSpringClientFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 对{@link XxlMqSpringClientFactory}的封装，改为Task语义。
 * 消费者侧（任务执行者侧）声明创建该Bean以开启接受任务调度中间件系统的任务调度。
 */
public class XxlTaskHandlerSpringFactory implements ApplicationContextAware, DisposableBean {
    private final XxlMqSpringClientFactory proxiedFactory;
    private final String adminAddress;

    /**
     * 构造器
     *
     * @param adminAddress 任务调度中间件的访问地址
     */
    public XxlTaskHandlerSpringFactory(String adminAddress) {
        this.adminAddress = adminAddress;
        proxiedFactory = new XxlMqSpringClientFactory();
        proxiedFactory.setAdminAddress(adminAddress);
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("start to destroy " + proxiedFactory.toString());
        proxiedFactory.destroy();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        proxiedFactory.setApplicationContext(applicationContext);
    }

    public String getAdminAddress() {
        return adminAddress;
    }
}
