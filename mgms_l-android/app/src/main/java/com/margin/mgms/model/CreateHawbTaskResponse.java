package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jun 27, 2016.
 *
 * @author Marta.Ginosyan
 */
public class CreateHawbTaskResponse {

    @SerializedName("result")
    private boolean mResult;
    @SerializedName("data")
    private CreateHawbTaskData mData;

    public boolean isResult() {
        return mResult;
    }

    public CreateHawbTaskData getData() {
        return mData;
    }
}
