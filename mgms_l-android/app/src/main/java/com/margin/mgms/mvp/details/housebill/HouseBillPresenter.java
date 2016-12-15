package com.margin.mgms.mvp.details.housebill;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.margin.camera.models.Photo;
import com.margin.mgms.R;
import com.margin.mgms.fragment.ContactCardFragment;
import com.margin.mgms.fragment.DamagesCardFragment;
import com.margin.mgms.fragment.DimensionsCardFragment;
import com.margin.mgms.fragment.HouseBillFragment;
import com.margin.mgms.fragment.LocationsCardFragment;
import com.margin.mgms.fragment.PhotoCardFragment;
import com.margin.mgms.fragment.SummaryCardFragment;
import com.margin.mgms.model.Dimensions;
import com.margin.mgms.model.HawbDetails;
import com.margin.mgms.model.HouseBill;
import com.margin.mgms.model.ShipmentLocation;
import com.margin.mgms.model.SpecialHandling;
import com.margin.mgms.util.LogUtils;
import com.margin.mgms.util.PrefsUtils;
import com.margin.mgms.util.RxUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on May 25, 2016.
 *
 * @author Marta.Ginosyan
 */
public class HouseBillPresenter implements HouseBillContract.Presenter {

    private HouseBillContract.View mView;
    private HouseBillContract.Model mModel;
    private HouseBill mHouseBill;
    private SpecialHandling mSpecialHandling;
    private String mReferenceNumber;
    private String mHouseBillTitle;
    private String mGateway;
    private ArrayList<Photo> mPhotos;

    private List<Subscription> mSubscriptions = new ArrayList<>();
    private boolean mIsPhotoCardUpdatePending;

    public HouseBillPresenter(HouseBillContract.View view, @NonNull Bundle bundle) {
        this.mView = view;
        mModel = new HouseBillModel();
        retrieveDataFromBundle(bundle);
    }

    @Override
    public void start() {
        // Get data from the server
        getHawbDetailsFromServer();
        fetchPhotos();

        if (null != mView) {
            mView.showProgress(true, R.string.message_loading);
            mView.showActionBarTitle(true);
            mView.showSpinner(false);
            mView.setActionBarTitle(mReferenceNumber);
        }
    }

    @Override
    @SuppressWarnings("Convert2streamapi")
    public void finish() {
        for (Subscription subscription : mSubscriptions) subscription.unsubscribe();
        mModel = null;
        mView = null;
    }

    @Override
    public void resume() {
        if (mIsPhotoCardUpdatePending) {
            mIsPhotoCardUpdatePending = false;
            updateCards();
        }
    }

    @Override
    public void onHouseBillReceived(HouseBill houseBill) {
        mHouseBill = houseBill;
        mHouseBill.getHawbDetails().setSpecialHandling(mSpecialHandling);
        if (null != mPhotos) mHouseBill.setPhotos(mPhotos);
        updateCards();
    }

    @Override
    public synchronized void onPhotosReceived(ArrayList<Photo> photos) {
        if (null == mHouseBill) {
            mModel.getPhotos(mReferenceNumber)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(photosFromDb -> {
                        for (Photo photo : photosFromDb) {
                            photos.add(0, photo);
                        }
                        mPhotos = photos;
                        if (mView != null) mView.hideProgressDialog();
                    });
            return;
        }
        if (!photos.equals(mHouseBill.getPhotos())) {
            mModel.getPhotos(mReferenceNumber)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(photosFromDb -> {
                        for (Photo photo : photosFromDb) {
                            photos.add(0, photo);
                        }
                        mHouseBill.setPhotos(photos);
                        updateCards();
                        if (mView != null) mView.hideProgressDialog();
                    });
        } else if (mView != null) mView.hideProgressDialog();
    }

    /**
     * Update cards using the current house bill information.
     */
    private void updateCards() {
        if (mHouseBill != null && mView != null) {
            for (HouseBillContract.CardType card : HouseBillContract.CARD_ORDER) {
                switch (card) {
                    case SUMMARY_CARD:
                        try {
                            SummaryCardFragment summary = SummaryCardFragment.newFragment(
                                    mHouseBill.getHawbDetails().getSpecialHandling(),
                                    mHouseBill.getSender(),
                                    mHouseBill.getReceiver(),
                                    mHouseBill.getTotalPieces(),
                                    mHouseBill.getHawbDetails().getWeight(),
                                    mHouseBill.getHawbDetails().getWeightUnit(),
                                    mHouseBill.getHawbDetails().getReferenceNum());

                            mView.addOrReplaceCard(summary, SummaryCardFragment.TAG);
                        } catch (NumberFormatException ex) {
                            LogUtils.e(ex.getMessage());
                        }
                        break;

                    case PHOTO_CARD:
                        PhotoCardFragment photos = PhotoCardFragment.newFragment(
                                mHouseBill.getPhotos(), mReferenceNumber);
                        mView.addOrReplaceCard(photos, PhotoCardFragment.TAG);
                        break;

                    case DAMAGES_CARD:
                        DamagesCardFragment damages =
                                DamagesCardFragment.newFragmentFromPhotos(
                                        mHouseBill.getPhotos(), mReferenceNumber);
                        mView.addOrReplaceCard(damages, DamagesCardFragment.TAG);
                        break;

                    case SENDER_CARD:
                        String senderTitle = mView.getString(R.string.title_sender);
                        ContactCardFragment sender = ContactCardFragment.newFragment(
                                mHouseBill.getSender(), senderTitle);
                        mView.addOrReplaceCard(sender, senderTitle);
                        break;

                    case RECEIVER_CARD:
                        String receiverTitle = mView.getString(R.string.title_receiver);
                        ContactCardFragment receiver = ContactCardFragment.newFragment(
                                mHouseBill.getReceiver(), receiverTitle);
                        mView.addOrReplaceCard(receiver, receiverTitle);
                        break;

                    case LOCATION_CARD:
                        LocationsCardFragment locations =
                                LocationsCardFragment.newFragment(mHouseBill.getLocations(),
                                        mHouseBill.getTotalPieces());
                        mView.addOrReplaceCard(locations, LocationsCardFragment.TAG);
                        break;

                    case DIMENSIONS_CARD:
                        DimensionsCardFragment dimensions =
                                DimensionsCardFragment.newFragment(mHouseBill.getDimensions(),
                                        mHouseBill.getTotalPieces());
                        mView.addOrReplaceCard(dimensions, DimensionsCardFragment.TAG);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    /**
     * Retains all the data that has been passed to {@link
     * com.margin.mgms.fragment.HouseBillFragment HouseBillFragment}.
     *
     * @param bundle The bundle that has been passed to {@link
     *               com.margin.mgms.fragment.HouseBillFragment HouseBillFragment} when
     *               instantiating with {@link
     *               com.margin.mgms.fragment.HouseBillFragment#newInstance(SpecialHandling,
     *               String, String, String)}
     *               String, String)} HouseBillFragment.newInstance()}.
     */
    private void retrieveDataFromBundle(Bundle bundle) {
        if (bundle.containsKey(HouseBillContract.KEY_SPECIAL_HANDLING)) {
            mSpecialHandling = bundle.getParcelable(HouseBillContract.KEY_SPECIAL_HANDLING);
        } else throwNoSufficientInputProvidedError();
        if (bundle.containsKey(HouseBillContract.KEY_REFERENCE)) {
            mReferenceNumber = bundle.getString(HouseBillContract.KEY_REFERENCE);
        } else throwNoSufficientInputProvidedError();
        if (bundle.containsKey(HouseBillContract.KEY_HAWB)) {
            mHouseBillTitle = bundle.getString(HouseBillContract.KEY_HAWB);
        } else throwNoSufficientInputProvidedError();
        mGateway = bundle.getString(HouseBillContract.KEY_GATEWAY, PrefsUtils.getDefaultGateway());
    }

    /**
     * Throws {@link IllegalArgumentException} with a meaningful message.
     */
    private void throwNoSufficientInputProvidedError() {
        throw new IllegalArgumentException("Construct instance of " +
                HouseBillFragment.class.getSimpleName() + " using newInstance() providing " +
                "necessary input data");
    }

    /**
     * Make a request to get the house bill details from the server.
     */
    private void getHawbDetailsFromServer() {
        if (null != mModel) {
            mSubscriptions.add(mModel.requestHouseBill(mHouseBillTitle, mGateway)
                    .subscribeOn(Schedulers.io())
                    .map(houseBill -> {
                        String pieces = houseBill.getHawbDetails().getPieces();
                        for (ShipmentLocation loc : houseBill.getLocations()) {
                            loc.setNumPieces(pieces);
                        }

                        try {
                            int piecesInt = Integer.valueOf(pieces);
                            for (Dimensions dimension : houseBill.getDimensions()) {
                                dimension.setNumPieces(piecesInt);
                            }
                        } catch (NumberFormatException e) {
                            LogUtils.e(e.getMessage());
                        }

                        return houseBill;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HouseBill>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) mView.showProgress(false, 0);
                            LogUtils.e("Error retrieving Hawb: " + e.getMessage());
                        }

                        @Override
                        public void onNext(HouseBill houseBill) {
                            if (null != mView) mView.showProgress(false, 0);
                            onHouseBillReceived(houseBill);
                        }
                    }));
        }
    }

    @Override
    public Map<String, String> provideProperties() {
        HawbDetails details = mHouseBill.getHawbDetails();

        Map<String, String> properties = new HashMap<>();
        {
            properties.put(HouseBillContract.INFO_AIRBILL_NUMBER, details.getAirbillNum());
            properties.put(HouseBillContract.INFO_REFERENCE_NUMBER, details.getReferenceNum());
            properties.put(HouseBillContract.INFO_ORIGIN, details.getOrigin());
            properties.put(HouseBillContract.INFO_DESTINATION, details.getDestination());
            properties.put(HouseBillContract.INFO_PIECES, details.getPieces());
            properties.put(HouseBillContract.INFO_WEIGHT, details.getWeight()
                    + " " + details.getWeightUnit());
            properties.put(HouseBillContract.INFO_DESCRIPTION, details.getDescription());
        }
        return properties;
    }

    @Override
    public void addPhotoAndUpdate(Photo photo) {
        mHouseBill.addPhoto(photo);
        updateCards();
    }

    @Override
    public void fetchPhotos() {
        if (null != mModel) {
            mSubscriptions.add(mModel.getPhotos(mReferenceNumber, mGateway)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(new Subscriber<ArrayList<Photo>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.e("Error retrieving photos: " + e.getMessage());
                            if (mView != null) mView.hideProgressDialog();
                        }

                        @Override
                        public void onNext(ArrayList<Photo> photos) {
                            if (null != photos) onPhotosReceived(photos);
                            else if (mView != null) mView.hideProgressDialog();
                        }
                    }));
        }
    }

    @Override
    public void onShareButtonPressed() {
        if (mView != null) {
            mView.showSharePhotosScreen(mReferenceNumber, mHouseBill.getPhotos(), mGateway);
        }
    }
}
