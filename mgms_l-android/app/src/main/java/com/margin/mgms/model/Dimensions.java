package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on May 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public class Dimensions implements Parcelable {

    public static final Creator<Dimensions> CREATOR = new Creator<Dimensions>() {
        @Override
        public Dimensions createFromParcel(Parcel in) {
            return new Dimensions(in);
        }

        @Override
        public Dimensions[] newArray(int size) {
            return new Dimensions[size];
        }
    };
    @SerializedName("DimLength")
    private float mLength;
    @SerializedName("DimWidth")
    private float mWidth;
    @SerializedName("DimHeight")
    private float mHeight;
    @SerializedName("DimUOM")
    private String mUnits;
    @SerializedName("Items")
    private int mNumPieces;
    // TODO: attention, no serialized name
    private transient int mTotalPieces;

    public Dimensions() {
    }

    public Dimensions(Parcel in) {
        mLength = in.readFloat();
        mWidth = in.readFloat();
        mHeight = in.readFloat();
        mUnits = in.readString();
        mNumPieces = in.readInt();
        mTotalPieces = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mLength);
        dest.writeFloat(mWidth);
        dest.writeFloat(mHeight);
        dest.writeString(mUnits);
        dest.writeInt(mNumPieces);
        dest.writeInt(mTotalPieces);
    }

    public float getLength() {
        return mLength;
    }

    public void setLength(float mLength) {
        this.mLength = mLength;
    }

    public float getWidth() {
        return mWidth;
    }

    public void setWidth(float mWidth) {
        this.mWidth = mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public void setHeight(float mHeight) {
        this.mHeight = mHeight;
    }

    public String getUnits() {
        return mUnits;
    }

    public void setUnits(String mUnits) {
        this.mUnits = mUnits;
    }

    public int getNumPieces() {
        return mNumPieces;
    }

    public void setNumPieces(int mNumPieces) {
        this.mNumPieces = mNumPieces;
    }

    public int getTotalPieces() {
        return mTotalPieces;
    }

    public void setTotalPieces(int mTotalPieces) {
        this.mTotalPieces = mTotalPieces;
    }
}
