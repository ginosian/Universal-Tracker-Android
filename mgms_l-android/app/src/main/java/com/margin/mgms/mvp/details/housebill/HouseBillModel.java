package com.margin.mgms.mvp.details.housebill;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.margin.camera.models.Photo;
import com.margin.components.utils.GATrackerUtils;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.database.DatabaseConnector;
import com.margin.mgms.model.HouseBill;
import com.margin.mgms.model.HouseBillRequest;
import com.margin.mgms.model.PhotosRequest;
import com.margin.mgms.rest.StrongLoopApi;
import com.margin.mgms.util.ApiUtils;
import com.margin.mgms.util.Constants;
import com.margin.mgms.util.GsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.margin.mgms.rest.StrongLoopApi.API_VERSION;
import static com.margin.mgms.rest.StrongLoopApi.PHOTO_CAPTURE;
import static com.margin.mgms.rest.StrongLoopApi.UTILITY;

/**
 * Created on May 25, 2016.
 *
 * @author Marta.Ginosyan
 */
public class HouseBillModel implements HouseBillContract.Model {

    private StrongLoopApi mStrongLoopApi;

    public HouseBillModel() {
        mStrongLoopApi = LipstickApplication.getAppComponent().getReactiveStrongLoopApi();
    }

    @Override
    public Observable<HouseBill> requestHouseBill(String houseBillNumber, String gateway) {
        HouseBillRequest request = new HouseBillRequest(houseBillNumber, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + UTILITY + "/hawbCompleteData",
                GsonUtil.getGson().toJson(request), 0);
        return mStrongLoopApi.getHouseBill(houseBillNumber, gateway)
                .doOnNext(houseBill ->
                        GATrackerUtils.trackEvent(
                                LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + UTILITY + "/hawbCompleteData",
                                GsonUtil.getGson().toJson(houseBill), 0))
                .doOnError(throwable ->
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), throwable));
    }

    @Override
    public Observable<ArrayList<Photo>> getPhotos(String reference, String gateway) {
        boolean embedded = false;
        PhotosRequest request = new PhotosRequest(reference, embedded, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + PHOTO_CAPTURE + "/photos",
                GsonUtil.getGson().toJson(request), 0);
        return mStrongLoopApi.getPhotos(reference, embedded, gateway)
                .map(photos -> {
                    preparePhotos(photos);
                    return photos;
                })
                .doOnNext(photos -> {
                    Type listOfPhotos = new TypeToken<List<Photo>>() {
                    }.getType();
                    GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                            Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                            API_VERSION + PHOTO_CAPTURE + "/photos",
                            GsonUtil.getGson().toJson(photos, listOfPhotos), 0);
                })
                .doOnError(throwable ->
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), throwable));
    }

    /**
     * Adds endpoint and api version to all photo urls. It must be done
     * before we use photos in the app
     */
    private void preparePhotos(List<Photo> photos) {
        if (photos != null && !photos.isEmpty()) {
            for (Photo photo : photos) {
                if (photo != null) {
                    photo.setIsSend(true);
                    if (!TextUtils.isEmpty(photo.url())) {
                        photo.setUrl(ApiUtils.buildImageUrl(photo.url()));
                    }
                }
            }
        }
    }

    @Override
    public Observable<List<Photo>> getPhotos(String reference) {
        return DatabaseConnector.getInstance().getPhotosByReference(reference);
    }
}
