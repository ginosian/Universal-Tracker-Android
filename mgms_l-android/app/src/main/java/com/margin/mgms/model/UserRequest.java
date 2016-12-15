package com.margin.mgms.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Jul 29, 2016.
 *
 * @author Marta.Ginosyan
 */
public class UserRequest {

    @SerializedName("filter")
    private String mFilter;
    @SerializedName("access_token")
    private String mAccessToken;

    public UserRequest(@Nullable String filter, @NonNull String accessToken) {
        mFilter = filter;
        mAccessToken = accessToken;
    }
}
