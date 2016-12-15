package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jul 29, 2016.
 *
 * @author Marta.Ginosyan
 */
public class HouseBillRequest {

    @SerializedName("hawb")
    private String mHouseBillNumber;
    @SerializedName("gateway")
    private String mGateway;

    public HouseBillRequest(String houseBillNumber, String gateway) {
        mHouseBillNumber = houseBillNumber;
        mGateway = gateway;
    }
}
