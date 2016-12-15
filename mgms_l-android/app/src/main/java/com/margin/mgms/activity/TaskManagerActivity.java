package com.margin.mgms.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.margin.mgms.R;
import com.margin.mgms.adapter.TasksAdapter;
import com.margin.mgms.fragment.DatePickerFragment;
import com.margin.mgms.misc.DividerItemDecoration;
import com.margin.mgms.model.Action;
import com.margin.mgms.mvp.task_manager.TaskManagerContract;
import com.margin.mgms.mvp.task_manager.TaskManagerPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public class TaskManagerActivity extends AppCompatActivity implements TaskManagerContract.View,
        DatePickerDialog.OnDateSetListener {

    private static final String DATE_PICKER = "datePicker";

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tasks_list)
    RecyclerView mTasksList;

    private TasksAdapter mTasksAdapter;
    private TaskManagerContract.Presenter mPresenter;
    private ProgressDialog mProgressDialog;

    private MenuItem mSelectDateItem;

    /**
     * Launches TaskManagerActivity with given arguments
     *
     * @param clearBackStack If true clears activity stack, so that the current activity would be the
     *                       first activity in stack
     */
    public static void launch(Activity activity, boolean clearBackStack) {
        Intent intent = new Intent(activity, TaskManagerActivity.class);
        intent.putExtras(new Bundle()); // This must be here so we can get default values
        if (clearBackStack) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
        } else activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        ButterKnife.bind(this);
        mPresenter = new TaskManagerPresenter(this, getIntent().getExtras());
        mPresenter.onCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_manager, menu);
        mSelectDateItem = menu.findItem(R.id.action_select_date);
        mPresenter.onCreateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_date:
                mPresenter.onSelectDateButtonClicked();
                break;
            case R.id.action_logout:
                mPresenter.onLogoutButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initNavigationDrawer() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                mToolbar.setNavigationIcon(R.drawable.menu_icon);
                mToolbar.setNavigationOnClickListener(
                        v -> mPresenter.onNavigationButtonClicked());
            }
        }
        //TODO: switched off temporarily
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void initRecyclerView() {
        mTasksAdapter = new TasksAdapter(new ArrayList<>(), mPresenter);
        mTasksList.setHasFixedSize(true);
        mTasksList.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider,
                DividerItemDecoration.VERTICAL_LIST));
        mTasksList.setAdapter(mTasksAdapter);
    }

    @Override
    public void updateRecyclerView(List<Action> actions) {
        mTasksAdapter.addActions(actions);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(String message) {
        mProgressDialog = ProgressDialog.show(this, null, message, true);
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void openNavigationDrawer() {
        //TODO: switched off temporarily
        // mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void openLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void destroy() {
        finish();
    }

    @Override
    public void showSelectDateDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), DATE_PICKER);
    }

    @Override
    public void showSelectDateMenuItem(boolean show) {
        if (mSelectDateItem != null) mSelectDateItem.setVisible(show);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mPresenter.onConfirmDateButtonClicked(year, monthOfYear, dayOfMonth);
    }
}
