package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jul 29, 2016.
 *
 * @author Marta.Ginosyan
 */
public class TasksRequest {

    @SerializedName("task")
    private int mActionId;
    @SerializedName("user")
    private String mUserId;
    @SerializedName("date")
    private String mDate;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("gateway")
    private String mGateway;

    public TasksRequest(int actionId, String userId, String date, String status, String gateway) {
        mActionId = actionId;
        mUserId = userId;
        mDate = date;
        mStatus = status;
        mGateway = gateway;
    }
}
