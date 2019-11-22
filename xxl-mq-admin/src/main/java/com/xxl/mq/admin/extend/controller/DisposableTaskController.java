package com.xxl.mq.admin.extend.controller;

import com.xxl.mq.client.extend.domain.DisposableTaskCreateCmdDTO;
import com.xxl.mq.admin.extend.biz.DisposableTaskBiz;
import com.xxl.mq.client.extend.domain.DisposableTaskUpdateCmdDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 【一次性任务】相关接口Controller
 */
@RestController
@RequestMapping(path = "extension/task")
public class DisposableTaskController {

    private final DisposableTaskBiz disposableTaskBiz;

    private RequestUtils requestUtils;

    @Autowired
    public DisposableTaskController(DisposableTaskBiz disposableTaskBiz, RequestUtils requestUtils) {
        this.disposableTaskBiz = disposableTaskBiz;
        this.requestUtils = requestUtils;
    }

    /**
     * 创建一次性任务
     *
     * @param disposableTaskCreateCmd 一次性任务信息
     * @return 任务ID
     */
    @PostMapping(path = "disposable-tasks")
    public Long createDisposableTask(HttpServletRequest request, @RequestBody DisposableTaskCreateCmdDTO disposableTaskCreateCmd) {
        return disposableTaskBiz.create(disposableTaskCreateCmd, requestUtils.getIp(request));
    }

    /**
     * 更新一次性任务
     *
     * @param updateCmd
     */
    @PutMapping(path = "disposable-tasks")
    public void updateDisposableTask(@RequestBody DisposableTaskUpdateCmdDTO updateCmd) {
        disposableTaskBiz.update(updateCmd);
    }
}
