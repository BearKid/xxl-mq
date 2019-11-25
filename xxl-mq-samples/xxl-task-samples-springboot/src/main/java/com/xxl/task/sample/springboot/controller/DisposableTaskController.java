package com.xxl.task.sample.springboot.controller;

import com.xxl.task.client.DisposableTaskClient;
import com.xxl.task.client.domain.DisposableTaskCreateCmdDTO;
import com.xxl.task.client.domain.DisposableTaskDTO;
import com.xxl.task.client.domain.DisposableTaskUpdateCmdDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(path = "task/disposable-task")
public class DisposableTaskController {

    private final DisposableTaskClient disposableTaskClient;

    @Autowired
    public DisposableTaskController(DisposableTaskClient disposableTaskClient) {
        this.disposableTaskClient = disposableTaskClient;
    }

    @PostMapping(path = "client-usage-demo")
    public void clientUsageDemo() {
        // create task
        final DisposableTaskCreateCmdDTO createCmd = new DisposableTaskCreateCmdDTO();
        createCmd.setTaskTopic("CreatedTopic");
        createCmd.setData("{\"orderId\": 123, \"email\": \"23@qq.com\"}");
        createCmd.setTriggerTime(Instant.now().plusSeconds(1000L).toEpochMilli());
        createCmd.setShardingKey(123);
        createCmd.setMaxRetryCount(5);
        createCmd.setExecuteTimeout(20);

        final Long taskId = disposableTaskClient.createDisposableTask(createCmd);

        // find task by id
        final DisposableTaskDTO createdTask = disposableTaskClient.findTaskById(taskId);

        // update task by id
        final DisposableTaskUpdateCmdDTO updateCmd = new DisposableTaskUpdateCmdDTO();
        updateCmd.setData(createdTask.getData() + "-changed");
        updateCmd.setStatus("Success");
        updateCmd.setMaxRetryCount(createCmd.getMaxRetryCount() + 1);
        updateCmd.setShardingKey(createCmd.getShardingKey() + 1);
        updateCmd.setTriggerTime(createCmd.getTriggerTime() + 1);
        updateCmd.setExecuteTimeout(createCmd.getExecuteTimeout() + 1);

        disposableTaskClient.updateDisposableTask(taskId, updateCmd);

        // delete by id
        disposableTaskClient.deleteById(taskId);
    }

    @PostMapping(path = "create-tasks-demo")
    public List<Long> createTask(@RequestParam(name = "taskNum", defaultValue = "3000") Integer taskNum) {
        return IntStream.range(1, taskNum).boxed().map(idx -> {
            final DisposableTaskCreateCmdDTO createCmd = new DisposableTaskCreateCmdDTO();
            createCmd.setTaskTopic("CreatedTopic");
            createCmd.setData(String.format("{\"idx\": %s, \"email\": \"23@qq.com\"}", idx));
            createCmd.setTriggerTime(Instant.now().plusSeconds(10L).toEpochMilli());
            createCmd.setShardingKey(idx);
            createCmd.setMaxRetryCount(5);
            createCmd.setExecuteTimeout(10);

            return disposableTaskClient.createDisposableTask(createCmd);
        }).collect(Collectors.toList());
    }
}
