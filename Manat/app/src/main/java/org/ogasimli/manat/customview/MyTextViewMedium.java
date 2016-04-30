package org.ogasimli.manat.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom TextView with Roboto-Medium font
 *
 * Created by Orkhan Gasimli on 29.04.2016.
 */
public class MyTextViewMedium extends TextView {

    public MyTextViewMedium(Context context) {
        super(context);
        setFont(context);
    }

    public MyTextViewMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public MyTextViewMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context);
    }

    private void setFont(Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        setTypeface(font);
    }
}
