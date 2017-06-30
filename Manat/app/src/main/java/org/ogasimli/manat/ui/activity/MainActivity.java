package org.ogasimli.manat.ui.activity;

import com.google.android.gms.ads.MobileAds;

import org.ogasimli.manat.ui.fragment.MainActivityFragment;
import org.ogasimli.manat.helper.Constants;

import android.content.Context;
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
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
