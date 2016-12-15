package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on June 07, 2016.
 *
 * @author Marta.Ginosyan
 */
public class TaskPostRequest {

    @SerializedName("user")
    private String mUser;
    @SerializedName("task")
    private String mTaskId;
    @SerializedName("status")
    private boolean mStatus;
    @SerializedName("gateway")
    private String mGateway;

    private TaskPostRequest(String user, String taskId, boolean status, String gateway) {
        this.mUser = user;
        this.mTaskId = taskId;
        this.mStatus = status;
        this.mGateway = gateway;
    }

    public static TaskPostRequest newTask(String user, String taskId, boolean status, String gateway) {
        return new TaskPostRequest(user, taskId, status, gateway);
    }
}
