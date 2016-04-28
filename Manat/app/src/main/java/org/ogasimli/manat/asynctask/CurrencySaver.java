package org.ogasimli.manat.asynctask;

import org.ogasimli.manat.model.Currency;
import org.ogasimli.manat.provigen.ManatContract;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * AsyncTask to store currency objects into database
 *
 * Created by Orkhan Gasimli on 22.04.2016.
 */
public class CurrencySaver extends AsyncTask<ArrayList<Currency>, Void, Void> {

    private final Context mContext;

    private final String mDateString;

    public CurrencySaver(Context context, String dateString) {
        mContext = context;
        mDateString = dateString;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(ArrayList<Currency>... params) {
        if (mContext != null) {
            //Get ContentResolver
            Uri contentUri = ManatContract.CONTENT_URI;
            ContentResolver contentResolver = mContext.getContentResolver();
            //First delete old data
            contentResolver.delete(contentUri,
                    ManatContract.DATE + " = ? ",
                    new String[]{mDateString});

            //Insert new values
            ArrayList<Currency> currencyList = params[0];
            for (Currency currency : currencyList) {
                ContentValues newValues = new ContentValues();
                newValues.put(ManatContract.CODE, currency.getCode());
                newValues.put(ManatContract.NOMINAL, currency.getNominal());
                newValues.put(ManatContract.VALUE, currency.getValue());
                newValues.put(ManatContract.DATE, currency.getDate());
                newValues.put(ManatContract.TREND, currency.getTrend());
                contentResolver.insert(contentUri, newValues);
            }
        }
        return null;
    }
}
