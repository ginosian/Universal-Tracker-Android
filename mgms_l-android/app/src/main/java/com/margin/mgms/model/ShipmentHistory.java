package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on May 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public class ShipmentHistory implements Parcelable {

    public static final Creator<ShipmentHistory> CREATOR = new Creator<ShipmentHistory>() {
        @Override
        public ShipmentHistory createFromParcel(Parcel in) {
            return new ShipmentHistory(in);
        }

        @Override
        public ShipmentHistory[] newArray(int size) {
            return new ShipmentHistory[size];
        }
    };
    @SerializedName("RecDate")
    private String mRecDate;
    @SerializedName("Description")
    private String mDescription;
    @SerializedName("UserID")
    private String mUserID;
    @SerializedName("StatusDescription")
    private String mStatusDescription;
    @SerializedName("Image")
    private String mImage;

    public ShipmentHistory() {
    }

    protected ShipmentHistory(Parcel in) {
        mRecDate = in.readString();
        mDescription = in.readString();
        mUserID = in.readString();
        mStatusDescription = in.readString();
        mImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRecDate);
        dest.writeString(mDescription);
        dest.writeString(mUserID);
        dest.writeString(mStatusDescription);
        dest.writeString(mImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getRecDate() {
        return mRecDate;
    }

    public void setRecDate(String recDate) {
        this.mRecDate = recDate;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        this.mUserID = userID;
    }

    public String getStatusDescription() {
        return mStatusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.mStatusDescription = statusDescription;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }
}
