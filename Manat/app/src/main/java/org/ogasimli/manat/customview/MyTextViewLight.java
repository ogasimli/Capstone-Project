package org.ogasimli.manat.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom TextView with Roboto_light font
 *
 * Created by Orkhan Gasimli on 10.01.2016.
 */
public class MyTextViewLight extends TextView {

    public MyTextViewLight(Context context) {
        super(context);
        setFont(context);
    }

    public MyTextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public MyTextViewLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context);
    }

    private void setFont(Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        setTypeface(font);
    }
}
