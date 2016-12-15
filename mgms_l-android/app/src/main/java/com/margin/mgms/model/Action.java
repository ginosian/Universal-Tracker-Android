package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;
import com.margin.mgms.R;
import com.margin.mgms.mvp.task_manager.TaskManagerContract;

/**
 * JSON example is:
 * <pre>
 * {
 *     "ActionID": "115",
 *     "label": "CARGO UTILITIES",
 *     "Icon1": null,
 *     "MobileResource": "Configuration",
 *     "ActionCategory": "EXPORT",
 *     "TaskCount": "0",
 *     "NotCompletedTaskCount": "0",
 *     "NotCompletedTaskCountByUserID": "0",
 *     "NotAssignedTaskCount": "0",
 *     "TaskCountByUser": "0",
 *     "CompletedTaskCount": "0"
 * }
 * </pre>
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
@SuppressWarnings("unused")
public class Action {

    @SerializedName("ActionID")
    private int mId;
    @SerializedName("label")
    private String mLabel;
    @SerializedName("Icon1")
    private String mIcon;
    @SerializedName("MobileResource")
    private String mMobileResource;
    @SerializedName("ActionCategory")
    private String mActionCategory;
    @SerializedName("TaskCount")
    private int mTaskCount;
    @SerializedName("NotCompletedTaskCount")
    private int mNotCompletedTaskCount;
    @SerializedName("NotCompletedTaskCountByUserID")
    private int mNotCompletedTaskCountByUserId;
    @SerializedName("NotAssignedTaskCount")
    private int mNotAssignedTaskCount;
    @SerializedName("TaskCountByUser")
    private int mTaskCountByUser;
    @SerializedName("CompletedTaskCount")
    private int mCompletedTaskCount;

    public Action() {
    }

    public Action(String label, int completedTaskCount, int taskCount) {
        mLabel = label;
        mCompletedTaskCount = completedTaskCount;
        mTaskCount = taskCount;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public int getCompletedTaskCount() {
        return mCompletedTaskCount;
    }

    public void setCompletedTaskCount(int completedTaskCount) {
        mCompletedTaskCount = completedTaskCount;
    }

    public int getTaskCount() {
        return mTaskCount;
    }

    public void setTaskCount(int taskCount) {
        mTaskCount = taskCount;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getIcon1() {
        return mIcon;
    }

    public void setIcon1(String icon1) {
        mIcon = icon1;
    }

    public String getMobileResource() {
        return mMobileResource;
    }

    public void setMobileResource(String mobileResource) {
        mMobileResource = mobileResource;
    }

    public String getActionCategory() {
        return mActionCategory;
    }

    public void setActionCategory(String actionCategory) {
        mActionCategory = actionCategory;
    }

    public int getNotCompletedTaskCount() {
        return mNotCompletedTaskCount;
    }

    public void setNotCompletedTaskCount(int notCompletedTaskCount) {
        mNotCompletedTaskCount = notCompletedTaskCount;
    }

    public int getNotCompletedTaskCountByUserId() {
        return mNotCompletedTaskCountByUserId;
    }

    public void setNotCompletedTaskCountByUserId(int notCompletedTaskCountByUserId) {
        mNotCompletedTaskCountByUserId = notCompletedTaskCountByUserId;
    }

    public int getNotAssignedTaskCount() {
        return mNotAssignedTaskCount;
    }

    public void setNotAssignedTaskCount(int notAssignedTaskCount) {
        mNotAssignedTaskCount = notAssignedTaskCount;
    }

    public int getTaskCountByUser() {
        return mTaskCountByUser;
    }

    public void setTaskCountByUser(int taskCountByUser) {
        mTaskCountByUser = taskCountByUser;
    }

    /**
     * Returns image drawableId if it's available, null otherwise
     */
    public Integer getImage() {
        switch (mId) {
            case 0:
                return null;
            case TaskManagerContract.CARGO_PHOTO_CAPTURE:
                // TODO: This does not work to give a specific image
                return R.drawable.unlock_icon;
            default:
                return null;
        }
    }
}
