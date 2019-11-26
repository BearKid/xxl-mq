package com.xxl.mq.admin.extension.adpater;

import com.xxl.task.client.enums.TaskStatusEnum;
import com.xxl.mq.client.message.XxlMqMessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TaskStatusEnumAdapter {
    private static final Map<XxlMqMessageStatus, TaskStatusEnum> xxlMqMessageStatusToTaskStatusEnum
        = new HashMap<>(XxlMqMessageStatus.values().length);

    private static final Map<TaskStatusEnum, XxlMqMessageStatus> taskStatusEnumToXxlMqMessageStatus
        = new HashMap<>(TaskStatusEnum.values().length);

    static {
        xxlMqMessageStatusToTaskStatusEnum.put(XxlMqMessageStatus.NEW, TaskStatusEnum.NEW);
        xxlMqMessageStatusToTaskStatusEnum.put(XxlMqMessageStatus.RUNNING, TaskStatusEnum.RUNNING);
        xxlMqMessageStatusToTaskStatusEnum.put(XxlMqMessageStatus.SUCCESS, TaskStatusEnum.SUCCESS);
        xxlMqMessageStatusToTaskStatusEnum.put(XxlMqMessageStatus.FAIL, TaskStatusEnum.FAIL);

        taskStatusEnumToXxlMqMessageStatus.put(TaskStatusEnum.NEW, XxlMqMessageStatus.NEW);
        taskStatusEnumToXxlMqMessageStatus.put(TaskStatusEnum.RUNNING, XxlMqMessageStatus.RUNNING);
        taskStatusEnumToXxlMqMessageStatus.put(TaskStatusEnum.SUCCESS, XxlMqMessageStatus.SUCCESS);
        taskStatusEnumToXxlMqMessageStatus.put(TaskStatusEnum.FAIL, XxlMqMessageStatus.FAIL);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 将{@link XxlMqMessageStatus}状态名映射至{@link TaskStatusEnum}。
     * 如果映射不存在，返回null
     *
     * @param statusName XxlMqMessageStatus name
     */
    public TaskStatusEnum convertFromXxlMqMessageStatus(String statusName) {
        XxlMqMessageStatus mqStatus;
        try {
            mqStatus = XxlMqMessageStatus.valueOf(statusName);
        } catch (IllegalArgumentException e) {
            logger.warn("XxlMqMessageStatus({}) not exists", statusName);
            mqStatus = null;
        }

        if (mqStatus == null) {
            return null;
        }
        return xxlMqMessageStatusToTaskStatusEnum.get(mqStatus);
    }

    /**
     * 映射{@link TaskStatusEnum} 至{@link XxlMqMessageStatus}
     * 如果映射不存在，返回null
     */
    public XxlMqMessageStatus convertToXxlMessageStatus(TaskStatusEnum taskStatusEnum) {
        return taskStatusEnumToXxlMqMessageStatus.get(taskStatusEnum);
    }
}
