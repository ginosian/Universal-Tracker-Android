package com.margin.mgms.mvp.photo_capture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.margin.camera.activities.PhotoActivity;
import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Photo;
import com.margin.components.utils.ConnectivityUtils;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.R;
import com.margin.mgms.database.DatabaseConnector;
import com.margin.mgms.fragment.HouseBillFragment;
import com.margin.mgms.fragment.MasterBillFragment;
import com.margin.mgms.listener.AirWaybillConnector;
import com.margin.mgms.misc.Config;
import com.margin.mgms.misc.EntityType;
import com.margin.mgms.model.APIError;
import com.margin.mgms.model.BarcodeModel;
import com.margin.mgms.model.HawbDetails;
import com.margin.mgms.model.HouseBill;
import com.margin.mgms.model.MasterBill;
import com.margin.mgms.model.MawbDetails;
import com.margin.mgms.model.PhotoUploadResponse;
import com.margin.mgms.model.Task;
import com.margin.mgms.mvp.task_manager.TaskManagerContract;
import com.margin.mgms.rest.StrongLoopApi;
import com.margin.mgms.util.DateUtils;
import com.margin.mgms.util.LogUtils;
import com.margin.mgms.util.PrefsUtils;
import com.margin.mgms.util.RxUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import rx.Subscription;

/**
 * Created on May 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoCapturePresenter implements PhotoCaptureContract.Presenter {

    public static final int CAMERA_REQUEST_CODE = 333;
    private final String DATE = DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()).toString();
    private final String PATH = Config.PATH_IMAGES + File.separator + "MGMS_" + DATE;
    private List<Subscription> mSubscriptions = new ArrayList<>();
    private PhotoCaptureContract.View mView;
    private PhotoCaptureContract.Model mModel;
    private List<Call> mCallList = new ArrayList<>();
    private ArrayList<AnnotationType> mHawbAnnotationType;
    private ArrayList<AnnotationType> mMawbAnnotationType;
    private boolean mWasBarcodeEditTextVisible;
    private Task mTask;

    private String mTwoStringPlaceholder;
    private String mInProgressByString;
    private String mCompletedByString;
    private String mPerformingOperationString;
    private String mGateway;
    private String mUser;
    private boolean mStatusWasChanged;

    public PhotoCapturePresenter(PhotoCaptureContract.View view, @Nullable Intent intent) {
        Context mContext = LipstickApplication.getAppComponent().getAppContext();
        this.mView = view;
        this.mModel = new PhotoCaptureModel(this);
        if (null != intent) {
            mGateway = intent.getStringExtra(PhotoCaptureContract.KEY_GATEWAY);
        }
        if (null == mGateway) mGateway = PrefsUtils.getDefaultGateway();
        mUser = PrefsUtils.getUsername();
        mTwoStringPlaceholder = mContext.getString(R.string.placeholder_two_strings);
        mInProgressByString = mContext.getString(R.string.title_in_progress_by);
        mCompletedByString = mContext.getString(R.string.title_completed_by);
        mPerformingOperationString = mContext.getString(R.string.message_performing_operation);
    }

    @Override
    public void onCreate() {
        if (null != mView) {
            mView.setupToolbar();
            mView.showTasksFragment();
        }
        if (null != mModel && !TextUtils.isEmpty(mGateway)) {
            mModel.getAnnotationTypes(StrongLoopApi.ENTITY_TYPE_HAWB, mGateway);
            mModel.getAnnotationTypes(StrongLoopApi.ENTITY_TYPE_MAWB, mGateway);
        }
    }

    @Override
    public void onDestroy() {
        for (Subscription subscription : mSubscriptions) subscription.unsubscribe();
        cancelCalls();
        mView = null;
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
    public void onAnnotationResponseError(APIError apiError, int responseCode) {
        LogUtils.e("Error retrieving annotations: " + apiError.getMessage()
                + ", response code : " + responseCode);
    }

    @Override
    public void onAnnotationResponseSuccess(ArrayList<AnnotationType> annotationTypes,
                                            @EntityType String entityType) {
        LogUtils.i("Annotation types " + entityType + " received successfully");
        switch (entityType) {
            case StrongLoopApi.ENTITY_TYPE_HAWB:
                mHawbAnnotationType = annotationTypes;
                break;
            case StrongLoopApi.ENTITY_TYPE_MAWB:
                mMawbAnnotationType = annotationTypes;
                break;
        }
    }

    @Override
    public void onAnnotationResponseFailure(Throwable t) {
        LogUtils.e("Failure retrieving annotations: " + t.getMessage());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onTaskItemClicked(Task task) {
        mTask = task;
        boolean isHawbEmpty = TextUtils.isEmpty(task.hawb());
        boolean isMawbEmpty = TextUtils.isEmpty(task.mawb());

        if (null != mView && !(isHawbEmpty && isMawbEmpty)) {
            mView.showAddButton(false);
            if (!isHawbEmpty) {
                mView.showHawbDetailsFragment(task.getSpecialHandling(), task.reference(),
                        task.hawb(), PrefsUtils.getDefaultGateway());
            } else {
                mView.showMawbDetailsFragment(task.getSpecialHandling(), task.getLocations(),
                        task.weight_uom(), task.reference(), task.carrier_namber(),
                        task.mawb(), PrefsUtils.getDefaultGateway());
            }
            mView.showDetailsMenuGroup(true);
            mView.showTaskManagementMenuGroup(false);
            if (mView.isBarcodeEditTextVisible()) {
                showBarcode(false);
                mWasBarcodeEditTextVisible = true;
            }
            if (task.getStatus() != Task.Status.Completed) {
                mView.showFab(true);
                mView.showCompleteButton(true);
                mView.showShareButton(false);
            } else {
                mView.showCompleteButton(false);
                mView.showShareButton(true);
            }
            mView.showStatusSpinnerLayout(true);

            if (task.getStatus() == Task.Status.NotStarted
                    || task.getStatus() == Task.Status.NotAssigned) {
                mModel.setTaskProgress(PrefsUtils.getUsername(), task.task_id(), true,
                        PrefsUtils.getDefaultGateway());
            } else updateStatusSpinnerLayout();
        } else {
            // TODO: Item that was clicked does not have a house bill or master bill number
            // Prompt an error:
            // Unintended behaviour - list item does not open
        }
    }

    @Override
    public void onTaskStatusSubmitSuccess(boolean opened) {
        if (mModel != null) {
            if (!opened) {// task was completed
                DatabaseConnector.getInstance().updateTaskStatus(mTask.task_id(),
                        Task.Status.Completed).subscribe(aVoid1 -> {
                    DatabaseConnector.getInstance().getTask(TaskManagerContract.CARGO_PHOTO_CAPTURE,
                            mTask.reference()).compose(RxUtils.applyIOtoMainThreadSchedulers())
                            .subscribe(task -> {
                                mTask = task;
                                mStatusWasChanged = true;
                                LogUtils.i("Task status submitted");
                                if (null != mView) {
                                    mView.refreshPhotos();
                                    mView.showCompleteButton(false);
                                    mView.showShareButton(true);
                                    updateStatusSpinnerLayout();
                                    mView.showFab(false);
                                }
                            });
                });
            } else {// task was set to in progress
                DatabaseConnector.getInstance().updateTaskStatus(mTask.task_id(),
                        Task.Status.InProgress).subscribe(aVoid1 -> {
                    DatabaseConnector.getInstance()
                            .updateTaskOwner(mTask.task_id(), PrefsUtils.getUsername())
                            .subscribe(aVoid -> {
                                DatabaseConnector.getInstance().getTask(
                                        TaskManagerContract.CARGO_PHOTO_CAPTURE, mTask.reference())
                                        .compose(RxUtils.applyIOtoMainThreadSchedulers())
                                        .subscribe(task -> {
                                            mTask = task;
                                            mStatusWasChanged = true;
                                            updateStatusSpinnerLayout();
                                        });
                            });
                });
            }
        }
    }

    public void onTaskStatusSubmitError(Throwable t) {
        if (null != mView) mView.showProgress(false, null);
        LogUtils.i("Task status submission failed: " + t.getMessage());
    }

    @Override
    public void onCompleteButtonClicked() {
        if (null != mView && !isTaskCompleted()) {
            Context context = LipstickApplication.getAppComponent().getAppContext();
            if (ConnectivityUtils.isNetworkAvailable(context)) {
                if (null != mModel) {
                    mView.showProgress(true, mPerformingOperationString);
                    DatabaseConnector.getInstance().getPhotosByTaskId(mTask.task_id())
                            .subscribe(photos -> {
                                //check whether all photos have isSend = true
                                List<Photo> notUploadedPhotos = null;
                                for (Photo photo : photos) {
                                    if (!photo.is_sent()) {
                                        if (notUploadedPhotos == null) {
                                            notUploadedPhotos = new ArrayList<>();
                                        }
                                        notUploadedPhotos.add(photo);
                                    }
                                }
                                if (notUploadedPhotos != null && !notUploadedPhotos.isEmpty()) {
                                    //upload all not uploaded photos
                                    for (Photo photo : notUploadedPhotos) mModel.uploadPhoto(photo);
                                } else {
                                    //if all photos are send we can use uploadPhotos API method
                                    mModel.uploadPhotos(PrefsUtils.getUsername(), mGateway,
                                            mTask.reference(), StrongLoopApi.REASON_PROOF_OF_CONDITION,
                                            photos, true);
                                }
                            });
                }
            } else mView.showToast(context.getString(R.string.message_no_internet));
        }
    }

    @Override
    public void onBackPressed() {
        if (mView != null) {
            if (mView.isDetailsFragmentVisible()) {
                mView.hideDetailsFragment();
                mView.showActionBarTitle(false);
                mView.showStatusSpinnerLayout(false);
                mView.showFab(false);
                mView.showAddButton(true);
                mView.showDetailsMenuGroup(false);
                if (mStatusWasChanged) {
                    mStatusWasChanged = false;
                    mView.onTaskChanged(mTask);
                }
                if (mWasBarcodeEditTextVisible) {
                    mWasBarcodeEditTextVisible = false;
                    mView.showSpinner(false);
                    showBarcode(true);
                } else mView.showTaskManagementMenuGroup(true);
            } else {
                if (mView.isBarcodeEditTextVisible()) {
                    mView.updateBarcodeEditText(null);
                    showSpinner(true);
                    showBarcode(false);
                } else mView.finish();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu() {
        if (mView != null) {
            mView.showTaskManagementMenuGroup(true);
            mView.showDetailsMenuGroup(false);
        }
    }

    @Override
    public void onSearchButtonClicked() {
        if (mView != null) {
            showSpinner(false);
            showBarcode(true);
        }
    }

    private void showBarcode(boolean show) {
        if (mView != null) {
            mView.showBarcodeEditText(show);
            mView.setBarcodeEditTextFocus(show);
            mView.showKeyboard(show);
        }
    }

    private void showSpinner(boolean show) {
        if (mView != null) {
            mView.showSpinner(show);
            mView.showTaskManagementMenuGroup(show);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onCameraFabClick(FragmentManager fm) {

        Fragment fragment = fm.findFragmentByTag(PhotoCaptureContract.FRAGMENT_DETAILS);
        if (null != fragment && (fragment instanceof AirWaybillConnector) && null != mView) {

            AirWaybillConnector dataProvider = (AirWaybillConnector) fragment;
            Map<String, String> properties = dataProvider.providesProperties();
            int entityId = new Random().nextInt();  // TODO: THIS SHOULD BE HANDLED BETTER

            List<AnnotationType> annotationTypes = null;
            if (fragment instanceof HouseBillFragment) annotationTypes = mHawbAnnotationType;
            else if (fragment instanceof MasterBillFragment) annotationTypes = mMawbAnnotationType;
            else LogUtils.e("Unknown fragment type");

            if (null != annotationTypes) {
                mView.showCamera(CAMERA_REQUEST_CODE, entityId, PATH, properties, annotationTypes);
            } else {
                // TODO: Prompt an error to the user
                // Unintended behaviour - camera does not show
            }
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (null != data && data.hasExtra(PhotoActivity.PHOTO)) {
                Photo photo = data.getExtras().getParcelable(PhotoActivity.PHOTO);
                if (null != photo) {
                    photo.setLocationCode(PrefsUtils.getDefaultGateway());
                    photo.setUsername(PrefsUtils.getUsername());
                    photo.setTaskId(mTask.task_id());
                    photo.setCreateDate(DateUtils.formatDate(new Date()));
                    photo.setReference(mTask.reference());
                    mModel.savePhotoToDb(photo).subscribe(aVoid -> onUploadPhoto(photo));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onUploadPhoto(Photo photo) {
        mModel.uploadPhoto(photo);
    }

    @Override
    public void photoUploadSuccess(Photo photo) {
        LogUtils.i("Photo" + photo.photo_id() + "successfully uploaded");
        mModel.updatePhotoStatusInDb(photo.photo_id(), true).subscribe(aVoid -> {
            if (null != mView) mView.addPhotoToChildFragment(photo);
        });
    }

    @Override
    public void photoUploadFailure(Photo photo, Throwable e) {
        LogUtils.e("Error uploading photo " + photo.photo_id() + ": " + e.getMessage());
    }

    @Override
    public void addSubscription(Subscription subscription) {
        mSubscriptions.add(subscription);
    }

    @Override
    public void onPhotoUploadsResponseError(APIError apiError, int responseCode) {
        LogUtils.e("Error uploading photos: " + apiError.getMessage()
                + ", response code : " + responseCode);
        if (null != mView) {
            mView.showProgress(false, null);
            mView.showPhotosUploadFailedError(apiError.getMessage());
        }
    }

    @Override
    public void onPhotoUploadsResponseSuccess(PhotoUploadResponse response) {
        LogUtils.i("Photos were upload successfully");
        //TODO: Get the task from server, do not change manually.
        DatabaseConnector.getInstance().deletePhotos(mTask.task_id()).subscribe(aVoid -> {
            mModel.setTaskProgress(PrefsUtils.getUsername(), mTask.task_id(), false,
                    PrefsUtils.getDefaultGateway());
        });
    }

    @Override
    public void onPhotoUploadsResponseFailure(Throwable t) {
        LogUtils.e("Failure uploading photos: " + t.getMessage());
        if (null != mView) {
            mView.showProgress(false, null);
            mView.showPhotosUploadFailedError(t.getMessage());
        }
    }

    @Override
    public void onBarcodeParserResponseSuccess(BarcodeModel barcodeModel) {
        String number = null;
        boolean isHawb = true;
        if (!TextUtils.isEmpty(barcodeModel.getHawbNum())) number = barcodeModel.getHawbNum();
        else if (!TextUtils.isEmpty(barcodeModel.getMawbNum())) {
            number = barcodeModel.getMawbNum();
            isHawb = false;
        }
        if (!TextUtils.isEmpty(number)) {
            final boolean finalIsHawb = isHawb;
            mModel.getTaskFromDb(TaskManagerContract.CARGO_PHOTO_CAPTURE, number)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(task -> {
                        if (task == null) {
                            if (!finalIsHawb) getMawbDetailsFromServer(barcodeModel);
                            else getHawbDetailsFromServer(barcodeModel);
                        } else onTaskItemClicked(task);
                    });
        }
    }

    @Override
    public void onBarcodeParserResponseError(APIError apiError, int responseCode) {
        LogUtils.e("Error parse barcode: " + apiError.getMessage()
                + ", response code : " + responseCode);
    }

    @Override
    public void onBarcodeParserResponseFailure(Throwable t) {
        LogUtils.e("Error parse barcode: " + t.getMessage());
    }

    @Override
    public void onBarcodeDoneListener() {
        String text = mView.getSearchBarText();
        if (!TextUtils.isEmpty(text)) onBarcodeReceived(text);
    }

    @Override
    public void onCreateTaskButtonClicked(String barcode, String origin, String destination,
                                          boolean isHawb) {
        if (isHawb) createHawbTask(barcode, origin, destination);
        else createMawbTask(barcode.substring(0, 3), barcode.substring(4), origin, destination);
    }

    @Override
    public boolean onCreateShipmentButtonClicked(String origin, String destination, String carrier,
                                                 String shipment, String pieces, boolean isHawb) {
        if (TextUtils.isEmpty(origin)) {
            if (mView != null) mView.setErrorToOrigin(true);
            return false;
        } else if (TextUtils.isEmpty(destination)) {
            if (mView != null) mView.setErrorToDestination(true);
            return false;
        } else if (TextUtils.isEmpty(pieces)) {
            if (mView != null) mView.setErrorToPieces(true);
            return false;
        } else {
            try {
                createShipment(origin, destination, carrier, shipment, Integer.parseInt(pieces));
                return true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                if (mView != null) mView.setErrorToPieces(true);
                return false;
            }
        }
    }

    @Override
    public void onAddButtonClicked() {
        if (mView != null && !mView.isBarcodeEditTextVisible()) onSearchButtonClicked();
    }

    @Override
    public void onShareButtonClicked() {
        if (mView != null) mView.showSharePhotosScreen();
    }

    // Private Methods

    private String getOwner() {
        return null != mTask ? mTask.owner() : "";
    }

    private boolean isTaskCompleted() {
        return mTask.getStatus() == Task.Status.Completed;
    }

    private void updateStatusSpinnerLayout() {
        boolean isTaskCompleted = isTaskCompleted();
        mView.setStatusSpinnerIcon(isTaskCompleted ?
                R.drawable.complete_icon : R.drawable.progress_icon);
        mView.setStatusSpinnerTitle(String.format(mTwoStringPlaceholder,
                isTaskCompleted ? mCompletedByString :
                        mInProgressByString, getOwner()));
        mView.setStatusSpinnerDate(isTaskCompleted ? mTask.getFormattedEndDate()
                : mTask.getFormattedStartDate());
        mView.setStatusSpinnerClickable(!isTaskCompleted);
    }

    @Override
    public void onBarcodeReceived(String barcode) {
        if (mView != null) {
            mView.updateBarcodeEditText(barcode);
            mModel.parseBarcode(barcode, PrefsUtils.getDefaultGateway());
        }
    }

    /**
     * Make a request to get the house bill details from the server.
     */
    private void getHawbDetailsFromServer(BarcodeModel barcodeModel) {
        if (null != mModel) {
            mSubscriptions.add(mModel.requestHouseBill(barcodeModel.getHawbNum(), mGateway)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(houseBill -> onHouseBillReceived(houseBill, barcodeModel)));
        }
    }

    /**
     * Make a request to get the master bill details from the server.
     */
    private void getMawbDetailsFromServer(BarcodeModel barcodeModel) {
        if (null != mModel) {
            mSubscriptions.add(mModel.requestMasterBill(barcodeModel.getCarrierNum(),
                    barcodeModel.getMawbNum(), mGateway)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(masterBill -> onMasterBillReceived(masterBill, barcodeModel)));
        }
    }

    /**
     * Master bill details have been received from the server.
     */
    private void onMasterBillReceived(MasterBill masterBill, BarcodeModel barcodeModel) {
        if (mView != null) {
            if (masterBill.getMawbDetails() == null
                    || masterBill.getMawbDetails().getAirbillNum() == null) {
                mView.showCreateShipmentDialog(barcodeModel, false);
            } else {
                MawbDetails details = masterBill.getMawbDetails();
                String barcodeResult = details.getCarrier() + "-" + details.getAirbillNum();
                String reference = details.getOrigin() + "-" + barcodeResult + "-"
                        + details.getDestination();
                mView.showCreateTaskDialog(reference, barcodeResult, details.getOrigin(),
                        details.getDestination(), false);
            }
        }
    }

    /**
     * House bill details have been received from the server.
     */
    private void onHouseBillReceived(HouseBill houseBill, BarcodeModel barcodeModel) {
        if (mView != null) {
            if (houseBill.getHawbDetails() == null
                    || houseBill.getHawbDetails().getAirbillNum() == null) {
                mView.showCreateShipmentDialog(barcodeModel, true);
            } else {
                HawbDetails details = houseBill.getHawbDetails();
                String reference = details.getOrigin() + "-" + details.getAirbillNum() + "-"
                        + details.getDestination();
                mView.showCreateTaskDialog(reference, details.getAirbillNum(), details.getOrigin(),
                        details.getDestination(), true);
            }
        }
    }

    private void createHawbTask(String number, String origin, String destination) {
        if (mModel != null && !TextUtils.isEmpty(number)
                && !TextUtils.isEmpty(origin) && !TextUtils.isEmpty(destination)) {
            mSubscriptions.add(mModel.createHawbTask(TaskManagerContract.CARGO_PHOTO_CAPTURE, mUser,
                    number, origin, destination, mGateway)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(response -> {
                        if (response != null && response.getData() != null) {
                            onTaskCreated(response.getData().getTaskId());
                        }
                    }));
        }
    }

    private void createMawbTask(String carrier, String number, String origin, String destination) {
        if (mModel != null && !TextUtils.isEmpty(carrier) && !TextUtils.isEmpty(number)
                && !TextUtils.isEmpty(origin) && !TextUtils.isEmpty(destination)) {
            mSubscriptions.add(mModel.createMawbTask(TaskManagerContract.CARGO_PHOTO_CAPTURE,
                    carrier, mUser, number, origin, destination, mGateway)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(response -> {
                        if (response != null && response.getData() != null) {
                            onTaskCreated(response.getData().getTaskId());
                        }
                    }));
        }
    }

    private void createShipment(String origin, String destination, String carrier,
                                String shipment, int pieces) {
        if (mModel != null && !TextUtils.isEmpty(origin) && !TextUtils.isEmpty(destination)
                && !TextUtils.isEmpty(shipment)) {
            mSubscriptions.add(mModel.createShipment(origin, destination, carrier, shipment,
                    pieces, mUser, TaskManagerContract.CARGO_PHOTO_CAPTURE, mGateway)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(response -> {
                        if (response != null && !TextUtils.isEmpty(response.getTaskId())) {
                            onTaskCreated(response.getTaskId());
                        }
                    }));
        }
    }

    private void onTaskCreated(String taskId) {
        if (mModel != null && taskId != null) {
            mSubscriptions.add(mModel.getTask(taskId, mGateway)
                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(task -> {
                        if (task != null) {
                            mModel.saveTask(task)
                                    .compose(RxUtils.applyIOtoMainThreadSchedulers())
                                    .subscribe(aVoid -> onTaskItemClicked(task));
                        }
                    }));
        }
    }
}
