package com.margin.mgms.mvp.details.mawb;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.margin.camera.models.Photo;
import com.margin.mgms.R;
import com.margin.mgms.fragment.DamagesCardFragment;
import com.margin.mgms.fragment.LocationsCardFragment;
import com.margin.mgms.fragment.MasterBillFragment;
import com.margin.mgms.fragment.PhotoCardFragment;
import com.margin.mgms.fragment.SummaryCardFragment;
import com.margin.mgms.fragment.UldCardFragment;
import com.margin.mgms.model.MasterBill;
import com.margin.mgms.model.MawbDetails;
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
import rx.schedulers.Schedulers;

/**
 * Created on May 25, 2016.
 *
 * @author Marta.Ginosyan
 */
public class MasterBillPresenter implements MasterBillContract.Presenter {

    private MasterBillContract.View mView;
    private MasterBillContract.Model mModel;
    private MasterBill mMasterBill;
    private List<Subscription> mSubscriptions = new ArrayList<>();
    private boolean mIsPhotoCardUpdatePending;
    private String mReferenceNumber;
    private SpecialHandling mSpecialHandling;
    private ArrayList<ShipmentLocation> mShipmentLocations;
    /**
     * Used temporary, until mMasterBill is received.
     */
    private ArrayList<Photo> mTempPhotos;

    private String mWeightUnit;
    private String mCarrier;
    private String mMawb;
    private String mGateway;

    public MasterBillPresenter(MasterBillContract.View view, @NonNull Bundle bundle) {
        this.mView = view;
        this.mModel = new MasterBillModel();
        retrieveDataFromBundle(bundle);
    }

    @Override
    public void start() {
        // Get the master bill details and photos
        getMawbDetailsFromServer();
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
    public void onMasterBillReceived(MasterBill masterBill) {
        mMasterBill = masterBill;
        MawbDetails mawbDetails = mMasterBill.getMawbDetails();
        mawbDetails.setSpecialHandling(mSpecialHandling);
        mawbDetails.setLocations(mShipmentLocations);
        mawbDetails.setWeightUnit(mWeightUnit);

        if (null != mTempPhotos) mMasterBill.setPhotos(mTempPhotos);
        updateCards();
    }

    @Override
    public void onPhotosReceived(ArrayList<Photo> photos) {
        if (null == mMasterBill) {
            mModel.getPhotos(mReferenceNumber)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(photosFromDb -> {
                        for (Photo photo : photosFromDb) {
                            photos.add(0, photo);
                        }
                        mTempPhotos = photos;
                        if (mView != null) mView.hideProgressDialog();
                    });
            return;
        }
        if (!photos.equals(mMasterBill.getPhotos())) {
            mModel.getPhotos(mReferenceNumber)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(photosFromDb -> {
                        for (Photo photo : photosFromDb) {
                            photos.add(0, photo);
                        }
                        mMasterBill.setPhotos(photos);
                        updateCards();
                        if (mView != null) mView.hideProgressDialog();
                    });
        } else if (mView != null) mView.hideProgressDialog();
    }

    /**
     * Update cards using the current master bill information.
     */
    private void updateCards() {
        if (mMasterBill != null && mView != null) {
            for (MasterBillContract.CardType card : MasterBillContract.CARD_ORDER) {
                switch (card) {
                    case SUMMARY_CARD:
                        try {
                            SummaryCardFragment summary = SummaryCardFragment.newFragment(
                                    mMasterBill.getMawbDetails().getSpecialHandling(),
                                    mMasterBill.getTotalPieces(),
                                    mMasterBill.getMawbDetails().getWeight(),
                                    mMasterBill.getMawbDetails().getWeightUnit(),
                                    mMasterBill.getMawbDetails().getReferenceNum());

                            mView.addOrReplaceCard(summary, SummaryCardFragment.TAG);
                        } catch (NumberFormatException ex) {
                            LogUtils.e(ex.getMessage());
                        }
                        break;

                    case PHOTO_CARD:
                        PhotoCardFragment photos = PhotoCardFragment.newFragment(
                                mMasterBill.getPhotos(), mReferenceNumber);
                        mView.addOrReplaceCard(photos, PhotoCardFragment.TAG);
                        break;

                    case DAMAGES_CARD:
                        DamagesCardFragment damages =
                                DamagesCardFragment.newFragmentFromPhotos(
                                        mMasterBill.getPhotos(), mReferenceNumber);
                        mView.addOrReplaceCard(damages, DamagesCardFragment.TAG);
                        break;

                    case LOCATION_CARD:
                        LocationsCardFragment locations =
                                LocationsCardFragment.newFragment(mMasterBill.getMawbDetails()
                                        .getLocations(), mMasterBill.getTotalPieces());
                        mView.addOrReplaceCard(locations, LocationsCardFragment.TAG);
                        break;

                    case ULD:
                        UldCardFragment uld = UldCardFragment.newFragment(mMasterBill.getUldList());
                        mView.addOrReplaceCard(uld, UldCardFragment.TAG);
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Retains all the data that has been passed to {@link
     * com.margin.mgms.fragment.MasterBillFragment HouseBillFragment}.
     *
     * @param bundle The bundle that has been passed to {@link
     *               com.margin.mgms.fragment.MasterBillFragment HouseBillFragment} when
     *               instantiating with {@link
     *               com.margin.mgms.fragment.MasterBillFragment#newInstance(SpecialHandling,
     *               List, String, String, String, String, String)}.
     */
    private void retrieveDataFromBundle(Bundle bundle) {
        if (bundle.containsKey(MasterBillContract.KEY_SPECIAL_HANDLING)) {
            mSpecialHandling = bundle.getParcelable(MasterBillContract.KEY_SPECIAL_HANDLING);
        } else throwNoSufficientInputProvidedError();
        if (bundle.containsKey(MasterBillContract.KEY_REFERENCE)) {
            mReferenceNumber = bundle.getString(MasterBillContract.KEY_REFERENCE);
        } else throwNoSufficientInputProvidedError();
        if (bundle.containsKey(MasterBillContract.KEY_LOCATIONS)) {
            mShipmentLocations = bundle.getParcelableArrayList(MasterBillContract.KEY_LOCATIONS);
        } else throwNoSufficientInputProvidedError();
        if (bundle.containsKey(MasterBillContract.KEY_WEIGHT_UNIT)) {
            mWeightUnit = bundle.getString(MasterBillContract.KEY_WEIGHT_UNIT);
        } else throwNoSufficientInputProvidedError();
        if (bundle.containsKey(MasterBillContract.KEY_CARRIER)) {
            mCarrier = bundle.getString(MasterBillContract.KEY_CARRIER);
        } else throwNoSufficientInputProvidedError();
        if (bundle.containsKey(MasterBillContract.KEY_MAWB)) {
            mMawb = bundle.getString(MasterBillContract.KEY_MAWB);
        } else throwNoSufficientInputProvidedError();
        mGateway = bundle.getString(MasterBillContract.KEY_GATEWAY, PrefsUtils.getDefaultGateway());
    }

    /**
     * Throws {@link IllegalArgumentException} with a meaningful message.
     */
    private void throwNoSufficientInputProvidedError() {
        throw new IllegalArgumentException("Construct instance of " +
                MasterBillFragment.class.getSimpleName() + " using newInstance() providing necessary " +
                "input data");
    }

    /**
     * Make a request to get the master bill details from the server.
     */
    private void getMawbDetailsFromServer() {
        if (null != mModel) {
            mSubscriptions.add(mModel.requestMasterBill(mCarrier, mMawb, mGateway)
                    .subscribeOn(Schedulers.io())
                    .map(masterBill -> {
                        String pieces = masterBill.getMawbDetails().getPieces();
                        for (ShipmentLocation loc : masterBill.getMawbDetails().getLocations()) {
                            loc.setNumPieces(pieces);
                        }
                        return masterBill;
                    })
                    .subscribe(new Subscriber<MasterBill>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) mView.showProgress(false, 0);
                            LogUtils.e("Error retrieving Mawb: " + e.getMessage());
                        }

                        @Override
                        public void onNext(MasterBill mawbCompleteData) {
                            if (null != mView) mView.showProgress(false, 0);
                            onMasterBillReceived(mawbCompleteData);
                        }
                    }));
        }
    }

    @Override
    public Map<String, String> provideProperties() {
        MawbDetails details = mMasterBill.getMawbDetails();
        Map<String, String> properties = new HashMap<>();
        {
            properties.put(MasterBillContract.INFO_AIRBILL_NUMBER, details.getAirbillNum());
            properties.put(MasterBillContract.INFO_REFERENCE_NUMBER, details.getReferenceNum());
            properties.put(MasterBillContract.INFO_ORIGIN, details.getOrigin());
            properties.put(MasterBillContract.INFO_DESTINATION, details.getDestination());
            properties.put(MasterBillContract.INFO_CARRIER, details.getCarrier());
            properties.put(MasterBillContract.INFO_FLIGHT, details.getFlight());
            properties.put(MasterBillContract.INFO_PIECES, details.getPieces());
            properties.put(MasterBillContract.INFO_SLAC, details.getSlac());
            properties.put(MasterBillContract.INFO_WEIGHT, details.getWeight()
                    + " " + details.getWeightUnit());
        }
        return properties;
    }

    @Override
    public void addPhotoAndUpdate(Photo photo) {
        mMasterBill.addPhoto(photo);
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
            mView.showSharePhotosScreen(mReferenceNumber, mMasterBill.getPhotos(), mGateway);
        }
    }
}
