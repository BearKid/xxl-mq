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
}
