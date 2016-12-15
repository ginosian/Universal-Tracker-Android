package com.margin.mgms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.margin.camera.models.Note;
import com.margin.camera.models.Photo;
import com.margin.components.views.FlowLayout;
import com.margin.mgms.R;
import com.margin.mgms.activity.ConditionReportActivity;

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
public class DamagesCardFragment extends Fragment {

    public static final String TAG = DamagesCardFragment.class.getCanonicalName();
    public static final String KEY_PHOTOS = "key_photos";
    public static final String KEY_REFERENCE = "key_reference";
    private static final int SEVERITY_BARRIER_1 = 33;
    private static final int SEVERITY_BARRIER_2 = 66;
    @Bind(R.id.damages_content)
    View mContentRoot;
    @Bind(R.id.viewstub_empty_damages)
    ViewStub mEmptyViewStub;
    @Bind(R.id.flow_layout)
    FlowLayout mFlowLayout;
    @Bind(R.id.view_full_report)
    View mViewFullReport;
    private ArrayList<Photo> mPhotos;
    private Collection<Note> mSeverityTypeCollection;
    private String mReferenceNumber;

    /**
     * Constructs an instance of {@link DamagesCardFragment}, passing {@code photo} via bundle.
     *
     * @return The constructed instance of {@link DamagesCardFragment}.
     */
    public static DamagesCardFragment newFragmentFromPhotos(ArrayList<Photo> photos,
                                                            String referenceNumber) {
        DamagesCardFragment fr = new DamagesCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_PHOTOS, photos);
        bundle.putString(KEY_REFERENCE, referenceNumber);
        fr.setArguments(bundle);
        return fr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != getArguments()) {
            mReferenceNumber = getArguments().getString(KEY_REFERENCE);
            if (getArguments().containsKey(KEY_PHOTOS)) {
                mPhotos = getArguments().getParcelableArrayList(KEY_PHOTOS);
                if (null == mPhotos) throwNoSufficientInputProvidedError();
            } else {
                throwNoSufficientInputProvidedError();
            }

            mSeverityTypeCollection = filterPhotosForSeverity(mPhotos);
        } else {
            throwNoSufficientInputProvidedError();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.card_damages, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!mSeverityTypeCollection.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            int severity;
            for (Note note : mSeverityTypeCollection) {
                View noteView = inflater.inflate(R.layout.row_note, mFlowLayout, false);
                TextView severityTextView = (TextView) noteView.findViewById(R.id.note_title);
                severityTextView.setText(note.type());
                severity = note.severity();
                ImageView bg = (ImageView) noteView.findViewById(R.id.bg);
                if (severity >= 0 && severity < SEVERITY_BARRIER_1) {
                    bg.setImageResource(R.drawable.shape_severity_low);
                } else if (severity >= SEVERITY_BARRIER_1 && severity < SEVERITY_BARRIER_2) {
                    bg.setImageResource(R.drawable.shape_severity_normal);
                } else if (severity >= SEVERITY_BARRIER_2) {
                    bg.setImageResource(R.drawable.shape_severity_high);
                }
                mFlowLayout.addView(noteView);
            }
        } else {
            TextView emptyTextView = (TextView) mEmptyViewStub.inflate().findViewById(R.id.empty_text);
            emptyTextView.setText(getString(R.string.empty_no_damages_recorded));
            mContentRoot.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.view_full_report})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.view_full_report:
                ConditionReportActivity.launch(getContext(), mPhotos, mReferenceNumber);
                break;
        }
    }

    /**
     * Filters {@link Note}s with similar type leaving the {@link Note} with higher severity value.
     */
    private Collection<Note> filterPhotosForSeverity(List<Photo> photos) {
        Map<String, Note> severityMap = new ArrayMap<>();
        String type;

        for (Photo photo : photos) {
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
        }
        return severityMap.values();
    }

    /**
     * Throws {@link IllegalArgumentException} with a meaningful message.
     */
    private void throwNoSufficientInputProvidedError() {
        throw new IllegalArgumentException(DamagesCardFragment.class.getCanonicalName() + " should " +
                "have as input either an ArrayList<" + Note.class.getSimpleName() +
                "> or " + Photo.class.getCanonicalName() + ". None was provided");
    }

}

