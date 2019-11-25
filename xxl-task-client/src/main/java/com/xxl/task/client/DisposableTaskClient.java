package com.xxl.task.client;

import com.xxl.task.client.domain.DisposableTaskCreateCmdDTO;
import com.xxl.task.client.domain.DisposableTaskDTO;
import com.xxl.task.client.domain.DisposableTaskUpdateCmdDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = XxlTaskClientsPackage.SERVICE_NAME_PLACEHOLDER,
    url = XxlTaskClientsPackage.SERVICE_URL_PLACEHOLDER,
    contextId = "xxl-task-DisposableTaskClient",
    path = "extension/task"
)
public interface DisposableTaskClient {

    /**
     * 创建一次性任务
     *
     * @param disposableTaskCreateCmd 一次性任务信息
     * @return 任务ID
     */
    @PostMapping(path = "disposable-tasks")
    Long createDisposableTask(@RequestBody DisposableTaskCreateCmdDTO disposableTaskCreateCmd);

    /**
     * 更新一次性任务
     *
     * @param updateCmd
     */
    @PutMapping(path = "disposable-tasks/{taskId}")
    void updateDisposableTask(@PathVariable(name = "taskId") Long taskId, DisposableTaskUpdateCmdDTO updateCmd);

    /**
     * 按任务id删除对应任务记录
     */
    @DeleteMapping(path = "disposable-tasks/{taskId}")
    void deleteById(@PathVariable(name = "taskId") Long taskId);

    /**
     * 按任务id查询相应的任务记录
     */
    @GetMapping(path = "disposable-tasks/{taskId}")
    DisposableTaskDTO findTaskById(@PathVariable(name = "taskId") Long taskId);
}
