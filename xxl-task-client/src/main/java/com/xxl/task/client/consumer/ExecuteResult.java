package com.xxl.task.client.consumer;

/**
 * 任务执行结果
 */
public class ExecuteResult {
    private static final int SUCCESS_CODE = 1;
    private static final int FAIL_CODE = 0;

    public static final ExecuteResult SUCCESS = success("");
    public static final ExecuteResult FAIL = fail("");

    public static ExecuteResult success(String msg) {
        return new ExecuteResult(SUCCESS_CODE, msg);
    }

    public static ExecuteResult fail(String msg) {
        return new ExecuteResult(FAIL_CODE, msg);
    }

    private final int code;
    private final String msg;

    private ExecuteResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }

    public boolean isFail() {
        return code == FAIL_CODE;
    }
}
