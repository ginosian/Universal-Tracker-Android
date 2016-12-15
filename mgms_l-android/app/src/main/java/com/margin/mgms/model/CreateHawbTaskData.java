package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jun 27, 2016.
 *
 * @author Marta.Ginosyan
 */
public class CreateHawbTaskData {

    @SerializedName("CreateAndAssignNewHouseBillTaskResult")
    private String mTaskId;

    public CreateHawbTaskData() {
    }

    public String getTaskId() {
        return mTaskId;
    }
}
