package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jul 21, 2016.
 *
 * @author Marta.Ginosyan
 */
public class CreateShipmentRequest {

    @SerializedName("origin")
    private String mOrigin;
    @SerializedName("destination")
    private String mDestination;
    @SerializedName("carrier")
    private String mCarrier;
    @SerializedName("shipment")
    private String mShipment;
    @SerializedName("pieces")
    private int mPieces;
    @SerializedName("user")
    private String mUser;
    @SerializedName("action")
    private int mAction;
    @SerializedName("gateway")
    private String mGateway;

    public CreateShipmentRequest(String origin, String destination, String carrier, String shipment,
                                 int pieces, String user, int action, String gateway) {
        mOrigin = origin;
        mDestination = destination;
        mCarrier = carrier;
        mShipment = shipment;
        mPieces = pieces;
        mUser = user;
        mAction = action;
        mGateway = gateway;
    }
}
