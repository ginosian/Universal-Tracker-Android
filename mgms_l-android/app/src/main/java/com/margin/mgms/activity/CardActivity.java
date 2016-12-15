package com.margin.mgms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.margin.camera.models.Photo;
import com.margin.components.utils.FragmentUtils;
import com.margin.mgms.R;
import com.margin.mgms.fragment.ContactCardFragment;
import com.margin.mgms.fragment.DamagesCardFragment;
import com.margin.mgms.fragment.DimensionsCardFragment;
import com.margin.mgms.fragment.LocationsCardFragment;
import com.margin.mgms.fragment.SummaryCardFragment;
import com.margin.mgms.fragment.UldCardFragment;
import com.margin.mgms.model.Contact;
import com.margin.mgms.model.Dimensions;
import com.margin.mgms.model.PhotoMock;
import com.margin.mgms.model.ShipmentLocation;
import com.margin.mgms.model.SpecialHandling;
import com.margin.mgms.model.Uld;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created on May 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public class CardActivity extends AppCompatActivity {

    private static final String KEY_CARD_TYPE = "key_card_type";
    private static final String KEY_PHOTOS = "key_photos";
    /**
     * For testing only.
     * <p>
     * TODO: remove later
     */
    private static final String INCHES = "INCHES";
    private static final Object[][] MOCK_DIMENSIONS_DATA = new Object[][]{
            {1.1f, 1.2f, 1.3f, INCHES, 31, 51},
            {2.1f, 2.2f, 2.3f, INCHES, 32, 52},
            {3.1f, 3.2f, 3.3f, INCHES, 33, 53},
            {4.1f, 4.2f, 4.3f, INCHES, 34, 54},
            {5.1f, 5.2f, 5.3f, INCHES, 35, 55},
            {6f, 6f, 6f, INCHES, 36, 56},
            {7.10f, 7.20f, 7.30f, INCHES, 37, 57},
            {8.1000f, 8.2000f, 8.3000f, INCHES, 38, 58},
            {09.1f, 09.2f, 09.3f, INCHES, 39, 59},
            {10.1f, 10.2f, 10.3f, INCHES, 40, 60},
            {11.1f, 11.2f, 11.3f, INCHES, 41, 66},
            {12.1f, 12.2f, 12.3f, INCHES, 42, 62}
    };
    private static final Object[][] MOCK_LOCATION_DATA = new Object[][]{
            {"AAA", "TYPE1", "1", 10},
            {"BBB", "TYPE2", "2", 20},
            {"CCC", "TYPE3", "3", 30},
            {"DDD", "TYPE4", "4", 40},
            {"EEE", "TYPE5", "5", 50},
            {"FFF", "TYPE6", "6", 60},
            {"GGG", "TYPE7", "7", 70},
            {"HHH", "TYPE8", "8", 80},
            {"III", "TYPE9", "9", 90},
            {"JJJ", "TYPE10", "10", 100},
    };
    private static final Contact CONTACT;
    private static final ArrayList<Uld> ULD_ARRAY_LIST;
    private static ArrayList<Dimensions> MOCK_DIMENSION_LIST;
    private static ArrayList<ShipmentLocation> MOCK_LOCATION_LIST;

    static {
        MOCK_DIMENSION_LIST = new ArrayList<>(MOCK_DIMENSIONS_DATA.length);

        for (Object[] mockData : MOCK_DIMENSIONS_DATA) {
            Dimensions dimension = new Dimensions();
            dimension.setLength((float) mockData[0]);
            dimension.setWidth((float) mockData[1]);
            dimension.setHeight((float) mockData[2]);
            dimension.setUnits((String) mockData[3]);
            dimension.setNumPieces((int) mockData[4]);
            dimension.setTotalPieces((int) mockData[5]);
            MOCK_DIMENSION_LIST.add(dimension);
        }
    }

    static {
        MOCK_LOCATION_LIST = new ArrayList<>(MOCK_LOCATION_DATA.length);

        for (Object[] mockData : MOCK_LOCATION_DATA) {
            ShipmentLocation location = new ShipmentLocation();
            location.setLocation((String) mockData[0]);
            location.setNumPieces((String) mockData[2]);
            MOCK_LOCATION_LIST.add(location);
        }
    }

    static {
        CONTACT = new Contact();
        CONTACT.setName("Supreme Logistixly");
        CONTACT.setCountry("United States");
        CONTACT.setCity("San Francisco");
        CONTACT.setState("CA");
        CONTACT.setPostalCode("901245");
        CONTACT.setAddress("1056 Edgles Cres");
    }

    static {
        ULD_ARRAY_LIST = new ArrayList<>();
        Uld uld;
        for (int i = 0; i < 5; ++i) {
            uld = new Uld();
            uld.setUldNum("uldNum " + (i + 1));
            uld.setUld("uld " + (i + 1));
            ULD_ARRAY_LIST.add(uld);
        }
    }

    public static void launch(Context context, CardType cardType) {
        Intent intent = new Intent(context, CardActivity.class);
        Bundle b = new Bundle();
        b.putString(KEY_CARD_TYPE, cardType.name());
        intent.putExtras(b);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        ButterKnife.bind(this);

        CardType cardType = null;
        if (null != getIntent().getExtras()) {
            String cardTypeString = getIntent().getExtras().getString(KEY_CARD_TYPE);
            cardType = CardType.valueOf(cardTypeString);
        }

        if (null == savedInstanceState && null != cardType) {
            ArrayList<Photo> photos = getIntent().getParcelableArrayListExtra(KEY_PHOTOS);
            switch (cardType) {

                case DIMENSION:
                    FragmentUtils.addRootFragment(this, R.id.content_frame,
                            DimensionsCardFragment.newFragment(MOCK_DIMENSION_LIST, "4"),
                            DimensionsCardFragment.TAG);
                    break;
                case LOCATIONS:
                    FragmentUtils.addRootFragment(this, R.id.content_frame,
                            LocationsCardFragment.newFragment(MOCK_LOCATION_LIST, "5MOCK"),
//                            LocationsCardFragment.newFragment(new ArrayList<>()),
                            LocationsCardFragment.TAG);
                    break;
                case DAMAGES:
                    FragmentUtils.addRootFragment(this, R.id.content_frame,
                            DamagesCardFragment.newFragmentFromPhotos(photos != null ?
                                    photos : PhotoMock.PHOTOS_LIST, null), DamagesCardFragment.TAG);
                    break;
                case CONTACT:
                    FragmentUtils.addRootFragment(this, R.id.content_frame,
                            ContactCardFragment.newFragment(CONTACT, "Sender"),
                            ContactCardFragment.TAG);
                    break;
                case SUMMARY:
                    //TODO: mocking
                    Contact sender = new Contact();
                    Contact receiver = new Contact();
                    sender.setName("SIEMENS AG");
                    receiver.setName("SIEMENS INDUSTRY, INC.");
                    SpecialHandling specialHandling = new SpecialHandling();
                    specialHandling.setExpedite(true);
                    specialHandling.setOverage(true);
                    specialHandling.setScreeningStatus(SpecialHandling.ScreeningStatus.Completed);
                    FragmentUtils.addRootFragment(this, R.id.content_frame,
                            SummaryCardFragment.newFragment(specialHandling, sender, receiver,
                                    10, (float) 170.2500, "kg", "SHA-6FB7668-ORD"),
                            SummaryCardFragment.TAG);
                    break;

                case ULD:
                    FragmentUtils.addRootFragment(this, R.id.content_frame,
                            UldCardFragment.newFragment(ULD_ARRAY_LIST),
                            UldCardFragment.TAG);
                    break;

            }
        }
    }

    public enum CardType {DIMENSION, LOCATIONS, DAMAGES, CONTACT, SUMMARY, ULD}

}
