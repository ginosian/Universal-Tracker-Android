package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    @SerializedName("firstname")
    private String mFirstName;
    @SerializedName("lastname")
    private String mLastName;
    @SerializedName("pin")
    private String mPin;
    @SerializedName("pimage")
    private String mImage;
    @SerializedName("defaultmenu")
    private String mDefaultMenu;
    @SerializedName("usernameMGMS")
    private String mUsername;
    @SerializedName("defaultGateway")
    private String mDefaultGateway;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("id")
    private String mId;

    public User() {
    }

    protected User(Parcel in) {
        mFirstName = in.readString();
        mLastName = in.readString();
        mPin = in.readString();
        mImage = in.readString();
        mDefaultMenu = in.readString();
        mUsername = in.readString();
        mDefaultGateway = in.readString();
        mEmail = in.readString();
        mId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeString(mPin);
        dest.writeString(mImage);
        dest.writeString(mDefaultMenu);
        dest.writeString(mUsername);
        dest.writeString(mDefaultGateway);
        dest.writeString(mEmail);
        dest.writeString(mId);
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getPin() {
        return mPin;
    }

    public String getImage() {
        return mImage;
    }

    public String getDefaultMenu() {
        return mDefaultMenu;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getDefaultGateway() {
        return mDefaultGateway;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getId() {
        return mId;
    }
}
