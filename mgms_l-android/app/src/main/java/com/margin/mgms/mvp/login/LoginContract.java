package com.margin.mgms.mvp.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.margin.mgms.model.APIError;
import com.margin.mgms.model.AccessToken;
import com.margin.mgms.model.AuthenticateRequest;
import com.margin.mgms.model.User;

import retrofit2.Call;
import rx.Observable;

/**
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface LoginContract {

    interface View {
        /**
         * @param enable If true - sets login button {@link android.view.View#setEnabled(boolean)
         *               setEnabled()}
         *               to true. Sets to false otherwise.
         */
        void setLoginButtonEnabled(boolean enable);

        /**
         * Sets listener for {@link com.margin.barcode.views.BarcodeEditText BarcodeEditText}'s
         * text change.
         */
        void addBarcodeTextChangeListener();

        /**
         * Action to perform when inputted PIN is invalid.
         */
        void setErrorMessage(String errorMsg);

        /**
         * Displays toast with provided {@code strings}.
         */
        void showToast(String... strings);

        /**
         * Controls {@link android.widget.ProgressBar ProgressBar}'s visibility state.
         *
         * @param show If true - shows {@link android.widget.ProgressBar ProgressBar}.
         *             Otherwise - hides it.
         */
        void showProgress(boolean show);

        /**
         * Launches {@link com.margin.mgms.activity.TaskManagerActivity TaskManagerActivity}.
         */
        void launchTaskManager();
    }

    interface Presenter {

        /**
         * Action to perform on setup.
         */
        void create();

        /**
         * Action to perform on onDestroy.
         */
        void destroy();

        /**
         * Delegates adding listener to {@link com.margin.barcode.views.BarcodeEditText
         * BarcodeEditText}
         * to {@link View} associated with this {@link Presenter}.
         */
        void delegateAddBarcodeTextChangeListener();

        /**
         * Retains {@link rx.Subscription} from {@code barcodeChangedObservable}.
         */
        void setBarcodeTextChangedObservable(Observable<CharSequence> barcodeChangedObservable);

        /**
         * Verifies if the PIN is in valid format.
         */
        void verifyPin(String pin);

        /**
         * Performs log in.
         */
        void performLogin(String pin);

        /**
         * Retains the instance of {@link Call}.
         */
        void addCall(Call call);

        /**
         * Cancel currently running {@link Call}s.
         */
        void cancelCalls();

        /**
         * Action to perform when mError response has been received.
         */
        void onAuthError(APIError apiError, int responseCode);

        /**
         * Action to perform when success response has been received.
         */
        void onAuthSuccess(AccessToken accessToken);

        /**
         * Action to perform when failure has happened.
         */
        void onAuthFailure(Throwable t);

        /**
         * Action to perform when mError response has been received.
         */
        void onGetUserError(APIError apiError, int responseCode);

        /**
         * Action to perform when success response has been received.
         */
        void onGetUserSuccess(User user);

        /**
         * Action to perform when failure has happened.
         */
        void onGetUserFailure(Throwable t);

        /**
         * Sets, whether login can be performed.
         */
        void setCanLogin(boolean canLogin);

        /**
         * @return True, whether login can be done. False otherwise.
         */
        boolean canLogin();

        /**
         * Action to perform when login button was clicked.
         */
        void onLoginClicked(String pin);
    }

    interface Model {
        /**
         * Performs async request.
         */
        void authenticate(AuthenticateRequest authenticateRequest);

        /**
         * Retrieves {@link com.margin.mgms.model.User User}.
         *
         * @param id          User's id: {@link AccessToken#getUserId()}
         * @param filter      Optional filter
         * @param accessToken Access Token: {@link AccessToken#getId()}
         */
        void getUserInfo(@NonNull String id, @Nullable String filter, @NonNull String accessToken);
    }
}
