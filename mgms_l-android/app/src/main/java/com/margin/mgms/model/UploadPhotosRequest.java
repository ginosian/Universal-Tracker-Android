package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;
import com.margin.camera.models.Photo;

import java.util.List;

/**
 * Created on Jun 10, 2016.
 *
 * @author Marta.Ginosyan
 */
public class UploadPhotosRequest {

    @SerializedName("user")
    public String user;
    @SerializedName("gateway")
    public String gateway;
    @SerializedName("reference")
    public String reference;
    @SerializedName("reason")
    public String reason;
    @SerializedName("photos")
    public List<Photo> photos;
    @SerializedName("byname")
    public boolean byname;
}
