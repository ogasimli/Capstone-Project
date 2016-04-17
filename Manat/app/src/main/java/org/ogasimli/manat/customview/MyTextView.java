package org.ogasimli.manat.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ogasimli on 10.01.2016.
 */
public class MyTextView extends TextView {


    public MyTextView(Context context) {
        super(context);
        setFont(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context);
    }

    private void setFont(Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        setTypeface(font);
    }
}
