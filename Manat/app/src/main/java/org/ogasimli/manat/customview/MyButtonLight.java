package org.ogasimli.manat.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Custom Button with Roboto_light font
 *
 * Created by Orkhan Gasimli on 29.04.2016.
 */
public class MyButtonLight extends Button {
    public MyButtonLight(Context context) {
        super(context);
        setFont(context);
    }

    public MyButtonLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public MyButtonLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context);
    }

    private void setFont(Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        setTypeface(font);
    }
}
