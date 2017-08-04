package org.ogasimli.manat.ui.activity;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;

import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.ui.fragment.MainActivityFragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import manat.ogasimli.org.manat.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * MainActivity class
 *
 * Created by Orkhan Gasimli on 10.01.2016.
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_app_id));

        if (savedInstanceState == null) {
            MainActivityFragment fragment = new MainActivityFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment, Constants.MAIN_ACTIVITY_FRAGMENT_TAG)
                    .commit();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications
            String channelId = getString(R.string.default_notification_channel_id);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(
                    new NotificationChannel(channelId,
                    Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW));
        }

        // Subscribe to FCM channel
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC_NAME);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
