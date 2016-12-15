package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on June 07, 2016.
 *
 * @author Marta.Ginosyan
 */
public class BarcodeModel implements Parcelable {

    public static final Creator<BarcodeModel> CREATOR = new Creator<BarcodeModel>() {
        @Override
        public BarcodeModel createFromParcel(Parcel in) {
            return new BarcodeModel(in);
        }

        @Override
        public BarcodeModel[] newArray(int size) {
            return new BarcodeModel[size];
        }
    };
    @SerializedName("BarcodeType")
    private String mType;
    @SerializedName("HouseBillNumber")
    private String mHawbNum;
    @SerializedName("MasterBillNumber")
    private String mMawbNum;
    @SerializedName("CarrierNumber")
    private String mCarrierNum;
    @SerializedName("PieceNumber")
    private String mPieceNum;

    public BarcodeModel() {
    }

    public BarcodeModel(String type, String hawbNum, String mawbNum, String carrierNum,
                        String pieceNum) {
        this.mType = type;
        this.mHawbNum = hawbNum;
        this.mMawbNum = mawbNum;
        this.mCarrierNum = carrierNum;
        this.mPieceNum = pieceNum;
    }

    protected BarcodeModel(Parcel in) {
        mType = in.readString();
        mHawbNum = in.readString();
        mMawbNum = in.readString();
        mCarrierNum = in.readString();
        mPieceNum = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
        dest.writeString(mHawbNum);
        dest.writeString(mMawbNum);
        dest.writeString(mCarrierNum);
        dest.writeString(mPieceNum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getHawbNum() {
        return mHawbNum;
    }

    public void setHawbNum(String hawbNum) {
        this.mHawbNum = hawbNum;
    }

    public String getMawbNum() {
        return mMawbNum;
    }

    public void setMawbNum(String mawbNum) {
        this.mMawbNum = mawbNum;
    }

    public String getCarrierNum() {
        return mCarrierNum;
    }

    public void setCarrierNum(String carrierNum) {
        this.mCarrierNum = carrierNum;
    }

    public String getPieceNum() {
        return mPieceNum;
    }

    public void setPieceNum(String pieceNum) {
        this.mPieceNum = pieceNum;
    }
}
