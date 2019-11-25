package com.xxl.mq.admin.it.dao;

import com.xxl.mq.admin.dao.IXxlMqMessageDao;
import com.xxl.mq.admin.it.SpringBootTestBase;
import com.xxl.mq.client.consumer.annotation.MqConsumer;
import com.xxl.mq.client.message.XxlMqMessage;
import com.xxl.mq.client.message.XxlMqMessageStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class IXxlMqMessageDaoTest extends SpringBootTestBase {

    @Autowired
    private IXxlMqMessageDao xxlMqMessageDao;

    @Before
    public void setUp() {
        xxlMqMessageDao.deleteAll();
    }

    @Test
    public void should_add() {
        final XxlMqMessage message = new XxlMqMessage(
            "Order.OrderCreated.Delay.MailNotifyUser",
            "{\"orderId\": 213, mail: \"1435@qq.com\"}",
            Date.from(Instant.now().plusSeconds(300))
        );
        message.setGroup(MqConsumer.DEFAULT_GROUP);
        message.setStatus(XxlMqMessageStatus.NEW.name());
        message.setRetryCount(5);
        message.setShardingId(235);
        message.setTimeout(1000);
        message.setLog("这是日志");
        final Date addTime = Date.from(Instant.now());
        xxlMqMessageDao.add(message);

        // then
        final XxlMqMessage existedEntity = xxlMqMessageDao.findById(message.getId());
        assertThat(existedEntity).isNotNull();
        assertThat(existedEntity).isEqualToIgnoringGivenFields(message, "effectTime", "addTime");
        assertThat(existedEntity.getEffectTime()).isInSameSecondWindowAs(message.getEffectTime());
        assertThat(existedEntity.getAddTime()).isInSameSecondWindowAs(addTime);
    }

    @Test
    public void should_update() {
        // given
        final XxlMqMessage message = new XxlMqMessage();
        message.setTopic("OldTopic");
        message.setGroup("OldGroup");
        message.setData("OldData");
        message.setStatus(XxlMqMessageStatus.NEW.name());
        message.setRetryCount(11);
        message.setShardingId(12);
        message.setEffectTime(Date.from(Instant.ofEpochMilli(13)));
        message.setTimeout(14);
        message.setLog("OldLog");
        xxlMqMessageDao.add(message);
        final long taskId = message.getId();

        // when
        final XxlMqMessage existedEntity = xxlMqMessageDao.findById(taskId);
        final Date addTime = existedEntity.getAddTime();
        existedEntity.setTopic("NewTopic");
        existedEntity.setGroup("NewGroup");
        existedEntity.setData("NewData");
        existedEntity.setStatus(XxlMqMessageStatus.RUNNING.name());
        existedEntity.setRetryCount(21);
        existedEntity.setShardingId(22);
        existedEntity.setEffectTime(Date.from(Instant.ofEpochMilli(23)));
        existedEntity.setTimeout(24);
        existedEntity.setAddTime(Date.from(Instant.ofEpochMilli(25)));
        existedEntity.setLog("NewLog");
        xxlMqMessageDao.update(existedEntity);

        // then
        final XxlMqMessage actual = xxlMqMessageDao.findById(taskId);
        assertThat(actual).isEqualToIgnoringGivenFields(existedEntity, "addTime", "effectTime");
        assertThat(actual.getAddTime()).isInSameSecondWindowAs(addTime);
        assertThat(actual.getEffectTime()).isInSameSecondWindowAs(existedEntity.getEffectTime());
    }

    @Test
    public void should_deleteAll() {
        // given
        final XxlMqMessage message1 = new XxlMqMessage("topic", "data", Date.from(Instant.now()));
        message1.setGroup(MqConsumer.DEFAULT_GROUP);
        message1.setStatus(XxlMqMessageStatus.NEW.name());
        message1.setRetryCount(5);
        message1.setShardingId(235);
        message1.setTimeout(1000);
        message1.setLog("这是日志");

        final XxlMqMessage message2 = new XxlMqMessage("topic", "data", Date.from(Instant.now()));
        message2.setGroup(MqConsumer.DEFAULT_GROUP);
        message2.setStatus(XxlMqMessageStatus.NEW.name());
        message2.setRetryCount(5);
        message2.setShardingId(235);
        message2.setTimeout(1000);
        message2.setLog("这是日志");

        xxlMqMessageDao.add(message1);
        xxlMqMessageDao.add(message2);
        assertThat(xxlMqMessageDao.count()).isEqualTo(2);

        // when
        xxlMqMessageDao.deleteAll();

        // then
        assertThat(xxlMqMessageDao.count()).isEqualTo(0);
    }

    @Test
    public void should_deleteById() {
        // given
        final XxlMqMessage message = new XxlMqMessage("topic1", "data1", Date.from(Instant.now()));
        message.setGroup(MqConsumer.DEFAULT_GROUP);
        message.setStatus(XxlMqMessageStatus.NEW.name());
        message.setRetryCount(5);
        message.setShardingId(235);
        message.setTimeout(1000);
        message.setLog("这是日志");
        xxlMqMessageDao.add(message);

        final XxlMqMessage message2 = new XxlMqMessage("topic2", "data2", Date.from(Instant.now()));
        message2.setGroup(MqConsumer.DEFAULT_GROUP);
        message2.setStatus(XxlMqMessageStatus.NEW.name());
        message2.setRetryCount(5);
        message2.setShardingId(235);
        message2.setTimeout(1000);
        message2.setLog("这是日志");
        xxlMqMessageDao.add(message2);

        // when
        xxlMqMessageDao.deleteById(message.getId());

        // then
        final XxlMqMessage actual = xxlMqMessageDao.findById(message.getId());
        assertThat(actual).isNull();

        final XxlMqMessage actual2 = xxlMqMessageDao.findById(message2.getId());
        assertThat(actual2).isNotNull();
    }
}
