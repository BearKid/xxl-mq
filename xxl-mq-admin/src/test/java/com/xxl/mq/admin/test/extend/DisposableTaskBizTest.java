package com.xxl.mq.admin.test.extend;

import com.xxl.mq.admin.core.model.extend.DisposableTaskCreateCmdDTO;
import com.xxl.mq.admin.dao.IXxlMqMessageDao;
import com.xxl.mq.admin.extend.biz.DisposableTaskBiz;
import com.xxl.mq.client.consumer.annotation.MqConsumer;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.message.XxlMqMessageStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;

public class DisposableTaskBizTest {

    private DisposableTaskBiz disposableTaskBiz;
    private IXxlMqMessageDao mqMessageDao;

    @Before
    public void setUp() {
        mqMessageDao = Mockito.mock(IXxlMqMessageDao.class);
        disposableTaskBiz = new DisposableTaskBiz(mqMessageDao);
    }

    @Test
    public void should_create_task() {
        // given
        Mockito.doAnswer(invocationOnMock -> {
            final XxlMqMessage entity = invocationOnMock.getArgumentAt(0, XxlMqMessage.class);
            entity.setId(1000);
            return null;
        }).when(mqMessageDao).add(any(XxlMqMessage.class));

        // when
        final DisposableTaskCreateCmdDTO taskCreateCmd = new DisposableTaskCreateCmdDTO(
            "Order.OrderCreated.Delay.MailNotifyUser", "helloData", 123L
        );
        final String requestIp = "192.168.62.3";
        final Long taskId = disposableTaskBiz.create(taskCreateCmd, requestIp);

        //then
        assertThat(taskId).isEqualTo(1000);

        final XxlMqMessage expectedMessage = new XxlMqMessage();
        expectedMessage.setTopic(taskCreateCmd.getTaskTopic());
        expectedMessage.setGroup(MqConsumer.DEFAULT_GROUP);
        expectedMessage.setData(taskCreateCmd.getData());
        expectedMessage.setStatus(XxlMqMessageStatus.NEW.name());
        expectedMessage.setRetryCount(taskCreateCmd.getMaxRetryCount());
        expectedMessage.setShardingId(taskCreateCmd.getShardingKey());
        expectedMessage.setEffectTime(Date.from(Instant.ofEpochMilli(taskCreateCmd.getTriggerTime())));
        expectedMessage.setTimeout(taskCreateCmd.getExecuteTimeout());

        then(mqMessageDao).should().add(argThat(new ArgumentMatcher<XxlMqMessage>() {
            @Override
            public boolean matches(Object o) {
                final XxlMqMessage actual = (XxlMqMessage) o;
                try {
                    assertThat(actual).isEqualToIgnoringGivenFields(expectedMessage, "log", "id");
                    assertThat(actual.getLog()).contains(requestIp);
                    return true;
                } catch (Throwable e) {
                    return false;
                }
            }
        }));
    }
}
