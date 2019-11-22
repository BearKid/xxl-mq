package com.xxl.mq.admin.core.model.extend;

import java.util.Objects;

/**
 * 一次性任务
 */
public class DisposableTaskCreateCmdDTO {

    /**
     * 任务主题。相同业务逻辑的任务都归类至同一个“任务主题”下。
     */
    private String taskTopic;

    /**
     * 业务数据
     */
    private String data;

    /**
     * 期望任务触发时间点。毫秒时间戳。
     * 不填或小于当前时间则表示立刻触发。
     */
    private long triggerTime;

    /**
     * 分片提示键。可选。作为同一主题下的任务集分片的依据。
     * 比如可以用订单ID、商品ID等作为分片提示键，任务将会被写入某个分片。
     * 一个分区由一个线程/进程消化任务，多个线程/进程并行消化各自分片的任务。
     */
    private int shardingKey;

    /**
     * 任务执行失败的最大重试次数
     */
    private int maxRetryCount;

    /**
     * 任务可执行最大时长。单位秒。超时将会导致任务失败。
     */
    private int executeTimeout;

    public DisposableTaskCreateCmdDTO() {
    }

    public DisposableTaskCreateCmdDTO(String taskTopic, String data, long triggerTime) {
        this.taskTopic = taskTopic;
        this.data = data;
        this.triggerTime = triggerTime;
    }

    public String getTaskTopic() {
        return taskTopic;
    }

    public void setTaskTopic(String taskTopic) {
        this.taskTopic = taskTopic;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public int getShardingKey() {
        return shardingKey;
    }

    public void setShardingKey(int shardingKey) {
        this.shardingKey = shardingKey;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public int getExecuteTimeout() {
        return executeTimeout;
    }

    public void setExecuteTimeout(int executeTimeout) {
        this.executeTimeout = executeTimeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisposableTaskCreateCmdDTO that = (DisposableTaskCreateCmdDTO) o;
        return getTriggerTime() == that.getTriggerTime() &&
            getMaxRetryCount() == that.getMaxRetryCount() &&
            getExecuteTimeout() == that.getExecuteTimeout() &&
            Objects.equals(getTaskTopic(), that.getTaskTopic()) &&
            Objects.equals(getData(), that.getData()) &&
            Objects.equals(getShardingKey(), that.getShardingKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTaskTopic(), getData(), getTriggerTime(), getShardingKey(), getMaxRetryCount(), getExecuteTimeout());
    }
}
