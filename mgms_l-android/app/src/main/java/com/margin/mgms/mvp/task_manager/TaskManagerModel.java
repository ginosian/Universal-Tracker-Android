package com.margin.mgms.mvp.task_manager;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.margin.components.utils.GATrackerUtils;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.R;
import com.margin.mgms.misc.Config;
import com.margin.mgms.model.APIError;
import com.margin.mgms.model.Action;
import com.margin.mgms.model.TasksTreeRequest;
import com.margin.mgms.rest.StrongLoopApi;
import com.margin.mgms.util.Constants;
import com.margin.mgms.util.GsonUtil;
import com.margin.mgms.util.PrefsUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

import static com.margin.mgms.rest.StrongLoopApi.API_VERSION;
import static com.margin.mgms.rest.StrongLoopApi.TASKS;

/**
 * Created on May 16, 2016.
 *
 * @author Marta.Ginosyan
 */
public class TaskManagerModel implements TaskManagerContract.Model {

    private final TaskManagerContract.Presenter mPresenter;
    private final StrongLoopApi mStrongLoopApi;
    private final Converter<ResponseBody, APIError> mErrorConverter;

    public TaskManagerModel(TaskManagerContract.Presenter presenter) {
        mPresenter = presenter;
        mStrongLoopApi = LipstickApplication.getAppComponent().getOrdinaryStrongLoopApi();
        mErrorConverter = LipstickApplication.getAppComponent().getErrorConverter();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void getTasksTree(String user, String date, String filter, String display,
                             String gateway) {
        Call<List<Action>> call = mStrongLoopApi.getTasksTree(user, date, filter, display, gateway);
        TasksTreeRequest request = new TasksTreeRequest(user, date, filter, display, gateway);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + TASKS + "/tasksTree",
                GsonUtil.getGson().toJson(request), 0);
        mPresenter.setCall(call);

        call.enqueue(new Callback<List<Action>>() {
            @Override
            public void onResponse(Call<List<Action>> call, Response<List<Action>> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        APIError error = mErrorConverter.convert(response.errorBody());
                        int responseCode = error.getStatus();
                        mPresenter.onResponseError(error, responseCode);
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + TASKS + "/tasksTree",
                                error.toString(), 0);
                    } catch (IOException e) {
                        mPresenter.onResponseFailure(e.getCause());
                        e.printStackTrace();
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), e);
                    }
                } else if (response != null && response.isSuccessful()) {
                    List<Action> actions = response.body();
                    mPresenter.onResponseSuccess(actions);
                    Type listOfActions = new TypeToken<List<Action>>() {
                    }.getType();
                    GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                            Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                            API_VERSION + TASKS + "/tasksTree",
                            GsonUtil.getGson().toJson(actions, listOfActions), 0);
                }
            }

            @Override
            public void onFailure(Call<List<Action>> call, Throwable t) {
                mPresenter.onResponseFailure(t);
                GATrackerUtils.trackException(
                        LipstickApplication.getAppComponent().getAppContext(), t);
            }
        });
    }

    @Override
    public void logout() {
        PrefsUtils.removeUser();
    }

    @Override
    public void setConfigDate(int year, int month, int day) {
        Context context = LipstickApplication.getAppComponent().getAppContext();
        Config.DATE_STRING_TEST = context.getString(R.string.value_config_date_format, year,
                month + 1, day);
    }
}
