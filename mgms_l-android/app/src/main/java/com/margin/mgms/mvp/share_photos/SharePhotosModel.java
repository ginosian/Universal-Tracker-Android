package com.margin.mgms.mvp.share_photos;

import com.margin.camera.models.Photo;
import com.margin.components.utils.GATrackerUtils;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.listener.OnEmailSentListener;
import com.margin.mgms.model.APIError;
import com.margin.mgms.model.EmailPhotosRequest;
import com.margin.mgms.model.EmailPhotosResponse;
import com.margin.mgms.rest.StrongLoopApi;
import com.margin.mgms.util.Constants;
import com.margin.mgms.util.GsonUtil;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

import static com.margin.mgms.rest.StrongLoopApi.API_VERSION;
import static com.margin.mgms.rest.StrongLoopApi.PHOTO_CAPTURE;

/**
 * Created on Jun 22, 2016.
 *
 * @author Marta.Ginosyan
 */

public class SharePhotosModel implements SharePhotosContract.Model {

    private final SharePhotosContract.Presenter mPresenter;
    private final Converter<ResponseBody, APIError> mErrorConverter;
    private StrongLoopApi mStrongLoopApi;

    public SharePhotosModel(SharePhotosContract.Presenter presenter) {
        this.mPresenter = presenter;
        mStrongLoopApi = LipstickApplication.getAppComponent().getOrdinaryStrongLoopApi();
        mErrorConverter = LipstickApplication.getAppComponent().getErrorConverter();
    }

    @Override
    public void sendEmail(String reference, String[] toEmail, String fromEmail, String subject,
                          String emailBody, ArrayList<Photo> photos, String gateway,
                          OnEmailSentListener listener) {
        EmailPhotosRequest request = EmailPhotosRequest.newInstance(reference, toEmail, fromEmail,
                subject, emailBody, photos, gateway);
        Call<EmailPhotosResponse> call = mStrongLoopApi.emailPhotos(request);
        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                Constants.EventCategory.STRONG_LOOP_API_REQUEST,
                API_VERSION + PHOTO_CAPTURE + "/emailPhotos",
                GsonUtil.getGson().toJson(request), 0);
        mPresenter.addCall(call);
        call.enqueue(new Callback<EmailPhotosResponse>() {
            @Override
            public void onResponse(Call<EmailPhotosResponse> call,
                                   Response<EmailPhotosResponse> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        APIError error = mErrorConverter.convert(response.errorBody());
                        listener.onEmailSentError(error);
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + PHOTO_CAPTURE + "/emailPhotos",
                                error.toString(), 0);
                    } catch (IOException e) {
                        listener.onEmailSentFailure(e.getCause());
                        e.printStackTrace();
                        GATrackerUtils.trackException(
                                LipstickApplication.getAppComponent().getAppContext(), e);
                    }
                } else if (null != response) {
                    if (response.isSuccessful()) {
                        mPresenter.removeCall(call);
                        listener.onEmailSentSuccess();
                        GATrackerUtils.trackEvent(LipstickApplication.getAppComponent().getAppContext(),
                                Constants.EventCategory.STRONG_LOOP_API_RESPONSE,
                                API_VERSION + PHOTO_CAPTURE + "/emailPhotos",
                                GsonUtil.getGson().toJson(response.body()), 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<EmailPhotosResponse> call, Throwable t) {
                mPresenter.removeCall(call);
                listener.onEmailSentFailure(t);
                GATrackerUtils.trackException(
                        LipstickApplication.getAppComponent().getAppContext(), t);
            }
        });
    }
}
