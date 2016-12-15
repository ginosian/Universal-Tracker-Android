package com.margin.mgms.mvp.photo_capture_tasks;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.margin.components.utils.ConnectivityUtils;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.R;
import com.margin.mgms.listener.OnTaskClickListener;
import com.margin.mgms.model.APIError;
import com.margin.mgms.model.Task;
import com.margin.mgms.model.TaskHeader;
import com.margin.mgms.mvp.task_manager.TaskManagerContract;
import com.margin.mgms.rest.StrongLoopApi;
import com.margin.mgms.util.PrefsUtils;
import com.margin.mgms.util.RxUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created on May 13, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoCaptureTasksPresenter implements PhotoCaptureTasksContract.Presenter {

    private Context mContext;
    private PhotoCaptureTasksContract.View mView;
    private PhotoCaptureTasksContract.Model mModel;
    private OnTaskClickListener mOnTaskClickListener;
    private List<Call> mCallList = new ArrayList<>();
    private boolean mCompleted;

    private int mActionId;
    private String mUserId;
    private String mDate;
    private String mStatus;
    private String mGateway;

    private Subscription mSpinnerSubscription;
    private Subscription mBarcodeSubscription;
    private Action1<List<Object>> mShowTaskItemsAction = taskItems -> {
        if (mView != null) mView.showTaskItems(taskItems);
    };

    public PhotoCaptureTasksPresenter(PhotoCaptureTasksContract.View view, OnTaskClickListener
            listener, @NonNull Bundle arguments) {
        mView = view;
        mModel = new PhotoCaptureTasksModel(this);
        mOnTaskClickListener = listener;
        mContext = LipstickApplication.getAppComponent().getAppContext();
        getDataFromArguments(arguments);
    }

    @Override
    public void onCreate(Context context) {
        if (mView != null) {
            mView.initSpinner();
            mView.setOnSpinnerItemSelectedListener();
            mView.setOnBarcodeTextChangeListener();
            if (ConnectivityUtils.isNetworkAvailable(mContext)) {
                executeGettingTasks(true);
            } else {
                if (mCompleted) showCompletedTasks(null);
                else showNotCompletedTasks(null);
            }
        }
    }

    @Override
    public void onCreateView() {
        if (mView != null) {
            mView.showActionBarTitle(false);
            mView.showSpinner(true);
            mView.initRecyclerView();
            mView.setupSwipeRefreshLayout();
        }
    }

    @Override
    public void onDestroy() {
        cancelCall();
        mView = null;
        if (mSpinnerSubscription != null) mSpinnerSubscription.unsubscribe();
        if (mBarcodeSubscription != null) mBarcodeSubscription.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        //TODO: just added for symmetry
    }

    @Override
    public void addSpinnerSelectedListener(Observable<Integer> listener) {
        mSpinnerSubscription = listener
                .skip(2)
                .subscribe(position -> {
                    if (mView != null) {
                        switch (position) {
                            case 0: //not completed tasks
                                mCompleted = false;
                                showNotCompletedTasks(null);
                                break;
                            case 1: //completed tasks
                                mCompleted = true;
                                showCompletedTasks(null);
                                break;
                        }
                    }
                });
    }

    @Override
    public void onTaskItemClicked(Task task) {
        if (mOnTaskClickListener != null) mOnTaskClickListener.onTaskItemClicked(task);
    }

    @Override
    public void executeGettingTasks(boolean showProgressDialog) {
        if (mView != null) {
            if (ConnectivityUtils.isNetworkAvailable(mContext)) {
                if (showProgressDialog) {
                    mView.showProgress(mContext.getString(R.string.message_task_loading));
                }
                mModel.getTasks(mActionId, mUserId, mDate, mStatus, mGateway);
            } else mView.showToast(mContext.getString(R.string.message_no_internet));
        }
    }

    @Override
    public void addCall(Call call) {
        if (null != call) mCallList.add(call);
    }

    @Override
    public void cancelCall() {
        for (Call call : mCallList) call.cancel();
    }

    @Override
    public void onTasksResponseError(APIError apiError, int responseCode) {
        if (mView != null) {
            mView.showToast(apiError.getMessage());
            mView.hideProgress();
            mView.showSwipeRefreshLayout(false);
        }
    }

    @Override
    public void onTasksResponseSuccess() {
        if (mView != null) {
            if (mCompleted) showCompletedTasks(null);
            else showNotCompletedTasks(null);
            mView.hideProgress();
            mView.showSwipeRefreshLayout(false);
        }
    }

    @Override
    public void onTasksResponseFailure(Throwable t) {
        if (mView != null) {
            mView.showToast(t.getMessage());
            mView.hideProgress();
            mView.showSwipeRefreshLayout(false);
        }
    }

    @Override
    public void getDataFromArguments(Bundle arguments) {
        if (arguments != null) {
            mActionId = arguments.getInt(PhotoCaptureTasksContract.TASK,
                    TaskManagerContract.CARGO_PHOTO_CAPTURE);
            mUserId = arguments.getString(PhotoCaptureTasksContract.USER_ID,
                    StrongLoopApi.SELECT_ALL);
            mDate = arguments.getString(PhotoCaptureTasksContract.DATE,
                    PhotoCaptureTasksContract.DATE_FORMAT.format(new Date()));
            mStatus = arguments.getString(PhotoCaptureTasksContract.STATUS,
                    Task.ALL);
            mGateway = arguments.getString(PhotoCaptureTasksContract.GATEWAY,
                    PrefsUtils.getDefaultGateway());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState, Parcelable viewInstanceState) {
        savedInstanceState.putParcelable(PhotoCaptureTasksContract.RECYCLER_VIEW_SAVED_STATE,
                viewInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (mView != null && savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable
                    (PhotoCaptureTasksContract.RECYCLER_VIEW_SAVED_STATE);
            mView.restoreRecyclerViewState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden && mView != null) {
            mView.showActionBarTitle(false);
            mView.showSpinner(true);
        }
    }

    private void showCompletedTasks(String queryReference) {
        mModel.getCompletedTasks(mActionId, queryReference).subscribe(tasks -> {
            prepareCompletedTaskItems(tasks).compose(RxUtils.applyIOtoMainThreadSchedulers())
                    .subscribe(mShowTaskItemsAction);
        });
    }

    private void showNotCompletedTasks(String queryReference) {
        mModel.getNotCompletedTasks(mActionId, queryReference).subscribe(assigned -> {
            mModel.getNotAssignedTasks(mActionId, queryReference).subscribe(notAssigned -> {
                prepareNotCompletedTaskItems(assigned, notAssigned)
                        .compose(RxUtils.applyIOtoMainThreadSchedulers())
                        .subscribe(mShowTaskItemsAction);
            });
        });
    }

    @Override
    public Observable<List<Object>> prepareCompletedTaskItems(List<Task> tasks) {
        return Observable.create((Observable.OnSubscribe<List<Object>>) subscriber -> {
            subscriber.onNext(new ArrayList<Object>(tasks));
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<List<Object>> prepareNotCompletedTaskItems(List<Task> assignedTasks,
                                                                 List<Task> notAssignedTasks) {
        return Observable.create((Observable.OnSubscribe<List<Object>>) subscriber -> {
            List<Object> totalTasks = new ArrayList<>();
            if (!assignedTasks.isEmpty()) {
                List<Object> inProgressTasks = new ArrayList<>();
                List<Object> notStartedTasks = new ArrayList<>();
                for (int i = 0; i < assignedTasks.size(); i++) {
                    Task task = assignedTasks.get(i);
                    switch (task.getStatus()) {
                        case InProgress:
                            inProgressTasks.add(task);
                            break;
                        case NotStarted:
                            notStartedTasks.add(task);
                            break;
                    }
                }
                totalTasks.addAll(inProgressTasks);
                totalTasks.addAll(notStartedTasks);
                totalTasks.add(0, new TaskHeader(true, totalTasks.size()));
            }
            if (!notAssignedTasks.isEmpty()) {
                TaskHeader notAssigned = new TaskHeader(false, notAssignedTasks.size());
                totalTasks.add(notAssigned);
                totalTasks.addAll(notAssignedTasks);
            }
            subscriber.onNext(totalTasks);
            subscriber.onCompleted();
        });
    }

    @Override
    public void updateTasks(boolean showProgressDialog) {
        executeGettingTasks(showProgressDialog);
    }

    @Override
    public void setBarcodeTextChangedObservable(Observable<CharSequence> barcodeChangedObservable) {
        mBarcodeSubscription = barcodeChangedObservable
                .skip(1)
                .debounce(RxUtils.DURATION_DEBOUNCE, TimeUnit.MILLISECONDS)
                .subscribe((CharSequence charSequence) -> {
                    if (mView != null) {
                        if (mCompleted) showCompletedTasks(charSequence.toString());
                        else showNotCompletedTasks(charSequence.toString());
                    }
                });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
