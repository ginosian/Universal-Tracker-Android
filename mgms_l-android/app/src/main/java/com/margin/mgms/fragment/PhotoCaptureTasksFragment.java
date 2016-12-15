package com.margin.mgms.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.margin.barcode.views.BarcodeEditText;
import com.margin.components.fragments.BackHandledFragment;
import com.margin.mgms.R;
import com.margin.mgms.adapter.PhotoCaptureTasksAdapter;
import com.margin.mgms.listener.OnTaskChangeListener;
import com.margin.mgms.listener.OnTaskClickListener;
import com.margin.mgms.misc.DividerItemDecoration;
import com.margin.mgms.misc.NpaLinearLayoutManager;
import com.margin.mgms.model.Task;
import com.margin.mgms.mvp.photo_capture_tasks.PhotoCaptureTasksContract;
import com.margin.mgms.mvp.photo_capture_tasks.PhotoCaptureTasksPresenter;
import com.margin.mgms.mvp.task_manager.TaskManagerContract;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created on May 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoCaptureTasksFragment extends BackHandledFragment implements
        PhotoCaptureTasksContract.View, OnTaskChangeListener {

    public static final String TAG = PhotoCaptureTasksFragment.class.getSimpleName();
    private static final String TASK = "task";
    private static final String USER_ID = "user_id";
    private static final String DATE = "date";
    private static final String STATUS = "status";
    private static final String GATEWAY = "gateway";

    @Bind(R.id.tasks_list)
    RecyclerView mTasksList;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Spinner mSpinner;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private View mSpinnerLayout;
    private BarcodeEditText mBarcodeEditText;

    private PhotoCaptureTasksAdapter mAdapter;
    private PhotoCaptureTasksContract.Presenter mPresenter;
    private ProgressDialog mProgressDialog;

    private OnTaskClickListener mOnTaskClickListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PhotoCaptureTasksFragment() {
    }

    /**
     * Creates new PhotoCaptureTasksFragment instance with given arguments
     *
     * @param task    Id of the "Photo Capture" task. Value is 104.
     * @param userId  UserId of the user. OR "Select All". For Photo Capture, you need to pass
     *                "Select All" and group them by Status/logged in User's ID on your end
     * @param date    Task date. This will be current date formatted as date (yyyy-mm-dd).
     *                Ex: "2015-09-02"
     * @param status  Status for filtering. To get all, you can pass "Show All". It will bring
     *                all tasks for the task Date in different statuses belonging to all users
     *                (if user = Select All or the logged in user if user = UserId) .
     *                Other values allowed are "Not Completed", "Not Started",  "In Progress",
     *                "Completed", "Canceled", "Assigned", "Not Assigned".
     * @param gateway Station name belonging to the user. Ex: "ORD"
     */
    public static PhotoCaptureTasksFragment newInstance(int task, String userId, String date,
                                                        String status, String gateway) {
        PhotoCaptureTasksFragment fragment = new PhotoCaptureTasksFragment();
        Bundle arguments = new Bundle();
        if (task < 0) arguments.putInt(TASK, task);
        if (userId != null) arguments.putString(USER_ID, userId);
        if (date != null) arguments.putString(DATE, date);
        if (status != null) arguments.putString(STATUS, status);
        if (gateway != null) arguments.putString(GATEWAY, gateway);
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * Creates new PhotoCaptureTasksFragment instance with given arguments
     *
     * @param userId UserId of the user. OR "Select All". For Photo Capture, you need to pass
     *               "Select All" and group them by Status/logged in User's ID on your end
     * @param status Status for filtering. To get all, you can pass "Show All". It will bring
     *               all tasks for the task Date in different statuses belonging to all users
     *               (if user = Select All or the logged in user if user = UserId) .
     *               Other values allowed are "Not Completed", "Not Started",  "In Progress",
     *               "Completed", "Canceled", "Assigned", "Not Assigned".
     */
    public static PhotoCaptureTasksFragment newInstance(String userId, String status) {
        return newInstance(TaskManagerContract.CARGO_PHOTO_CAPTURE, userId, null, status, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnTaskClickListener = (OnTaskClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTaskClickListener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTaskClickListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mSpinnerLayout = mToolbar.findViewById(R.id.toolbar_spinner_layout);
            mToolbarTitle = (TextView) mToolbar.findViewById(R.id.title);
            mBarcodeEditText = (BarcodeEditText) mToolbar.findViewById(R.id.barcode_edit_text);
            mBarcodeEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        }
        mPresenter = new PhotoCaptureTasksPresenter(this, mOnTaskClickListener, getArguments());
        mPresenter.onCreate(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_capture_tasks, container, false);
        ButterKnife.bind(this, view);
        mPresenter.onCreateView();
        return view;
    }

    @Override
    public void initSpinner() {
        if (mToolbar != null) mSpinner = (Spinner) mToolbar.findViewById(R.id.filter_spinner);
        if (mSpinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.spinner_tasks_values, R.layout.spinner_item_tasks);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(adapter);
        }
    }

    @Override
    public void initRecyclerView() {
        mTasksList.setLayoutManager(new NpaLinearLayoutManager(getContext()));
        mTasksList.setHasFixedSize(true);
        mTasksList.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider,
                DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        mSwipeRefreshLayout.setOnRefreshListener(() -> mPresenter.updateTasks(false));
    }

    @Override
    public void clearRecyclerView() {
        if (mAdapter != null) mAdapter.clearData();
    }

    @Override
    public void showTaskItems(List<Object> tasks) {
        if (mAdapter == null) {
            mAdapter = new PhotoCaptureTasksAdapter(tasks, mPresenter);
            mTasksList.setAdapter(mAdapter);
        } else {
            mAdapter.clearData();
            mAdapter.addTaskItems(tasks);
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(String message) {
        mProgressDialog = ProgressDialog.show(getContext(), null, message, true);
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showActionBarTitle(boolean show) {
        if (mToolbarTitle != null) mToolbarTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showSpinner(boolean show) {
        if (mSpinnerLayout != null) mSpinnerLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void restoreRecyclerViewState(Parcelable state) {
        mTasksList.getLayoutManager().onRestoreInstanceState(state);
    }

    @Override
    public void setOnSpinnerItemSelectedListener() {
        mPresenter.addSpinnerSelectedListener(RxAdapterView.itemSelections(mSpinner));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState,
                mTasksList.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mPresenter.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mPresenter.onHiddenChanged(hidden);
    }

    @Override
    public void showSwipeRefreshLayout(boolean show) {
        mSwipeRefreshLayout.setRefreshing(show);
    }

    @Override
    public void onTaskChanged(Task task) {
        mAdapter.updateTaskItem(task);
    }

    @Override
    public void setOnBarcodeTextChangeListener() {
        Observable<CharSequence> barcodeChangedObservable = RxTextView.textChanges(mBarcodeEditText);
        mPresenter.setBarcodeTextChangedObservable(barcodeChangedObservable);
    }

    @Override
    public boolean onBackPressed() {
        return mPresenter.onBackPressed();
    }
}
