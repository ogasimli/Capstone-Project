package org.ogasimli.manat;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

import manat.ogasimli.org.manat.R;

/**
 * Application class
 *
 * Created by Orkhan Gasimli on 16.04.2016.
 */
public class ManatApplication extends Application {

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
            mTracker.setSessionTimeout(300);
            mTracker.enableAdvertisingIdCollection(true);
        }
        return mTracker;
    }
}
