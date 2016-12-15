package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.margin.camera.models.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on June 14, 2016.
 *
 * @author Marta.Ginosyan
 */
public class EmailPhotosRequest implements Parcelable {

    public static final Creator<EmailPhotosRequest> CREATOR = new Creator<EmailPhotosRequest>() {
        @Override
        public EmailPhotosRequest createFromParcel(Parcel in) {
            return new EmailPhotosRequest(in);
        }

        @Override
        public EmailPhotosRequest[] newArray(int size) {
            return new EmailPhotosRequest[size];
        }
    };
    @SerializedName("reference")
    private String mReference;
    @SerializedName("to")
    private String[] mTo;
    @SerializedName("from")
    private String mFrom;
    @SerializedName("subject")
    private String mSubject;
    @SerializedName("body")
    private String mBody;
    @SerializedName("photos")
    private List<String> mRecordIds = new ArrayList<>();
    @SerializedName("gateway")
    private String mGateway;

    public EmailPhotosRequest() {
    }

    private EmailPhotosRequest(String reference, String[] to, String from, String subject,
                               String body, ArrayList<Photo> photos, String gateway) {
        this.mReference = reference;
        this.mTo = to;
        this.mFrom = from;
        this.mSubject = subject;
        this.mBody = body;
        if (photos != null && !photos.isEmpty()) {
            for (Photo photo : photos) {
                mRecordIds.add(photo.record_id());
            }
        }
        this.mGateway = gateway;
    }

    protected EmailPhotosRequest(Parcel in) {
        mReference = in.readString();
        in.readStringArray(mTo);
        mFrom = in.readString();
        mSubject = in.readString();
        mBody = in.readString();
        mGateway = in.readString();
        in.readStringList(mRecordIds);
    }

    public static EmailPhotosRequest newInstance(String reference, String[] to, String from,
                                                 String subject, String body, ArrayList<Photo> photos,
                                                 String gateway) {
        return new EmailPhotosRequest(reference, to, from, subject, body, photos, gateway);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mReference);
        dest.writeStringArray(mTo);
        dest.writeString(mFrom);
        dest.writeString(mSubject);
        dest.writeString(mBody);
        dest.writeString(mGateway);
        dest.writeStringList(mRecordIds);
    }
}
