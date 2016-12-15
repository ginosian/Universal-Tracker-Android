package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jul 29, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotosRequest {

    @SerializedName("embedded")
    boolean mEmbedded;
    @SerializedName("reference")
    private String mReference;
    @SerializedName("gateway")
    private String mGateway;

    public PhotosRequest(String reference, boolean embedded, String gateway) {
        mReference = reference;
        mGateway = gateway;
        mEmbedded = embedded;
    }
}
