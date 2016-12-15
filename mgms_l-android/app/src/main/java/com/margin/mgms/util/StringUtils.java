package com.margin.mgms.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.Locale;

/**
 * Created on May 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public class StringUtils {

    private static final String SPLIT_COMMA_PATTERN = "\\s*,\\s*";
    private static String[] EMPTY_STRING_ARRAY = new String[]{};

    /**
     * Formats float values to look nice.
     * <pre>
     *     232.00000000       232
     *     0.18000000000      0.18
     *     1237875192.0       1237875192
     *     4.5800000000       4.58
     *     0.00000000         0
     *     1.23450000         1.2345
     *     </pre>
     *
     * @see <a href="http://stackoverflow.com/a/14126736/1083957">StackOverflow post</a>
     */
    public static String format(float d) {
        if (d == (long) d) return String.format(Locale.getDefault(), "%d", (long) d);
        else return String.format("%s", d);
    }

    public static String format(double d) {
        return format((float) d);
    }

    /**
     * Validates email addresses
     *
     * @see Patterns
     */
    public static boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Splits a comma-separated string into string array
     */
    @NonNull
    public static String[] splitWithComma(String text) {
        if (TextUtils.isEmpty(text)) return EMPTY_STRING_ARRAY;
        else return text.trim().split(SPLIT_COMMA_PATTERN);
    }
}
