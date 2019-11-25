package com.xxl.task.sample.springboot.taskhandler;

import com.xxl.task.client.consumer.ExecuteResult;
import com.xxl.task.client.consumer.TaskHandler;
import com.xxl.task.client.consumer.TaskHandlerBase;
import org.springframework.stereotype.Component;

@TaskHandler(topic = "Topic_1")
@Component
public class TaskHandlerDemo1 extends TaskHandlerBase {
    @Override
    public ExecuteResult execute(String data) {
        System.out.println("hello1 :" + data);
        return ExecuteResult.success("Topic_1 execute successfully with: " + data);
    }
}