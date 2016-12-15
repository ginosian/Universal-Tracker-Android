package com.margin.mgms.adapter;

import android.support.annotation.Nullable;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.margin.camera.models.Photo;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.R;
import com.margin.mgms.listener.OnPhotoClickListener;
import com.margin.mgms.listener.RecyclerViewContentSizeChangeListener;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on May 12, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEWTYPE_HEADER = 111;
    public static final int VIEWTYPE_CONTENT = 222;
    public static final int VIEWTYPE_FOOTER = 333;
    public static final int VIEWTYPE_EMPTY = 444;

    public static final int INITIAL_IMAGES_COUNT = 6;

    private final List<Photo> mData;
    private final RequestManager mGlide;
    public int mStep;
    private RecyclerViewContentSizeChangeListener mListener;
    private OnPhotoClickListener mPhotoClickListener;
    private int mCurrentStep;
    /**
     * Controls the visibility state of footer item.
     */
    private boolean mShowFooter;

    /**
     * Tracks if data is empty to display empty view.
     */
    private boolean mIsDataEmpty;

    /**
     * @param photoList A {@link List} of {@link Photo}s.
     * @param step      Amount of photos to be added each time "view more" is pressed.
     */
    public PhotosAdapter(List<Photo> photoList,
                         @Nullable RecyclerViewContentSizeChangeListener listener,
                         OnPhotoClickListener photoClickListener, int step) {
        this.mData = photoList;
        this.mListener = listener;
        this.mStep = step;
        mPhotoClickListener = photoClickListener;

        mIsDataEmpty = mData.size() == 0;
        mGlide = LipstickApplication.getAppComponent().getGlide();
        mShowFooter = mData.size() > INITIAL_IMAGES_COUNT;
        mCurrentStep = INITIAL_IMAGES_COUNT + 1 /*header*/ + (mShowFooter ? 1 : 0) /*footer*/;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_photo_header, parent, false));
            case VIEWTYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_photo_footer, parent, false));
            case VIEWTYPE_CONTENT:
                return new ContentViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_photo_content, parent, false), mPhotoClickListener);
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
    public int getItemCount() {
        return canIncrement() ? mCurrentStep : mData.size() + 1 + (mShowFooter ? 1 : 0) +
                (mIsDataEmpty ? 1 : 0) /* empty view */;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 1 && mData.size() == 0) return VIEWTYPE_EMPTY;
        else if (position == 0) return VIEWTYPE_HEADER;
        else if (position == getItemCount() - 1 && mShowFooter) return VIEWTYPE_FOOTER;
        return VIEWTYPE_CONTENT;
    }

    private void setupContent(ContentViewHolder holder, int position) {
        Photo photo = mData.get(position - 1);

        if (!TextUtils.isEmpty(photo.url())) {
            // Load the image from the specified URL
            mGlide.load(photo.url()).centerCrop().into(holder.photo);
        } else {
            File file = new File(photo.image_path());
            if (file.exists()) {
                // Load the image on the device if the path exists
                mGlide.load(file).centerCrop().into(holder.photo);
            }
        }
    }

    private void setupFooter(FooterViewHolder holder) {
        holder.divider.setVisibility(mShowFooter ? View.VISIBLE : View.GONE);
        holder.viewMoreTextView.setVisibility(mShowFooter ? View.VISIBLE : View.GONE);
        holder.bottomSpace.setVisibility(mShowFooter ? View.GONE : View.VISIBLE);

        if (mShowFooter) {
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
    }

    private void setupEmpty(EmptyViewHolder holder) {
        holder.emptyText.setText(LipstickApplication.getAppComponent().getAppContext()
                .getString(R.string.empty_no_photos_capture));
    }

    public void hideFooterView() {
        mShowFooter = false;
    }

    /**
     * Increments shown items quantity by {@link PhotosAdapter#mStep}.
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

    /**
     * Returns all photos
     */
    public List<Photo> getItems() {
        return mData;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.divider)
        View divider;
        @Bind(R.id.view_more)
        TextView viewMoreTextView;
        @Bind(R.id.bottom_space)
        Space bottomSpace;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.photo_iv)
        ImageView photo;
        private OnPhotoClickListener mListener;

        public ContentViewHolder(View itemView, OnPhotoClickListener listener) {
            super(itemView);
            mListener = listener;
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.photo_iv})
        void onClick(View v) {
            if (mListener != null && getAdapterPosition() > 0) {
                mListener.onPhotoClicked(getAdapterPosition() - 1);
            }
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.empty_text)
        TextView emptyText;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
