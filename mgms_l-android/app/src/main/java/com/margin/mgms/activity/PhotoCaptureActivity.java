package com.margin.mgms.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.margin.barcode.views.BarcodeEditText;
import com.margin.camera.activities.AnnotationPhotoActivity;
import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Photo;
import com.margin.components.fragments.BackHandledFragment;
import com.margin.components.utils.ImeUtils;
import com.margin.mgms.R;
import com.margin.mgms.fragment.HouseBillFragment;
import com.margin.mgms.fragment.MasterBillFragment;
import com.margin.mgms.fragment.PhotoCaptureTasksFragment;
import com.margin.mgms.listener.AirWaybillConnector;
import com.margin.mgms.listener.OnTaskChangeListener;
import com.margin.mgms.listener.OnTaskClickListener;
import com.margin.mgms.model.BarcodeModel;
import com.margin.mgms.model.ShipmentLocation;
import com.margin.mgms.model.SpecialHandling;
import com.margin.mgms.model.Task;
import com.margin.mgms.mvp.details.housebill.HouseBillContract;
import com.margin.mgms.mvp.details.mawb.MasterBillContract;
import com.margin.mgms.mvp.photo_capture.PhotoCaptureContract;
import com.margin.mgms.mvp.photo_capture.PhotoCapturePresenter;
import com.margin.mgms.rest.StrongLoopApi;
import com.margin.mgms.util.LogUtils;
import com.margin.mgms.util.PrefsUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on May 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoCaptureActivity extends AppCompatActivity implements PhotoCaptureContract.View,
        BackHandledFragment.IBackPressedHandler, OnTaskClickListener {

    private static final int MENU_GROUP_TASK_MANAGEMENT = R.id.task_management_group;
    private static final int MENU_GROUP_DETAILS = R.id.details_group;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.toolbar_spinner_layout)
    View mSpinnerLayout;
    @Bind(R.id.status_spinner_layout)
    ViewGroup mStatusSpinnerLayout;
    @Bind(R.id.status_spinner_icon)
    ImageView mStatusSpinnerIcon;
    @Bind(R.id.status_spinner)
    TextView mStatusSpinner;
    @Bind(R.id.status_spinner_date)
    TextView mStatusSpinnerDate;
    @Bind(R.id.fab_photo)
    FloatingActionButton mCameraFab;
    @Bind(R.id.fab_add)
    FloatingActionButton mAddFab;
    @Bind(R.id.barcode_edit_text)
    BarcodeEditText mBarcodeEditText;
    @BindString(R.string.title_completed_by)
    String mCompletedBy;
    @BindString(R.string.message_task_not_exist)
    String mTaskNotExist;
    @BindString(R.string.message_shipment_not_exist)
    String mShipmentNotExist;
    @BindString(R.string.message_task_prompt_create)
    String mTaskPromptCreate;
    @BindString(R.string.message_shipment_prompt_create)
    String mShipmentPromptCreate;
    @BindString(R.string.title_create)
    String mCreate;
    @BindString(android.R.string.cancel)
    String mCancel;
    @BindString(R.string.error_empty_string)
    String mEmpty;
    @BindDimen(R.dimen.z_toolbar)
    int mPopupWindowElevation;
    ProgressDialog mProgressDialog;
    private EditText mOriginEditText;
    private EditText mDestinationEditText;
    private EditText mPiecesEditText;

    private PhotoCaptureContract.Presenter mPresenter;
    private Menu mMenu;
    private MenuItem mCompleteItem;
    private MenuItem mShareItem;
    private BackHandledFragment mSelectedFragment;

    /**
     * Launch PhotoCaptureActivity with given arguments
     */
    public static void launch(Context context) {
        Intent intent = new Intent(context, PhotoCaptureActivity.class);
        intent.putExtra(PhotoCaptureContract.KEY_GATEWAY, PrefsUtils.getDefaultGateway());
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_capture, menu);
        mMenu = menu;
        mCompleteItem = menu.findItem(R.id.action_complete);
        mShareItem = menu.findItem(R.id.action_share);
        mPresenter.onCreateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                mPresenter.onSearchButtonClicked();
                break;
            case R.id.action_complete:
                mPresenter.onCompleteButtonClicked();
                break;
            case R.id.action_share:
                mPresenter.onShareButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);
        ButterKnife.bind(this);
        mPresenter = new PhotoCapturePresenter(this, getIntent());
        mPresenter.onCreate();
        mBarcodeEditText.setOnBarcodeNumberListener(this);
        mBarcodeEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                mPresenter.onBarcodeDoneListener();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    protected void onPause() {
        if (isFinishing()) mPresenter.onDestroy();
        super.onPause();
    }

    @Override
    public void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setSubtitle(R.string.subtitle_tasks);
                mToolbar.setNavigationIcon(R.drawable.navigation_back_icon);
                actionBar.setDisplayShowTitleEnabled(false);
                mToolbar.setNavigationOnClickListener(v -> {
                    if (mSelectedFragment == null || !mSelectedFragment.onBackPressed()) {
                        // Selected fragment did not consume the back press event.
                        mPresenter.onBackPressed();
                    }
                });
            }
        }
    }

    @Override
    public void showStatusSpinnerLayout(boolean show) {
        mStatusSpinnerLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setStatusSpinnerIcon(@DrawableRes int drawable) {
        mStatusSpinnerIcon.setImageResource(drawable);
    }

    @Override
    public void setStatusSpinnerDate(String text) {
        mStatusSpinnerDate.setText(text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mPresenter.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void setActionBarTitle(String title) {
        if (mToolbarTitle != null) mToolbarTitle.setText(title);
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
    public void showBarcodeEditText(boolean show) {
        if (mBarcodeEditText != null) {
            mBarcodeEditText.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void showTasksFragment() {
        Fragment fragmentTasks = getSupportFragmentManager().findFragmentByTag(
                PhotoCaptureContract.FRAGMENT_TASKS);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragmentTasks == null) {
            fragmentTransaction.add(R.id.container, PhotoCaptureTasksFragment.newInstance(
                    StrongLoopApi.SELECT_ALL, Task.Status.All.toString()),
                    PhotoCaptureContract.FRAGMENT_TASKS);
        } else fragmentTransaction.show(fragmentTasks);
        fragmentTransaction.commit();
    }

    @Override
    public void showTaskManagementMenuGroup(boolean show) {
        if (mMenu != null) mMenu.setGroupVisible(MENU_GROUP_TASK_MANAGEMENT, show);
    }

    @Override
    public void showDetailsMenuGroup(boolean show) {
        if (mMenu != null) mMenu.setGroupVisible(MENU_GROUP_DETAILS, show);
    }

    @Override
    public void showMawbDetailsFragment(SpecialHandling handling, List<ShipmentLocation> locations,
                                        String weightUnit, String reference, String carrier,
                                        String mawbNum, String gateway) {
        LogUtils.i(reference);
        MasterBillFragment fragment = MasterBillFragment.newInstance(
                handling, locations, weightUnit, reference, carrier, mawbNum, gateway);
        getSupportFragmentManager()
                .beginTransaction()
                .hide(getSupportFragmentManager().findFragmentByTag(
                        PhotoCaptureContract.FRAGMENT_TASKS))
                .add(R.id.container, fragment, PhotoCaptureContract.FRAGMENT_DETAILS)
                .addToBackStack(PhotoCaptureContract.FRAGMENT_DETAILS).commit();
    }

    @Override
    public void showHawbDetailsFragment(SpecialHandling handling, String reference,
                                        String hawbNum, String gateway) {
        LogUtils.i(reference);
        HouseBillFragment fragment = HouseBillFragment.newInstance(
                handling, reference, hawbNum, gateway);
        getSupportFragmentManager()
                .beginTransaction()
                .hide(getSupportFragmentManager().findFragmentByTag(
                        PhotoCaptureContract.FRAGMENT_TASKS))
                .add(R.id.container, fragment, PhotoCaptureContract.FRAGMENT_DETAILS)
                .addToBackStack(PhotoCaptureContract.FRAGMENT_DETAILS).commit();
    }

    @Override
    public void hideDetailsFragment() {
        getSupportFragmentManager().popBackStackImmediate(PhotoCaptureContract.FRAGMENT_DETAILS,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean isDetailsFragmentVisible() {
        return getSupportFragmentManager()
                .findFragmentByTag(PhotoCaptureContract.FRAGMENT_DETAILS) != null;
    }

    @Override
    public void onTaskItemClicked(Task task) {
        mPresenter.onTaskItemClicked(task);
    }

    @Override
    public void setBarcodeEditTextFocus(boolean isFocus) {
        if (mBarcodeEditText != null) {
            if (isFocus) mBarcodeEditText.requestFocus();
            else mBarcodeEditText.clearFocus();
        }
    }

    @Override
    public boolean isBarcodeEditTextVisible() {
        return mBarcodeEditText != null && mBarcodeEditText.getVisibility() == View.VISIBLE;
    }

    @Override
    public void showKeyboard(boolean show) {
        if (show) ImeUtils.showIme(mBarcodeEditText);
        else ImeUtils.hideIme(mBarcodeEditText);
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        mSelectedFragment = backHandledFragment;
    }

    @Override
    public void onBackPressed() {
        if (mSelectedFragment == null || !mSelectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            mPresenter.onBackPressed();
        }
    }

    @Override
    public void showProgress(boolean show, @Nullable String message) {
        if (show) {
            mProgressDialog = ProgressDialog.show(this, null, null != message ? message : "", true,
                    true, dialog -> onBackPressed());
            mProgressDialog.setCanceledOnTouchOutside(false);
        } else if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.fab_photo})
    public void onCameraFabClick() {
        mPresenter.onCameraFabClick(getSupportFragmentManager());
    }

    @OnClick({R.id.fab_add})
    public void onAddFabClick() {
        mPresenter.onAddButtonClicked();
    }

    @Override
    public void showCamera(int requestCode, int entityId, String path,
                           Map<String, String> properties, Collection<AnnotationType> types) {
        AnnotationPhotoActivity.startActivityForResult(this, requestCode, entityId,
                path, properties, types);
    }

    @Override
    public void addPhotoToChildFragment(Photo photo) {
        Fragment fr = getSupportFragmentManager().findFragmentByTag(PhotoCaptureContract.FRAGMENT_DETAILS);
        if (null != fr && (fr instanceof AirWaybillConnector)) {
            AirWaybillConnector connector = (AirWaybillConnector) fr;
            connector.providesPhoto(photo);
        }
    }

    @Override
    public void setStatusSpinnerClickable(boolean clickable) {
        mStatusSpinnerLayout.setClickable(clickable);
    }

    @Override
    public void setStatusSpinnerTitle(String title) {
        mStatusSpinner.setText(title);
    }

    @Override
    public void showFab(boolean show) {
        mCameraFab.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateBarcodeEditText(String text) {
        mBarcodeEditText.setText(text);
    }

    @Override
    public void showCreateTaskDialog(String reference, String barcode, String origin,
                                     String destination, boolean isHawb) {
        new AlertDialog.Builder(this)
                .setTitle(mTaskNotExist)
                .setMessage(String.format(mTaskPromptCreate, reference))
                .setNegativeButton(mCancel, null)
                .setPositiveButton(mCreate, (dialog, which) ->
                        mPresenter.onCreateTaskButtonClicked(barcode, origin, destination, isHawb))
                .create()
                .show();
    }

    @Override
    public void showCreateShipmentDialog(BarcodeModel barcodeModel, boolean isHawb) {
        View view = View.inflate(this, R.layout.dialog_create_task, null);
        mOriginEditText = (EditText) view.findViewById(R.id.origin);
        mDestinationEditText = (EditText) view.findViewById(R.id.destination);
        mPiecesEditText = (EditText) view.findViewById(R.id.pieces);
        String number = isHawb ? barcodeModel.getHawbNum() : barcodeModel.getMawbNum();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(mShipmentNotExist)
                .setMessage(String.format(mShipmentPromptCreate, number))
                .setNegativeButton(mCancel, null)
                .setPositiveButton(mCreate, (dialog, which) -> {
                })
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (mPresenter.onCreateShipmentButtonClicked(mOriginEditText.getText().toString(),
                    mDestinationEditText.getText().toString(), barcodeModel.getCarrierNum(),
                    number, mPiecesEditText.getText().toString(), isHawb)) alertDialog.dismiss();
        });
    }

    @Override
    public String getSearchBarText() {
        return mBarcodeEditText.getText().toString();
    }

    @Override
    public void setErrorToOrigin(boolean show) {
        setEmptyErrorToEditText(mOriginEditText, show);
    }

    @Override
    public void setErrorToDestination(boolean show) {
        setEmptyErrorToEditText(mDestinationEditText, show);
    }

    @Override
    public void setErrorToPieces(boolean show) {
        setEmptyErrorToEditText(mPiecesEditText, show);
    }

    private void setEmptyErrorToEditText(EditText editText, boolean show) {
        if (editText != null) {
            if (show) editText.setError(mEmpty);
            else editText.setError(null);
        }
    }

    @Override
    public void showAddButton(boolean show) {
        mAddFab.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showCompleteButton(boolean visible) {
        if (mCompleteItem != null) mCompleteItem.setVisible(visible);
    }

    @Override
    public void showShareButton(boolean visible) {
        if (mShareItem != null) mShareItem.setVisible(visible);
    }

    @Override
    public void showSharePhotosScreen() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                PhotoCaptureContract.FRAGMENT_DETAILS);
        if (fragment instanceof HouseBillContract.View) {
            ((HouseBillContract.View) fragment).onShareButtonPressed();
        } else if (fragment instanceof MasterBillContract.View) {
            ((MasterBillContract.View) fragment).onShareButtonPressed();
        }
    }

    @Override
    public void refreshPhotos() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                PhotoCaptureContract.FRAGMENT_DETAILS);
        if (fragment instanceof AirWaybillConnector) {
            ((AirWaybillConnector) fragment).updatePhotos();
        }
    }

    @Override
    public void showPhotosUploadFailedError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskChanged(Task task) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                PhotoCaptureContract.FRAGMENT_TASKS);
        if (fragment instanceof OnTaskChangeListener) {
            OnTaskChangeListener listener = (OnTaskChangeListener) fragment;
            listener.onTaskChanged(task);
        }
    }

    @Override
    public void onBarcodeReceived(String barcode) {
        mPresenter.onBarcodeReceived(barcode);
    }
}
