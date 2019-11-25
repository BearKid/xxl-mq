package com.xxl.task.client.consumer;

import com.xxl.mq.client.consumer.annotation.MqConsumer;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@MqConsumer
public @interface TaskHandler {
    @AliasFor(annotation = MqConsumer.class, attribute = "topic")
    String topic();
}