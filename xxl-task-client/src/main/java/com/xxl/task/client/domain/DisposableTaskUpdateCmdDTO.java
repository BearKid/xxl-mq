package com.xxl.task.client.domain;

/**
 * 某单个一次性任务的更新命令
 */
public class DisposableTaskUpdateCmdDTO {

    /**
     * 任务业务数据
     */
    private String data;

    /**
     * 任务执行状态
     */
    private String status;

    /**
     * 失败的最大可重试次数
     */
    private Integer maxRetryCount;

    /**
     * 分片提示键
     */
    private Integer shardingKey;

    /**
     * 期望任务触发时间点。毫秒时间戳。
     */
    private Long triggerTime;

    /**
     * 任务可执行最大时长
     */
    private Integer executeTimeout;

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setShardingKey(int shardingKey) {
        this.shardingKey = shardingKey;
    }

    public int getShardingKey() {
        return shardingKey;
    }

    public void setTriggerTime(long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public long getTriggerTime() {
        return triggerTime;
    }

    public void validate() {
        String errMsg = null;

        if (data == null) {
            errMsg = "data must not be null";
        }

        if (status == null) {
            errMsg = "status must not be null";
        }
        if (maxRetryCount == null) {
            errMsg = "maxRetryCount must not be null";
        }

        if (shardingKey == null) {
            errMsg = "shardingKey must not be null";
        }

        if (triggerTime == null) {
            errMsg = "triggerTime must not be null";
        }

        if (errMsg != null) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    public void setExecuteTimeout(Integer executeTimeout) {
        this.executeTimeout = executeTimeout;
    }

    public Integer getExecuteTimeout() {
        return executeTimeout;
    }
}
