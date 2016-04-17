package org.ogasimli.manat.activity;

import org.ogasimli.manat.fragment.DetailActivityFragment;
import org.ogasimli.manat.helper.Constants;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import manat.ogasimli.org.manat.R;

/**
 * Created by ogasimli on 10.01.2016.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_details);

        Bundle extras = getIntent().getExtras();
        String code;
        if (extras != null) {
            code = extras.getString(Constants.SELECTED_CODE_KEY);
        } else {
            throw new NullPointerException("No movie found in extras");
        }

        if (savedInstanceState == null) {
            DetailActivityFragment fragment = DetailActivityFragment.getInstance(code);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
