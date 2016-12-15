package com.margin.mgms.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.margin.mgms.R;
import com.margin.mgms.database.DateAdapter;
import com.margin.mgms.util.DateUtils;
import com.margin.mgms.util.PrefsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created on May 13, 2016.
 *
 * @author Marta.Ginosyan
 */
@SuppressWarnings("unused")
public class Task extends DatabaseObject implements TaskModel {

    public static final String NOT_COMPLETED = "Not Completed";
    public static final String NOT_STARTED = "Not Started";
    public static final String IN_PROGRESS = "In Progress";
    public static final String COMPLETED = "Completed";
    public static final String CANCELED = "Canceled";
    public static final String NOT_ASSIGNED = "Not Assigned";
    public static final String ALL = "Show All";
    public static final String UNDEFINED = "Undefined";
    private static final DateAdapter DATE_ADAPTER = new DateAdapter();
    public static final Mapper<Task> MAPPER = new Mapper<>((
            _id, task_id, action_id, name, date, owner, users, status, entity_type, reference,
            carrier_number, mawb, hawb, origin, destination, total_pieces, total_weight,
            weight_uom, start_date, end_date) -> {
        Task task = new Task(task_id, action_id, name, date, owner, users,
                status, entity_type, reference, carrier_number, mawb, hawb, origin, destination,
                total_pieces, total_weight, weight_uom, start_date, end_date);
        task.setId(_id);
        return task;
    }, DATE_ADAPTER, DATE_ADAPTER, DATE_ADAPTER);
    @SerializedName("taskId")
    private String mId;
    @SerializedName("taskActionId")
    private int mActionId;
    @SerializedName("taskName")
    private String mName;
    @SerializedName("taskDate")
    private String mDate;
    @SerializedName("taskOwner")
    private String mOwner;
    @SerializedName("taskUsers")
    private String mUsers;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("taskEntityType")
    private String mEntityType;
    @SerializedName("taskReference")
    private String mReference;
    @SerializedName("taskCarrierNumber")
    private String mCarrierNumber;
    @SerializedName("taskMAWB")
    private String mMawb;
    @SerializedName("taskHAWB")
    private String mHawb;
    @SerializedName("taskOrigin")
    private String mOrigin;
    @SerializedName("taskDestination")
    private String mDestination;
    @SerializedName("taskTotalPieces")
    private int mTotalPieces;
    @SerializedName("taskTotalWeight")
    private float mTotalWeight;
    @SerializedName("taskWeightUOM")
    private String mWeightUom;
    @SerializedName("taskIsExpedite")
    private int mIsExpedite;
    @SerializedName("taskAlertCount")
    private int mAlertCount;
    @SerializedName("taskIsTempControlled")
    private int mIsTempControlled;
    @SerializedName("taskIsHazmat")
    private int mIsHazmat;
    @SerializedName("taskReopened")
    private int mReopened;
    @SerializedName("taskIsOSD")
    private int mIsOSD;
    @SerializedName("taskIsUnknownShipper")
    private int mIsUnknownShipper;
    @SerializedName("taskIsScreened")
    private String mIsScreened;
    @SerializedName("taskLocationList")
    private String mLocationList;
    @SerializedName("taskOverage")
    private int mOverage;
    @SerializedName("taskShortage")
    private int mShortage;
    @SerializedName("taskLeftBehind")
    private int mLeftBehind;
    @SerializedName("taskStartDate")
    private String mStartDate;
    @SerializedName("taskEndDate")
    private String mEndDate;
    private transient SpecialHandling mSpecialHandling;

    public Task(String taskId, int actionId, String name, Date date, String owner, String users,
                String status, String entityType, String reference, String carrierNumber, String
                        mawb, String hawb, String origin, String destination, int totalPieces,
                float totalWeight, String weightUom, Date startDate, Date endDate) {
        mId = taskId;
        mActionId = actionId;
        mName = name;
        setDate(date);
        mOwner = owner;
        mUsers = users;
        mStatus = status;
        mEntityType = entityType;
        mReference = reference;
        mCarrierNumber = carrierNumber;
        mMawb = mawb;
        mHawb = hawb;
        mOrigin = origin;
        mDestination = destination;
        mTotalPieces = totalPieces;
        mTotalWeight = totalWeight;
        mWeightUom = weightUom;
        setStartDate(startDate);
        setEndDate(endDate);
    }

    public void setDate(Date date) {
        mDate = DateUtils.formatDate(date);
    }

    public String getFormattedDate() {
        return DateUtils.getFormattedDate(date());
    }

    public void setStartDate(Date startDate) {
        mStartDate = DateUtils.formatDate(startDate);
    }

    public String getFormattedStartDate() {
        return DateUtils.getFormattedDate(start_date());
    }

    public void setEndDate(Date endDate) {
        mEndDate = DateUtils.formatDate(endDate);
    }

    public String getFormattedEndDate() {
        return DateUtils.getFormattedDate(end_date());
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public List<ShipmentLocation> getLocations() {
        if (!TextUtils.isEmpty(mLocationList)) {
            List<String> locationsString = Arrays.asList(mLocationList.split("\\s*,\\s*"));
            List<ShipmentLocation> locations = new ArrayList<>(locationsString.size());
            if (!locationsString.isEmpty()) {
                for (String locationString : locationsString) {
                    String name = locationString.substring(0, locationString.indexOf("("));
                    String count = locationString.substring(
                            locationString.indexOf("(") + 1, locationString.indexOf(")"));
                    locations.add(new ShipmentLocation(name, count, null, null, mId));
                }
            }
            return locations;
        }
        return null;
    }

    public void setLocations(List<ShipmentLocation> locations) {
        if (locations != null && !locations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < locations.size(); i++) {
                ShipmentLocation location = locations.get(i);
                builder.append(location.location());
                builder.append("(");
                builder.append(location.pieces());
                builder.append(")");
                if (i != locations.size() - 1) {
                    builder.append(",");
                }
            }
            mLocationList = builder.toString();
        }
    }

    public Status getStatus() {
        if (mStatus != null) {
            switch (mStatus) {
                case NOT_COMPLETED:
                    return Status.NotCompleted;
                case NOT_STARTED:
                    return Status.NotStarted;
                case IN_PROGRESS:
                    return Status.InProgress;
                case COMPLETED:
                    return Status.Completed;
                case CANCELED:
                    return Status.Canceled;
                case NOT_ASSIGNED:
                    return Status.NotAssigned;
                case ALL:
                    return Status.All;
            }
        }
        return Status.Undefined;
    }

    public void setStatus(Status status) {
        mStatus = status.toString();
    }

    public boolean isAssignedToMe() {
        String username = PrefsUtils.getUsername();
        if (username != null) {
            if (mOwner != null && username.equals(mOwner)) return true;
            else if (null != mUsers) {
                for (String user : mUsers.split(",")) {
                    if (user.equals(username)) return true;
                }
            }
        }
        return false;
    }

    public List<Integer> getSpecialIcons() {
        return getSpecialHandling().getSpecialIcons();
    }

    public String getFormattedTitle(Context context) {
        return context.getString(R.string.title_tasks_headline, origin(),
                reference(), destination());
    }

    public SpecialHandling getSpecialHandling() {
        if (mSpecialHandling == null) {
            mSpecialHandling = new SpecialHandling(mIsExpedite > 0, mAlertCount,
                    mIsTempControlled > 0, mIsHazmat > 0, mReopened > 0, mIsOSD > 0,
                    mIsUnknownShipper > 0, mIsScreened, mOverage > 0, mShortage > 0,
                    mLeftBehind > 0, mId);
        }
        return mSpecialHandling;
    }

    public void setSpecialHandling(SpecialHandling handling) {
        mSpecialHandling = handling;
        mIsExpedite = handling.is_expedite() ? 1 : 0;
        mAlertCount = handling.alert_count();
        mIsTempControlled = handling.is_temp_controlled() ? 1 : 0;
        mIsHazmat = handling.is_hazmat() ? 1 : 0;
        mReopened = handling.is_reopened() ? 1 : 0;
        mIsOSD = handling.is_osd() ? 1 : 0;
        mIsUnknownShipper = handling.is_unknown_snipper() ? 1 : 0;
        mIsScreened = handling.screening_status();
        mOverage = handling.is_overage() ? 1 : 0;
        mShortage = handling.is_shortage() ? 1 : 0;
        mLeftBehind = handling.is_left_behind() ? 1 : 0;
    }

    @NonNull
    @Override
    public String task_id() {
        return mId;
    }

    @Override
    public int action_id() {
        return mActionId;
    }

    @Nullable
    @Override
    public String name() {
        return mName;
    }

    @NonNull
    @Override
    public Date date() {
        return DateUtils.getDateFromString(mDate);
    }

    @Nullable
    @Override
    public String owner() {
        return mOwner;
    }

    @Nullable
    @Override
    public String users() {
        return mUsers;
    }

    @Nullable
    @Override
    public String status() {
        return mStatus;
    }

    @Nullable
    @Override
    public String entity_type() {
        return mEntityType;
    }

    @Nullable
    @Override
    public String reference() {
        return mReference;
    }

    @Nullable
    @Override
    public String carrier_namber() {
        return mCarrierNumber;
    }

    @Nullable
    @Override
    public String mawb() {
        return mMawb;
    }

    @Nullable
    @Override
    public String hawb() {
        return mHawb;
    }

    @Nullable
    @Override
    public String origin() {
        return mOrigin;
    }

    @Nullable
    @Override
    public String destination() {
        return mDestination;
    }

    @Override
    public int total_pieces() {
        return mTotalPieces;
    }

    @Override
    public float total_weight() {
        return mTotalWeight;
    }

    @Nullable
    @Override
    public String weight_uom() {
        return mWeightUom;
    }

    @NonNull
    @Override
    public Date start_date() {
        return DateUtils.getDateFromString(mStartDate);
    }

    @NonNull
    @Override
    public Date end_date() {
        return DateUtils.getDateFromString(mEndDate);
    }

    public enum Status {

        NotCompleted(NOT_COMPLETED),
        NotStarted(NOT_STARTED),
        InProgress(IN_PROGRESS),
        Completed(COMPLETED),
        Canceled(CANCELED),
        NotAssigned(NOT_ASSIGNED),
        All(ALL),
        Undefined(UNDEFINED);

        private String mStatus;

        Status(String status) {
            mStatus = status;
        }

        @Override
        public String toString() {
            return mStatus;
        }
    }

    public static final class Marshal extends TaskMarshal<Marshal> {

        public Marshal() {
            super(DATE_ADAPTER, DATE_ADAPTER, DATE_ADAPTER);
        }

        public Marshal(TaskModel taskModel) {
            super(taskModel, DATE_ADAPTER, DATE_ADAPTER, DATE_ADAPTER);
        }

        @Override
        public Marshal _id(long /*ignored*/_id) {
            return this;
        }
    }
}
