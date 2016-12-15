package com.margin.mgms.mvp.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.margin.components.utils.GATrackerUtils;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.model.APIError;
import com.margin.mgms.model.AccessToken;
import com.margin.mgms.model.AuthenticateRequest;
import com.margin.mgms.model.User;
import com.margin.mgms.model.UserRequest;
import com.margin.mgms.rest.StrongLoopApi;
import com.margin.mgms.util.Constants;
import com.margin.mgms.util.GsonUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

import static com.margin.mgms.rest.StrongLoopApi.API_VERSION;
import static com.margin.mgms.rest.StrongLoopApi.CLIENT;
import static com.margin.mgms.rest.StrongLoopApi.CLIENTS;

/**
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public class LoginModel implements LoginContract.Model {

    private final LoginContract.Presenter mPresenter;
    private final StrongLoopApi mStrongLoopApi;
    private final Converter<ResponseBody, APIError> mErrorConverter;

    public LoginModel(LoginContract.Presenter presenter) {
        mPresenter = presenter;
        mStrongLoopApi = LipstickApplication.getAppComponent().getOrdinaryStrongLoopApi();
        mErrorConverter = LipstickApplication.getAppComponent().getErrorConverter();
    }

    @Override
    public void authenticate(AuthenticateRequest authenticateRequest) {

        Call<AccessToken> call = mStrongLoopApi.authenticate(authenticateRequest);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + CLIENTS + "/authenticate",
                GsonUtil.getGson().toJson(authenticateRequest), 0);
        mPresenter.addCall(call);

        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response != null) {
                    if (!response.isSuccessful() && response.errorBody() != null) {
                        try {
                            APIError error = mErrorConverter.convert(response.errorBody());
                            int responseCode = error.getStatus();
                            mPresenter.onAuthError(error, responseCode);
                            GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                    Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                    API_VERSION + CLIENTS + "/authenticate",
                                    error.toString(), 0);
                        } catch (IOException e) {
                            mPresenter.onAuthFailure(e.getCause());
                            e.printStackTrace();
                            GATrackerUtils.trackException(
                                    LipstickApplication.getAppComponent().getAppContext(), e);
                        }
                    } else {
                        if (response.isSuccessful()) {
                            AccessToken loginResponse = response.body();
                            mPresenter.onAuthSuccess(loginResponse);
                            GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                    Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                    API_VERSION + CLIENTS + "/authenticate",
                                    loginResponse.toString(), 0);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                mPresenter.onAuthFailure(t);
                GATrackerUtils.trackException(
                        LipstickApplication.getAppComponent().getAppContext(), t);
            }
        });
    }

    @Override
    public void getUserInfo(@NonNull String id, @Nullable String filter, @NonNull String accessToken) {
        Call<User> call = mStrongLoopApi.getUser(id, filter, accessToken);
        UserRequest request = new UserRequest(filter, accessToken);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + CLIENT + "/" + id,
                GsonUtil.getGson().toJson(request), 0);
        mPresenter.addCall(call);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        APIError error = mErrorConverter.convert(response.errorBody());
                        int responseCode = error.getStatus();
                        mPresenter.onGetUserError(error, responseCode);
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + CLIENT + "/" + id,
                                error.toString(), 0);
                    } catch (IOException e) {
                        mPresenter.onGetUserFailure(e.getCause());
                        e.printStackTrace();
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), e);
                    }
                } else if (null != response) {
                    if (response.isSuccessful()) {
                        User loginResponse = response.body();
                        mPresenter.onGetUserSuccess(loginResponse);
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + CLIENT + "/" + id,
                                GsonUtil.getGson().toJson(loginResponse), 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mPresenter.onGetUserFailure(t);
                GATrackerUtils.trackException(
                        LipstickApplication.getAppComponent().getAppContext(), t);
            }
        });
    }
}
