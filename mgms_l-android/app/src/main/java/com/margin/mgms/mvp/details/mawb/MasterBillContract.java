package com.margin.mgms.mvp.details.mawb;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.margin.camera.models.Photo;
import com.margin.mgms.model.MasterBill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created on May 25, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface MasterBillContract {

    /* Keys into the Fragment */
    String KEY_SPECIAL_HANDLING = "key_special_handling";
    String KEY_LOCATIONS = "key_locations";
    String KEY_REFERENCE = "key_reference";
    String KEY_WEIGHT_UNIT = "key_weight_unit";
    String KEY_CARRIER = "key_carrier";
    String KEY_MAWB = "key_mawb";
    String KEY_GATEWAY = "key_gateway";

    /* Info used for photo capture */
    String INFO_AIRBILL_NUMBER = "Airbill Number";
    String INFO_REFERENCE_NUMBER = "Reference Number";
    String INFO_ORIGIN = "Origin";
    String INFO_DESTINATION = "Destination";
    String INFO_CARRIER = "Carrier";
    String INFO_FLIGHT = "Flight";
    String INFO_SLAC = "SLAC";
    String INFO_PIECES = "Pieces";
    String INFO_WEIGHT = "Weight";
    /**
     * Order that the cards will be displayed in the Details Fragment.
     */
    CardType[] CARD_ORDER = new CardType[]{
            CardType.PHOTO_CARD,
            CardType.DAMAGES_CARD,
            CardType.SUMMARY_CARD,
            CardType.LOCATION_CARD,
            CardType.ULD,
    };

    /* Different types of cards that are displayed */
    enum CardType {
        SUMMARY_CARD,
        LOCATION_CARD,
        PHOTO_CARD,
        DAMAGES_CARD,
        ULD,
    }

    interface View {

        /**
         * Change the action bar title.
         *
         * @param title Title that the action bar will be changed to
         */
        void setActionBarTitle(String title);

        /**
         * Add a Card Fragment to the list, or if it already exists in the list, update it.
         *
         * @param fragment Fragment to add or replace
         * @param tag      Unique ID to identify the card
         */
        void addOrReplaceCard(Fragment fragment, String tag);

        /**
         * Shows the action bar title
         *
         * @param isShow shows the action bar title if true, hides otherwise
         */
        void showActionBarTitle(boolean isShow);

        /**
         * Shows the spinner
         *
         * @param isShow shows the spinner if true, hides otherwise
         */
        void showSpinner(boolean isShow);

        /**
         * @param show If true - shows indeterminate {@link android.widget.ProgressBar}. False -
         *             hides it.
         */
        void showProgress(boolean show, @StringRes int message);

        /**
         * Shares currently opened task in details fragment
         */
        void onShareButtonPressed();

        /**
         * Shares currently opened task in details fragment
         */
        void showSharePhotosScreen(String referenceNumber, List<Photo> photos, String gateway);

        void hideProgressDialog();
    }

    interface Presenter {

        /**
         * Initialize the Details Fragment by getting the master/house bill details.
         */
        void start();

        /**
         * Clean up any resources before destroying the Details Fragment.
         */
        void finish();

        /**
         * Perform action, when Details Fragment is resumed.
         */
        void resume();

        /**
         * House bill details have been received from the server.
         */
        void onMasterBillReceived(MasterBill masterBill);

        /**
         * {@link List} of {@link Photo}s have been fetched.
         */
        void onPhotosReceived(ArrayList<Photo> photos);

        /**
         * @return Info properties about the master bill to give to the photo capture widget.
         */
        Map<String, String> provideProperties();

        /**
         * Retains and shows {@code photo}.
         */
        void addPhotoAndUpdate(Photo photo);

        /**
         * Fetches a {@link List} of {@link Photo}s.
         */
        void fetchPhotos();

        void onShareButtonPressed();
    }

    interface Model {
        /**
         * Create a request to get house waybill details from the server.
         */
        Observable<MasterBill> requestMasterBill(String carrier, String masterBill, String gateway);

        /**
         * @return A {@link List} of {@link Photo}s.
         */
        Observable<ArrayList<Photo>> getPhotos(String reference, String gateway);

        /**
         * Gets photos from database for selected task
         */
        Observable<List<Photo>> getPhotos(String taskId);
    }
}
