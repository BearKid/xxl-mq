package com.xxl.mq.admin.extend.controller;

import com.xxl.mq.admin.core.model.extend.DisposableTaskCreateCmdDTO;
import com.xxl.mq.admin.extend.biz.DisposableTaskBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(path = "task")
public class TaskController {

    private final DisposableTaskBiz disposableTaskBiz;

    private RequestUtils requestUtils;

    @Autowired
    public TaskController(DisposableTaskBiz disposableTaskBiz, RequestUtils requestUtils) {
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
    public Long createDisposableTask(HttpServletRequest request, DisposableTaskCreateCmdDTO disposableTaskCreateCmd) {
       return disposableTaskBiz.create(disposableTaskCreateCmd, requestUtils.getIp(request));
    }
}
