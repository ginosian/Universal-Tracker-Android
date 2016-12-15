package com.margin.mgms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.margin.mgms.R;
import com.margin.mgms.adapter.DimensionsAdapter;
import com.margin.mgms.listener.RecyclerViewContentSizeChangeListener;
import com.margin.mgms.model.Dimensions;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created on May 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public class DimensionsCardFragment extends Fragment implements RecyclerViewContentSizeChangeListener {

    public static final String TAG = DimensionsCardFragment.class.getCanonicalName();
    public static final String KEY_DIMENSIONS = "key_dimensions";
    public static final String KEY_TOTAL_SHIPMENT_PIECES = "key_total_shipment_pieces";
    @Bind(R.id.card_dimension)
    ViewGroup mRoot;
    @Bind(R.id.recycler_dimensions)
    RecyclerView mDimensionsRecyclerView;
    private DimensionsAdapter mAdapter;
    private Transition mCardExpandTransition;

    public static DimensionsCardFragment newFragment(ArrayList<Dimensions> dimensions,
                                                     String totalShipmentPcs) {
        DimensionsCardFragment fr = new DimensionsCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_DIMENSIONS, dimensions);
        bundle.putString(KEY_TOTAL_SHIPMENT_PIECES, totalShipmentPcs);
        fr.setArguments(bundle);
        return fr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != getArguments()) {
            ArrayList<Dimensions> dimensions = getArguments().getParcelableArrayList(KEY_DIMENSIONS);
            if (null == dimensions) throwNoSufficientInputProvidedError();
            String totalShipmentPcs = getArguments().getString(KEY_TOTAL_SHIPMENT_PIECES);
            mAdapter = new DimensionsAdapter(dimensions, this, totalShipmentPcs);
            mCardExpandTransition = TransitionInflater.from(getActivity())
                    .inflateTransition(R.transition.card_dimensions_expand);
        } else {
            throwNoSufficientInputProvidedError();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_dimensions, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDimensionsRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mDimensionsRecyclerView.setLayoutManager(layoutManager);
        mDimensionsRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * Throws {@link IllegalArgumentException} with a meaningful message.
     */
    private void throwNoSufficientInputProvidedError() {
        throw new IllegalArgumentException("No ArrayList<" + Dimensions.class.getSimpleName() +
                "> passed to " + DimensionsCardFragment.class.getSimpleName());
    }

    @Override
    public void onSizeChanged() {
        TransitionManager.beginDelayedTransition(mRoot, mCardExpandTransition);
    }
}
