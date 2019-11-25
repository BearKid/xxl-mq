package com.xxl.task.sample.springboot.taskhandler;

import com.xxl.task.client.consumer.ExecuteResult;
import com.xxl.task.client.consumer.TaskHandlerBase;
import com.xxl.task.client.consumer.TaskHandler;
import org.springframework.stereotype.Component;

@TaskHandler(topic = "Topic_2")
@Component
public class TaskHandlerDemo2 extends TaskHandlerBase {
    @Override
    public ExecuteResult execute(String data) {
        System.out.println("hello2 :" + data);
        return ExecuteResult.success("topic_2 execute successfully with: " + data);
    }
}