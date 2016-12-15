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
import com.margin.mgms.adapter.UldAdapter;
import com.margin.mgms.listener.RecyclerViewContentSizeChangeListener;
import com.margin.mgms.model.Uld;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created on June 10, 2016.
 *
 * @author Marta.Ginosyan
 */
public class UldCardFragment extends Fragment implements RecyclerViewContentSizeChangeListener {

    public static final String TAG = UldCardFragment.class.getCanonicalName();
    public static final String KEY_ULDS = "key_ulds";
    @Bind(R.id.card_uld)
    ViewGroup mRoot;
    @Bind(R.id.recycler_ulds)
    RecyclerView mRecyclerView;
    private UldAdapter mAdapter;
    private Transition mCardExpandTransition;

    public static UldCardFragment newFragment(@Nullable ArrayList<Uld> ulds) {
        UldCardFragment fr = new UldCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_ULDS, ulds);
        fr.setArguments(bundle);
        return fr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (null != bundle) {
            ArrayList<Uld> ulds = bundle.getParcelableArrayList(KEY_ULDS);

            mAdapter = new UldAdapter(null == ulds ? new ArrayList<>() : ulds, this);
            mCardExpandTransition = TransitionInflater.from(getActivity())
                    .inflateTransition(R.transition.card_locations_expand);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_ulds, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onSizeChanged() {
        TransitionManager.beginDelayedTransition(mRoot, mCardExpandTransition);
    }

}
