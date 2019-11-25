package com.xxl.mq.admin.extension.adpater;

import com.xxl.task.client.enums.TaskStatusEnum;
import com.xxl.mq.client.message.XxlMqMessageStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TaskStatusEnumAdapter {
    private static final Map<String, TaskStatusEnum> mappingByXxlMqMessageStatus
        = new HashMap<>(XxlMqMessageStatus.values().length);

    static {
        mappingByXxlMqMessageStatus.put(XxlMqMessageStatus.NEW.name(), TaskStatusEnum.NEW);
        mappingByXxlMqMessageStatus.put(XxlMqMessageStatus.RUNNING.name(), TaskStatusEnum.RUNNING);
        mappingByXxlMqMessageStatus.put(XxlMqMessageStatus.SUCCESS.name(), TaskStatusEnum.SUCCESS);
        mappingByXxlMqMessageStatus.put(XxlMqMessageStatus.FAIL.name(), TaskStatusEnum.FAIL);
    }

    /**
     * 将{@link XxlMqMessageStatus}状态名映射至{@link TaskStatusEnum}。
     * 如果映射不存在，返回null
     *
     * @param statusName XxlMqMessageStatus name
     */
    public TaskStatusEnum convertFromXxlMqMessageStatus(String statusName) {
        return mappingByXxlMqMessageStatus.get(statusName);
    }
}
