package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PhotoUploadResponse implements Parcelable {

    public static final Creator<PhotoUploadResponse> CREATOR = new Creator<PhotoUploadResponse>() {
        @Override
        public PhotoUploadResponse createFromParcel(Parcel in) {
            return new PhotoUploadResponse(in);
        }

        @Override
        public PhotoUploadResponse[] newArray(int size) {
            return new PhotoUploadResponse[size];
        }
    };
    @SerializedName("result")
    private boolean result;
    @SerializedName("name")
    private String name;
    @SerializedName("size")
    private String size;

    public PhotoUploadResponse() {
    }

    protected PhotoUploadResponse(Parcel in) {
        result = in.readByte() != 0;
        name = in.readString();
        size = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (result ? 1 : 0));
        dest.writeString(name);
        dest.writeString(size);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
