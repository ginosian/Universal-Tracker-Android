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
import com.margin.mgms.adapter.LocationsAdapter;
import com.margin.mgms.listener.RecyclerViewContentSizeChangeListener;
import com.margin.mgms.model.ShipmentLocation;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created on May 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public class LocationsCardFragment extends Fragment implements RecyclerViewContentSizeChangeListener {

    public static final String TAG = LocationsCardFragment.class.getCanonicalName();
    public static final String KEY_LOCATIONS = "key_locations";
    public static final String KEY_TOTAL_SHIPMENT_PIECES = "key_total_shipment_pieces";
    @Bind(R.id.card_location)
    ViewGroup mRoot;
    @Bind(R.id.recycler_locations)
    RecyclerView mLocationsRecyclerView;
    private LocationsAdapter mAdapter;
    private Transition mCardExpandTransition;

    public static LocationsCardFragment newFragment(ArrayList<ShipmentLocation> locations,
                                                    String totalShipmentPieces) {
        LocationsCardFragment fr = new LocationsCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_LOCATIONS, locations);
        bundle.putString(KEY_TOTAL_SHIPMENT_PIECES, totalShipmentPieces);
        fr.setArguments(bundle);
        return fr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (null != bundle) {
            if (!(bundle.containsKey(KEY_TOTAL_SHIPMENT_PIECES) && bundle.containsKey(KEY_LOCATIONS))) {
                throwNoSufficientInputProvidedError();
            }
            ArrayList<ShipmentLocation> locations = getArguments()
                    .getParcelableArrayList(KEY_LOCATIONS);

            String totalShipmentPieces = bundle.getString(KEY_TOTAL_SHIPMENT_PIECES);
            mAdapter = new LocationsAdapter(locations, this, totalShipmentPieces);
            mCardExpandTransition = TransitionInflater.from(getActivity())
                    .inflateTransition(R.transition.card_locations_expand);
        } else {
            throwNoSufficientInputProvidedError();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_locations, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLocationsRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mLocationsRecyclerView.setLayoutManager(layoutManager);
        mLocationsRecyclerView.setNestedScrollingEnabled(false);
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
        throw new IllegalArgumentException("No ArrayList<" + ShipmentLocation.class.getSimpleName() +
                "> and totalShipmentLocation passed to " + LocationsCardFragment.class.getSimpleName());
    }

    @Override
    public void onSizeChanged() {
        TransitionManager.beginDelayedTransition(mRoot, mCardExpandTransition);
    }
}
