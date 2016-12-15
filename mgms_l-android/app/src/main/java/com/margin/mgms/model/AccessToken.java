package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AccessToken implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public AccessToken createFromParcel(Parcel in) {
            return new AccessToken(in);
        }

        public AccessToken[] newArray(int size) {
            return new AccessToken[size];
        }
    };
    @SerializedName("data")
    public Data mData;

    public AccessToken(Parcel in) {
        mData = in.readParcelable(Data.class.getClassLoader());
    }

    public String getId() {
        return mData.getId();
    }

    public String getTtl() {
        return mData.getTtl();
    }

    public String getCreated() {
        return mData.getCreated();
    }

    public String getUserId() {
        return mData.getUserId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mData, flags);
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "data=" + mData +
                '}';
    }

    private static class Data implements Parcelable {

        public static final Creator CREATOR = new Creator() {
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            public Data[] newArray(int size) {
                return new Data[size];
            }
        };
        @SerializedName("id")
        private String mId;
        @SerializedName("ttl")
        private String mTtl;
        @SerializedName("created")
        private String mCreated;
        @SerializedName("userId")
        private String mUserId;

        public Data(Parcel in) {
            mId = in.readString();
            mTtl = in.readString();
            mCreated = in.readString();
            mUserId = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mId);
            dest.writeString(mTtl);
            dest.writeString(mCreated);
            dest.writeString(mUserId);
        }

        @Override
        public String toString() {
            return "Data{" +
                    "id='" + mId + '\'' +
                    ", ttl='" + mTtl + '\'' +
                    ", created='" + mCreated + '\'' +
                    ", userId='" + mUserId + '\'' +
                    '}';
        }

        public String getId() {
            return mId;
        }

        public String getTtl() {
            return mTtl;
        }

        public String getCreated() {
            return mCreated;
        }

        public String getUserId() {
            return mUserId;
        }
    }

}
