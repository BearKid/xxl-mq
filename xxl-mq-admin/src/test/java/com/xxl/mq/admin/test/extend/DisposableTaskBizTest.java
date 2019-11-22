package com.xxl.mq.admin.test.extend;

import com.xxl.mq.client.extend.domain.DisposableTaskCreateCmdDTO;
import com.xxl.mq.admin.dao.IXxlMqMessageDao;
import com.xxl.mq.admin.extend.biz.DisposableTaskBiz;
import com.xxl.mq.client.consumer.annotation.MqConsumer;
import com.xxl.mq.client.extend.domain.DisposableTaskUpdateCmdDTO;
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
import static org.mockito.Mockito.when;

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
            "Order.OrderCreated.Delay.MailNotifyUser", 123L, "helloData"
        );
        final String requestIp = "192.168.62.3";
        final Long taskId = disposableTaskBiz.create(taskCreateCmd, requestIp);

        //then
        assertThat(taskId).isEqualTo(1000);

        final XxlMqMessage expectedMessage = new XxlMqMessage();
        expectedMessage.setId(taskId);
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
                    assertThat(actual).isEqualToIgnoringGivenFields(expectedMessage, "log");
                    assertThat(actual.getLog()).contains(requestIp);
                    return true;
                } catch (Throwable e) {
                    return false;
                }
            }
        }));
    }

    @Test
    public void should_update_task() {
        final long testId = 1000L;

        // given:
        final XxlMqMessage existedEntity = new XxlMqMessage();
        existedEntity.setId(testId);
        existedEntity.setTopic("OldTopic");
        existedEntity.setGroup("OldGroup");
        existedEntity.setData("OldData");
        existedEntity.setStatus(XxlMqMessageStatus.RUNNING.name());
        existedEntity.setRetryCount(1);
        existedEntity.setShardingId(1);
        existedEntity.setEffectTime(Date.from(Instant.ofEpochMilli(1)));
        existedEntity.setTimeout(1);
        existedEntity.setAddTime(Date.from(Instant.ofEpochMilli(1)));
        existedEntity.setLog("OldLog");
        when(mqMessageDao.findById(testId)).thenReturn(existedEntity);

        // when
        final DisposableTaskUpdateCmdDTO updateCmd = new DisposableTaskUpdateCmdDTO();
        updateCmd.setId(testId);
        updateCmd.setData("changed data");
        updateCmd.setStatus(XxlMqMessageStatus.SUCCESS.name());
        updateCmd.setMaxRetryCount(2);
        updateCmd.setShardingKey(346346);
        updateCmd.setTriggerTime(Instant.now().plusSeconds(3566).toEpochMilli());
        updateCmd.setExecuteTimeout(3600);
        disposableTaskBiz.update(updateCmd);

        // then
        final XxlMqMessage expectedMessage = new XxlMqMessage();
        expectedMessage.setId(updateCmd.getId());
        expectedMessage.setTopic(existedEntity.getTopic());
        expectedMessage.setGroup(existedEntity.getGroup());
        expectedMessage.setData(updateCmd.getData());
        expectedMessage.setStatus(updateCmd.getStatus());
        expectedMessage.setRetryCount(updateCmd.getMaxRetryCount());
        expectedMessage.setShardingId(updateCmd.getShardingKey());
        expectedMessage.setEffectTime(Date.from(Instant.ofEpochMilli(updateCmd.getTriggerTime())));
        expectedMessage.setTimeout(updateCmd.getExecuteTimeout());
        expectedMessage.setAddTime(existedEntity.getAddTime());
        expectedMessage.setLog(existedEntity.getLog());

        then(mqMessageDao).should().update(argThatIsEqualToComparingFieldByField(expectedMessage));
    }

    private XxlMqMessage argThatIsEqualToComparingFieldByField(XxlMqMessage expectedMessage) {
        return argThat(new ArgumentMatcher<XxlMqMessage>() {
            @Override
            public boolean matches(Object o) {
                final XxlMqMessage actual = (XxlMqMessage) o;
                try {
                    assertThat(actual).isEqualToComparingFieldByField(expectedMessage);
                    return true;
                } catch (Throwable e) {
                    return false;
                }
            }
        });
    }
}
