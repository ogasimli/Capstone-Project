package org.ogasimli.manat.activity;

import org.ogasimli.manat.fragment.MainActivityFragment;
import org.ogasimli.manat.helper.Constants;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import manat.ogasimli.org.manat.R;

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

        if (savedInstanceState == null) {
            MainActivityFragment fragment = new MainActivityFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment, Constants.MAIN_ACTIVITY_FRAGMENT_TAG)
                    .commit();
        }
    }
}
