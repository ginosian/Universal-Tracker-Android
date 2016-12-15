package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on May 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public class Contact implements Parcelable {

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
    @SerializedName("AccountNo")
    private String mAccountNum;
    @SerializedName("RefNo")
    private String mRefNum;
    @SerializedName("Name")
    private String mName;
    @SerializedName("Address")
    private String mAddress;
    @SerializedName("State")
    private String mState;
    @SerializedName("PostalCode")
    private String mPostalCode;
    @SerializedName("Country")
    private String mCountry;
    @SerializedName("Attn")
    private String mAttention;
    @SerializedName("Phone")
    private String mPhone;
    @SerializedName("City")
    private String mCity;

    public Contact() {
    }

    protected Contact(Parcel in) {
        mAccountNum = in.readString();
        mRefNum = in.readString();
        mName = in.readString();
        mAddress = in.readString();
        mState = in.readString();
        mPostalCode = in.readString();
        mCountry = in.readString();
        mAttention = in.readString();
        mPhone = in.readString();
        mCity = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAccountNum);
        dest.writeString(mRefNum);
        dest.writeString(mName);
        dest.writeString(mAddress);
        dest.writeString(mState);
        dest.writeString(mPostalCode);
        dest.writeString(mCountry);
        dest.writeString(mAttention);
        dest.writeString(mPhone);
        dest.writeString(mCity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAccountNum() {
        return mAccountNum;
    }

    public void setAccountNum(String mAccountNum) {
        this.mAccountNum = mAccountNum;
    }

    public String getRefNum() {
        return mRefNum;
    }

    public void setRefNum(String mRefNum) {
        this.mRefNum = mRefNum;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getState() {
        return mState;
    }

    public void setState(String mState) {
        this.mState = mState;
    }

    public String getPostalCode() {
        return mPostalCode;
    }

    public void setPostalCode(String mPostalCode) {
        this.mPostalCode = mPostalCode;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getAttention() {
        return mAttention;
    }

    public void setAttention(String mAttention) {
        this.mAttention = mAttention;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }
}
