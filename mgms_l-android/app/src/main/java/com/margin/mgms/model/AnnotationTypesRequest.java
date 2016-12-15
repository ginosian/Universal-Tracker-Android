package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jul 29, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationTypesRequest {

    @SerializedName("entityType")
    private String mEntityType;
    @SerializedName("gateway")
    private String mGateway;

    public AnnotationTypesRequest(String entityType, String gateway) {
        mEntityType = entityType;
        mGateway = gateway;
    }
}
