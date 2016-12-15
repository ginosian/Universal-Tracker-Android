package com.margin.mgms.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.margin.mgms.LipstickApplication;

/**
 * Created on May 20, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PrefsUtils {

    public static final String KEY_TOKEN = "key_token";
    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_USERNAME = "key_username";
    public static final String KEY_DEFAULT_GATEWAY = "key_default_gateway";
    public static final String KEY_EMAIL = "key_email";

    private static final SharedPreferences PREFS = PreferenceManager
            .getDefaultSharedPreferences(LipstickApplication.getAppComponent().getAppContext());

    private PrefsUtils() {
    }

    /**
     * Saves the token {@link com.margin.mgms.model.AccessToken.Data#mId id} and
     * {@link com.margin.mgms.model.AccessToken.Data#mUserId userId} in default shared preferences.
     */
    public static void putAuthData(String token, String userId) {
        PREFS.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    /**
     * Saves {@code userName}, {@code defaultGateway} and {@code email} in default shared preferences.
     */
    public static void putUserData(String userName, String defaultGateway, String email) {
        PREFS.edit()
                .putString(KEY_USERNAME, userName)
                .putString(KEY_DEFAULT_GATEWAY, defaultGateway)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    /**
     * Determines if there is a signed in user by checking whether {@link SharedPreferences}
     * contains non-empty values with keys {@code KEY_USERNAME} and {@code KEY_TOKEN}.
     * <p>
     * Assumes that these keys are removed from {@link SharedPreferences} by using
     * {@link #removeUser()} when the user is logged out.
     *
     * @return True - if there is signed in user. False otherwise.
     */
    public static boolean isUserSignedIn() {
        return PREFS.contains(KEY_USERNAME) && PREFS.contains(KEY_TOKEN)
                && !TextUtils.isEmpty(PREFS.getString(KEY_USERNAME, null))
                && !TextUtils.isEmpty(PREFS.getString(KEY_TOKEN, null))
                && !TextUtils.isEmpty(PREFS.getString(KEY_EMAIL, null));
    }

    /**
     * Get the authentication token needed for requests.
     */
    @SuppressWarnings("unused")
    public static String getToken() {
        return PREFS.getString(KEY_TOKEN, null);
    }

    /**
     * Get the signed in user's unique user-friendly username.
     */
    public static String getUsername() {
        return PREFS.getString(KEY_USERNAME, null);
    }

    /**
     * Removes currently signed in user's data.
     */
    @SuppressWarnings("unused")
    public static void removeUser() {
        PREFS.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USER_ID)
                .remove(KEY_USERNAME)
                .remove(KEY_DEFAULT_GATEWAY)
                .apply();
    }

    /**
     * @return Value associated with key {@value KEY_DEFAULT_GATEWAY} in default shared
     * preferences.
     */
    public static String getDefaultGateway() {
        return PREFS.getString(KEY_DEFAULT_GATEWAY, null);
    }

    /**
     * @return Value associated with key {@value KEY_EMAIL} in default shared
     * preferences.
     */
    @Nullable
    public static String getEmail() {
        return PREFS.getString(KEY_EMAIL, null);
    }

}
