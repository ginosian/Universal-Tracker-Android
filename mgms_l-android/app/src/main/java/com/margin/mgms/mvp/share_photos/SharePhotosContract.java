package com.margin.mgms.mvp.share_photos;

import android.view.MenuItem;

import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.margin.camera.models.Photo;
import com.margin.mgms.listener.OnClearButtonClickListener;
import com.margin.mgms.listener.OnEmailSentListener;

import java.util.ArrayList;

import retrofit2.Call;
import rx.Observable;

/**
 * Created on Jun 22, 2016.
 *
 * @author Marta.Ginosyan
 */

public interface SharePhotosContract {

    /**
     * Unique tag used to identify the reference number used in the SharePhotosActivity.
     */
    String KEY_REFERENCE_NUMBER = "key_reference_number";

    /**
     * Unique tag used to identify the photos used in the SharePhotosActivity.
     */
    String KEY_PHOTOS = "key_photos";

    /**
     * Unique tag used to identify the gateway used in the SharePhotosActivity.
     */
    String KEY_GATEWAY = "key_gateway";


    interface View {

        void setupRecyclerView(ArrayList<Photo> photos, String referenceNumber);

        void setupToolbar(String referenceNumber);

        void setupHeaderView();

        void showToInputError(boolean show);

        void showToInvalidError(String email, boolean show);

        void showSubjectInputError(boolean show);

        void promptDiscardDialog();

        ArrayList<Photo> getSelectedPhotos();

        /**
         * Destroys the view (i.e. finish activity)
         */
        void destroy();

        /**
         * Returns {@link Observable} that subscribes on "To" TextView changes
         */
        Observable<TextViewTextChangeEvent> getOnToTextViewChangeObserver();

        /**
         * Returns {@link Observable} that subscribes on "Subject" TextView changes
         */
        Observable<TextViewTextChangeEvent> getOnSubjectTextViewChangeObserver();

        /**
         * Returns {@link Observable} that subscribes on "Message" TextView changes
         */
        Observable<TextViewTextChangeEvent> getOnMessageTextViewChangeObserver();

        /**
         * Returns text from "To" TextView
         */
        String getToTextViewText();

        /**
         * Returns text from "To" TextView
         */
        String getSubjectTextViewText();

        /**
         * Returns text from "To" TextView
         */
        String getMessageTextViewText();

        /**
         * Removes photo item from the list with selected position
         */
        void removePhotoItem(int position);

        void enableSendMenuItem(boolean enable);

        /**
         * Show a {@link android.widget.Toast} with a specified {@param message} for a
         * predetermined {@param length}.
         */
        void showToast(String message, int length);

        /**
         * Close the {@link android.app.Activity}.
         */
        void finish();

        void showSendingProgress(boolean show);
    }


    interface Presenter extends OnEmailSentListener, OnClearButtonClickListener {

        /**
         * Perform actions to initialize the Presenter.
         */
        void start();

        /**
         * Perform actions to clean up the Presenter.
         */
        void finish();

        /**
         * Saves the call object in order to release resources when needed.
         */
        void addCall(Call call);

        /**
         * Removes the {@code call} from the list.
         */
        void removeCall(Call call);

        /**
         * Cancels all {@link Call}s.
         */
        void cancelCalls();

        boolean onOptionsItemSelected(MenuItem item);

        /**
         * Performs an action when back button pressed
         */
        void onBackPressed();
    }


    interface Model {

        /**
         * Send am email for a {@param reference} to the {@param toEmail} from {@param fromEmail}
         * with the {@param subject}, {@param emailBody}, and {@param photos}.
         */
        void sendEmail(String reference, String[] toEmail, String fromEmail, String subject,
                       String emailBody, ArrayList<Photo> photos, String gateway,
                       OnEmailSentListener listener);
    }
}
