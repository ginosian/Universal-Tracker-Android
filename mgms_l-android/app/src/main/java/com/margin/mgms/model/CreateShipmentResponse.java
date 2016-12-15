package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jul 21, 2016.
 *
 * @author Marta.Ginosyan
 */
public class CreateShipmentResponse {

    @SerializedName("RecordID")
    private String mTaskId;

    public String getTaskId() {
        return mTaskId;
    }
}
