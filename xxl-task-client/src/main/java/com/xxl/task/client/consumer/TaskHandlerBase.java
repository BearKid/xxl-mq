package com.xxl.task.client.consumer;

import com.xxl.mq.client.consumer.IMqConsumer;
import com.xxl.mq.client.consumer.MqResult;

public abstract class TaskHandlerBase implements IMqConsumer {
    public abstract ExecuteResult execute(String data);

    @Override
    public final MqResult consume(String data) {
        final ExecuteResult code = execute(data);
        return code.isSuccess() ? MqResult.SUCCESS : MqResult.FAIL;
    }
}
