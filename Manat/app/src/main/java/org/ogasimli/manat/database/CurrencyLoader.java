package org.ogasimli.manat.database;

import org.ogasimli.manat.model.Currency;
import org.ogasimli.manat.database.provigen.ManatContract;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Async task loader to load rates
 *
 * Created by Orkhan Gasimli on 22.04.2016.
 */
public class CurrencyLoader extends AsyncTaskLoader<ArrayList<Currency>> {

    private final String mDateString;

    private final String[] mCodeList;

    public CurrencyLoader(Context context, String dateString, String[] codeList) {
        super(context);
        mDateString = dateString;
        mCodeList = codeList;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mDateString == null || mDateString.equals("")) {
            deliverResult(new ArrayList<Currency>());
        } else {
            forceLoad();
        }
    }

    @Override
    public ArrayList<Currency> loadInBackground() {
        ArrayList<Currency> currencyList = new ArrayList<>();
        Uri uri = ManatContract.CONTENT_URI;
        String selection = ManatContract.DATE + " = ? AND " + ManatContract.CODE + " = ?";
        for (String codeString : mCodeList) {
            String[] args = new String[]{mDateString, codeString};
            Cursor cursor = getContext().getContentResolver().
                    query(uri, null, selection, args, "");
            if (null == cursor) {
                return null;
            } else if (cursor.getCount() < 1) {
                cursor.close();
                return new ArrayList<>();
            } else {
                int code = cursor.getColumnIndex(ManatContract.CODE);
                int nominal = cursor.getColumnIndex(ManatContract.NOMINAL);
                int value = cursor.getColumnIndex(ManatContract.VALUE);
                int date = cursor.getColumnIndex(ManatContract.DATE);
                int trend = cursor.getColumnIndex(ManatContract.TREND);
                while (cursor.moveToNext()) {
                    Currency currency = new Currency();
                    currency.setCode(cursor.getString(code));
                    currency.setNominal(cursor.getString(nominal));
                    currency.setValue(cursor.getString(value));
                    currency.setDate(cursor.getString(date));
                    currency.setTrend(cursor.getString(trend));
                    currencyList.add(currency);
                }
            }
            cursor.close();
        }
        return currencyList;
    }

    @Override
    public void deliverResult(ArrayList<Currency> data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
