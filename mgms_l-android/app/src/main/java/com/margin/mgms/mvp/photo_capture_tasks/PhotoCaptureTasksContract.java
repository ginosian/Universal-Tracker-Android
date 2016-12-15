package com.margin.mgms.mvp.photo_capture_tasks;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import com.margin.mgms.listener.OnTaskClickListener;
import com.margin.mgms.model.APIError;
import com.margin.mgms.model.Task;
import com.margin.mgms.rest.StrongLoopApi;

import java.text.DateFormat;
import java.util.List;

import retrofit2.Call;
import rx.Observable;

/**
 * Created on May 13, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface PhotoCaptureTasksContract {

    DateFormat DATE_FORMAT = StrongLoopApi.sDateFormat;

    String TASK = "task";
    String USER_ID = "user_id";
    String DATE = "date";
    String STATUS = "status";
    String GATEWAY = "gateway";

    String RECYCLER_VIEW_SAVED_STATE = "recycler_view_saved_state";

    interface View {

        /**
         * Sets up {@link android.support.v7.widget.AppCompatSpinner}.
         */
        void initSpinner();

        /**
         * Sets up {@link android.support.v7.widget.RecyclerView}.
         */
        void initRecyclerView();

        /**
         * Clears all items from {@link android.support.v7.widget.RecyclerView}.
         */
        void clearRecyclerView();

        /**
         * Shows only completed task items in the list
         */
        void showTaskItems(List<Object> tasks);

        /**
         * Displays {@link android.widget.Toast} with provided string.
         */
        void showToast(String message);

        /**
         * Displays {@link android.app.ProgressDialog} with provided message
         */
        void showProgress(String message);

        /**
         * Hides {@link android.app.ProgressDialog} if it's shown
         */
        void hideProgress();

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
         * Restores the {@link android.support.v7.widget.RecyclerView} view
         */
        void restoreRecyclerViewState(Parcelable state);

        /**
         * Sets listener on spinner item selected action
         */
        void setOnSpinnerItemSelectedListener();

        /**
         * Sets up {@link android.support.v4.widget.SwipeRefreshLayout SwipeRefreshLayout}
         * associated with the activity current {@link View} is attached to.
         */
        void setupSwipeRefreshLayout();

        /**
         * @param show If true - shows {@link android.support.v4.widget.SwipeRefreshLayout
         *             SwipeRefreshLayout}, otherwise hides it.
         */
        void showSwipeRefreshLayout(boolean show);

        /**
         * Sets listener for {@link com.margin.barcode.views.BarcodeEditText BarcodeEditText}'s
         * text change.
         */
        void setOnBarcodeTextChangeListener();
    }

    interface Presenter extends OnTaskClickListener {

        /**
         * Performs onCreate actions
         */
        void onCreate(Context context);

        /**
         * Performs onCreate view actions
         */
        void onCreateView();

        /**
         * Performs onDestroy actions
         */
        void onDestroy();

        /**
         * Performs onDestroy view actions
         */
        void onDestroyView();

        /**
         * Adds spinner item selected listener
         */
        void addSpinnerSelectedListener(Observable<Integer> listener);

        /**
         * Executes request for getting tasks from API.
         *
         * @param showProgressDialog If true - show {@link android.app.ProgressDialog ProgressDialog}.
         */
        void executeGettingTasks(boolean showProgressDialog);

        /**
         * Retains the instance of {@link Call}.
         */
        void addCall(Call call);

        /**
         * Cancel current {@link Call}.
         */
        void cancelCall();

        /**
         * Action to perform when error response has been received.
         */
        void onTasksResponseError(APIError apiError, int responseCode);

        /**
         * Action to perform when success response has been received.
         */
        void onTasksResponseSuccess();

        /**
         * Action to perform when failure has happened.
         */
        void onTasksResponseFailure(Throwable t);

        /**
         * Retrieves data from {@link android.os.Bundle} object
         */
        void getDataFromArguments(Bundle arguments);

        /**
         * Does on save instance state changes
         *
         * @param state             the whole instance state of fragment
         * @param viewInstanceState instance state of a particularly view (i.e. RecyclerView)
         */
        void onSaveInstanceState(Bundle state, Parcelable viewInstanceState);

        /**
         * Does on restore instance state changes
         */
        void onViewStateRestored(Bundle state);

        /**
         * Performs an action when fragment visibility was changed
         */
        void onHiddenChanged(boolean hidden);

        /**
         * Creates completed taskItem views for showing in the list
         */
        Observable<List<Object>> prepareCompletedTaskItems(List<Task> tasks);

        /**
         * Creates not completed taskItem views for showing in the list
         */
        Observable<List<Object>> prepareNotCompletedTaskItems(List<Task> assignedTasks,
                                                              List<Task> notAssignedTasks);

        /**
         * Updates the tasks that are being shown.
         *
         * @param showProgressDialog If true - {@link android.app.ProgressDialog ProgressDialog}
         *                           would be shown.
         */
        void updateTasks(boolean showProgressDialog);

        /**
         * Retains {@link rx.Subscription} from {@code barcodeChangedObservable}.
         */
        void setBarcodeTextChangedObservable(Observable<CharSequence> barcodeChangedObservable);

        /**
         * Performs an action when back button was pressed
         *
         * @return true if you want to consume this
         * event in fragment, and false if you want to pass it to activity.
         */
        boolean onBackPressed();
    }

    interface Model {

        /**
         * Gets tasks from server
         */
        void getTasks(int actionId, String userId, String date, String status, String gateway);

        /**
         * Gets only not completed tasks
         */
        Observable<List<Task>> getNotCompletedTasks(int actionId, String queryReference);

        /**
         * Gets only completed tasks
         */
        Observable<List<Task>> getCompletedTasks(int actionId, String queryReference);

        /**
         * Gets only not assigned tasks
         */
        Observable<List<Task>> getNotAssignedTasks(int actionId, String queryReference);
    }
}
