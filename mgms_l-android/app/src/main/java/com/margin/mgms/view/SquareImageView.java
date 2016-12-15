package com.margin.mgms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An extension of {@link ImageView}, that makes view's height be equal to it's width.
 * Particularly usable in a row of grid.
 * * <p>
 * Created on May 19, 2016.
 *
 * @author Marta.Ginosyan
 */
public class SquareImageView extends ImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

}