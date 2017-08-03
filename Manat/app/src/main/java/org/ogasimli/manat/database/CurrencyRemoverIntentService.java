package org.ogasimli.manat.database;

import org.joda.time.LocalDate;
import org.ogasimli.manat.database.provigen.ManatContract;
import org.ogasimli.manat.helper.Constants;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * IntentService class to delete old data from DB
 *
 * Created by Orkhan Gasimli on 03.08.2017.
 */

public class CurrencyRemoverIntentService extends IntentService {

    private static final String LOG_TAG = CurrencyRemoverIntentService.class.getSimpleName();

    public CurrencyRemoverIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            LocalDate today = LocalDate.now().minusMonths(1);
            String formattedDate  = today
                    .toString(Constants.DATE_FORMATTER_WITH_DASH)
                    .concat(Constants.DATE_APPENDIX);

            // Get ContentResolver
            Uri contentUri = ManatContract.CONTENT_URI;
            ContentResolver contentResolver = getContentResolver();
            // First delete old data
            contentResolver.delete(contentUri, ManatContract.DATE + " <= ? ",
                    new String[]{formattedDate});
        }
    }
}
