package com.margin.mgms.mvp.photo_capture;

import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.margin.barcode.listeners.OnBarcodeReceivedListener;
import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Photo;
import com.margin.mgms.fragment.PhotoCaptureTasksFragment;
import com.margin.mgms.listener.OnTaskChangeListener;
import com.margin.mgms.misc.EntityType;
import com.margin.mgms.model.APIError;
import com.margin.mgms.model.BarcodeModel;
import com.margin.mgms.model.CreateHawbTaskResponse;
import com.margin.mgms.model.CreateMawbTaskResponse;
import com.margin.mgms.model.CreateShipmentResponse;
import com.margin.mgms.model.HouseBill;
import com.margin.mgms.model.MasterBill;
import com.margin.mgms.model.PhotoUploadResponse;
import com.margin.mgms.model.ShipmentLocation;
import com.margin.mgms.model.SpecialHandling;
import com.margin.mgms.model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

/**
 * Created on May 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface PhotoCaptureContract {

    /**
     * Unique tag used to identfiy the gateway used in the PhotoCaptureActivity.
     */
    String KEY_GATEWAY = "key_gateway";

    /**
     * Unique TAG used to identify the detail screens
     * ({@link com.margin.mgms.fragment.HouseBillFragment} and
     * {@link com.margin.mgms.fragment.MasterBillFragment}).
     */
    String FRAGMENT_DETAILS = "fragment_details";

    /**
     * Unique TAG used to identify the {@link PhotoCaptureTasksFragment}.
     */
    String FRAGMENT_TASKS = "fragment_tasks";

    interface View extends OnBarcodeReceivedListener, OnTaskChangeListener {

        /**
         * Sets up {@link android.support.v7.widget.Toolbar}.
         */
        void setupToolbar();

        /**
         * Sets title into the {@link android.support.v7.widget.Toolbar}.
         */
        void setActionBarTitle(String title);

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
         * Shows the {@link com.margin.barcode.views.BarcodeEditText}
         *
         * @param isShow shows the editText if true, hides otherwise
         */
        void showBarcodeEditText(boolean isShow);

        /**
         * Instantiates and shows the {@link PhotoCaptureTasksFragment}
         */
        void showTasksFragment();

        /**
         * Shows and hide the task management menu group in toolbar
         */
        void showTaskManagementMenuGroup(boolean show);

        /**
         * Shows and hide the details menu group in toolbar
         */
        void showDetailsMenuGroup(boolean show);

        /**
         * Show the Master Bill details on the screen.
         */
        void showMawbDetailsFragment(SpecialHandling handling, List<ShipmentLocation> locations,
                                     String weightUnit, String reference, String carrier,
                                     String mawbNum, String gateway);

        /**
         * Show the House Bill details on the screen.
         */
        void showHawbDetailsFragment(SpecialHandling handling, String reference,
                                     String hawbNum, String gateway);

        /**
         * Hides Details fragment (i.e. pop it from the fragment back stack)
         */
        void hideDetailsFragment();

        /**
         * Returns whether details fragment visible or not
         */
        boolean isDetailsFragmentVisible();

        /**
         * Close the {@link android.app.Activity}.
         */
        void finish();

        /**
         * Updates the view when task item was clicked
         */
        void onTaskItemClicked(Task task);

        /**
         * Sets the focus to the barcode edit text
         */
        void setBarcodeEditTextFocus(boolean isFocus);

        /**
         * Returns whether barcode editText visible or not
         */
        boolean isBarcodeEditTextVisible();

        /**
         * Shows the soft keyboard
         *
         * @param show shows the keyboard if true, hides otherwise
         */
        void showKeyboard(boolean show);

        /**
         * Controls status spinner's visibility state.
         *
         * @param show If true - sets visibility to {@code View.VISIBLE},
         *             otherwise - {@code View.GONE}.
         */
        void showStatusSpinnerLayout(boolean show);

        /**
         * Sets status spinner's lhs icon to {@code drawable}.
         */
        void setStatusSpinnerIcon(@DrawableRes int drawable);

        /**
         * Sets the {@param title} to the status spinner.
         */
        void setStatusSpinnerTitle(String title);

        /**
         * Sets status spinner's date to {@code text}.
         */
        void setStatusSpinnerDate(String text);

        /**
         * Performs {@link android.view.View#setClickable(boolean) setClickable()} on status
         * spinner.
         *
         * @param clickable If true - sets to true. False otherwise.
         */
        void setStatusSpinnerClickable(boolean clickable);

        /**
         * @param show If true - displays {@link android.app.ProgressDialog ProgressDialog}.
         *             Otherwise - hides currently displayed (if there is some).
         */
        void showProgress(boolean show, @Nullable String message);

        /**
         * Shows the {@link android.widget.Toast} with message text
         */
        void showToast(String message);

        /**
         * Initialize the Annotation Photo Activity.
         *
         * @param requestCode Unique request code that is used when getting the result back
         * @param entityId    Unique ID of the shipment
         * @param path        Path where images will be saved
         * @param properties  Properties that will be shown in the camera
         * @param types       Annotation types that will be available in the camera
         */
        void showCamera(int requestCode, int entityId, String path, Map<String,
                String> properties, Collection<AnnotationType> types);

        /**
         * Adds {@code photo} to child fragment.
         */
        void addPhotoToChildFragment(Photo photo);

        /**
         * @param show If true - sets fab's visibility to {@code View.VISIBLE}. Otherwise sets to
         *             {@code View.GONE}.
         */
        void showFab(boolean show);

        /**
         * Sets text into the {@link com.margin.barcode.views.BarcodeEditText}
         */
        void updateBarcodeEditText(String text);

        /**
         * Shows a {@link android.app.Dialog} to create Task with predefined values
         */
        void showCreateTaskDialog(String reference, String barcode, String origin,
                                  String destination, boolean isHawb);

        /**
         * Shows a {@link android.app.Dialog} to create Shipment with predefined values
         */
        void showCreateShipmentDialog(BarcodeModel barcodeModel, boolean isHawb);

        /**
         * Returns text that was entered into the barcode editText
         */
        String getSearchBarText();

        /**
         * Show error in origin editText
         *
         * @param show if true shows the error false otherwise
         */
        void setErrorToOrigin(boolean show);

        /**
         * Show error in destination editText
         *
         * @param show if true shows the error false otherwise
         */
        void setErrorToDestination(boolean show);

        /**
         * Show error in pieces editText
         *
         * @param show if true shows the error false otherwise
         */
        void setErrorToPieces(boolean show);

        /**
         * Changes the visibility of add floating button
         */
        void showAddButton(boolean show);

        void showCompleteButton(boolean visible);

        void showShareButton(boolean visible);

        /**
         * Shares currently opened task in details fragment
         */
        void showSharePhotosScreen();

        /**
         * Refreshes photos on details screen
         */
        void refreshPhotos();

        void showPhotosUploadFailedError(String error);
    }

    interface Presenter extends OnBarcodeReceivedListener {

        /**
         * Perform actions to initialize the Presenter.
         */
        void onCreate();

        /**
         * Perform actions to clean up the Presenter.
         */
        void onDestroy();

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

        /**
         * Action to perform when error response has been received.
         */
        void onAnnotationResponseError(APIError apiError, int responseCode);

        /**
         * Action to perform when success response has been received.
         */
        void onAnnotationResponseSuccess(ArrayList<AnnotationType> annotationTypes,
                                         @EntityType String entityType);

        /**
         * Action to perform when failure has happened.
         */
        void onAnnotationResponseFailure(Throwable t);

        /**
         * An item from the {@link PhotoCaptureTasksFragment} was clicked.
         *
         * @param task the {@link Task} item that was clicked
         */
        void onTaskItemClicked(Task task);

        /**
         * The back button was pressed.
         */
        void onBackPressed();

        /**
         * Performs action when options menu was created
         */
        void onCreateOptionsMenu();

        /**
         * Performs action when search button has been clicked
         */
        void onSearchButtonClicked();

        /**
         * Fired when user marked the tasked as completed.
         */
        void onCompleteButtonClicked();

        /**
         * Called upon successfully submitting task's status.
         *
         * @param status If true - the task is being closed. If false - the task is being opened.
         */
        void onTaskStatusSubmitSuccess(boolean status);

        /**
         * Called when en error occurred when submitting task's status.
         */
        void onTaskStatusSubmitError(Throwable t);

        /**
         * Called, when the camera floating action button has been clicked.
         */
        void onCameraFabClick(FragmentManager manager);

        /**
         * Activity result was returned to the Tasks Fragment.
         *
         * @return True - if the result was consumed. False - if the result should be passed to super.
         */
        @CheckResult
        boolean onActivityResult(int requestCode, int resultCode, Intent data);

        /**
         * Called upon uploading {@link Photo}.
         */
        void onUploadPhoto(Photo photo);

        /**
         * Called upon successful {@code photo} upload.
         */
        void photoUploadSuccess(Photo photo);

        /**
         * Called upon unsuccessful {@code photo} upload.
         */
        void photoUploadFailure(Photo photo, Throwable e);

        /**
         * Retains the {@code subscription} in order to unsubscribe from it later if needed.
         */
        void addSubscription(Subscription subscription);

        /**
         * Action to perform when error response has been received.
         */
        void onPhotoUploadsResponseError(APIError apiError, int responseCode);

        /**
         * Action to perform when success response has been received.
         */
        void onPhotoUploadsResponseSuccess(PhotoUploadResponse response);

        /**
         * Action to perform when failure has happened.
         */
        void onPhotoUploadsResponseFailure(Throwable t);

        void onBarcodeParserResponseSuccess(BarcodeModel barcodeModel);

        void onBarcodeParserResponseError(APIError apiError, int responseCode);

        void onBarcodeParserResponseFailure(Throwable t);

        void onBarcodeDoneListener();

        /**
         * Creates new {@link Task} based on barcode values
         *
         * @param barcode     reference for the new Task
         * @param origin      origin for the new Task
         * @param destination destination for the new Task
         * @param isHawb      return whether task is hawb or mawb
         */
        void onCreateTaskButtonClicked(String barcode, String origin, String destination,
                                       boolean isHawb);

        /**
         * Creates new Shipment based on barcode values
         *
         * @param origin      origin for the new Task
         * @param destination destination for the new Task
         * @param carrier     (optional) got from barcode parser (if is a master bill)
         * @param shipment    hawb or mawb# (got from barcode parser)
         * @param pieces      number of pieces
         * @param isHawb      return whether task is hawb or mawb
         * @return true if we can close the dialog, false otherwise
         */
        boolean onCreateShipmentButtonClicked(String origin, String destination, String carrier,
                                              String shipment, String pieces, boolean isHawb);

        /**
         * Performs an action when add buttom was clicked
         */
        void onAddButtonClicked();

        /**
         * Action to perform when share button has been pressed.
         */
        void onShareButtonClicked();
    }

    interface Model {

        /**
         * Retrieves annotation types.
         */
        void getAnnotationTypes(@EntityType String entityType, String gateway);

        /**
         * @param status If true - the task is being closed. If false - the task is being opened.
         */
        void setTaskProgress(String username, String taskId, boolean status, String gateway);

        /**
         * Upload {@link Photo}.
         */
        void uploadPhoto(Photo photo);

        /**
         * Saves {@link Photo} to database
         */
        Observable<Void> savePhotoToDb(Photo photo);

        /**
         * Updates upload status of {@link Photo} in database
         *
         * @param photoId id of the {@link Photo}
         * @param isSend  indicates whether photo was upload or not
         */
        Observable<Void> updatePhotoStatusInDb(String photoId, boolean isSend);

        /**
         * Saves list of {@link Photo} objects to database
         */
        Observable<Void> savePhotosToDb(List<Photo> photos);

        /**
         * Returns list of {@link Photo} objects from database
         *
         * @param taskId id of the {@link Task}
         */
        Observable<List<Photo>> getPhotosFromDb(String taskId);

        /**
         * Returns {@link Task} object from database
         */
        Observable<Task> getTaskFromDb(int actionId, String reference);

        /**
         * Upload photos
         */
        void uploadPhotos(String user, String gateway, String reference, String reason,
                          List<Photo> photos, boolean byName);

        void parseBarcode(String barcode, String gateway);

        /**
         * Create a request to get house waybill details from the server.
         */
        Observable<HouseBill> requestHouseBill(String houseBill, String gateway);

        /**
         * Create a request to get house waybill details from the server.
         */
        Observable<MasterBill> requestMasterBill(String carrier, String masterBill, String gateway);

        /**
         * Creates HAWB Task on the server
         */
        Observable<CreateHawbTaskResponse> createHawbTask(int action, String user, String hawb,
                                                          String origin, String destination,
                                                          String gateway);

        /**
         * Creates MAWB Task on the server
         */
        Observable<CreateMawbTaskResponse> createMawbTask(int action, String carrier, String user,
                                                          String mawb, String origin,
                                                          String destination, String gateway);

        /**
         * Creates Shipment on the server
         */
        Observable<CreateShipmentResponse> createShipment(String origin, String destination,
                                                          String carrier, String shipment,
                                                          int pieces, String user, int action,
                                                          String gateway);

        /**
         * Gets Task from the server by task ID
         */
        Observable<Task> getTask(String taskId, String gateway);

        /**
         * Saves Task to the local database
         */
        Observable<Void> saveTask(Task task);
    }
}
