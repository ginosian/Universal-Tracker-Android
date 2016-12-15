package com.margin.mgms.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.margin.mgms.R;
import com.margin.mgms.model.Contact;
import com.margin.mgms.model.SpecialHandling;

import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created on June 02, 2016.
 *
 * @author Marta.Ginosyan
 */
public class SummaryCardFragment extends Fragment {

    public static final String TAG = SummaryCardFragment.class.getSimpleName();

    public static final String KEY_SPECIAL_HANDLING = "key_special_handling";
    public static final String KEY_SENDER = "key_sender";
    public static final String KEY_RECEIVER = "key_receiver";
    public static final String KEY_TOTAL_PIECES = "key_total_pieces";
    public static final String KEY_WEIGHT = "key_weight";
    public static final String KEY_WEIGHT_UNITS = "key_weight_units";
    public static final String KEY_REFERENCE_NUM = "key_reference_num";
    @Bind(R.id.summary_header_title)
    TextView mHeaderTitle;
    @Bind(R.id.summary_header_subtitle)
    TextView mHeaderSubtitle;
    @Bind(R.id.summary_sender_container)
    ViewGroup mSenderContainer;
    @Bind(R.id.summary_sender_title)
    TextView mSenderTitle;
    @Bind(R.id.summary_receiver_container)
    ViewGroup mReceiverContainer;
    @Bind(R.id.summary_receiver_title)
    TextView mReceiverTitle;
    @Bind(R.id.divider)
    View mDivider;
    @Bind(R.id.container_special_handling)
    LinearLayout mSpecialHandlingContainer;
    @BindString(R.string.placeholder_summary_subtitle)
    String mHeaderSubtitlePlaceholder;
    @Nullable
    private SpecialHandling mSpecialHandling;
    @Nullable
    private Contact mSender, mReceiver;
    private int mTotalPieces;
    private float mWeight;
    private String mWeightUnits;
    private String mReferenceNum;

    public static SummaryCardFragment newFragment(@Nullable SpecialHandling specialHandling,
                                                  @Nullable Contact sender,
                                                  @Nullable Contact receiver,
                                                  int totalPieces,
                                                  float weight,
                                                  String weightUnits,
                                                  String referenceNum) {

        SummaryCardFragment fragment = new SummaryCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SPECIAL_HANDLING, specialHandling);
        bundle.putParcelable(KEY_SENDER, sender);
        bundle.putParcelable(KEY_RECEIVER, receiver);
        bundle.putInt(KEY_TOTAL_PIECES, totalPieces);
        bundle.putFloat(KEY_WEIGHT, weight);
        bundle.putString(KEY_WEIGHT_UNITS, weightUnits);
        bundle.putString(KEY_REFERENCE_NUM, referenceNum);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SummaryCardFragment newFragment(@Nullable SpecialHandling specialHandling,
                                                  @Nullable Contact sender,
                                                  @Nullable Contact receiver,
                                                  String totalPieces,
                                                  String weight,
                                                  String weightUnits,
                                                  String referenceNum) throws NumberFormatException {

        return newFragment(specialHandling, sender, receiver, Integer.parseInt(totalPieces),
                Float.parseFloat(weight), weightUnits, referenceNum);
    }

    public static SummaryCardFragment newFragment(@Nullable SpecialHandling specialHandling,
                                                  String totalPieces,
                                                  String weight,
                                                  String weightUnits,
                                                  String referenceNum) {

        return newFragment(specialHandling, null, null, totalPieces,
                weight, weightUnits, referenceNum);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (null != arguments && containsNecessaryData(arguments)) {
            mSpecialHandling = arguments.getParcelable(KEY_SPECIAL_HANDLING);
            mSender = arguments.getParcelable(KEY_SENDER);
            mReceiver = arguments.getParcelable(KEY_RECEIVER);
            mTotalPieces = arguments.getInt(KEY_TOTAL_PIECES);
            mWeight = arguments.getFloat(KEY_WEIGHT);
            mWeightUnits = arguments.getString(KEY_WEIGHT_UNITS);
            mReferenceNum = arguments.getString(KEY_REFERENCE_NUM);
        } else throwNoSufficientInputProvidedError();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.card_summary, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeaderTitle.setText(mReferenceNum);

        String subtitle = String.format(Locale.getDefault(), mHeaderSubtitlePlaceholder,
                mTotalPieces, mWeight, mWeightUnits).toLowerCase();
        mHeaderSubtitle.setText(subtitle);

        if (null == mSender) mSenderContainer.setVisibility(View.GONE);
        else mSenderTitle.setText(mSender.getName());

        if (null == mReceiver) mReceiverContainer.setVisibility(View.GONE);
        else mReceiverTitle.setText(mReceiver.getName());

        List<Integer> mSpecialHandlingIcons = getSpecialIcons();
        if (null != mSpecialHandlingIcons && !mSpecialHandlingIcons.isEmpty()) {
            for (Integer drawableId : mSpecialHandlingIcons) {
                addSpecialHandlingView(getContext(), drawableId, mSpecialHandlingContainer);
            }
        } else {
            mDivider.setVisibility(View.GONE);
            mSpecialHandlingContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public List<Integer> getSpecialIcons() {
        if (null != mSpecialHandling) {
            return mSpecialHandling.getSpecialIcons();
        }
        return null;
    }

    /**
     * Adds specialHandlingView into the special handling container
     *
     * @param context    needed for getting resources and view inflating
     * @param drawableId drawable resource id for image
     * @param parent     special handling container
     */
    private void addSpecialHandlingView(Context context, @DrawableRes int drawableId,
                                        ViewGroup parent) {
        int margin = (int) context.getResources().getDimension(R.dimen.spacing_tiny);
        int imageSize = (int) context.getResources().getDimension(R.dimen.spacing_large);
        ImageView icon = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
        params.setMargins(parent.getChildCount() > 0 ? margin : 0, margin, margin, margin);
        icon.setLayoutParams(params);
        icon.setImageDrawable(context.getDrawable(drawableId));
        parent.addView(icon);
    }


    /**
     * Checks whether bundle contains the data, that is being provided via
     * {@link SummaryCardFragment#newFragment(SpecialHandling, Contact, Contact, int, float,
     * String, String)}.
     *
     * @return True if bundle contains keys with necessary input. False otherwise.
     */
    private boolean containsNecessaryData(Bundle bundle) {
        return /*bundle.containsKey(KEY_SPECIAL_HANDLING) &&*/ /* might not be */
                bundle.containsKey(KEY_SENDER) &&
                        bundle.containsKey(KEY_RECEIVER) &&
                        bundle.containsKey(KEY_TOTAL_PIECES) &&
                        bundle.containsKey(KEY_WEIGHT) &&
                        bundle.containsKey(KEY_WEIGHT_UNITS) &&
                        bundle.containsKey(KEY_REFERENCE_NUM);
    }

    /**
     * Throws {@link IllegalArgumentException} with a meaningful message.
     */
    private void throwNoSufficientInputProvidedError() {
        throw new IllegalArgumentException("Instantiate an instance of "
                + SummaryCardFragment.class.getSimpleName() + " with newInstance() static method, " +
                "providing necessary input.");
    }
}
