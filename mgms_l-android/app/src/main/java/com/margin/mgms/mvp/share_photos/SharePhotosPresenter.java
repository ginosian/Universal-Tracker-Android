package com.margin.mgms.mvp.share_photos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.margin.camera.models.Photo;
import com.margin.mgms.R;
import com.margin.mgms.activity.SharePhotosActivity;
import com.margin.mgms.model.APIError;
import com.margin.mgms.util.LogUtils;
import com.margin.mgms.util.PrefsUtils;
import com.margin.mgms.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created on Jun 22, 2016.
 *
 * @author Marta.Ginosyan
 */

public class SharePhotosPresenter implements SharePhotosContract.Presenter {

    private static final int DURATION_DEBOUNCE = 100;

    private List<Call> mCallList = new ArrayList<>();
    private SharePhotosContract.View mView;
    private SharePhotosContract.Model mModel;
    private Intent mIntent;

    private ArrayList<Photo> mPhotos;
    private String mReferenceNumber;
    private String mGateWay;
    /**
     * If true - a data has been changed by the user.
     */
    private boolean mHasDataChanged;
    /**
     * If true - the data is valid.
     */
    private boolean mIsDataValid;

    private CompositeSubscription mCompositeSubscription;
    private BehaviorRelay<Void> mEditTextRelay;

    public SharePhotosPresenter(SharePhotosContract.View view, Intent intent) {
        this.mView = view;
        this.mModel = new SharePhotosModel(this);
        this.mIntent = intent;
    }

    @Override
    public void start() {
        getValuesFromIntent(mIntent);
        initSubscriptions();
        if (null != mView) {
            mView.setupRecyclerView(mPhotos, mReferenceNumber);
            mView.setupToolbar(mReferenceNumber);
            mView.setupHeaderView();
        }
    }

    @Override
    public void finish() {
        cancelCalls();
        if (mCompositeSubscription != null) mCompositeSubscription.unsubscribe();
        mView = null;
        mModel = null;
    }

    @Override
    public void addCall(Call call) {
        mCallList.add(call);
    }

    @Override
    public void removeCall(Call call) {
        if (mCallList.contains(call)) mCallList.remove(call);
    }

    @Override
    public void cancelCalls() {
        for (Call call : mCallList) call.cancel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (null != mView) {
            switch (item.getItemId()) {
                case R.id.menu_item_send:
                    if (mIsDataValid) {
                        emailPhotos(mView.getToTextViewText(),
                                mView.getSubjectTextViewText(),
                                mView.getMessageTextViewText(),
                                mView.getSelectedPhotos());
                    } else if (mEditTextRelay != null) mEditTextRelay.call(null);
                    return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mView != null) {
            if (mHasDataChanged) mView.promptDiscardDialog();
            else mView.destroy();
        }
    }

    @Override
    public void onEmailSentSuccess() {
        if (mView != null) {
            mView.showSendingProgress(false);
            mView.showToast("Email was sent successfully!", Toast.LENGTH_SHORT);
            mView.finish();
        }
    }

    @Override
    public void onEmailSentError(APIError error) {
        if (mView != null && error != null) {
            mView.showSendingProgress(false);
            mView.showToast(error.getMessage()
                    + " (" + error.getStatus() + "): "
                    + error.getError(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onEmailSentFailure(Throwable throwable) {
        mView.showSendingProgress(false);
        LogUtils.i("Email failed to send: " + throwable.getMessage());
    }

    private void emailPhotos(String toEmail, String subject, String emailBody,
                             ArrayList<Photo> photos) {
        if (null != mModel && mView != null) {
            String[] emails = StringUtils.splitWithComma(toEmail);
            for (String email : emails) {
                if (!StringUtils.isValidEmail(email)) {
                    if (mView != null) mView.showToInvalidError(email, true);
                    return;
                }
            }
            mView.showSendingProgress(true);
            mModel.sendEmail(mReferenceNumber, emails, PrefsUtils.getEmail(),
                    subject, emailBody, photos, mGateWay, this);
        }
    }

    private void getValuesFromIntent(@NonNull Intent intent) {
        if (!(intent.hasExtra(SharePhotosContract.KEY_REFERENCE_NUMBER)
                && intent.hasExtra(SharePhotosContract.KEY_PHOTOS)
                && intent.hasExtra(SharePhotosContract.KEY_GATEWAY))) {
            throwNoSufficientInputProvidedError();
        }
        mReferenceNumber = intent.getStringExtra(SharePhotosContract.KEY_REFERENCE_NUMBER);
        mPhotos = intent.getParcelableArrayListExtra(SharePhotosContract.KEY_PHOTOS);
        mGateWay = intent.getStringExtra(SharePhotosContract.KEY_GATEWAY);
    }

    /**
     * Throws {@link IllegalArgumentException} with a meaningful message.
     */
    private void throwNoSufficientInputProvidedError() {
        throw new IllegalArgumentException(SharePhotosActivity.class.getCanonicalName() +
                " should be launched from static launch() method");
    }

    private void initSubscriptions() {
        if (mView != null) {
            mCompositeSubscription = new CompositeSubscription();
            mEditTextRelay = BehaviorRelay.create();
            mCompositeSubscription.add(mEditTextRelay.subscribe(aVoid -> {
                if (mView != null) {
                    if (TextUtils.isEmpty(mView.getToTextViewText())) {
                        mView.showToInputError(true);
                    }
                    if (TextUtils.isEmpty(mView.getSubjectTextViewText())) {
                        mView.showSubjectInputError(true);
                    }
                }
            }));

            Observable<TextViewTextChangeEvent> to =
                    mView.getOnToTextViewChangeObserver().skip(1).share();
            Observable<TextViewTextChangeEvent> subject =
                    mView.getOnSubjectTextViewChangeObserver().share();
            Observable<TextViewTextChangeEvent> message =
                    mView.getOnMessageTextViewChangeObserver().skip(1);

            mCompositeSubscription.add(to.subscribe(textViewTextChangeEvent -> {
                if (mView != null) mView.showToInputError(false);
            }));
            mCompositeSubscription.add(subject.subscribe(textViewTextChangeEvent -> {
                if (mView != null) mView.showSubjectInputError(false);
            }));

            mCompositeSubscription.add(Observable.merge(message, subject, to)
                    .subscribe(textViewTextChangeEvent -> mHasDataChanged = true));

            // Combining `to` and `subject` editText change events
            // If they both are not empty - the data is valid
            mCompositeSubscription.add(Observable.combineLatest(
                    to.debounce(DURATION_DEBOUNCE, TimeUnit.MILLISECONDS)
                            .map(textChangeEvent -> !TextUtils.isEmpty(textChangeEvent.text())),
                    subject.debounce(DURATION_DEBOUNCE, TimeUnit.MILLISECONDS)
                            .map(textChangeEvent -> !TextUtils.isEmpty(textChangeEvent.text())),
                    (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setDataValid));
        }
    }

    /**
     * Sets that data is valid.
     */
    private void setDataValid(boolean valid) {
        mIsDataValid = valid;
    }

    @Override
    public void onClearButtonClicked(int position) {
        if (mView != null) {
            mView.removePhotoItem(position);
            if (mPhotos.isEmpty()) mView.enableSendMenuItem(false);
        }
    }
}
