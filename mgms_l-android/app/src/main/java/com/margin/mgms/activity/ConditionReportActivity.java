package com.margin.mgms.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.margin.camera.models.Photo;
import com.margin.mgms.R;
import com.margin.mgms.adapter.PhotoNoteAdapter;
import com.margin.mgms.database.DatabaseConnector;
import com.margin.mgms.listener.OnPhotoClickListener;
import com.margin.mgms.model.Task;
import com.margin.mgms.mvp.task_manager.TaskManagerContract;
import com.margin.mgms.util.PrefsUtils;
import com.margin.mgms.util.RxUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.ButterKnife;

/**
 * Created on June 09, 2016.
 *
 * @author Marta.Ginosyan
 */
public class ConditionReportActivity extends AppCompatActivity implements OnPhotoClickListener {

    public static final String KEY_PHOTOS = "key_photos";
    public static final String KEY_REFERENCE = "key_reference";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_condition_report)
    RecyclerView mDamageReportRecycler;
    @BindDrawable(R.drawable.clear_button_icon_black)
    Drawable mClearDrawable;
    @BindColor(R.color.white)
    int mTintColor;
    private ArrayList<Photo> mPhotos;
    private String mReferenceNumber;

    public static void launch(Context context, ArrayList<Photo> photos, String referenceNumber) {
        Intent intent = new Intent(context, ConditionReportActivity.class);
        intent.putParcelableArrayListExtra(KEY_PHOTOS, photos);
        intent.putExtra(KEY_REFERENCE, referenceNumber);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getIntent().hasExtra(KEY_PHOTOS)) throwNoSufficientInputProvidedError();
        mReferenceNumber = getIntent().getStringExtra(KEY_REFERENCE);
        setContentView(R.layout.activity_condition_report);
        ButterKnife.bind(this);
        mPhotos = getIntent().getParcelableArrayListExtra(KEY_PHOTOS);
        PhotoNoteAdapter adapter = new PhotoNoteAdapter(mPhotos, false, null, this);
        mDamageReportRecycler.setAdapter(adapter);
        setupToolbar();
    }

    public void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.title_condition_report);
                actionBar.setSubtitle(mReferenceNumber);
                mClearDrawable.setTint(mTintColor);
                mToolbar.setNavigationIcon(mClearDrawable);
                mToolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_condition_report, menu);
        DatabaseConnector.getInstance()
                .getTask(TaskManagerContract.CARGO_PHOTO_CAPTURE, mReferenceNumber)
                .compose(RxUtils.applyIOtoMainThreadSchedulers())
                .subscribe(task -> {
                    if (task != null && task.getStatus() != Task.Status.Completed) {
                        menu.findItem(R.id.menu_item_share).setVisible(false);
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                SharePhotosActivity.launch(this, mReferenceNumber, mPhotos,
                        PrefsUtils.getDefaultGateway());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Throws {@link IllegalArgumentException} with a meaningful message.
     */
    private void throwNoSufficientInputProvidedError() {
        throw new IllegalArgumentException(ConditionReportActivity.class.getCanonicalName() +
                " should be launched from static launch() method");
    }

    @Override
    public void onPhotoClicked(int position) {
        DatabaseConnector.getInstance().getTask(TaskManagerContract.CARGO_PHOTO_CAPTURE,
                mReferenceNumber).subscribe(task -> {
            boolean completed = task.getStatus() == Task.Status.Completed;
            MgmsPhotoGalleryActivity.start(this, MgmsPhotoGalleryActivity.class, mPhotos, position,
                    mReferenceNumber, completed, !completed);
        });
    }
}
