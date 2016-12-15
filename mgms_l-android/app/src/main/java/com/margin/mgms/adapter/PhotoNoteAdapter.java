package com.margin.mgms.adapter;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.margin.camera.models.Note;
import com.margin.camera.models.Photo;
import com.margin.components.views.FlowLayout;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.R;
import com.margin.mgms.listener.OnClearButtonClickListener;
import com.margin.mgms.listener.OnPhotoClickListener;
import com.margin.mgms.util.DateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on May 13, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoNoteAdapter extends RecyclerView.Adapter<PhotoNoteAdapter.PhotoViewHolder> {

    private static final int SEVERITY_BARRIER_1 = 33;
    private static final int SEVERITY_BARRIER_2 = 66;

    private final ArrayList<Photo> mData;
    private final ArrayMap<Photo, Collection<Note>> mFilteredMap;
    private final RequestManager mGlide;
    private OnPhotoClickListener mListener;

    private boolean mShowClearButton;
    private OnClearButtonClickListener mOnClearButtonClickListener;

    public PhotoNoteAdapter(@NonNull ArrayList<Photo> data) {
        this(data, false, null, null);
    }

    public PhotoNoteAdapter(@NonNull ArrayList<Photo> data, boolean showClearButton,
                            OnClearButtonClickListener onClearButtonClickListener,
                            OnPhotoClickListener listener) {
        mData = data;
        mFilteredMap = new ArrayMap<>(data.size());
        mShowClearButton = showClearButton;
        mOnClearButtonClickListener = onClearButtonClickListener;
        mListener = listener;
        mGlide = LipstickApplication.getAppComponent().getGlide();
        filterNotesForSeverity(mData);
    }

    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_condition_report, parent, false);
        return new PhotoViewHolder(view, mListener, mOnClearButtonClickListener);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Photo photo = mFilteredMap.keyAt(mFilteredMap.indexOfKey(mData.get(position)));
        holder.clearButton.setVisibility(mShowClearButton ? View.VISIBLE : View.GONE);

        if (!TextUtils.isEmpty(photo.url())) {
            // Load the image from the specified URL
            mGlide.load(photo.url()).centerCrop().into(holder.photo);
        } else {
            @SuppressWarnings("ConstantConditions")
            File file = new File(photo.image_path());
            if (file.exists()) {
                // Load the image on the device if the path exists
                mGlide.load(file).centerCrop().into(holder.photo);
            }
        }

        boolean isLocationCodeValid = !TextUtils.isEmpty(photo.location_code());
        if (isLocationCodeValid) {
            holder.location.setVisibility(View.VISIBLE);
            holder.location.setText(photo.location_code());
        } else {
            holder.location.setVisibility(View.GONE);
        }

        holder.name.setText(photo.username());
        holder.date.setText(DateUtils.getFormattedDate(photo.create_date()));

        if (TextUtils.isEmpty(photo.comment()))
            holder.comment.setVisibility(View.GONE);
        else holder.comment.setText(photo.comment());

        if (mFilteredMap.get(photo).size() == 0) {
            holder.flowLayout.setVisibility(View.GONE);
        } else {
            holder.flowLayout.setVisibility(View.VISIBLE);
            for (Note note : mFilteredMap.get(photo)) {
                addNoteView(holder.flowLayout, note);
            }
        }
    }

    private void addNoteView(ViewGroup parent, Note note) {
        View noteView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_note, parent, false);
        TextView severityTextView = (TextView) noteView.findViewById(R.id.note_title);
        severityTextView.setText(note.type());
        int severity = note.severity();
        ImageView bg = (ImageView) noteView.findViewById(R.id.bg);
        if (severity >= 0 && severity < SEVERITY_BARRIER_1) {
            bg.setImageResource(R.drawable.shape_severity_low);
        } else if (severity >= SEVERITY_BARRIER_1 && severity < SEVERITY_BARRIER_2) {
            bg.setImageResource(R.drawable.shape_severity_normal);
        } else if (severity >= SEVERITY_BARRIER_2) {
            bg.setImageResource(R.drawable.shape_severity_high);
        }
        parent.addView(noteView);
    }

    @Override
    public void onViewRecycled(PhotoViewHolder holder) {
        super.onViewRecycled(holder);
        holder.flowLayout.removeAllViews();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * Filters {@link Note}s with similar type leaving the {@link Note} with higher severity value.
     */
    private void filterNotesForSeverity(List<Photo> photos) {
        Map<String, Note> severityMap;
        String type;

        if (null != photos && photos.size() >= 1) {
            for (Photo photo : photos) {
                severityMap = new ArrayMap<>();
                for (Note note : photo.getNotes()) {

                    type = note.type();
                    if (severityMap.containsKey(type)) {
                        if (severityMap.get(type).severity() < note.severity()) {
                            severityMap.put(type, note);
                        }
                    } else {
                        severityMap.put(type, note);
                    }
                }
                mFilteredMap.put(photo, severityMap.values());
            }
        }
    }

    /**
     * Removes the item with particular position
     */
    public void remove(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * @return The collection, with which the adapter is backed by.
     */
    public ArrayList<Photo> getCurrentPhotos() {
        return mData;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image_condition_report)
        ImageView photo;
        @Bind(R.id.bt_clear)
        ImageView clearButton;
        @Bind(R.id.location)
        TextView location;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.date)
        TextView date;
        @Bind(R.id.comment)
        TextView comment;
        @Bind(R.id.flow_layout)
        FlowLayout flowLayout;
        private OnPhotoClickListener mListener;
        private OnClearButtonClickListener mOnClearButtonClickListener;

        public PhotoViewHolder(View itemView, OnPhotoClickListener listener,
                               OnClearButtonClickListener clearButtonClickListener) {
            super(itemView);
            mListener = listener;
            mOnClearButtonClickListener = clearButtonClickListener;
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.image_condition_report})
        void onClick(View v) {
            if (mListener != null) {
                mListener.onPhotoClicked(getAdapterPosition());
            }
        }

        @OnClick({R.id.bt_clear})
        public void OnClick(View v) {
            if (mOnClearButtonClickListener != null) {
                mOnClearButtonClickListener.onClearButtonClicked(getAdapterPosition());
            }
        }
    }
}
