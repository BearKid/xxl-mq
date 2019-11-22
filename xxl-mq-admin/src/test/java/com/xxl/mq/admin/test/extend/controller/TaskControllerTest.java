package com.xxl.mq.admin.test.extend.controller;

import com.xxl.mq.admin.extend.controller.RequestUtils;
import com.xxl.mq.admin.extend.controller.TaskController;
import com.xxl.mq.admin.core.model.extend.DisposableTaskCreateCmdDTO;
import com.xxl.mq.admin.extend.biz.DisposableTaskBiz;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class TaskControllerTest {
    private TaskController taskController;

    private DisposableTaskBiz disposableTaskBiz;

    private RequestUtils requestUtils;

    @Before
    public void setUp() {
        disposableTaskBiz = Mockito.mock(DisposableTaskBiz.class);
        requestUtils = Mockito.mock(RequestUtils.class);
        taskController = new TaskController(disposableTaskBiz, requestUtils);
    }

    @Test
    public void should_create_disposable_task() {

        // given
        final String requestIp = "192.168.52.53";
        when(requestUtils.getIp(any(HttpServletRequest.class))).thenReturn(requestIp);

        when(disposableTaskBiz.create(any(DisposableTaskCreateCmdDTO.class), anyString())).thenReturn(1000L);

        // when
        final DisposableTaskCreateCmdDTO task = new DisposableTaskCreateCmdDTO(
            "Order.OrderCreated.DelayMailNotifyUser",
            "{\"orderId\": 100, \"email\": \"123@qq.com\"",
            Instant.now().plusSeconds(3600).toEpochMilli()
        );
        task.setShardingKey(10);
        task.setExecuteTimeout(10);
        task.setMaxRetryCount(4);

        final Long taskId = taskController.createDisposableTask(Mockito.mock(HttpServletRequest.class), task);

        // then
        assertThat(taskId).isEqualTo(1000L);
        then(disposableTaskBiz).should().create(task, requestIp);
    }
}
