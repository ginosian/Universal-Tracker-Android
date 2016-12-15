package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jul 29, 2016.
 *
 * @author Marta.Ginosyan
 */
public class ParseBarcodeRequest {

    @SerializedName("barcode")
    private String mBarcode;
    @SerializedName("gateway")
    private String mGateway;

    public ParseBarcodeRequest(String barcode, String gateway) {
        mBarcode = barcode;
        mGateway = gateway;
    }
}
