package com.margin.mgms.mvp.photo_capture;

import com.google.gson.reflect.TypeToken;
import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Photo;
import com.margin.components.utils.GATrackerUtils;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.database.DatabaseConnector;
import com.margin.mgms.misc.EntityType;
import com.margin.mgms.model.APIError;
import com.margin.mgms.model.AnnotationTypesRequest;
import com.margin.mgms.model.BarcodeModel;
import com.margin.mgms.model.CreateHawbTaskRequest;
import com.margin.mgms.model.CreateHawbTaskResponse;
import com.margin.mgms.model.CreateMawbTaskRequest;
import com.margin.mgms.model.CreateMawbTaskResponse;
import com.margin.mgms.model.CreateShipmentRequest;
import com.margin.mgms.model.CreateShipmentResponse;
import com.margin.mgms.model.HouseBill;
import com.margin.mgms.model.HouseBillRequest;
import com.margin.mgms.model.MasterBill;
import com.margin.mgms.model.MasterBillRequest;
import com.margin.mgms.model.ParseBarcodeRequest;
import com.margin.mgms.model.PhotoUploadResponse;
import com.margin.mgms.model.Task;
import com.margin.mgms.model.TaskPostRequest;
import com.margin.mgms.model.TaskPostResponse;
import com.margin.mgms.model.UploadPhotosRequest;
import com.margin.mgms.rest.StrongLoopApi;
import com.margin.mgms.util.Constants;
import com.margin.mgms.util.GsonUtil;
import com.margin.mgms.util.LogUtils;
import com.margin.mgms.util.RxUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;

import static com.margin.mgms.rest.StrongLoopApi.API_VERSION;
import static com.margin.mgms.rest.StrongLoopApi.MGMSL;
import static com.margin.mgms.rest.StrongLoopApi.PHOTO_CAPTURE;
import static com.margin.mgms.rest.StrongLoopApi.UTILITY;

/**
 * Created on May 27, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoCaptureModel implements PhotoCaptureContract.Model {

    private final PhotoCaptureContract.Presenter mPresenter;
    private final StrongLoopApi mOrdinaryStrongLoopApi, mReactiveStrongLoopApi;
    private final Converter<ResponseBody, APIError> mErrorConverter;

    public PhotoCaptureModel(PhotoCaptureContract.Presenter presenter) {
        this.mPresenter = presenter;
        this.mOrdinaryStrongLoopApi = LipstickApplication.getAppComponent().getOrdinaryStrongLoopApi();
        this.mReactiveStrongLoopApi = LipstickApplication.getAppComponent().getReactiveStrongLoopApi();
        this.mErrorConverter = LipstickApplication.getAppComponent().getErrorConverter();
    }

    @Override
    public void getAnnotationTypes(final @EntityType String entityType, String gateway) {
        AnnotationTypesRequest request = new AnnotationTypesRequest(entityType, gateway);
        Call<ArrayList<AnnotationType>> call = mOrdinaryStrongLoopApi
                .getAnnotationTypes(entityType, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + MGMSL + "/annotationTypes",
                GsonUtil.getGson().toJson(request), 0);
        mPresenter.addCall(call);

        call.enqueue(new Callback<ArrayList<AnnotationType>>() {
            @Override
            public void onResponse(Call<ArrayList<AnnotationType>> call,
                                   Response<ArrayList<AnnotationType>> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        APIError error = mErrorConverter.convert(response.errorBody());
                        int responseCode = error.getStatus();
                        mPresenter.onAnnotationResponseError(error, responseCode);
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + MGMSL + "/annotationTypes",
                                error.toString(), 0);
                    } catch (IOException e) {
                        mPresenter.onAnnotationResponseFailure(e.getCause());
                        e.printStackTrace();
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), e);
                    }
                } else if (null != response) {
                    if (response.isSuccessful()) {
                        ArrayList<AnnotationType> annotationTypes = response.body();
                        mPresenter.onAnnotationResponseSuccess(annotationTypes, entityType);
                        Type listOfAnnotations = new TypeToken<List<AnnotationType>>() {
                        }.getType();
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + MGMSL + "/annotationTypes",
                                GsonUtil.getGson().toJson(annotationTypes, listOfAnnotations), 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AnnotationType>> call, Throwable t) {
                mPresenter.onAnnotationResponseFailure(t);
                GATrackerUtils.trackException(
                        LipstickApplication.getAppComponent().getAppContext(), t);
            }
        });
    }

    @Override
    public void setTaskProgress(String username, String taskId, boolean status, String gateway) {
        TaskPostRequest request = TaskPostRequest.newTask(username, taskId, status, gateway);
        Call<TaskPostResponse> call = mOrdinaryStrongLoopApi.postTask(request);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + PHOTO_CAPTURE + "/task",
                GsonUtil.getGson().toJson(request), 0);
        mPresenter.addCall(call);
        call.enqueue(new Callback<TaskPostResponse>() {
            @Override
            public void onResponse(Call<TaskPostResponse> call, Response<TaskPostResponse> response) {
                mPresenter.removeCall(call);
                mPresenter.onTaskStatusSubmitSuccess(status);
                GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                        Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                        API_VERSION + PHOTO_CAPTURE + "/task",
                        GsonUtil.getGson().toJson(response.body()), 0);
            }

            @Override
            public void onFailure(Call<TaskPostResponse> call, Throwable t) {
                mPresenter.removeCall(call);
                mPresenter.onTaskStatusSubmitError(t);
                GATrackerUtils.trackException(
                        LipstickApplication.getAppComponent().getAppContext(), t);
            }
        });
    }

    @Override
    public void uploadPhoto(Photo photo) {

        File imageFile = new File(photo.image_path());
        if (!imageFile.exists()) return;

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + PHOTO_CAPTURE + "/upload/" + photo.photo_id(),
                "image/jpeg", 0);
        mPresenter.addSubscription(mReactiveStrongLoopApi.uploadPhoto(photo.photo_id(), requestBody)
                .compose(RxUtils.applyIOtoMainThreadSchedulers())
                .subscribe(new Subscriber<PhotoUploadResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mPresenter.photoUploadFailure(photo, e);
                        LogUtils.e("Error uploading photos: " + e.getMessage());
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), e);
                    }

                    @Override
                    public void onNext(PhotoUploadResponse photoUploadResponse) {
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + PHOTO_CAPTURE + "/upload/" + photo.photo_id(),
                                GsonUtil.getGson().toJson(photoUploadResponse), 0);
                        if (null != photoUploadResponse) {
                            mPresenter.photoUploadSuccess(photo);
                            LogUtils.i("Successfully uploaded photos: " + photoUploadResponse);
                        } else {
                            LogUtils.e("Received null response while uploading photo");
                        }
                    }
                }));
    }

    @Override
    public Observable<Void> savePhotoToDb(Photo photo) {
        return DatabaseConnector.getInstance().savePhoto(photo);
    }

    @Override
    public Observable<Void> updatePhotoStatusInDb(String photoId, boolean isSend) {
        return DatabaseConnector.getInstance().updatePhotoStatus(photoId, isSend);
    }

    @Override
    public Observable<Void> savePhotosToDb(List<Photo> photos) {
        return DatabaseConnector.getInstance().savePhotos(photos);
    }

    @Override
    public Observable<List<Photo>> getPhotosFromDb(String taskId) {
        return DatabaseConnector.getInstance().getPhotosByTaskId(taskId);
    }

    @Override
    public Observable<Task> getTaskFromDb(int actionId, String reference) {
        return DatabaseConnector.getInstance().getTask(actionId, reference);
    }

    @Override
    public void uploadPhotos(String user, String gateway, String reference, String reason,
                             List<Photo> photos, boolean byName) {
        UploadPhotosRequest request = new UploadPhotosRequest();
        request.user = user;
        request.gateway = gateway;
        request.reference = reference;
        request.reason = reason;
        request.photos = photos;
        request.byname = byName;
        Call<PhotoUploadResponse> call = mOrdinaryStrongLoopApi.uploadPhotos(request);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + PHOTO_CAPTURE + "/uploadPhotos",
                GsonUtil.getGson().toJson(request), 0);
        mPresenter.addCall(call);

        call.enqueue(new Callback<PhotoUploadResponse>() {
            @Override
            public void onResponse(Call<PhotoUploadResponse> call,
                                   Response<PhotoUploadResponse> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        APIError error = mErrorConverter.convert(response.errorBody());
                        int responseCode = error.getStatus();
                        mPresenter.onPhotoUploadsResponseError(error, responseCode);
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + PHOTO_CAPTURE + "/uploadPhotos",
                                error.toString(), 0);
                    } catch (IOException e) {
                        mPresenter.onPhotoUploadsResponseFailure(e.getCause());
                        e.printStackTrace();
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), e);
                    }
                } else if (null != response) {
                    if (response.isSuccessful()) {
                        PhotoUploadResponse response1 = response.body();
                        mPresenter.onPhotoUploadsResponseSuccess(response1);
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + PHOTO_CAPTURE + "/uploadPhotos",
                                GsonUtil.getGson().toJson(response1), 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<PhotoUploadResponse> call, Throwable t) {
                mPresenter.onPhotoUploadsResponseFailure(t);
                GATrackerUtils.trackException(
                        LipstickApplication.getAppComponent().getAppContext(), t);
            }
        });
    }

    @Override
    public void parseBarcode(String barcode, String gateway) {

        ParseBarcodeRequest request = new ParseBarcodeRequest(barcode, gateway);
        Call<BarcodeModel> call = mOrdinaryStrongLoopApi.barcodeParser(barcode, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + UTILITY + "/barcodeParser",
                GsonUtil.getGson().toJson(request), 0);
        mPresenter.addCall(call);

        call.enqueue(new Callback<BarcodeModel>() {
            @Override
            public void onResponse(Call<BarcodeModel> call, Response<BarcodeModel> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        APIError error = mErrorConverter.convert(response.errorBody());
                        int responseCode = error.getStatus();
                        mPresenter.onBarcodeParserResponseError(error, responseCode);
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + UTILITY + "/barcodeParser",
                                error.toString(), 0);
                    } catch (IOException e) {
                        mPresenter.onBarcodeParserResponseFailure(e.getCause());
                        e.printStackTrace();
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), e);
                    }
                } else if (response != null && response.isSuccessful()) {
                    mPresenter.onBarcodeParserResponseSuccess(response.body());
                    GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                            Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                            API_VERSION + UTILITY + "/barcodeParser",
                            GsonUtil.getGson().toJson(response.body()), 0);
                }
            }

            @Override
            public void onFailure(Call<BarcodeModel> call, Throwable t) {
                mPresenter.onBarcodeParserResponseFailure(t);
                GATrackerUtils.trackException(
                        LipstickApplication.getAppComponent().getAppContext(), t);
            }
        });
    }

    @Override
    public Observable<HouseBill> requestHouseBill(String houseBillNumber, String gateway) {
        HouseBillRequest request = new HouseBillRequest(houseBillNumber, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + UTILITY + "/hawbCompleteData",
                GsonUtil.getGson().toJson(request), 0);
        return mReactiveStrongLoopApi.getHouseBill(houseBillNumber, gateway)
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
    public Observable<MasterBill> requestMasterBill(String carrier, String masterBillNumber,
                                                    String gateway) {
        MasterBillRequest request = new MasterBillRequest(carrier, masterBillNumber, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + UTILITY + "/mawbCompleteData",
                GsonUtil.getGson().toJson(request), 0);
        return mReactiveStrongLoopApi.getMasterBill(carrier, masterBillNumber, gateway)
                .doOnNext(masterBill ->
                        GATrackerUtils.trackEvent(
                                LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + UTILITY + "/mawbCompleteData",
                                GsonUtil.getGson().toJson(masterBill), 0))
                .doOnError(throwable ->
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), throwable));
    }

    @Override
    public Observable<CreateHawbTaskResponse> createHawbTask(int action, String user, String hawb,
                                                             String origin, String destination,
                                                             String gateway) {
        CreateHawbTaskRequest request = new CreateHawbTaskRequest(action, user, hawb, origin,
                destination, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + PHOTO_CAPTURE + "/hawbCreateTask",
                GsonUtil.getGson().toJson(request), 0);
        return mReactiveStrongLoopApi.hawbCreateTask(request)
                .doOnNext(createHawbTaskResponse -> {
                    GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                            Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                            API_VERSION + PHOTO_CAPTURE + "/hawbCreateTask",
                            GsonUtil.getGson().toJson(createHawbTaskResponse), 0);
                })
                .doOnError(throwable ->
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), throwable));
    }

    @Override
    public Observable<CreateMawbTaskResponse> createMawbTask(int action, String carrier, String user,
                                                             String mawb, String origin,
                                                             String destination, String gateway) {
        CreateMawbTaskRequest request = new CreateMawbTaskRequest(action, carrier, user, mawb,
                origin, destination, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + PHOTO_CAPTURE + "/mawbCreateTask",
                GsonUtil.getGson().toJson(request), 0);
        return mReactiveStrongLoopApi.mawbCreateTask(request)
                .doOnNext(createMawbTaskResponse -> {
                    GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                            Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                            API_VERSION + PHOTO_CAPTURE + "/mawbCreateTask",
                            GsonUtil.getGson().toJson(createMawbTaskResponse), 0);
                })
                .doOnError(throwable ->
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), throwable));
    }

    @Override
    public Observable<CreateShipmentResponse> createShipment(String origin, String destination,
                                                             String carrier, String shipment,
                                                             int pieces, String user, int action,
                                                             String gateway) {
        CreateShipmentRequest request = new CreateShipmentRequest(origin, destination, carrier,
                shipment, pieces, user, action, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + MGMSL + "/skeletonShipmentTask",
                GsonUtil.getGson().toJson(request), 0);
        return mReactiveStrongLoopApi.createShipment(request)
                .doOnNext(createShipmentResponse ->
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + MGMSL + "/skeletonShipmentTask",
                                GsonUtil.getGson().toJson(createShipmentResponse), 0))
                .doOnError(throwable ->
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), throwable));
    }

    @Override
    public Observable<Task> getTask(String taskId, String gateway) {
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + MGMSL + "/tasks/" + taskId,
                "{\"gateway\":\"" + gateway + "\"}", 0);
        return mReactiveStrongLoopApi.getTask(taskId, gateway)
                .doOnNext(task ->
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + MGMSL + "/tasks/" + taskId,
                                GsonUtil.getGson().toJson(task), 0))
                .doOnError(throwable ->
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), throwable));
    }

    @Override
    public Observable<Void> saveTask(Task task) {
        return DatabaseConnector.getInstance().saveTask(task);
    }
}
