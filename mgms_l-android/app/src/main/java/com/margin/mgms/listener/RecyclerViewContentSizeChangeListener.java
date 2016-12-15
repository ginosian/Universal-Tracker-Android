package com.margin.mgms.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created on May 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface RecyclerViewContentSizeChangeListener {
    /**
     * Called when the amount of items being shown by the {@link RecyclerView} has been changed.
     */
    void onSizeChanged();
}
