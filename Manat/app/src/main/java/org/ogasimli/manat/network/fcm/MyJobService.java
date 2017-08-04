package org.ogasimli.manat.network.fcm;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.ogasimli.manat.database.CurrencySaverIntentService;
import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.helper.Utilities;
import org.ogasimli.manat.model.Currency;
import org.ogasimli.manat.network.retrofit.ApiService;
import org.ogasimli.manat.network.retrofit.RetrofitAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * JobService to update currencies based on the FCM message
 *
 * Created by Orkhan Gasimli on 30.06.2017.
 */

public class MyJobService extends JobService {

    private static final String LOG_TAG = MyJobService.class.getSimpleName();

    private boolean mIsSuccessful;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(LOG_TAG, "Performing long running task in scheduled job");
        Bundle bundle = jobParameters.getExtras();
        return bundle != null && refreshData(jobParameters.getExtras()
                .getString(Constants.FCM_MESSAGE_EXTRA_KEY));
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    /**
     * Helper method to refresh rates
     */
    private boolean refreshData(final String dateString) {
        final String formattedDate = Utilities.modifyDateString(dateString,
                Constants.DATE_FORMATTER_DDMMYYYY_WITH_DOT,
                Constants.DATE_FORMATTER_WITH_DASH,
                Constants.DATE_APPENDIX);
        // Load data from API
        RestAdapter adapter = RetrofitAdapter.getRestAdapter();
        ApiService service = adapter.create(ApiService.class);
        service.getCurrencyByDate(formattedDate, new Callback<ArrayList<Currency>>() {
            @SuppressWarnings("unchecked")
            @Override
            public void success(ArrayList<Currency> currencyList, Response response) {
                if (currencyList != null && currencyList.size() == 44) {
                    // Sort list in desired order
                    currencyList = Utilities.sortList(currencyList);
                    Log.d(LOG_TAG, "Loaded from API");
                    // Delete old data and insert new
                    saveCurrencyList(currencyList, formattedDate);
                    Log.d(LOG_TAG, "Inserted into DB");
                    mIsSuccessful = true;
                } else {
                    Log.d(LOG_TAG, "List is either empty or incomplete");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("RetrofitError", error.toString());
            }
        });
        return mIsSuccessful;
    }

    /**
     * Helper method to start CurrencySaverIntentService
     */
    private void saveCurrencyList(ArrayList<Currency> currencyList, String dateString) {
        Intent intent = new Intent(this, CurrencySaverIntentService.class);
        intent.putParcelableArrayListExtra(Constants.CURRENCY_SAVER_LIST_EXTRA_KEY, currencyList);
        intent.putExtra(Constants.CURRENCY_SAVER_DATE_EXTRA_KEY, dateString);
        startService(intent);
    }
}
