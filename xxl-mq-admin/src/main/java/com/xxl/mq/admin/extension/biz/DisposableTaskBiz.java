package com.xxl.mq.admin.extension.biz;

import com.xxl.task.client.domain.DisposableTaskCreateCmdDTO;
import com.xxl.mq.admin.dao.IXxlMqMessageDao;
import com.xxl.mq.client.consumer.annotation.MqConsumer;
import com.xxl.task.client.domain.DisposableTaskDTO;
import com.xxl.task.client.domain.DisposableTaskUpdateCmdDTO;
import com.xxl.mq.admin.extension.adpater.TaskStatusEnumAdapter;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.message.XxlMqMessageStatus;
import com.xxl.mq.client.util.LogHelper;
import com.xxl.task.client.enums.TaskStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class DisposableTaskBiz {

    private IXxlMqMessageDao mqMessageDao;
    private final TaskStatusEnumAdapter taskStatusEnumAdapter;

    @Autowired
    public DisposableTaskBiz(IXxlMqMessageDao mqMessageDao, TaskStatusEnumAdapter taskStatusEnumAdapter) {
        this.mqMessageDao = mqMessageDao;
        this.taskStatusEnumAdapter = taskStatusEnumAdapter;
    }

    /**
     * 新增一次性任务。
     *
     * @param disposableTaskCreateCmd 任务信息
     * @param requestIp               请求来源IP
     * @return 任务id
     */
    public Long create(DisposableTaskCreateCmdDTO disposableTaskCreateCmd, String requestIp) {
        final XxlMqMessage message = new XxlMqMessage();
        message.setTopic(disposableTaskCreateCmd.getTaskTopic());
        message.setData(disposableTaskCreateCmd.getData() == null ? "" : disposableTaskCreateCmd.getData());
        message.setEffectTime(Date.from(Instant.ofEpochMilli(
            disposableTaskCreateCmd.getTriggerTime() < 0 ? 0 : disposableTaskCreateCmd.getTriggerTime())
        ));
        message.setGroup(MqConsumer.DEFAULT_GROUP);
        message.setStatus(XxlMqMessageStatus.NEW.name());
        message.setRetryCount(disposableTaskCreateCmd.getMaxRetryCount() < 0 ? 0 : disposableTaskCreateCmd.getMaxRetryCount());
        message.setShardingId(disposableTaskCreateCmd.getShardingKey() < 0 ? 0 : disposableTaskCreateCmd.getShardingKey());
        message.setTimeout(disposableTaskCreateCmd.getExecuteTimeout() < 0 ? 0 : disposableTaskCreateCmd.getExecuteTimeout());
        message.setLog(LogHelper.makeLog("生产消息", "消息生产者IP=" + requestIp));

        validate(message);
        mqMessageDao.add(message);
        return message.getId();
    }

    private void validate(XxlMqMessage mqMessage) {
        if (mqMessage == null) {
            throw new IllegalArgumentException("xxl-mq, XxlMqMessage can not be null.");
        }

        // topic
        if (mqMessage.getTopic() == null || mqMessage.getTopic().trim().length() == 0) {
            throw new IllegalArgumentException("xxl-mq, topic empty.");
        }
        if (!(mqMessage.getTopic().length() >= 4 && mqMessage.getTopic().length() <= 255)) {
            throw new IllegalArgumentException("xxl-mq, topic length invalid[4~255].");
        }

        // group
        if (!(mqMessage.getGroup().length() >= 4 && mqMessage.getGroup().length() <= 255)) {
            throw new IllegalArgumentException("xxl-mq, group length invalid[4~255].");
        }

        // data
        if (mqMessage.getData().length() > 20000) {
            throw new IllegalArgumentException("xxl-mq, data length invalid[0~60000].");
        }
    }

    /**
     * 修改更新任务信息
     *
     * @param taskId 任务ID
     * @param updateCmd 更新命令信息
     */
    public void update(Long taskId, DisposableTaskUpdateCmdDTO updateCmd) {
        final XxlMqMessage existedEntity = mqMessageDao.findById(taskId);

        if (existedEntity == null) {
            throw new IllegalArgumentException(String.format("task(id = %s) not exists", taskId));
        }
        existedEntity.setData(updateCmd.getData());

        final XxlMqMessageStatus mqMsgStatus
            = taskStatusEnumAdapter.convertToXxlMessageStatus(TaskStatusEnum.findByKey(updateCmd.getStatus()));

        existedEntity.setStatus(mqMsgStatus.name());
        existedEntity.setRetryCount(updateCmd.getMaxRetryCount());
        existedEntity.setShardingId(updateCmd.getShardingKey());
        existedEntity.setEffectTime(Date.from(Instant.ofEpochMilli(updateCmd.getTriggerTime())));
        existedEntity.setTimeout(updateCmd.getExecuteTimeout());
        mqMessageDao.update(existedEntity);
    }

    /**
     * 按任务id删除对应任务记录
     *
     * @param taskId 任务id
     */
    public void deleteById(Long taskId) {
        mqMessageDao.deleteById(taskId);
    }

    /**
     * 按任务id查询相应的任务记录
     *
     * @param taskId 任务id
     * @return
     */
    public DisposableTaskDTO findTaskById(Long taskId) {
        final XxlMqMessage mqMessage = mqMessageDao.findById(taskId);
        if (mqMessage == null) {
            return null;
        }
        final DisposableTaskDTO task = new DisposableTaskDTO();
        task.setId(mqMessage.getId());
        task.setTaskTopic(mqMessage.getTopic());
        task.setData(mqMessage.getData());
        task.setStatus(taskStatusEnumAdapter.convertFromXxlMqMessageStatus(mqMessage.getStatus()).getKey());
        task.setMaxRetryCount(mqMessage.getRetryCount());
        task.setShardingKey(mqMessage.getShardingId());
        task.setTriggerTime(mqMessage.getEffectTime().getTime());
        task.setExecuteTimeout(mqMessage.getTimeout());
        task.setCreateTime(mqMessage.getAddTime().getTime());
        task.setLog(mqMessage.getLog());
        return task;
    }
}
