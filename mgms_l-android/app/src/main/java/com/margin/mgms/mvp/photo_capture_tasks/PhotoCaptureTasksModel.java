package com.margin.mgms.mvp.photo_capture_tasks;

import com.google.gson.reflect.TypeToken;
import com.margin.components.utils.GATrackerUtils;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.database.DatabaseConnector;
import com.margin.mgms.misc.Config;
import com.margin.mgms.model.APIError;
import com.margin.mgms.model.Task;
import com.margin.mgms.model.TasksRequest;
import com.margin.mgms.rest.StrongLoopApi;
import com.margin.mgms.util.Constants;
import com.margin.mgms.util.GsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

import static com.margin.mgms.misc.Config.IS_DOGFOOD_BUILD;
import static com.margin.mgms.rest.StrongLoopApi.API_VERSION;
import static com.margin.mgms.rest.StrongLoopApi.MGMSL;

/**
 * Created on May 19, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoCaptureTasksModel implements PhotoCaptureTasksContract.Model {

    private static final String[] ORIGINS = {
            "JFK", "YYZ", "ORD", "YUL", "IAH", "EWR"
    };
    private static final String[] DESTINATIONS = {
            "TXL", "LAX", "LGA", "ATH", "SFO", "YYC"
    };
    private static final String[] LOCATION_1 = {
            "B-1232", "B-1234", "C-4532", "D-2134", "B-9876"
    };
    private static final String[] LOCATION_2 = {
            "B-1726", "B-1726", "C-9394", "D-8387", "B-2736"
    };
    private final PhotoCaptureTasksContract.Presenter mPresenter;
    private final StrongLoopApi mStrongLoopApi;
    private final Converter<ResponseBody, APIError> mErrorConverter;

    public PhotoCaptureTasksModel(PhotoCaptureTasksContract.Presenter presenter) {
        mPresenter = presenter;
        mStrongLoopApi = LipstickApplication.getAppComponent().getOrdinaryStrongLoopApi();
        mErrorConverter = LipstickApplication.getAppComponent().getErrorConverter();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void getTasks(int actionId, String userId, String date, String status, String gateway) {

        TasksRequest request = new TasksRequest(actionId, userId, date, status, gateway);
        Call<List<Task>> call = mStrongLoopApi.tasks(actionId, userId, IS_DOGFOOD_BUILD ?
                Config.DATE_STRING_TEST : date, status, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + MGMSL + "/tasks",
                GsonUtil.getGson().toJson(request), 0);
        mPresenter.addCall(call);

        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        APIError error = mErrorConverter.convert(response.errorBody());
                        int responseCode = error.getStatus();
                        mPresenter.onTasksResponseError(error, responseCode);
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + MGMSL + "/tasks",
                                error.toString(), 0);
                    } catch (IOException e) {
                        mPresenter.onTasksResponseFailure(e.getCause());
                        e.printStackTrace();
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), e);
                    }
                } else if (response != null && response.isSuccessful()) {
                    DatabaseConnector.getInstance().saveTasks(response.body(), actionId)
                            .subscribe(o -> mPresenter.onTasksResponseSuccess());
                    Type listOfTasks = new TypeToken<List<Task>>() {
                    }.getType();
                    GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                            Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                            API_VERSION + MGMSL + "/tasks",
                            GsonUtil.getGson().toJson(response.body(), listOfTasks), 0);
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                mPresenter.onTasksResponseFailure(t);
                GATrackerUtils.trackException(
                        LipstickApplication.getAppComponent().getAppContext(), t);
            }
        });
    }

    @Override
    public Observable<List<Task>> getCompletedTasks(int actionId, String queryReference) {
        return DatabaseConnector.getInstance().getCompletedTasksOrderByDate(actionId, false,
                queryReference);
    }

    @Override
    public Observable<List<Task>> getNotAssignedTasks(int actionId, String queryReference) {
        return DatabaseConnector.getInstance().getNotAssignedTasksOrderByDate(actionId, false,
                queryReference);
    }

    @Override
    public Observable<List<Task>> getNotCompletedTasks(int actionId, String queryReference) {
        return DatabaseConnector.getInstance().getNotCompletedTasksOrderByDate(actionId, false,
                queryReference);
    }

    private List<Task> createDummyNotCompletedTasks() {
        // TODO: May have to re-check some of this logic
        List<Task> tasks = createDummyTasks(15);
        for (int i = 0; i < tasks.size(); i++) {
            if (i < tasks.size() / 3) {
                tasks.get(i).setStatus(Task.Status.InProgress);
            } else {
                tasks.get(i).setStatus(Task.Status.NotStarted);
            }
        }
        return tasks;
    }

    private List<Task> createDummyCompletedTasks() {
        List<Task> tasks = createDummyTasks(8);
        for (Task task : tasks) task.setStatus(Task.Status.Completed);
        return tasks;
    }

    private List<Task> createDummyTasks(int count) {
        // TODO may have to re-check some of this logic
        List<Task> tasks = new ArrayList<>();

//        for (int i = 0; i < count; i++) {
//            Task task = new Task();
//
//            // Basic information
//            task.setOrigin(ORIGINS[new Random().nextInt(5)]);
//            task.setDestination(DESTINATIONS[new Random().nextInt(5)]);
//            task.setReference(new Random().nextInt(8999) + 10000 + ""); // Reference is a 5-digit #
//            task.setTotalPieces(new Random().nextInt(20));
//            task.setTotalWeight(new Random().nextInt(600));
//            task.setWeightUom("kg");
//            task.setDate(new Date(System.currentTimeMillis() - i * 66666666));
//
//            // Add locations
//            List<ShipmentLocation> locations = new ArrayList<>(2);
//            ShipmentLocation location1 = new ShipmentLocation();
//            location1.setLocation(LOCATION_1[new Random().nextInt(4)]);
//            location1.setNumPieces(task.getTotalPieces());
//            if (new Random().nextInt(9) % 2 == 0) {
//                ShipmentLocation location2 = new ShipmentLocation();
//                location2.setLocation(LOCATION_2[new Random().nextInt(4)]);
//                int pieces = task.getTotalPieces() / 2;
//                location1.setNumPieces(pieces);
//                location2.setNumPieces(task.getTotalPieces() - pieces);
//                locations.add(location2);
//                task.getSpecialHandling().setTempControlled(true);
//
////                int screening = new Random().nextInt(2);
////                task.setScreeningCompleted(screening == 0);
////                task.setScreeningPending(screening == 1);
////                task.setScreeningFailed(screening == 2);
//            }
//            locations.add(location1);
//            task.setLocations(locations);
//
//            // Special handling
//            task.getSpecialHandling().setOsd(true);
//            task.getSpecialHandling().setAlertCount(1);
//
//            tasks.add(task);
//        }

        return tasks;
    }

}
