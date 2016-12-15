package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jul 29, 2016.
 *
 * @author Marta.Ginosyan
 */
public class MasterBillRequest {

    @SerializedName("carrier")
    private String mCarrier;
    @SerializedName("mawb")
    private String mMasterBill;
    @SerializedName("gateway")
    private String mGateway;

    public MasterBillRequest(String carrier, String masterBill, String gateway) {
        mCarrier = carrier;
        mMasterBill = masterBill;
        mGateway = gateway;
    }
}
