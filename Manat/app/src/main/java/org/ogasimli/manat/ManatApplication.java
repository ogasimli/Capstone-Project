package org.ogasimli.manat;

import android.app.Application;

import manat.ogasimli.org.manat.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Base Application class
 *
 * Created by Orkhan Gasimli on 09.07.2016.
 */

public class ManatApplication extends Application {

    public static String globalSelectedDate;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
