package com.margin.mgms.listener;

import com.margin.camera.models.Photo;

import java.util.Map;

/**
 * Created on June 09, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface AirWaybillConnector {

    /**
     * Provides a {@link Map} of properties for input to photo capture widget.
     */
    Map<String, String> providesProperties();

    /**
     * Provides {@code photo} to detail fragment.
     */
    void providesPhoto(Photo photo);

    /**
     * Refreshes details screen with photos
     */
    void updatePhotos();
}
