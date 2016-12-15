package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on June 14, 2016.
 *
 * @author Marta.Ginosyan
 */
public class EmailPhotosResponse implements Parcelable {

    public static final Creator<EmailPhotosResponse> CREATOR = new Creator<EmailPhotosResponse>() {
        @Override
        public EmailPhotosResponse createFromParcel(Parcel in) {
            return new EmailPhotosResponse(in);
        }

        @Override
        public EmailPhotosResponse[] newArray(int size) {
            return new EmailPhotosResponse[size];
        }
    };
    @SerializedName("result")
    private String mResult;
    @SerializedName("data")
    private String mData;

    public EmailPhotosResponse() {
    }

    protected EmailPhotosResponse(Parcel in) {
        mResult = in.readString();
        mData = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mResult);
        dest.writeString(mData);
    }
}
