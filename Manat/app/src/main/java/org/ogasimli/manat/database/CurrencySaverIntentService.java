package org.ogasimli.manat.database;

import org.joda.time.DateTime;
import org.ogasimli.manat.ManatApplication;
import org.ogasimli.manat.database.provigen.ManatContract;
import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.model.Currency;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

/**
 * IntentService class to save data into DB
 *
 * Created by Orkhan Gasimli on 01.07.2017.
 */

public class CurrencySaverIntentService extends IntentService {

    private static final String LOG_TAG = CurrencySaverIntentService.class.getSimpleName();

    public CurrencySaverIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String dateString = intent.getStringExtra(Constants.CURRENCY_SAVER_DATE_EXTRA_KEY);
            //Get ContentResolver
            Uri contentUri = ManatContract.CONTENT_URI;
            ContentResolver contentResolver = getContentResolver();
            //First delete old data
            contentResolver.delete(contentUri, ManatContract.DATE + " = ? ",
                    new String[]{dateString});

            //Insert new values
            ArrayList<Currency> currencyList =
                    intent.getParcelableArrayListExtra(Constants.CURRENCY_SVER_LIST_EXTRA_KEY);
            for (Currency currency : currencyList) {
                ContentValues newValues = new ContentValues();
                newValues.put(ManatContract.CODE, currency.getCode());
                newValues.put(ManatContract.NOMINAL, currency.getNominal());
                newValues.put(ManatContract.VALUE, currency.getValue());
                newValues.put(ManatContract.DATE, currency.getDate());
                newValues.put(ManatContract.TREND, currency.getTrend());
                contentResolver.insert(contentUri, newValues);
            }

            //Update UI if today rates are updated
            if (dateString.equals(ManatApplication.globalSelectedDate)) {
                LocalBroadcastManager.getInstance(this)
                        .sendBroadcast(new Intent(Constants.ACTION_DB_DATA_UPDATED));
            }

            //Update widget if today rates are updated
            DateTime dateTime = new DateTime();
            String todayString = Constants.DATE_FORMATTER_WITH_DASH.print(dateTime)
                    + Constants.DATE_APPENDIX;
            if (dateString.equals(todayString)) {
                updateWidgets();
            }
        }
    }

    /**
     * Helper method to update app widget
     */
    private void updateWidgets() {
        Intent dataUpdatedIntent = new Intent(Constants.ACTION_WIDGET_DATA_UPDATED)
                .setPackage(this.getPackageName());
        this.sendBroadcast(dataUpdatedIntent);
    }
}
