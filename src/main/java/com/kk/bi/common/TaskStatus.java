package com.kk.bi.common;

/**
 * 图表任务执行状态
 *
 * @author : LXRkk
 * @date : 2025/2/7 22:34
 */
public enum TaskStatus {
    WAITING("waiting"),
    EXECUTING("executing"),
    SUCCEED("succeed"),
    FAILED("failed");

    /**
     * 描述
     */
    private String message;

    TaskStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
