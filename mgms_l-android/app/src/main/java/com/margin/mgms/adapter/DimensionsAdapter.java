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
import com.margin.mgms.model.Dimensions;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.margin.mgms.util.StringUtils.format;

/**
 * Created on May 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public class DimensionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEWTYPE_HEADER = 111;
    public static final int VIEWTYPE_CONTENT = 222;
    public static final int VIEWTYPE_FOOTER = 333;

    public static final int INITIAL_DIMENSIONS_COUNT = 2;
    public static final int STEP = 2;

    private List<Dimensions> mData;
    private RecyclerViewContentSizeChangeListener mRecyclerViewContentSizeChangeListener;
    private int mCurrentStep;
    private int mStep;
    private String mDimensionTitlePlaceholderString;
    private String mDimensionSubtitlePlaceholderString;
    private String mInches;
    private String mPieces;
    /**
     * Controls the visibility state of footer item.
     */
    private boolean mShowFooter;
    private String mTotalShipmentPcs;

    /**
     * @param dimensions A {@link List} of {@link Dimensions}s.
     * @param step       Amount of dimensions to be added each time "view more" is pressed.
     */
    public DimensionsAdapter(List<Dimensions> dimensions,
                             @Nullable RecyclerViewContentSizeChangeListener listener,
                             int step, String totalShipmentPcs) {
        this.mData = dimensions;
        this.mRecyclerViewContentSizeChangeListener = listener;
        this.mStep = step;

        mShowFooter = mData.size() > INITIAL_DIMENSIONS_COUNT;
        this.mCurrentStep = INITIAL_DIMENSIONS_COUNT + 1 /*header*/ + (mShowFooter ? 1 : 0) /*footer*/;
        Context context = LipstickApplication.getAppComponent().getAppContext();
        mDimensionTitlePlaceholderString = context.getString(R.string.placeholder_dimension_title);
        mDimensionSubtitlePlaceholderString = context
                .getString(R.string.placeholder_dimension_subtitle);
        mInches = context.getString(R.string.inches);
        mPieces = context.getString(R.string.pieces).toLowerCase();
        this.mTotalShipmentPcs = totalShipmentPcs;
    }

    /**
     * Creates dimensions adapter with step sized {@value STEP}.
     *
     * @param dimensions A {@link List} of {@link Dimensions}s.
     */
    public DimensionsAdapter(List<Dimensions> dimensions,
                             @Nullable RecyclerViewContentSizeChangeListener listener,
                             String totalShipmentPcs) {
        this(dimensions, listener, STEP, totalShipmentPcs);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_dimensions_header, parent, false));
            case VIEWTYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_dimensions_footer, parent, false));
            case VIEWTYPE_CONTENT:
                return new ContentViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_dimensions_content, parent, false));
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
        }
    }

    @Override
    public int getItemCount() {
        return canIncrement() ? mCurrentStep : mData.size() + 1 + (mShowFooter ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEWTYPE_HEADER;
        else if (position == getItemCount() - 1 && mShowFooter) return VIEWTYPE_FOOTER;
        return VIEWTYPE_CONTENT;
    }

    private void setupContent(ContentViewHolder holder, int position) {
        Dimensions dimension = mData.get(position - 1);

        String titleText = String.format(mDimensionTitlePlaceholderString,
                format(dimension.getLength()),
                format(dimension.getWidth()),
                format(dimension.getHeight())) + mInches;
        String subtitleText = String.format(mDimensionSubtitlePlaceholderString,
                dimension.getNumPieces(),
                mTotalShipmentPcs) + mPieces;

        holder.title.setText(titleText);
        holder.subtitle.setText(subtitleText);
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
                    if (null != mRecyclerViewContentSizeChangeListener) {
                        mRecyclerViewContentSizeChangeListener.onSizeChanged();
                    }
                }
            }
        });
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
        @Bind(R.id.dimension_title)
        TextView title;
        public
        @Bind(R.id.dimension_subtitle)
        TextView subtitle;

        public ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
