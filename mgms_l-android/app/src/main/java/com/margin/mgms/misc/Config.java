package com.margin.mgms.misc;

import android.content.Context;
import android.os.Environment;

import com.margin.mgms.LipstickApplication;
import com.margin.mgms.R;

import java.util.Calendar;

/**
 * Created on May 16, 2016.
 *
 * @author Marta.Ginosyan
 */
@SuppressWarnings("unused")
public class Config {
    // General configuration

    // Is this a dogfood build?
    public static final boolean IS_DOGFOOD_BUILD = true;

    // Warning messages for dogfood build
    public static final String DOGFOOD_BUILD_WARNING_TEXT = "This is a test build.";

    /**
     * The root directory, where captured images are stored.
     */
    public static final String PATH_IMAGES = Environment
            .getExternalStorageDirectory().toString() + "/Lipstick";

    public static String DATE_STRING_TEST = "2015-09-04";

    static {
        Calendar c = Calendar.getInstance();
        Context context = LipstickApplication.getAppComponent().getAppContext();
        DATE_STRING_TEST = context.getString(R.string.value_config_date_format,
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }
}
