package com.margin.mgms.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;

import com.margin.camera.activities.AnnotationPhotoGalleryActivity;
import com.margin.camera.models.Photo;
import com.margin.mgms.R;
import com.margin.mgms.database.DatabaseConnector;
import com.margin.mgms.util.PrefsUtils;
import com.margin.mgms.util.RxUtils;

import java.util.ArrayList;
import java.util.Collection;

import static com.margin.mgms.activity.ConditionReportActivity.KEY_REFERENCE;

/**
 * Created on Jul 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public class MgmsPhotoGalleryActivity extends AnnotationPhotoGalleryActivity {

    private static final String KEY_SHARE_ENABLED = "key_share_enabled";
    private static final String KEY_DELETE_ENABLED = "key_delete_enabled";
    private String mReferenceNumber;
    private boolean mIsShareEnabled;
    private boolean mIsDeleteEnabled;

    public static void start(Context context, Class<?> activityClass, Collection<Photo> photos,
                             int currentPosition, @NonNull String referenceNumber,
                             boolean isShareEnabled, boolean isDeleteEnabled) {
        Bundle extras = new Bundle();
        extras.putString(KEY_REFERENCE, referenceNumber);
        extras.putBoolean(KEY_SHARE_ENABLED, isShareEnabled);
        extras.putBoolean(KEY_DELETE_ENABLED, isDeleteEnabled);
        launch(context, activityClass, photos, currentPosition, extras);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReferenceNumber = getIntent().getStringExtra(KEY_REFERENCE);
        mIsShareEnabled = getIntent().getBooleanExtra(KEY_SHARE_ENABLED, true);
        mIsDeleteEnabled = getIntent().getBooleanExtra(KEY_DELETE_ENABLED, true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_share).setVisible(mIsShareEnabled);
        menu.findItem(R.id.action_delete).setVisible(mIsDeleteEnabled);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClosedPressed() {
        super.onClosedPressed();
        //TODO: implement
    }

    @Override
    public void onDeletePressed(Photo photo, int position) {
        super.onDeletePressed(photo, position);
        if (photo != null && !TextUtils.isEmpty(photo.photo_id())) {
            DatabaseConnector.getInstance().deletePhoto(photo.photo_id())
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(aVoid -> {
                    });
        }
    }

    @Override
    public void onUndoPressed(Photo photo, int position) {
        super.onUndoPressed(photo, position);
        DatabaseConnector.getInstance().savePhoto(photo)
                .compose(RxUtils.applyIOtoMainThreadSchedulers())
                .subscribe(aVoid -> {
                });
    }

    @Override
    public void onSharePressed(Photo photo) {
        super.onSharePressed(photo);
        ArrayList<Photo> photos = new ArrayList<>(1);
        photos.add(photo);
        SharePhotosActivity.launch(this, mReferenceNumber, photos, PrefsUtils.getDefaultGateway());
    }
}
