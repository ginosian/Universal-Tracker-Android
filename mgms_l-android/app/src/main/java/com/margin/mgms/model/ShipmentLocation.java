package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on May 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public class ShipmentLocation extends DatabaseObject implements ShipmentLocationModel, Parcelable {

    public static final Creator<ShipmentLocation> CREATOR = new Creator<ShipmentLocation>() {
        @Override
        public ShipmentLocation createFromParcel(Parcel in) {
            return new ShipmentLocation(in);
        }

        @Override
        public ShipmentLocation[] newArray(int size) {
            return new ShipmentLocation[size];
        }
    };
    public static final Mapper<ShipmentLocation> MAPPER = new Mapper<>(
            (Mapper.Creator<ShipmentLocation>) (
                    _id, location, pieces, user, description, task_id) -> {
                ShipmentLocation shipmentLocation = new ShipmentLocation(location, pieces, user,
                        description, task_id);
                shipmentLocation.setId(_id);
                return shipmentLocation;
            });
    @SerializedName("Location")
    private String mLocation;
    @SerializedName("Pieces")
    private String mNumPieces;
    @SerializedName("UserID")
    private String mUser;
    @SerializedName("Description")
    private String mDescription;
    private transient String mTaskId;

    public ShipmentLocation() {

    }

    public ShipmentLocation(String location, String numPieces, String user, String description,
                            String taskId) {
        this(location, numPieces, user, description);
        mTaskId = taskId;
    }

    public ShipmentLocation(String location, String numPieces, String user, String description) {
        this(location, numPieces);
        mUser = user;
        mDescription = description;
    }

    public ShipmentLocation(String location, String count) {
        mLocation = location;
        mNumPieces = count;
    }

    protected ShipmentLocation(Parcel in) {
        mLocation = in.readString();
        mNumPieces = in.readString();
        mUser = in.readString();
        mDescription = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLocation);
        dest.writeString(mNumPieces);
        dest.writeString(mUser);
        dest.writeString(mDescription);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Nullable
    @Override
    public String location() {
        return mLocation;
    }

    @Nullable
    @Override
    public String pieces() {
        return mNumPieces;
    }

    @Nullable
    @Override
    public String user() {
        return mUser;
    }

    @Nullable
    @Override
    public String description() {
        return mDescription;
    }

    @NonNull
    @Override
    public String task_id() {
        return mTaskId;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public void setNumPieces(String numPieces) {
        mNumPieces = numPieces;
    }

    public static final class Marshal extends ShipmentLocationMarshal<Marshal> {

        public Marshal(ShipmentLocationModel model) {
            super(model);
        }

        @Override
        public Marshal _id(long /*ignored*/_id) {
            return this;
        }
    }

}
