package com.xxl.mq.admin.extend.biz;

import com.xxl.mq.admin.core.model.extend.DisposableTaskCreateCmdDTO;
import com.xxl.mq.admin.dao.IXxlMqMessageDao;
import com.xxl.mq.client.consumer.annotation.MqConsumer;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.message.XxlMqMessageStatus;
import com.xxl.mq.client.util.LogHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class DisposableTaskBiz {

    private IXxlMqMessageDao mqMessageDao;

    @Autowired
    public DisposableTaskBiz(IXxlMqMessageDao mqMessageDao) {
        this.mqMessageDao = mqMessageDao;
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
}
