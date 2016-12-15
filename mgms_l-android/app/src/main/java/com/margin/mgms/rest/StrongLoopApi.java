package com.margin.mgms.rest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Photo;
import com.margin.mgms.misc.EntityType;
import com.margin.mgms.model.AccessToken;
import com.margin.mgms.model.Action;
import com.margin.mgms.model.AuthenticateRequest;
import com.margin.mgms.model.BarcodeModel;
import com.margin.mgms.model.CreateHawbTaskRequest;
import com.margin.mgms.model.CreateHawbTaskResponse;
import com.margin.mgms.model.CreateMawbTaskRequest;
import com.margin.mgms.model.CreateMawbTaskResponse;
import com.margin.mgms.model.CreateShipmentRequest;
import com.margin.mgms.model.CreateShipmentResponse;
import com.margin.mgms.model.EmailPhotosRequest;
import com.margin.mgms.model.EmailPhotosResponse;
import com.margin.mgms.model.HouseBill;
import com.margin.mgms.model.MasterBill;
import com.margin.mgms.model.PhotoUploadResponse;
import com.margin.mgms.model.Task;
import com.margin.mgms.model.TaskPostRequest;
import com.margin.mgms.model.TaskPostResponse;
import com.margin.mgms.model.UploadPhotosRequest;
import com.margin.mgms.model.User;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created on March 31, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface StrongLoopApi {

    String SELECT_ALL = "Select All";

    String ENDPOINT = "http://margin-mgmsl-d-api-01.margin.com:3000";
    String API_VERSION = "/api/v1";
    String CLIENTS = "/Clients";
    String TASKS = "/Tasks";
    String MGMSL = "/MGMSL";
    String UTILITY = "/Utility";
    String CLIENT = "/Clients";
    String PHOTO_CAPTURE = "/PhotoCapture";
    String REASON_PROOF_OF_CONDITION = "Proof of Condition";
    String ENTITY_TYPE_HAWB = "HAWB";
    String ENTITY_TYPE_MAWB = "MAWB";

    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    /**
     * StrongLoop API</a>
     */
    @POST(API_VERSION + CLIENTS + "/authenticate")
    Call<AccessToken> authenticate(@Body AuthenticateRequest pin);

    /**
     * StrongLoop API</a>
     */
    @GET(API_VERSION + TASKS + "/tasksTree")
    Call<List<Action>> getTasksTree(@Query("user") String user,
                                    @Query("date") String date,
                                    @Query("filter") String filter,
                                    @Query("display") String display,
                                    @Query("gateway") String gateway);

    /**
     * Method to get details of the ENTITY_TYPE_HAWB excluding Photos and SpecialHandling.
     *
     * @param hawb    Housebill Number. taskHAWB returned by tasks API will be used here.
     * @param gateway Station name belonging to the user. Ex: "ORD"
     * StrongLoop API</a>
     */
    @GET(API_VERSION + UTILITY + "/hawbCompleteData")
    Observable<HouseBill> getHouseBill(@Query("hawb") String hawb,
                                       @Query("gateway") String gateway);

    /**
     * Method to get details of the ENTITY_TYPE_MAWB excluding Photos and SpecialHandling.
     *
     * @param carrier Masterbill Number. taskCarrierNumber returned by tasks API will be used here.
     * @param mawb    Masterbill Number. taskMAWB returned by tasks API will be used here.
     * @param gateway Station name belonging to the user. Ex: "ORD"
     * StrongLoop API</a>
     */
    @GET(API_VERSION + UTILITY + "/mawbCompleteData")
    Observable<MasterBill> getMasterBill(@Query("carrier") String carrier,
                                         @Query("mawb") String mawb,
                                         @Query("gateway") String gateway);

    /**
     * StrongLoop API</a>
     */
    @GET(API_VERSION + CLIENT + "/{id}")
    Call<User> getUser(@Path("id") String id, @Nullable @Query("filter") String filter,
                       @NonNull @Query("access_token") String accessToken);

    /**
     * StrongLoop API</a>
     */
    @GET(API_VERSION + PHOTO_CAPTURE + "/photos")
    Observable<ArrayList<Photo>> getPhotos(@Query("reference") String reference,
                                           @Query("embedded") boolean embedded,
                                           @Query("gateway") String gateway);

    /**
     * StrongLoop API</a>
     */
    @Headers({
            "Content-Type: image/jpeg",
            "Accept: application/json",
    })
    @PUT(API_VERSION + PHOTO_CAPTURE + "/upload/{image_uuid}")
    Observable<PhotoUploadResponse> uploadPhoto(@Path("image_uuid") String imageUUID,
                                                @Body RequestBody image);

    /**
     * StrongLoop API</a>
     */
    @POST(API_VERSION + PHOTO_CAPTURE + "/uploadPhotos")
    Call<PhotoUploadResponse> uploadPhotos(@Body UploadPhotosRequest request);

    /**
     * StrongLoop API</a>
     */
    @GET(API_VERSION + MGMSL + "/annotationTypes")
    Call<ArrayList<AnnotationType>> getAnnotationTypes(@Query("entityType") @EntityType String entityType,
                                                       @Query("gateway") String gateway);

    /**
     * Method to get a list of the Photo Capture tasks.
     *
     * @param actionId Id of the "Photo Capture" task. Value is 104.
     * @param userId   UserId of the user. OR "Select All". For Photo Capture, you need to pass
     *                 "Select All" and group them by Status/logged in User's ID on your end
     * @param date     Task date. This will be current date formatted as date (yyyy-mm-dd).
     *                 Ex: "2015-09-02"
     * @param status   Status for filtering. To get all, you can pass "Show All". It will bring
     *                 all tasks for the task Date in different statuses belonging to all users
     *                 (if user = Select All or the logged in user if user = UserId) .
     *                 Other values allowed are "Not Completed", "Not Started",  "In Progress",
     *                 "Completed", "Canceled", "Assigned", "Not Assigned".
     * @param gateway  Station name belonging to the user. Ex: "ORD"
     */
    @GET(API_VERSION + MGMSL + "/tasks")
    Call<List<Task>> tasks(@Query("task") int actionId,
                           @Query("user") String userId,
                           @Query("date") String date,
                           @Query("status") @Status String status,
                           @Query("gateway") String gateway);

    /**
     * E.g.:
     * <ul>
     * <li>user - DKENDALLORD</li>
     * <li>task - 8465563</li>
     * <li>status - true</li>
     * <li>gateway - ORD</li>
     * </ul>
     *
     * StrongLoop API</a>
     */
    @POST(API_VERSION + PHOTO_CAPTURE + "/task")
    Call<TaskPostResponse> postTask(@Body TaskPostRequest taskPost);

    /**
     * Method to parse a barcode to identify what information it holds.
     *
     * @param barcode Barcode number
     * @param gateway Station that the user is working in
     * StrongLoop API</a>
     */
    @GET(API_VERSION + UTILITY + "/barcodeParser")
    Call<BarcodeModel> barcodeParser(@Query("barcode") String barcode,
                                     @Query("gateway") String gateway);

    /**
     * StrongLoop API</a>
     */
    @POST(API_VERSION + PHOTO_CAPTURE + "/emailPhotos")
    Call<EmailPhotosResponse> emailPhotos(@Body EmailPhotosRequest emailPhotos);

    /**
     * StrongLoop API</a>
     */
    @POST(API_VERSION + PHOTO_CAPTURE + "/hawbCreateTask")
    Observable<CreateHawbTaskResponse> hawbCreateTask(@Body CreateHawbTaskRequest request);

    /**
     * StrongLoop API</a>
     */
    @POST(API_VERSION + PHOTO_CAPTURE + "/mawbCreateTask")
    Observable<CreateMawbTaskResponse> mawbCreateTask(@Body CreateMawbTaskRequest request);

    /**
     * StrongLoop API</a>
     */
    @POST(API_VERSION + MGMSL + "/skeletonShipmentTask")
    Observable<CreateShipmentResponse> createShipment(@Body CreateShipmentRequest request);

    /**
     * Method to get a task from API.
     *
     * @param taskId  Id of the Task
     * @param gateway Station name belonging to the user. Ex: "ORD"
     */
    @GET(API_VERSION + MGMSL + "/tasks/{id}")
    Observable<Task> getTask(@Path("id") String taskId,
                             @Query("gateway") String gateway);

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            Task.ALL, Task.NOT_COMPLETED, Task.NOT_STARTED, Task.IN_PROGRESS,
            Task.COMPLETED, Task.CANCELED, Task.NOT_ASSIGNED
    })
    @interface Status {
    }
}
