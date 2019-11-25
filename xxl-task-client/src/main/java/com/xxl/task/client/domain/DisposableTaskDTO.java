package com.xxl.task.client.domain;

import java.util.Objects;

/**
 * 一次性任务的详情信息
 */
public class DisposableTaskDTO {
    /**
     * 任务id
     */
    private Long id;

    /**
     * 任务主题
     */
    private String taskTopic;

    /**
     * 业务数据
     */
    private String data;

    /**
     * 执行状态
     */
    private Integer status;

    /**
     * 执行失败的最大重试次数
     */
    private Integer maxRetryCount;

    /**
     * 分片提示键
     */
    private Long shardingKey;

    /**
     * 任务触发时间点
     */
    private Long triggerTime;

    /**
     * 执行超时时长。单位秒
     */
    private Integer executeTimeout;

    /**
     * 任务创建时间。毫秒时间戳
     */
    private Long createTime;

    /**
     * 执行日志
     */
    private String log;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTaskTopic(String taskTopic) {
        this.taskTopic = taskTopic;
    }

    public String getTaskTopic() {
        return taskTopic;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }

    public Long getShardingKey() {
        return shardingKey;
    }

    public void setShardingKey(Long shardingKey) {
        this.shardingKey = shardingKey;
    }

    public void setTriggerTime(Long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public Long getTriggerTime() {
        return triggerTime;
    }

    public void setExecuteTimeout(Integer executeTimeout) {
        this.executeTimeout = executeTimeout;
    }

    public Integer getExecuteTimeout() {
        return executeTimeout;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLog() {
        return log;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisposableTaskDTO that = (DisposableTaskDTO) o;
        return Objects.equals(getId(), that.getId()) &&
            Objects.equals(getTaskTopic(), that.getTaskTopic()) &&
            Objects.equals(getData(), that.getData()) &&
            Objects.equals(getStatus(), that.getStatus()) &&
            Objects.equals(getMaxRetryCount(), that.getMaxRetryCount()) &&
            Objects.equals(getShardingKey(), that.getShardingKey()) &&
            Objects.equals(getTriggerTime(), that.getTriggerTime()) &&
            Objects.equals(getExecuteTimeout(), that.getExecuteTimeout()) &&
            Objects.equals(getCreateTime(), that.getCreateTime()) &&
            Objects.equals(getLog(), that.getLog());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            getId(), getTaskTopic(), getData(), getStatus(), getMaxRetryCount(),
            getShardingKey(), getTriggerTime(), getExecuteTimeout(), getCreateTime(), getLog()
        );
    }
}
