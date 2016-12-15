package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AuthenticateRequest {
    @SerializedName("pin")
    public String pin;
}
