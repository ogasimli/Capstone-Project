package org.ogasimli.manat.activity;

import com.afollestad.inquiry.Inquiry;

import org.ogasimli.manat.helper.Constants;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import manat.ogasimli.org.manat.R;

public class MainActivity extends AppCompatActivity {

/*    @Bind(R.id.toolbar_main)
    Toolbar mToolbar;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Inquiry.init(this, Constants.DB_NAME, Constants.DB_VERSION);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Inquiry.deinit();
    }
}
