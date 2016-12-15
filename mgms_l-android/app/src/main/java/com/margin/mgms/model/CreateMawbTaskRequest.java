package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jun 27, 2016.
 *
 * @author Marta.Ginosyan
 */
public class CreateMawbTaskRequest {

    @SerializedName("action")
    private int mAction;
    @SerializedName("user")
    private String mUser;
    @SerializedName("carrier")
    private String mCarrier;
    @SerializedName("mawb")
    private String mMawb;
    @SerializedName("origin")
    private String mOrigin;
    @SerializedName("destination")
    private String mDestination;
    @SerializedName("gateway")
    private String mGateway;

    public CreateMawbTaskRequest(int action, String carrier, String user, String mawb,
                                 String origin, String destination, String gateway) {
        mAction = action;
        mUser = user;
        mCarrier = carrier;
        mMawb = mawb;
        mOrigin = origin;
        mDestination = destination;
        mGateway = gateway;
    }
}
