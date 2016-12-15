package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.margin.camera.models.Photo;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class HouseBill implements Parcelable {

    public static final Creator<HouseBill> CREATOR = new Creator<HouseBill>() {
        @Override
        public HouseBill createFromParcel(Parcel in) {
            return new HouseBill(in);
        }

        @Override
        public HouseBill[] newArray(int size) {
            return new HouseBill[size];
        }
    };
    @SerializedName("HBDetails")
    private HawbDetails mHawbDetails;
    @SerializedName("HBReceiver")
    private Contact mReceiver;
    @SerializedName("HBSender")
    private Contact mSender;
    @SerializedName("HBDims")
    private ArrayList<Dimensions> mDimensions = new ArrayList<>();
    @SerializedName("HBLocations")
    private ArrayList<ShipmentLocation> mLocations = new ArrayList<>();
    private transient ArrayList<Photo> mPhotos = new ArrayList<>();

    public HouseBill() {
    }

    protected HouseBill(Parcel in) {
        mHawbDetails = in.readParcelable(HawbDetails.class.getClassLoader());
        mReceiver = in.readParcelable(Contact.class.getClassLoader());
        mSender = in.readParcelable(Contact.class.getClassLoader());
        mDimensions = in.createTypedArrayList(Dimensions.CREATOR);
        mLocations = in.createTypedArrayList(ShipmentLocation.CREATOR);
        mPhotos = in.createTypedArrayList(Photo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mHawbDetails, flags);
        dest.writeParcelable(mReceiver, flags);
        dest.writeParcelable(mSender, flags);
        dest.writeTypedList(mDimensions);
        dest.writeTypedList(mLocations);
        dest.writeTypedList(mPhotos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public HawbDetails getHawbDetails() {
        return mHawbDetails;
    }

    public void setHawbDetails(HawbDetails hawbDetails) {
        mHawbDetails = hawbDetails;
    }

    public String getTotalPieces() {
        return mHawbDetails.getPieces();
    }

    public Contact getReceiver() {
        return mReceiver;
    }

    public Contact getSender() {
        return mSender;
    }

    public ArrayList<Dimensions> getDimensions() {
        return mDimensions;
    }

    public ArrayList<ShipmentLocation> getLocations() {
        return mLocations;
    }

    public ArrayList<Photo> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(ArrayList<Photo> mPhotos) {
        this.mPhotos = mPhotos;
    }

    public void addPhoto(Photo photo) {
        this.mPhotos.add(0, photo);
    }

    public String getReferenceNum() {
        return mHawbDetails.getReferenceNum();
    }
}
