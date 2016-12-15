package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jun 27, 2016.
 *
 * @author Marta.Ginosyan
 */
public class CreateHawbTaskRequest {

    @SerializedName("action")
    private int mAction;
    @SerializedName("user")
    private String mUser;
    @SerializedName("hawb")
    private String mHawb;
    @SerializedName("origin")
    private String mOrigin;
    @SerializedName("destination")
    private String mDestination;
    @SerializedName("gateway")
    private String mGateway;

    public CreateHawbTaskRequest(int action, String user, String hawb, String origin,
                                 String destination, String gateway) {
        mAction = action;
        mUser = user;
        mHawb = hawb;
        mOrigin = origin;
        mDestination = destination;
        mGateway = gateway;
    }
}
