package com.margin.mgms.misc;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * No Predictive Animations LinearLayoutManager
 * <p>
 * Created on Jul 19, 2016.
 *
 * @author Marta.Ginosyan
 */
public class NpaLinearLayoutManager extends LinearLayoutManager {

    public NpaLinearLayoutManager(Context context) {
        super(context);
    }

    public NpaLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NpaLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
