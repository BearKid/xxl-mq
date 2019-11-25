package com.xxl.mq.admin.test.extend.adapter;

import com.xxl.mq.client.extension.enums.TaskStatusEnum;
import com.xxl.mq.admin.extension.adpater.TaskStatusEnumAdapter;
import com.xxl.mq.client.message.XxlMqMessageStatus;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskStatusEnumAdapterTest {

    private TaskStatusEnumAdapter taskStatusEnumAdapter = new TaskStatusEnumAdapter();

    @Test
    public void should_convertFromXxlMqMessageStatus() {

        // assert all normal status
        Arrays.stream(XxlMqMessageStatus.values()).forEach(xxlMqMessageStatus -> {
            final TaskStatusEnum taskStatusEnum
                = taskStatusEnumAdapter.convertFromXxlMqMessageStatus(xxlMqMessageStatus.name());

            assertThat(taskStatusEnum).isNotNull();
        });

        // assert not existed status
        final TaskStatusEnum notExistsStatus
            = taskStatusEnumAdapter.convertFromXxlMqMessageStatus("not exists status");

        assertThat(notExistsStatus).isNull();
    }
}