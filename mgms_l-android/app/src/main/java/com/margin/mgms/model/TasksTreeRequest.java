package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jul 29, 2016.
 *
 * @author Marta.Ginosyan
 */
public class TasksTreeRequest {

    @SerializedName("user")
    private String mUser;
    @SerializedName("date")
    private String mDate;
    @SerializedName("filter")
    private String mFilter;
    @SerializedName("display")
    private String mDisplay;
    @SerializedName("gateway")
    private String mGateway;

    public TasksTreeRequest(String user, String date, String filter, String display,
                            String gateway) {
        mUser = user;
        mDate = date;
        mFilter = filter;
        mDisplay = display;
        mGateway = gateway;
    }
}
