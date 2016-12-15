package com.margin.mgms.model;

/**
 * Created on Jun 01, 2016.
 *
 * @author Marta.Ginosyan
 */
public abstract class DatabaseObject {

    /**
     * Row id in database
     */
    private long mId;

    public long _id() {
        return mId;
    }

    protected void setId(long id) {
        mId = id;
    }
}
