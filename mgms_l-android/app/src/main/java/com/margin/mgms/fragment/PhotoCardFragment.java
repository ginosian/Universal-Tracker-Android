package com.margin.mgms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.margin.camera.models.Photo;
import com.margin.mgms.R;
import com.margin.mgms.activity.MgmsPhotoGalleryActivity;
import com.margin.mgms.adapter.PhotosAdapter;
import com.margin.mgms.database.DatabaseConnector;
import com.margin.mgms.listener.OnPhotoClickListener;
import com.margin.mgms.listener.RecyclerViewContentSizeChangeListener;
import com.margin.mgms.model.Task;
import com.margin.mgms.mvp.task_manager.TaskManagerContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.ButterKnife;

import static com.margin.mgms.activity.ConditionReportActivity.KEY_REFERENCE;

/**
 * Created on May 13, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoCardFragment extends Fragment implements RecyclerViewContentSizeChangeListener,
        OnPhotoClickListener {

    public static final String TAG = PhotoCardFragment.class.getCanonicalName();
    public static final String KEY_PHOTOS = "key_photos";
    @Bind(R.id.card_photos)
    ViewGroup mRoot;
    @Bind(R.id.recycler_images)
    RecyclerView mPhotosRecyclerView;
    @BindInt(R.integer.num_columns)
    int mNumColumns;
    @BindString(R.string.empty_no_photos_capture)
    String mEmptyText;
    private PhotosAdapter mAdapter;
    private Transition mCardExpandTransition;
    private List<Photo> mPhotos;
    private String mReference;

    public static PhotoCardFragment newFragment(ArrayList<Photo> photos, String reference) {
        PhotoCardFragment fr = new PhotoCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_PHOTOS, photos);
        bundle.putString(KEY_REFERENCE, reference);
        fr.setArguments(bundle);
        return fr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != getArguments()) {
            mPhotos = getArguments().getParcelableArrayList(KEY_PHOTOS);
            if (null == mPhotos) throwNoSufficientInputProvidedError();
            mReference = getArguments().getString(KEY_REFERENCE);
            mAdapter = new PhotosAdapter(mPhotos, this, this,
                    getResources().getInteger(R.integer.num_columns));
            mCardExpandTransition = TransitionInflater.from(getActivity())
                    .inflateTransition(R.transition.card_expand);

        } else throwNoSufficientInputProvidedError();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_photo, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPhotosRecyclerView.setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), mNumColumns);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = mAdapter.getItemViewType(position);
                return viewType == PhotosAdapter.VIEWTYPE_HEADER ||
                        viewType == PhotosAdapter.VIEWTYPE_FOOTER ||
                        viewType == PhotosAdapter.VIEWTYPE_EMPTY
                        ? layoutManager.getSpanCount() : 1;
            }
        });
        mPhotosRecyclerView.setLayoutManager(layoutManager);
        mPhotosRecyclerView.setNestedScrollingEnabled(false);
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
        throw new IllegalArgumentException("No ArrayList<" + Photo.class.getSimpleName() +
                "> passed to " + PhotoCardFragment.class.getSimpleName());
    }

    @Override
    public void onSizeChanged() {
        TransitionManager.beginDelayedTransition(mRoot, mCardExpandTransition);
    }

    @Override
    public void onPhotoClicked(int position) {
        DatabaseConnector.getInstance().getTask(TaskManagerContract.CARGO_PHOTO_CAPTURE,
                mReference).subscribe(task -> {
            boolean completed = task.getStatus() == Task.Status.Completed;
            MgmsPhotoGalleryActivity.start(getContext(), MgmsPhotoGalleryActivity.class,
                    mPhotos, position, mReference, completed, !completed);
        });
    }
}
