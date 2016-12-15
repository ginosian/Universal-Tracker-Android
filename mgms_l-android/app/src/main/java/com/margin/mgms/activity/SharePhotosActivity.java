package com.margin.mgms.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.margin.camera.models.Photo;
import com.margin.mgms.R;
import com.margin.mgms.adapter.PhotoNoteAdapter;
import com.margin.mgms.mvp.share_photos.SharePhotosContract;
import com.margin.mgms.mvp.share_photos.SharePhotosPresenter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created on June 14, 2016.
 *
 * @author Marta.Ginosyan
 */
public class SharePhotosActivity extends AppCompatActivity implements SharePhotosContract.View {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_share_photos)
    RecyclerView mRecycler;
    @Bind(R.id.til_to)
    TextInputLayout mToTil;
    @Bind(R.id.til_subject)
    TextInputLayout mSubjectTil;
    @Bind(R.id.til_message)
    TextInputLayout mMessageTil;
    @Bind(R.id.to)
    EditText mToEditText;
    @Bind(R.id.subject)
    EditText mSubjectEditText;
    @Bind(R.id.message)
    EditText mMessageEditText;
    @BindDrawable(R.drawable.clear_button_icon)
    Drawable mClearDrawable;
    @BindColor(R.color.white)
    int mTintColor;
    @BindString(R.string.title_picture_of_conditions_for)
    String mPictureOfConditionFor;
    @BindString(R.string.placeholder_two_strings)
    String mTwoStringPlaceHolder;
    @BindString(R.string.title_cannot_be_empty)
    String mCannotBeEmpty;
    @BindString(R.string.message_sending)
    String mSending;
    @BindString(R.string.error_invalid_email)
    String mInvalidEmail;
    private String mReferenceNumber;
    private MenuItem mSendMenuItem;
    private SharePhotosPresenter mPresenter;
    private ProgressDialog mProgressDialog;

    private PhotoNoteAdapter mAdapter;

    public static void launch(Context context, @NonNull String referenceNumber,
                              @NonNull ArrayList<Photo> photos, @NonNull String gateway) {
        Intent intent = new Intent(context, SharePhotosActivity.class);
        intent.putExtra(SharePhotosContract.KEY_REFERENCE_NUMBER, referenceNumber);
        intent.putParcelableArrayListExtra(SharePhotosContract.KEY_PHOTOS, photos);
        intent.putExtra(SharePhotosContract.KEY_GATEWAY, gateway);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_photos);
        ButterKnife.bind(this);
        mReferenceNumber = getIntent().getStringExtra(SharePhotosContract.KEY_REFERENCE_NUMBER);
        mPresenter = new SharePhotosPresenter(this, getIntent());
        mPresenter.start();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSendMenuItem = menu.findItem(R.id.menu_item_send);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPresenter.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBackPressed();
    }

    @Override
    public void setupRecyclerView(ArrayList<Photo> photos, String referenceNumber) {
        mAdapter = new PhotoNoteAdapter(photos, true, mPresenter, null);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void setupToolbar(String referenceNumber) {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.title_share_photos);
                actionBar.setSubtitle(referenceNumber);
                mClearDrawable.setTint(mTintColor);
                mToolbar.setNavigationIcon(mClearDrawable);
                mToolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        }
    }

    @Override
    public void setupHeaderView() {
        if (null != mTwoStringPlaceHolder && null != mPictureOfConditionFor
                && null != mReferenceNumber) {
            mSubjectEditText.setText(String.format(mTwoStringPlaceHolder,
                    mPictureOfConditionFor, mReferenceNumber));
        }
    }

    @Override
    public void showToInputError(boolean show) {
        if (show) mToTil.setError(mCannotBeEmpty);
        else mToTil.setError(null);
    }

    @Override
    public void showToInvalidError(String email, boolean show) {
        if (show) mToTil.setError(String.format(mInvalidEmail, email));
        else mToTil.setError(null);
    }

    @Override
    public void showSubjectInputError(boolean show) {
        if (show) mSubjectTil.setError(mCannotBeEmpty);
        else mSubjectTil.setError(null);
    }

    @Override
    public void promptDiscardDialog() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.title_discard_email))
                .setPositiveButton(getString(R.string.title_discard), (arg0, arg1) -> finish())
                .setNegativeButton(getString(R.string.title_cancel), (dialog, which) -> {
                })
                .create()
                .show();
    }

    @Override
    public ArrayList<Photo> getSelectedPhotos() {
        return mAdapter != null ? mAdapter.getCurrentPhotos() : null;
    }

    @Override
    public void destroy() {
        finish();
    }

    @Override
    public Observable<TextViewTextChangeEvent> getOnToTextViewChangeObserver() {
        return RxTextView.textChangeEvents(mToEditText);
    }

    @Override
    public Observable<TextViewTextChangeEvent> getOnSubjectTextViewChangeObserver() {
        return RxTextView.textChangeEvents(mSubjectEditText);
    }

    @Override
    public Observable<TextViewTextChangeEvent> getOnMessageTextViewChangeObserver() {
        return RxTextView.textChangeEvents(mMessageEditText);
    }

    @Override
    public String getToTextViewText() {
        return mToEditText.getText().toString();
    }

    @Override
    public String getSubjectTextViewText() {
        return mSubjectEditText.getText().toString();
    }

    @Override
    public String getMessageTextViewText() {
        return mMessageEditText.getText().toString();
    }

    @Override
    public void removePhotoItem(int position) {
        if (mAdapter != null) mAdapter.remove(position);
    }

    @Override
    public void enableSendMenuItem(boolean enable) {
        mSendMenuItem.setEnabled(enable);
    }

    @Override
    public void showToast(String message, int length) {
        Toast.makeText(this, message, length).show();
    }

    @Override
    public void showSendingProgress(boolean show) {
        if (show) mProgressDialog = ProgressDialog.show(this, null, mSending, true);
        else if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
