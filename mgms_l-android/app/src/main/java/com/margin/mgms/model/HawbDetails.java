package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class HawbDetails implements Parcelable {

    public static final Creator<HawbDetails> CREATOR = new Creator<HawbDetails>() {
        @Override
        public HawbDetails createFromParcel(Parcel in) {
            return new HawbDetails(in);
        }

        @Override
        public HawbDetails[] newArray(int size) {
            return new HawbDetails[size];
        }
    };
    @SerializedName("Origin")
    private String mOrigin;
    @SerializedName("AirbillNo")
    private String mAirbillNum;
    @SerializedName("Destination")
    private String mDestination;
    @SerializedName("CountryOfOrigin")
    private String mOriginCountry;
    @SerializedName("CountryOfDestination")
    private String mDestinationCountry;
    @SerializedName("NoOfPackages")
    private String mPieces;
    @SerializedName("GrossWeight")
    private String mWeight;
    @SerializedName("WeightUOM")
    private String mWeightUnit;
    @SerializedName("Description")
    private String mDescription;
    @SerializedName("Notes")
    private String mNotes;
    //TODO: temporary value
    private transient SpecialHandling specialHandling = new SpecialHandling();

    public HawbDetails() {
    }

    protected HawbDetails(Parcel in) {
        mOrigin = in.readString();
        mAirbillNum = in.readString();
        mDestination = in.readString();
        mOriginCountry = in.readString();
        mDestinationCountry = in.readString();
        mPieces = in.readString();
        mWeight = in.readString();
        mWeightUnit = in.readString();
        mDescription = in.readString();
        mNotes = in.readString();
        specialHandling = in.readParcelable(SpecialHandling.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOrigin);
        dest.writeString(mAirbillNum);
        dest.writeString(mDestination);
        dest.writeString(mOriginCountry);
        dest.writeString(mDestinationCountry);
        dest.writeString(mPieces);
        dest.writeString(mWeight);
        dest.writeString(mWeightUnit);
        dest.writeString(mDescription);
        dest.writeString(mNotes);
        dest.writeParcelable(specialHandling, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getReferenceNum() {
        return mOrigin + "-" + mAirbillNum + "-" + mDestination;
    }

    public String getOrigin() {
        return mOrigin;
    }

    public String getAirbillNum() {
        return mAirbillNum;
    }

    public void setAirbillNum(String mAirbillNum) {
        this.mAirbillNum = mAirbillNum;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String mDestination) {
        this.mDestination = mDestination;
    }

    public String getOriginCountry() {
        return mOriginCountry;
    }

    public void setOriginCountry(String mOriginCountry) {
        this.mOriginCountry = mOriginCountry;
    }

    public String getDestinationCountry() {
        return mDestinationCountry;
    }

    public void setDestinationCountry(String mDestinationCountry) {
        this.mDestinationCountry = mDestinationCountry;
    }

    public String getPieces() {
        return mPieces;
    }

    public void setPieces(String mPieces) {
        this.mPieces = mPieces;
    }

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String mWeight) {
        this.mWeight = mWeight;
    }

    public String getWeightUnit() {
        return mWeightUnit;
    }

    public void setWeightUnit(String mWeightUnit) {
        this.mWeightUnit = mWeightUnit;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String mNotes) {
        this.mNotes = mNotes;
    }

    public SpecialHandling getSpecialHandling() {
        return specialHandling;
    }

    public void setSpecialHandling(SpecialHandling specialHandling) {
        this.specialHandling = specialHandling;
    }

    public void setmOrigin(String mOrigin) {
        this.mOrigin = mOrigin;
    }
}
