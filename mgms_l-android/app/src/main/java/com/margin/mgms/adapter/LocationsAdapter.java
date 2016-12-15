package com.margin.mgms.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.margin.mgms.LipstickApplication;
import com.margin.mgms.R;
import com.margin.mgms.listener.RecyclerViewContentSizeChangeListener;
import com.margin.mgms.model.ShipmentLocation;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created on May 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public class LocationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEWTYPE_HEADER = 111;
    public static final int VIEWTYPE_CONTENT = 222;
    public static final int VIEWTYPE_FOOTER = 333;
    public static final int VIEWTYPE_EMPTY = 444;

    public static final int INITIAL_LOCATIONS_COUNT = 2;
    public static final int STEP = 2;
    private final String mTotalShipmentPieces;
    private List<ShipmentLocation> mData;
    private RecyclerViewContentSizeChangeListener mListener;
    private int mCurrentStep;
    private int mStep;
    private String mLocationSubtitlePlaceholderString;
    private String mPieces;
    /**
     * Controls the visibility state of footer item.
     */
    private boolean mShowFooter;

    /**
     * Tracks if data is empty to display empty view.
     */
    private boolean mIsDataEmpty;

    /**
     * @param locations A {@link List} of {@link ShipmentLocation}s.
     * @param step      Amount of locations to be added each time "view more" is pressed.
     */
    public LocationsAdapter(List<ShipmentLocation> locations,
                            @Nullable RecyclerViewContentSizeChangeListener listener,
                            int step,
                            String totalShipmentPieces) {
        this.mData = locations;
        this.mListener = listener;
        this.mCurrentStep = INITIAL_LOCATIONS_COUNT;
        this.mStep = step;
        this.mTotalShipmentPieces = totalShipmentPieces;

        mIsDataEmpty = mData.size() == 0;
        mShowFooter = mData.size() > INITIAL_LOCATIONS_COUNT;
        this.mCurrentStep = INITIAL_LOCATIONS_COUNT + 1 /*header*/ + (mShowFooter ? 1 : 0) /*footer*/;
        Context context = LipstickApplication.getAppComponent().getAppContext();
        mLocationSubtitlePlaceholderString = context.getString(R.string.placeholder_location_subtitle);
        mPieces = context.getString(R.string.pieces).toLowerCase();
    }

    /**
     * Creates locations adapter with step sized {@value STEP}.
     *
     * @param locations A {@link List} of {@link ShipmentLocation}s.
     */
    public LocationsAdapter(List<ShipmentLocation> locations,
                            @Nullable RecyclerViewContentSizeChangeListener listener,
                            String totalShipmentPieces) {
        this(locations, listener, STEP, totalShipmentPieces);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_location_header, parent, false));
            case VIEWTYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_location_footer, parent, false));
            case VIEWTYPE_CONTENT:
                return new ContentViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_location_content, parent, false));
            case VIEWTYPE_EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.empty, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEWTYPE_CONTENT:
                setupContent((ContentViewHolder) holder, position);
                break;
            case VIEWTYPE_FOOTER:
                setupFooter((FooterViewHolder) holder);
                break;
            case VIEWTYPE_EMPTY:
                setupEmpty((EmptyViewHolder) holder);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 1 && mData.size() == 0) return VIEWTYPE_EMPTY;
        else if (position == 0) return VIEWTYPE_HEADER;
        else if (position == getItemCount() - 1 && mShowFooter) return VIEWTYPE_FOOTER;
        return VIEWTYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return canIncrement() ? mCurrentStep : mData.size() + 1 + (mShowFooter ? 1 : 0) +
                (mIsDataEmpty ? 1 : 0) /* empty view */;
    }

    private void setupContent(ContentViewHolder holder, int position) {
        ShipmentLocation location = mData.get(position - 1);

        String subTitleText = String.format(mLocationSubtitlePlaceholderString,
                location.pieces(), mTotalShipmentPieces) + mPieces;

        holder.title.setText(location.location());
        holder.subtitle.setText(subTitleText);
    }

    private void setupFooter(FooterViewHolder holder) {
        holder.divider.setVisibility(mShowFooter ? View.VISIBLE : View.GONE);
        holder.viewMoreTextView.setVisibility(mShowFooter ? View.VISIBLE : View.GONE);
        holder.bottomSpace.setVisibility(mShowFooter ? View.GONE : View.VISIBLE);

        holder.viewMoreTextView.setOnClickListener(v -> {
            if (canIncrement()) {
                boolean isIncremented = increment();
                if (isIncremented) {
                    notifyDataSetChanged();
                    if (!canIncrement()) hideFooterView();
                    if (null != mListener) {
                        mListener.onSizeChanged();
                    }
                }
            }
        });
    }

    private void setupEmpty(EmptyViewHolder holder) {
        holder.emptyText.setText(LipstickApplication.getAppComponent().getAppContext()
                .getString(R.string.empty_no_locations_recorded));
    }

    public void hideFooterView() {
        mShowFooter = false;
    }

    /**
     * Increments shown items quantity by {@link ContentViewHolder#mStep}.
     *
     * @return False if all items were been shown, true if there were items left not shown.
     */
    public boolean increment() {
        if (canIncrement()) {
            mCurrentStep += mStep;
            return true;
        }
        return false;
    }

    /**
     * @return True if shown items quantity is less than adapter's real size.
     */
    public boolean canIncrement() {
        return mCurrentStep < mData.size() + 1 + (mShowFooter ? 1 : 0);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {

        public
        @Bind(R.id.divider)
        View divider;
        public
        @Bind(R.id.view_more)
        TextView viewMoreTextView;
        public
        @Bind(R.id.bottom_space)
        Space bottomSpace;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {

        public
        @Bind(R.id.location_title)
        TextView title;
        public
        @Bind(R.id.location_subtitle)
        TextView subtitle;

        public ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public
        @Bind(R.id.empty_text)
        TextView emptyText;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
