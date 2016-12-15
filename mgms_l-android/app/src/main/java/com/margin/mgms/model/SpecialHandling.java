package com.margin.mgms.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.margin.mgms.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on May 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public class SpecialHandling extends DatabaseObject implements SpecialHandlingModel, Parcelable {

    public static final Parcelable.Creator<SpecialHandling> CREATOR =
            new Parcelable.Creator<SpecialHandling>() {

                @Override
                public SpecialHandling createFromParcel(Parcel source) {
                    return new SpecialHandling(source);
                }

                @Override
                public SpecialHandling[] newArray(int size) {
                    return new SpecialHandling[size];
                }
            };
    public static final Mapper<SpecialHandling> MAPPER = new Mapper<>(
            (Mapper.Creator<SpecialHandling>) (
                    _id, is_expedite, alert_count, is_temp_controlled, is_hazmat, is_reopened,
                    is_osd, is_unknown_snipper, screening_status, is_overage, is_shortage,
                    is_left_behind, task_id) -> {
                SpecialHandling handling = new SpecialHandling(is_expedite, alert_count,
                        is_temp_controlled, is_hazmat, is_reopened, is_osd, is_unknown_snipper,
                        screening_status, is_overage, is_shortage, is_left_behind, task_id);
                handling.setId(_id);
                return handling;
            });
    private static final String SCREENING_COMPLETED = "Screening Completed";
    private static final String SCREENING_FAILED = "Screening Failed";
    private static final String SCREENING_PENDING = "Screening Pending";
    @SerializedName("taskIsExpedite")
    private boolean mIsExpedite;
    @SerializedName("taskAlertCount")
    private int mAlertCount;
    @SerializedName("taskIsTempControlled")
    private boolean mIsTempControlled;
    @SerializedName("taskIsHazmat")
    private boolean mIsHazmat;
    @SerializedName("taskReopened")
    private boolean mIsReopened;
    @SerializedName("taskIsOSD")
    private boolean mIsOsd;
    @SerializedName("taskIsUnknownShipper")
    private boolean mIsUnknownShipper;
    @SerializedName("taskIsScreened")
    private String mScreeningStatus;
    @SerializedName("taskOverage")
    private boolean mIsOverage;
    @SerializedName("taskShortage")
    private boolean mIsShortage;
    @SerializedName("taskLeftBehind")
    private boolean mIsLeftBehind;
    private transient String mTaskId;

    public SpecialHandling() {
    }

    public SpecialHandling(boolean isExpedite, int alertCount, boolean isTempControlled,
                           boolean isHazmat, boolean isReopened, boolean isOsd,
                           boolean isUnknownShipper, String screeningStatus, boolean isOverage,
                           boolean isShortage, boolean isLeftBehind, String taskId) {
        mIsExpedite = isExpedite;
        mAlertCount = alertCount;
        mIsTempControlled = isTempControlled;
        mIsHazmat = isHazmat;
        mIsReopened = isReopened;
        mIsOsd = isOsd;
        mIsUnknownShipper = isUnknownShipper;
        mScreeningStatus = screeningStatus;
        mIsOverage = isOverage;
        mIsShortage = isShortage;
        mIsLeftBehind = isLeftBehind;
        mTaskId = taskId;
    }

    /**
     * Retrieving SpecialHandling data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private SpecialHandling(Parcel in) {
        boolean[] values = in.createBooleanArray();
        mIsExpedite = values[0];
        mIsTempControlled = values[1];
        mIsHazmat = values[2];
        mIsReopened = values[3];
        mIsOsd = values[4];
        mIsUnknownShipper = values[5];
        mIsOverage = values[6];
        mIsShortage = values[7];
        mIsLeftBehind = values[8];
        mAlertCount = in.readInt();
        mScreeningStatus = in.readString();
    }

    public void setExpedite(boolean expedite) {
        mIsExpedite = expedite;
    }

    public void setOverage(boolean overage) {
        mIsOverage = overage;
    }

    @Override
    public boolean is_expedite() {
        return mIsExpedite;
    }

    @Override
    public int alert_count() {
        return mAlertCount;
    }

    @Override
    public boolean is_temp_controlled() {
        return mIsTempControlled;
    }

    @Override
    public boolean is_hazmat() {
        return mIsHazmat;
    }

    @Override
    public boolean is_reopened() {
        return mIsReopened;
    }

    @Override
    public boolean is_osd() {
        return mIsOsd;
    }

    @Override
    public boolean is_unknown_snipper() {
        return mIsUnknownShipper;
    }

    @Nullable
    @Override
    public String screening_status() {
        return mScreeningStatus;
    }

    @Override
    public boolean is_overage() {
        return mIsOverage;
    }

    @Override
    public boolean is_shortage() {
        return mIsShortage;
    }

    @Override
    public boolean is_left_behind() {
        return mIsLeftBehind;
    }

    @NonNull
    @Override
    public String task_id() {
        return mTaskId;
    }

    public ScreeningStatus getScreeningStatus() {
        if (mScreeningStatus != null) {
            switch (mScreeningStatus) {
                case SCREENING_COMPLETED:
                    return ScreeningStatus.Completed;
                case SCREENING_FAILED:
                    return ScreeningStatus.Failed;
                case SCREENING_PENDING:
                    return ScreeningStatus.Pending;
            }
        }
        return ScreeningStatus.Undefined;
    }

    public void setScreeningStatus(ScreeningStatus screeningStatus) {
        mScreeningStatus = screeningStatus.toString();
    }

    public List<Integer> getSpecialIcons() {
        List<Integer> icons = new ArrayList<>();
        if (is_expedite()) icons.add(R.drawable.expedite_icon);
        if (is_hazmat()) icons.add(R.drawable.dangerous_goods_icon);
        if (is_temp_controlled()) icons.add(R.drawable.temp_controlled_icon);
        if (is_overage()) icons.add(R.drawable.oversize_icon);
        if (alert_count() > 0) icons.add(R.drawable.alert_icon);
        if (is_osd()) icons.add(R.drawable.damaged_icon);
        if (is_shortage()) icons.add(R.drawable.short_icon);
        switch (getScreeningStatus()) {
            case Completed:
                icons.add(R.drawable.screening_completed_icon);
                break;
            case Failed:
                icons.add(R.drawable.screening_failed_icon);
                break;
            case Pending:
                icons.add(R.drawable.screening_pending_icon);
                break;
        }
        return icons;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[]{mIsExpedite, mIsTempControlled, mIsHazmat,
                mIsReopened, mIsOsd, mIsUnknownShipper, mIsOverage, mIsShortage, mIsLeftBehind});
        dest.writeInt(mAlertCount);
        dest.writeString(mScreeningStatus);
    }

    public enum ScreeningStatus {

        Completed(SCREENING_COMPLETED),
        Failed(SCREENING_FAILED),
        Pending(SCREENING_PENDING),
        Undefined(Task.UNDEFINED);

        private String mStatus;

        ScreeningStatus(String status) {
            mStatus = status;
        }

        @Override
        public String toString() {
            return mStatus;
        }
    }

    public static final class Marshal extends SpecialHandlingMarshal<Marshal> {

        public Marshal(SpecialHandlingModel model) {
            super(model);
        }

        @Override
        public Marshal _id(long /*ignored*/_id) {
            return this;
        }
    }
}
