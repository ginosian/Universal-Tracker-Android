package com.margin.mgms.mvp.task_manager;

import android.content.Context;
import android.os.Bundle;

import com.margin.components.utils.ConnectivityUtils;
import com.margin.mgms.R;
import com.margin.mgms.activity.PhotoCaptureActivity;
import com.margin.mgms.misc.Config;
import com.margin.mgms.model.APIError;
import com.margin.mgms.model.Action;
import com.margin.mgms.model.Task;
import com.margin.mgms.util.PrefsUtils;

import java.util.Date;
import java.util.List;

import retrofit2.Call;

/**
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public class TaskManagerPresenter implements TaskManagerContract.Presenter {

    private TaskManagerContract.View mView;
    private TaskManagerContract.Model mModel;
    private Call mCall;

    private String mUser;
    private String mDate;
    private String mFilter;
    private String mDisplay;
    private String mGateway;

    public TaskManagerPresenter(TaskManagerContract.View view, Bundle arguments) {
        mView = view;
        mModel = new TaskManagerModel(this);
        getDataFromArguments(arguments);
    }

    @Override
    public void onCreate(Context context) {
        if (mView != null) {
            mView.initNavigationDrawer();
            mView.initRecyclerView();
            executeGettingTasksTree(context, mUser, mDate, mFilter, mDisplay, mGateway);
        }
    }

    @Override
    public void onDestroy() {
        cancelCall();
        mView = null;
    }

    @Override
    public void executeGettingTasksTree(Context context, String user, String date, String filter,
                                        String display, String gateway) {
        if (mView != null) {
            if (ConnectivityUtils.isNetworkAvailable(context)) {
                mView.showProgress(context.getString(R.string.message_task_loading));
                mModel.getTasksTree(user, date, filter, display, gateway);
//                mModel.getTasksTree(user, "2016-08-03", filter, display, gateway);
            } else mView.showToast(context.getString(R.string.message_no_internet));
        }
    }

    @Override
    public void setCall(Call call) {
        mCall = call;
    }

    @Override
    public void cancelCall() {
        if (mCall != null && !mCall.isCanceled()) mCall.cancel();
    }

    @Override
    public void onResponseError(APIError apiError, int responseCode) {
        if (mView != null) {
            mView.showToast(apiError.getMessage());
            mView.hideProgress();
        }
    }

    @Override
    public void onResponseSuccess(List<Action> actions) {
        if (null != mView) {
            mView.updateRecyclerView(actions);
            mView.hideProgress();
        }
    }

    @Override
    public void onResponseFailure(Throwable t) {
        if (mView != null) {
            mView.showToast(t.getMessage());
            mView.hideProgress();
        }
    }

    @Override
    public void onItemClicked(Context context, Action action, int position) {
        switch (action.getId()) {
            case TaskManagerContract.CARGO_PHOTO_CAPTURE:
                PhotoCaptureActivity.launch(context);
                break;
            default:
                if (mView != null) {
                    mView.showToast(context.getString(R.string.message_task_locked,
                            action.getLabel()));
                }
                break;
        }
    }

    @Override
    public void onNavigationButtonClicked() {
        if (mView != null) {
            mView.openNavigationDrawer();
        }
    }

    @Override
    public void getDataFromArguments(Bundle arguments) {
        if (arguments != null) {
            mUser = arguments.getString(TaskManagerContract.USER,
                    PrefsUtils.getUsername());
//            mDate = arguments.getString("2016-08-03");
            mDate = arguments.getString(TaskManagerContract.DATE,
                    TaskManagerContract.DATE_FORMAT.format(new Date()));
            mFilter = arguments.getString(TaskManagerContract.FILTER,
                    Task.ALL);
            mDisplay = arguments.getString(TaskManagerContract.DISPLAY,
                    TaskManagerContract.MOBILE);
            mGateway = arguments.getString(TaskManagerContract.GATEWAY,
                    PrefsUtils.getDefaultGateway());
        }
    }

    @Override
    public void onLogoutButtonClicked() {
        if (mView != null) {
            mModel.logout();
            mView.openLoginScreen();
            mView.destroy();
        }
    }

    @Override
    public void onSelectDateButtonClicked() {
        if (mView != null) mView.showSelectDateDialog();
    }

    @Override
    public void onConfirmDateButtonClicked(int year, int month, int day) {
        mModel.setConfigDate(year, month, day);
    }

    @Override
    public void onCreateOptionsMenu() {
        if (Config.IS_DOGFOOD_BUILD) mView.showSelectDateMenuItem(true);
        else mView.showSelectDateMenuItem(false);
    }
}
