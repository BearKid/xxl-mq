package com.xxl.mq.admin.test.extend.biz;

import com.xxl.task.client.domain.DisposableTaskCreateCmdDTO;
import com.xxl.mq.admin.dao.IXxlMqMessageDao;
import com.xxl.mq.admin.extension.biz.DisposableTaskBiz;
import com.xxl.mq.client.consumer.annotation.MqConsumer;
import com.xxl.task.client.domain.DisposableTaskDTO;
import com.xxl.task.client.domain.DisposableTaskUpdateCmdDTO;
import com.xxl.mq.admin.extension.adpater.TaskStatusEnumAdapter;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.message.XxlMqMessageStatus;
import com.xxl.task.client.enums.TaskStatusEnum;
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
    private TaskStatusEnumAdapter taskStatusEnumAdapter;

    @Before
    public void setUp() {
        mqMessageDao = Mockito.mock(IXxlMqMessageDao.class);
        taskStatusEnumAdapter = new TaskStatusEnumAdapter();
        disposableTaskBiz = new DisposableTaskBiz(mqMessageDao, taskStatusEnumAdapter);
    }

    @Test
    public void should_createTask() {
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
    public void should_updateTask() {
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
        updateCmd.setData("changed data");
        updateCmd.setStatus(TaskStatusEnum.SUCCESS.getKey());
        updateCmd.setMaxRetryCount(2);
        updateCmd.setShardingKey(346346);
        updateCmd.setTriggerTime(Instant.now().plusSeconds(3566).toEpochMilli());
        updateCmd.setExecuteTimeout(3600);
        disposableTaskBiz.update(testId, updateCmd);

        // then
        final XxlMqMessage expectedMessage = new XxlMqMessage();
        expectedMessage.setId(testId);
        expectedMessage.setTopic(existedEntity.getTopic());
        expectedMessage.setGroup(existedEntity.getGroup());
        expectedMessage.setData(updateCmd.getData());
        expectedMessage.setStatus(
            taskStatusEnumAdapter.convertToXxlMessageStatus(
                TaskStatusEnum.findByKey(updateCmd.getStatus())
            ).name()
        );
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

    @Test
    public void should_deleteTaskById() {
        // when
        disposableTaskBiz.deleteById(1L);
        // then
        then(mqMessageDao).should().deleteById(1L);
    }

    @Test
    public void should_findTaskById() {
        // given:
        final XxlMqMessage mqMessage = new XxlMqMessage();
        mqMessage.setId(1L);
        mqMessage.setTopic("MyTopic");
        mqMessage.setGroup(MqConsumer.DEFAULT_GROUP);
        mqMessage.setData("MyData");
        mqMessage.setStatus(XxlMqMessageStatus.RUNNING.name());
        mqMessage.setRetryCount(5);
        mqMessage.setShardingId(1234);
        mqMessage.setEffectTime(Date.from(Instant.ofEpochMilli(11)));
        mqMessage.setTimeout(12);
        mqMessage.setAddTime(Date.from(Instant.ofEpochMilli(13)));
        mqMessage.setLog("MyLog这是日志");

        when(mqMessageDao.findById(1L)).thenReturn(mqMessage);
        // when
        final DisposableTaskDTO task = disposableTaskBiz.findTaskById(1L);

        // then
        then(mqMessageDao).should().findById(1L);

        final DisposableTaskDTO expectedTask = new DisposableTaskDTO();
        expectedTask.setId(mqMessage.getId());
        expectedTask.setTaskTopic(mqMessage.getTopic());
        expectedTask.setData(mqMessage.getData());
        expectedTask.setStatus(taskStatusEnumAdapter.convertFromXxlMqMessageStatus(mqMessage.getStatus()).getKey());
        expectedTask.setMaxRetryCount(mqMessage.getRetryCount());
        expectedTask.setShardingKey(mqMessage.getShardingId());
        expectedTask.setTriggerTime(mqMessage.getEffectTime().getTime());
        expectedTask.setExecuteTimeout(mqMessage.getTimeout());
        expectedTask.setCreateTime(mqMessage.getAddTime().getTime());
        expectedTask.setLog(mqMessage.getLog());

        assertThat(task).isEqualTo(expectedTask);
    }
}
