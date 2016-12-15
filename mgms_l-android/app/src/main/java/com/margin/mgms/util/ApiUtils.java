package com.margin.mgms.util;

import android.text.TextUtils;

import static com.margin.mgms.rest.StrongLoopApi.API_VERSION;
import static com.margin.mgms.rest.StrongLoopApi.ENDPOINT;

/**
 * Created on June 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public class ApiUtils {

    private ApiUtils() {
    }

    /**
     * Construct legitimate url out, appending domain name.
     *
     * @param url e.g.: PhotoCapture/photo?image=SHA-6FB7668-ORD_115604&index=697366&gateway=ORD
     */
    public static String buildImageUrl(String url) {
        if (!TextUtils.isEmpty(url) && !url.contains(ENDPOINT) && !url.contains(API_VERSION)) {
            return ENDPOINT + API_VERSION + "/" + url;
        }
        return url;
    }
}
