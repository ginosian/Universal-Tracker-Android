package com.margin.mgms.mvp.task_manager;

import android.content.Context;
import android.os.Bundle;

import com.margin.mgms.model.APIError;
import com.margin.mgms.model.Action;
import com.margin.mgms.rest.StrongLoopApi;

import java.text.DateFormat;
import java.util.List;

import retrofit2.Call;

/**
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface TaskManagerContract {

    int CARGO_PHOTO_CAPTURE = 104;

    DateFormat DATE_FORMAT = StrongLoopApi.sDateFormat;
    String USER = "user";
    String DATE = "date";
    String FILTER = "filter";
    String DISPLAY = "display";
    String GATEWAY = "gateway";
    String MOBILE = "MOBILE";

    interface View {

        /**
         * Sets up Navigation Drawer ({@link android.support.v4.widget.DrawerLayout}).
         */
        void initNavigationDrawer();

        /**
         * Sets up {@link android.support.v7.widget.RecyclerView}.
         */
        void initRecyclerView();

        /**
         * Adds actions list to {@link android.support.v7.widget.RecyclerView}
         */
        void updateRecyclerView(List<Action> actions);

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
         * Opens the NavigationDrawer when user clicked navigation button
         */
        void openNavigationDrawer();

        /**
         * Shows the date select dialog
         */
        void showSelectDateDialog();

        /**
         * Shows or hide select date menu item
         */
        void showSelectDateMenuItem(boolean show);

        /**
         * Shows the login screen
         */
        void openLoginScreen();

        /**
         * Destroys the view (i.e. finishes the activity)
         */
        void destroy();
    }

    interface Presenter {

        /**
         * Does necessary actions when view has been created
         */
        void onCreate(Context context);

        /**
         * Does necessary actions when view has been destroyed
         */
        void onDestroy();

        /**
         * Executes request for getting tasks from API.
         */
        void executeGettingTasksTree(Context context, String user, String date, String filter,
                                     String display, String connection);

        /**
         * Retains the instance of {@link Call}.
         */
        void setCall(Call call);

        /**
         * Cancel current {@link Call}.
         */
        void cancelCall();

        /**
         * Action to perform when mError response has been received.
         */
        void onResponseError(APIError apiError, int responseCode);

        /**
         * Action to perform when success response has been received.
         */
        void onResponseSuccess(List<Action> actions);

        /**
         * Action to perform when failure has happened.
         */
        void onResponseFailure(Throwable t);

        /**
         * Perfoms action when list item has been clicked
         */
        void onItemClicked(Context context, Action action, int position);

        /**
         * Perfoms action when navigation button has been clicked
         */
        void onNavigationButtonClicked();

        /**
         * Retrieves data from {@link Bundle} object
         */
        void getDataFromArguments(Bundle arguments);

        /**
         * Performs logout action
         */
        void onLogoutButtonClicked();

        /**
         * Performs an action when select date button was clicked
         */
        void onSelectDateButtonClicked();

        /**
         * Performs an action when date was selected
         *
         * @param year  Year (like 2016)
         * @param month Month (from 0-11)
         * @param day   Day of month
         */
        void onConfirmDateButtonClicked(int year, int month, int day);

        /**
         * Performs an action when options menu was created
         */
        void onCreateOptionsMenu();
    }

    interface Model {

        /**
         * Gets tasks tree from API
         */
        void getTasksTree(String user, String date, String filter, String display,
                          String gateway);

        /**
         * Performs logout action
         */
        void logout();

        /**
         * Changes the default date in DOGFOOD build
         *
         * @param year  Year (like 2016)
         * @param month Month (from 0-11)
         * @param day   Day of month
         */
        void setConfigDate(int year, int month, int day);
    }
}
