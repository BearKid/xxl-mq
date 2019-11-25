package com.xxl.mq.client.consumer;

import com.xxl.mq.client.consumer.annotation.MqConsumer;

public class MqConsumerWrapper {

    private IMqConsumer consumer;

    private MqConsumer metaData;

    public MqConsumerWrapper(IMqConsumer consumer, MqConsumer metaData) {
        this.consumer = consumer;
        this.metaData = metaData;
    }

    public IMqConsumer getConsumer() {
        return consumer;
    }

    public MqConsumer getMetaData() {
        return metaData;
    }
}
