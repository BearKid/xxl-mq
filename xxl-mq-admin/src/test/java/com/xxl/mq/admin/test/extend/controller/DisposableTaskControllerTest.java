package com.xxl.mq.admin.test.extend.controller;

import com.xxl.mq.admin.extend.controller.RequestUtils;
import com.xxl.mq.admin.extend.controller.DisposableTaskController;
import com.xxl.mq.client.extend.domain.DisposableTaskCreateCmdDTO;
import com.xxl.mq.admin.extend.biz.DisposableTaskBiz;
import com.xxl.mq.client.extend.domain.DisposableTaskUpdateCmdDTO;
import com.xxl.mq.client.message.XxlMqMessageStatus;
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

public class DisposableTaskControllerTest {
    private DisposableTaskController disposableTaskController;

    private DisposableTaskBiz disposableTaskBiz;

    private RequestUtils requestUtils;

    @Before
    public void setUp() {
        disposableTaskBiz = Mockito.mock(DisposableTaskBiz.class);
        requestUtils = Mockito.mock(RequestUtils.class);
        disposableTaskController = new DisposableTaskController(disposableTaskBiz, requestUtils);
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
            Instant.now().plusSeconds(3600).toEpochMilli(), "{\"orderId\": 100, \"email\": \"123@qq.com\""
        );
        task.setShardingKey(10);
        task.setExecuteTimeout(100);
        task.setMaxRetryCount(4);

        final Long taskId = disposableTaskController.createDisposableTask(Mockito.mock(HttpServletRequest.class), task);

        // then
        assertThat(taskId).isEqualTo(1000L);
        then(disposableTaskBiz).should().create(task, requestIp);
    }

    @Test
    public void should_update_disposable_task() {

        // when
        final DisposableTaskUpdateCmdDTO updateCmd = new DisposableTaskUpdateCmdDTO();
        updateCmd.setId(1000L);
        updateCmd.setData("changed data");
        updateCmd.setStatus(XxlMqMessageStatus.SUCCESS.name());
        updateCmd.setMaxRetryCount(3);
        updateCmd.setShardingKey(555);
        updateCmd.setTriggerTime(Instant.now().plusSeconds(356).toEpochMilli());
        updateCmd.setExecuteTimeout(3000);
        disposableTaskController.updateDisposableTask(updateCmd);

        // then
        then(disposableTaskBiz).should().update(updateCmd);
    }
}
