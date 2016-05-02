package org.ogasimli.manat.widget;

import org.joda.time.DateTime;
import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.model.Currency;
import org.ogasimli.manat.provigen.ManatContract;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Locale;

import manat.ogasimli.org.manat.R;

/**
 * RemoteViewsService class controlling the data being shown in the app widget
 *
 * Created by Orkhan Gasimli on 02.05.2016.
 */
public class WidgetRemoteViewsService extends RemoteViewsService {

    private static final String LOG_TAG = WidgetRemoteViewsService.class.getSimpleName();

    private String mDateString;

    private ArrayList<Currency> mCurrencyList;

    private String[] mCodes;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            @Override
            public void onCreate() {
                initializeData();
                //Get rates for today
                loadFromDb();
            }

            @Override
            public void onDataSetChanged() {

            }

            @Override
            public void onDestroy() {
                mCurrencyList.clear();
            }

            @Override
            public int getCount() {
                return mCurrencyList == null ? 0 : mCurrencyList.size();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                Currency currency = mCurrencyList.get(position);
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout
                        .main_currency_item);

                //int flagImage = Utilities.getCurrencyImage(getBaseContext(), currency.getCode());
                //int trendImage = Utilities.getTrendImage(currency.getTrend());
                String currencyCode = currency.getCode();
                /*String currencyName;
                if (Integer.valueOf(currency.getNominal()) != 1) {
                    currencyName = String.format("%s %s",
                            currency.getNominal(), currency.getName());
                } else {
                    currencyName = currency.getName();
                }*/
                double value = 0;
                if (!currency.getValue().equals("")) {
                    value = Double.parseDouble(currency.getValue());
                }
                String currencyValue = String.format(Locale.getDefault(), "%.4f", value);

                //remoteViews.setImageViewResource(R.id.list_item_country_flag, flagImage);
                remoteViews.setTextViewText(R.id.list_item_currency_textview, currencyCode);
                //remoteViews.setTextViewText(R.id.list_item_currency_name_textview, currencyName);
                remoteViews.setTextViewText(R.id.list_item_rate_textview, currencyValue);
                //remoteViews.setImageViewResource(R.id.list_item_trend_imageview, trendImage);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.main_currency_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

    public void loadFromDb() {
        mCurrencyList = new ArrayList<>();
        Uri uri = ManatContract.CONTENT_URI;
        String selection = ManatContract.DATE + " = ? AND " + ManatContract.CODE + " = ?";
        for (String codeString : mCodes) {
            String[] args = new String[]{mDateString, codeString};
            Cursor cursor = getContentResolver().query(uri, null, selection, args, "");
            if (null == cursor) {
                mCurrencyList = null;
            } else if (cursor.getCount() < 1) {
                cursor.close();
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
                    /*currency.setName(Utilities.getCurrencyName(getBaseContext(),
                            currency.getCode()));*/
                    mCurrencyList.add(currency);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Helper method to get necessary data before loading
     */
    private void initializeData() {
        DateTime date = new DateTime();
        mDateString = Constants.DATE_FORMATTER_WITH_DASH.print(date) + Constants.DATE_APPENDIX;
        mCodes = getResources().getStringArray(R.array.currency_codes);
    }
}
