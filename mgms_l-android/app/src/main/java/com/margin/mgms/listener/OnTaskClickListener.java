package com.margin.mgms.listener;

import com.margin.mgms.model.Task;

/**
 * Created on Jun 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnTaskClickListener {
    /**
     * Performs actions when task item has been clicked
     */
    void onTaskItemClicked(Task task);
}
