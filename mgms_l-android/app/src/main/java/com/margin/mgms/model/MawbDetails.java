package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class MawbDetails implements Parcelable {

    public static final Creator<MawbDetails> CREATOR = new Creator<MawbDetails>() {
        @Override
        public MawbDetails createFromParcel(Parcel in) {
            return new MawbDetails(in);
        }

        @Override
        public MawbDetails[] newArray(int size) {
            return new MawbDetails[size];
        }
    };
    @SerializedName("Origin")
    private String mOrigin;
    @SerializedName("Carrier")
    private String mCarrier;
    @SerializedName("Consol")
    private String mAirbillNum;
    @SerializedName("Destination")
    private String mDestination;
    @SerializedName("Pieces")
    private String mPieces;
    @SerializedName("Weight")
    private String mWeight;
    @SerializedName("Flight")
    private String mFlight;
    @SerializedName("Slac")
    private String mSlac;
    private transient String mWeightUnit;
    private transient ArrayList<ShipmentLocation> mLocations = new ArrayList<>();
    private transient SpecialHandling mSpecialHandling = new SpecialHandling();

    public MawbDetails() {
    }

    protected MawbDetails(Parcel in) {
        mOrigin = in.readString();
        mCarrier = in.readString();
        mAirbillNum = in.readString();
        mDestination = in.readString();
        mPieces = in.readString();
        mWeight = in.readString();
        mFlight = in.readString();
        mSlac = in.readString();
        mWeightUnit = in.readString();
        mLocations = in.createTypedArrayList(ShipmentLocation.CREATOR);
        mSpecialHandling = in.readParcelable(SpecialHandling.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOrigin);
        dest.writeString(mCarrier);
        dest.writeString(mAirbillNum);
        dest.writeString(mDestination);
        dest.writeString(mPieces);
        dest.writeString(mWeight);
        dest.writeString(mFlight);
        dest.writeString(mSlac);
        dest.writeString(mWeightUnit);
        dest.writeTypedList(mLocations);
        dest.writeParcelable(mSpecialHandling, flags);
    }

    public String getOrigin() {
        return mOrigin;
    }

    public void setOrigin(String mOrigin) {
        this.mOrigin = mOrigin;
    }

    public String getCarrier() {
        return mCarrier;
    }

    public void setCarrier(String carrier) {
        this.mCarrier = carrier;
    }

    public String getAirbillNum() {
        return mAirbillNum;
    }

    public void setAirbillNum(String airbillNum) {
        this.mAirbillNum = airbillNum;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String destination) {
        this.mDestination = destination;
    }

    public String getPieces() {
        return mPieces;
    }

    public void setPieces(String pieces) {
        this.mPieces = pieces;
    }

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String weight) {
        this.mWeight = weight;
    }

    public String getFlight() {
        return mFlight;
    }

    public void setFlight(String flight) {
        this.mFlight = flight;
    }

    public String getSlac() {
        return mSlac;
    }

    public void setSlac(String slac) {
        this.mSlac = slac;
    }

    public String getWeightUnit() {
        return mWeightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.mWeightUnit = weightUnit;
    }

    public String getReferenceNum() {
        return mOrigin + "-" + mCarrier + "-" + mAirbillNum + "-" + mDestination;
    }

    public ArrayList<ShipmentLocation> getLocations() {
        return mLocations;
    }

    public void setLocations(ArrayList<ShipmentLocation> locations) {
        this.mLocations = locations;
    }

    public SpecialHandling getSpecialHandling() {
        return mSpecialHandling;
    }

    public void setSpecialHandling(SpecialHandling specialHandling) {
        this.mSpecialHandling = specialHandling;
    }
}
