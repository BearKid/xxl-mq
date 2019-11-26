package com.xxl.task.client.enums;

import java.util.Arrays;

/**
 * 任务执行状态
 */
public enum TaskStatusEnum {
    NEW(0, "新创建"),
    RUNNING(1, "执行中"),
    SUCCESS(2, "执行成功"),
    FAIL(3, "执行失败");

    private final int key;
    private final String description;

    TaskStatusEnum(int key, String description) {
        this.key = key;
        this.description = description;
    }

    public static TaskStatusEnum findByKey(int key) {
        return Arrays.stream(values())
            .filter(s -> s.getKey() == key)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("未知key = " + key));
    }

    public int getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }}
