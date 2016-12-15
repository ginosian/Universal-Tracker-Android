package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on June 07, 2016.
 *
 * @author Marta.Ginosyan
 */
public class TaskPostResponse implements Parcelable {

    public static final Creator<TaskPostResponse> CREATOR = new Creator<TaskPostResponse>() {
        @Override
        public TaskPostResponse createFromParcel(Parcel in) {
            return new TaskPostResponse(in);
        }

        @Override
        public TaskPostResponse[] newArray(int size) {
            return new TaskPostResponse[size];
        }
    };
    @SerializedName("result")
    private boolean mResult;
    @SerializedName("data")
    private String mData;

    public TaskPostResponse() {
    }

    protected TaskPostResponse(Parcel in) {
        mResult = in.readByte() != 0;
        mData = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mResult ? 1 : 0));
        dest.writeString(mData);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isResult() {
        return mResult;
    }

    public void setResult(boolean result) {
        this.mResult = result;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        this.mData = data;
    }
}
