package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jun 27, 2016.
 *
 * @author Marta.Ginosyan
 */
public class CreateMawbTaskData {

    @SerializedName("CreateAndAssignNewMasterBillTaskResult")
    private String mTaskId;

    public CreateMawbTaskData() {
    }

    public String getTaskId() {
        return mTaskId;
    }
}
