package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jun 27, 2016.
 *
 * @author Marta.Ginosyan
 */
public class CreateMawbTaskResponse {

    @SerializedName("result")
    private boolean mResult;
    @SerializedName("data")
    private CreateMawbTaskData mData;

    public boolean isResult() {
        return mResult;
    }

    public CreateMawbTaskData getData() {
        return mData;
    }
}
