package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.margin.camera.models.Photo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class MasterBill implements Parcelable {

    public static final Creator<MasterBill> CREATOR = new Creator<MasterBill>() {
        @Override
        public MasterBill createFromParcel(Parcel in) {
            return new MasterBill(in);
        }

        @Override
        public MasterBill[] newArray(int size) {
            return new MasterBill[size];
        }
    };
    @SerializedName("MBDetails")
    private MawbDetails mMawbDetails;
    @SerializedName("MBRemarks")
    private List<ShipmentHistory> mHistoryList = new ArrayList<>();
    @SerializedName("MBULDs")
    private ArrayList<Uld> mUldList;
    private transient ArrayList<Photo> mPhotos = new ArrayList<>();

    public MasterBill() {
    }

    protected MasterBill(Parcel in) {
        mMawbDetails = in.readParcelable(MawbDetails.class.getClassLoader());
        mHistoryList = in.createTypedArrayList(ShipmentHistory.CREATOR);
        mUldList = in.createTypedArrayList(Uld.CREATOR);
        mPhotos = in.createTypedArrayList(Photo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mMawbDetails, flags);
        dest.writeTypedList(mHistoryList);
        dest.writeTypedList(mUldList);
        dest.writeTypedList(mPhotos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTotalPieces() {
        return mMawbDetails.getPieces();
    }

    public MawbDetails getMawbDetails() {
        return mMawbDetails;
    }

    public void setMawbDetails(MawbDetails mawbDetails) {
        this.mMawbDetails = mawbDetails;
    }

    public List<ShipmentHistory> getHistoryList() {
        return mHistoryList;
    }

    public void setHistoryList(List<ShipmentHistory> historyList) {
        this.mHistoryList = historyList;
    }

    public ArrayList<Photo> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.mPhotos = photos;
    }

    public void addPhoto(Photo photo) {
        this.mPhotos.add(0, photo);
    }

    public String getReferenceNum() {
        return mMawbDetails.getReferenceNum();
    }

    @Nullable
    public ArrayList<Uld> getUldList() {
        return mUldList;
    }
}
