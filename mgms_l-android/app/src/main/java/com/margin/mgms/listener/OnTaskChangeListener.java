package com.margin.mgms.listener;

import com.margin.mgms.model.Task;

/**
 * Created on Jun 14, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnTaskChangeListener {

    /**
     * Performs an action when {@link Task} has been changed
     */
    void onTaskChanged(Task task);
}
