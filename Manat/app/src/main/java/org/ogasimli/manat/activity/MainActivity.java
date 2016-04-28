package org.ogasimli.manat.activity;

import org.ogasimli.manat.fragment.MainActivityFragment;
import org.ogasimli.manat.helper.Constants;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import manat.ogasimli.org.manat.R;

public class MainActivity extends AppCompatActivity {

/*    @Bind(R.id.toolbar_main)
    Toolbar mToolbar;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/

        if (savedInstanceState == null) {
            MainActivityFragment fragment = new MainActivityFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment, Constants.MAIN_ACTIVITY_FRAGMENT_TAG)
                    .commit();
        }
    }
/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }*/
}
