package com.margin.mgms.model;

/**
 * Created on Jul 20, 2016.
 *
 * @author Marta.Ginosyan
 */
public class TaskHeader {

    private boolean mIsAssigned;
    private int mTaskCount;

    public TaskHeader(boolean isAssigned, int taskCount) {
        mIsAssigned = isAssigned;
        mTaskCount = taskCount;
    }

    public boolean isAssigned() {
        return mIsAssigned;
    }

    public void setAssigned(boolean assigned) {
        mIsAssigned = assigned;
    }

    public int getTaskCount() {
        return mTaskCount;
    }

    public void setTaskCount(int taskCount) {
        mTaskCount = taskCount;
    }
}
