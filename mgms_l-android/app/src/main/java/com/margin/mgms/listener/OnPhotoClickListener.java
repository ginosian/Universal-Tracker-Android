package com.margin.mgms.listener;

/**
 * Created on Jul 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnPhotoClickListener {

    /**
     * Performs an action when photo was clicked
     *
     * @param position position of photo in the data list
     */
    void onPhotoClicked(int position);
}
