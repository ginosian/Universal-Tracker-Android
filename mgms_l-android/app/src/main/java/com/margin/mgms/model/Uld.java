package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on June 10, 2016.
 *
 * @author Marta.Ginosyan
 */
@SuppressWarnings("unused")
public class Uld implements Parcelable {

    public static final Creator<Uld> CREATOR = new Creator<Uld>() {
        @Override
        public Uld createFromParcel(Parcel in) {
            return new Uld(in);
        }

        @Override
        public Uld[] newArray(int size) {
            return new Uld[size];
        }
    };
    @SerializedName("RecID")
    String mUldId;
    @SerializedName("ULD")
    String mUld;
    @SerializedName("ULDNo")
    String mUldNum;
    @SerializedName("FinalUlds")
    String mFinalUld;
    @SerializedName("MasterBillNo")
    String mMasterBillNum;

    public Uld() {
    }

    protected Uld(Parcel in) {
        mUldId = in.readString();
        mUld = in.readString();
        mUldNum = in.readString();
        mFinalUld = in.readString();
        mMasterBillNum = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUldId);
        dest.writeString(mUld);
        dest.writeString(mUldNum);
        dest.writeString(mFinalUld);
        dest.writeString(mMasterBillNum);
    }

    public String getUldId() {
        return mUldId;
    }

    public void setUldId(String uldId) {
        this.mUldId = uldId;
    }

    public String getUld() {
        return mUld;
    }

    public void setUld(String uld) {
        this.mUld = uld;
    }

    public String getUldNum() {
        return mUldNum;
    }

    public void setUldNum(String uldNum) {
        this.mUldNum = uldNum;
    }

    public String getFinalUld() {
        return mFinalUld;
    }

    public void setFinalUld(String finalUld) {
        this.mFinalUld = finalUld;
    }

    public String getMasterBillNum() {
        return mMasterBillNum;
    }

    public void setMasterBillNum(String masterBillNum) {
        this.mMasterBillNum = masterBillNum;
    }
}
