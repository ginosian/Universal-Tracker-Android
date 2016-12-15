package com.margin.mgms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.margin.mgms.R;
import com.margin.mgms.model.Contact;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created on May 19, 2016.
 *
 * @author Marta.Ginosyan
 */
public class ContactCardFragment extends Fragment {

    public static final String TAG = ContactCardFragment.class.getSimpleName();
    public static final String KEY_CONTACT = "key_contact";
    public static final String KEY_POSITION = "key_position";
    @Bind(R.id.contact_title)
    TextView mTitle;
    @Bind(R.id.contact_subtitle)
    TextView mSubtitle;
    @Bind(R.id.contact_info)
    TextView mInfo;
    @BindString(R.string.empty_no_location_specified)
    String mNoLocationSpecified;
    private Contact mContact;
    private String mPosition;

    /**
     * @param contactTitle The text, that would be should as a subtitle in the card
     *                     (e.g.: Sender, Receiver).
     */
    public static ContactCardFragment newFragment(Contact contact, String contactTitle) {
        ContactCardFragment fragment = new ContactCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_CONTACT, contact);
        bundle.putString(KEY_POSITION, contactTitle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (null != arguments && arguments.containsKey(KEY_CONTACT)) {
            mContact = arguments.getParcelable(KEY_CONTACT);
            mPosition = arguments.getString(KEY_POSITION);
        } else throwNoSufficientInputProvidedError();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_contact, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitle.setText(mContact.getName());
        mSubtitle.setText(mPosition);

        String newLine = "\n";
        String emptyString = "";

        String info = mContact.getAddress() + newLine;
        info += mContact.getCity();
        info += mContact.getState() != null ? " " + mContact.getState() : emptyString;
        info += ", " + mContact.getPostalCode() + newLine;
        info += mContact.getCountry();
        if (TextUtils.isEmpty(info.replaceAll("[,\\s\\r\\n]", ""))) {
            mInfo.setText(mNoLocationSpecified);
        } else mInfo.setText(info);
    }

    /**
     * Throws {@link IllegalArgumentException} with a meaningful message.
     */
    private void throwNoSufficientInputProvidedError() {
        throw new IllegalArgumentException(ContactCardFragment.class.getCanonicalName() + " should " +
                "have as input " + Contact.class.getSimpleName());
    }

}
