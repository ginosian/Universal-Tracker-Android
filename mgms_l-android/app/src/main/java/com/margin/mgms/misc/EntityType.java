package com.margin.mgms.misc;

import android.support.annotation.StringDef;

import com.margin.mgms.rest.StrongLoopApi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on May 27, 2016.
 *
 * @author Marta.Ginosyan
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({
        StrongLoopApi.ENTITY_TYPE_HAWB, StrongLoopApi.ENTITY_TYPE_MAWB
})
public @interface EntityType {
}