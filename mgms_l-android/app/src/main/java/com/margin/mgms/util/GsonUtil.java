package com.margin.mgms.util;

import com.google.gson.Gson;

/**
 * Created on Jul 29, 2016.
 *
 * @author Marta.Ginosyan
 */
public class GsonUtil {

    private static Gson sGson;

    private GsonUtil() {
    }

    public static Gson getGson() {
        if (sGson == null) {
            sGson = new Gson();
        }
        return sGson;
    }
}
