package com.margin.mgms.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.margin.camera.models.Photo;
import com.margin.mgms.R;
import com.margin.mgms.activity.SharePhotosActivity;
import com.margin.mgms.listener.AirWaybillConnector;
import com.margin.mgms.model.SpecialHandling;
import com.margin.mgms.mvp.details.housebill.HouseBillContract;
import com.margin.mgms.mvp.details.housebill.HouseBillPresenter;
import com.margin.mgms.mvp.photo_capture.PhotoCaptureContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created on May 25, 2016.
 *
 * @author Marta.Ginosyan
 */
public class HouseBillFragment extends Fragment implements HouseBillContract.View, AirWaybillConnector {

    public static final String TAG = HouseBillFragment.class.getSimpleName();
    @Bind(R.id.cards_root)
    LinearLayout mCardRoot;
    private TextView mToolbarTitle;
    private View mSpinnerLayout;
    private ProgressDialog mProgressDialog;
    private HouseBillContract.Presenter mPresenter;

    /**
     * Creates a new HouseBillFragment instance with the given arguments.
     *
     * @param specialHandling {@link SpecialHandling} for this house bill shipment
     * @param reference       The reference number for this shipment
     * @param hawb            The house bill number for this shipment
     */

    public static HouseBillFragment newInstance(@NonNull SpecialHandling specialHandling,
                                                @NonNull String reference, @NonNull String hawb,
                                                @NonNull String gateway) {
        HouseBillFragment fr = new HouseBillFragment();
        Bundle b = new Bundle();
        b.putParcelable(HouseBillContract.KEY_SPECIAL_HANDLING, specialHandling);
        b.putString(HouseBillContract.KEY_REFERENCE, reference);
        b.putString(HouseBillContract.KEY_HAWB, hawb);
        b.putString(HouseBillContract.KEY_GATEWAY, gateway);
        fr.setArguments(b);
        return fr;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        View toolbar = getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            mSpinnerLayout = toolbar.findViewById(R.id.toolbar_spinner_layout);
            mToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        }
        mPresenter = new HouseBillPresenter(this, getArguments());
        mPresenter.start();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    public void onDestroyView() {
        mPresenter.finish();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void setActionBarTitle(String title) {
        if (mToolbarTitle != null) mToolbarTitle.setText(title);
    }

    @Override
    public void addOrReplaceCard(Fragment fragment, String tag) {
        Fragment fr = getChildFragmentManager().findFragmentByTag(tag);
        if (null == fr) {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.cards_root, fragment, tag).commitAllowingStateLoss();
        } else {
            getChildFragmentManager().beginTransaction()
                    .remove(fr)
                    .add(R.id.cards_root, fragment, tag).commitAllowingStateLoss();
        }
    }

    @Override
    public void showProgress(boolean show, @StringRes int message) {
        if (show) {
            mProgressDialog = ProgressDialog.show(getContext(), null, getString(message), true,
                    true, dialog -> getActivity().onBackPressed());
            mProgressDialog.setCanceledOnTouchOutside(false);
        } else if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onShareButtonPressed() {
        mPresenter.onShareButtonPressed();
    }

    @Override
    public void showSharePhotosScreen(String referenceNumber, List<Photo> photos, String gateway) {
        SharePhotosActivity.launch(getContext(), referenceNumber, new ArrayList<>(photos), gateway);
    }

    @Override
    public void hideProgressDialog() {
        if (getActivity() instanceof PhotoCaptureContract.View) {
            ((PhotoCaptureContract.View) getActivity()).showProgress(false, null);
        }
    }

    @Override
    public void updatePhotos() {
        mPresenter.fetchPhotos();
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
    public Map<String, String> providesProperties() {
        return mPresenter.provideProperties();
    }

    @Override
    public void providesPhoto(Photo photo) {
        mPresenter.addPhotoAndUpdate(photo);
    }

}
