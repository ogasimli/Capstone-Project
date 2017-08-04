package org.ogasimli.manat.helper;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Helper class to hold constants
 *
 * Created by Orkhan Gasimli on 03.04.2016.
 */
public class Constants {

    // Package name constant
    private static final String PACKAGE_NAME = "org.ogasimli.manat";

    // MongoDB query constants
    public static final String BASE_URL = "https://manat.herokuapp.com";
    public static final String FROM_DATE_QUERY_PARAM = "fromDate";
    public static final String TILL_DATE_QUERY_PARAM = "tillDate";
    public static final String CODE_QUERY_PARAM = "code";

    // Intent extra keys
    public static final String CODE = "CODE";
    public static final String AMOUNT = "AMOUNT";
    public static final String BUTTON_KEY = "BUTTON_KEY";
    public static final int SELECT_CURRENCY_DIALOG_RESULT = 0;
    public static final int CALCULATOR_DIALOG_RESULT = 1;
    public static final int INVITATION_REQUEST_RESULT = 2;

    // Fragment tags
    public static final String MAIN_ACTIVITY_FRAGMENT_TAG = "MAIN_ACTIVITY_FRAGMENT";
    public static final String DETAIL_ACTIVITY_FRAGMENT_TAG = "DETAIL_ACTIVITY_FRAGMENT";
    public static final String DATA_PICKER_DIALOG_FRAGMENT_TAG = "DATA_PICKER_DIALOG_FRAGMENT";
    public static final String CALCULATOR_DIALOG_FRAGMENT_TAG = "CALCULATOR_DIALOG_FRAGMENT";
    public static final String SELECT_CURRENCY_DIALOG_FRAGMENT_TAG = "SELECT_CURRENCY_DIALOG_FRAGMENT";

    // Date constants
    public static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = 
            DateTimeFormat.forPattern("dd MM yyyy");
    public static final DateTimeFormatter DATE_FORMATTER_DMMMMYYYY = 
            DateTimeFormat.forPattern("d MMMM yyyy");
    public static final DateTimeFormatter DATE_FORMATTER_DMMMYY = 
            DateTimeFormat.forPattern("d MMM yy");
    public static final DateTimeFormatter DATE_FORMATTER_DDMMMYYYY = 
            DateTimeFormat.forPattern("dd MMMM yyyy");
    public static final DateTimeFormatter DATE_FORMATTER_WITH_DASH = 
            DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY_WITH_DOT =
            DateTimeFormat.forPattern("dd.MM.yyyy");
    public static final String DATE_APPENDIX = "T00:00:00.000+04:00";

    // SharedPreference keys
    public static final String PREFERENCE_FILE_KEY = PACKAGE_NAME + "PREFERENCE_FILE_KEY";
    public static final String SELECTED_CODE_KEY = PACKAGE_NAME + "SELECTED_CODE_KEY";
    public static final String NOTIFICATION_SHOW_KEY = PACKAGE_NAME + "NOTIFICATION_SHOW_KEY";

    // Bundle keys
    public static final String DATE_PICKER_BUNDLE_KEY = "DATE_PICKER_BUNDLE_KEY";
    public static final String CALCULATOR_BUNDLE_KEY = "CALCULATOR_BUNDLE_KEY";
    public static final String LIST_STATE_KEY = "LIST_STATE_KEY";
    public static final String SECONDARY_LIST_STATE_KEY = "SECONDARY_LIST_STATE_KEY";
    public static final String VIEW_STATE_KEY = "VIEW_STATE_KEY";
    public static final String PRESSED_AMOUNT_FIELD_KEY = "PRESSED_AMOUNT_FIELD_KEY";
    public static final String BUTTON_STATE_KEY = "BUTTON_STATE_KEY";
    public static final String IGNORE_CHANGE_KEY = "IGNORE_CHANGE_KEY";
    public static final String SWAP_ORDER_KEY = "SWAP_ORDER_KEY";

    // Activity state keys
    public static final int VIEW_STATE_ERROR = 0;
    public static final int VIEW_STATE_RESULTS = 1;
    public static final int WEEKLY_BTN_SELECTED = 0;
    public static final int MONTHLY_BTN_SELECTED = 1;
    public static final int QUARTERLY_BTN_SELECTED = 2;
    public static final int SEMI_ANNUALLY_BTN_SELECTED = 3;
    public static final int YEARLY_BTN_SELECTED = 4;

    // Database constants
    public static final String DB_NAME = "manat_db";
    public static final int DB_VERSION = 1;
    public static final String DB_AUTHORITY = "content://org.ogasimli.manat/";
    public static final int CURRENCY_LOADER_ID = 0;

    // HashMap keys
    public static final String CURRENT_RATE_KEY = "CURRENT_RATE";
    public static final String AVERAGE_RATE_KEY = "AVERAGE_RATE";
    public static final String MAX_RATE_KEY = "MAX_RATE";
    public static final String MIN_RATE_KEY = "MIN_RATE";

    // First available date
    public static final String MIN_DATE = "25 11 1993";

    // BroadcastReceiver keys
    // Setting the package ensures that only components in our app will receive the broadcast
    public static final String ACTION_WIDGET_DATA_UPDATED = PACKAGE_NAME + ".ACTION_WIDGET_DATA_UPDATED";
    public static final String ACTION_DB_DATA_UPDATED = PACKAGE_NAME + ".ACTION_DB_DATA_UPDATED";

    // Intent extra keys for CurrencySaverIntentService
    public static final String CURRENCY_SAVER_DATE_EXTRA_KEY = "SAVE_DATE_EXTRA";
    public static final String CURRENCY_SAVER_LIST_EXTRA_KEY = "CURRENCY_LIST_EXTRA";
    public static final String CURRENCY_SAVER_SWITCH_EXTRA_KEY = "MANUAL_REFRESH";

    // Intent extra keys for local BroadCastManager
    public static final String LOCAL_BROADCAST_DATE_EXTRA = PACKAGE_NAME + "LOCAL_BROADCAST_DATE_EXTRA";

    // Notification channel constants
    public static final String NOTIFICATION_CHANNEL_NAME = "news";
    public static final int NOTIFICATION_ID = 1986;

    // Topic name for FCM
    public static final String FCM_TOPIC_NAME = "update";
    public static final String FCM_MESSAGE_EXTRA_KEY = "date";
    public static final String FCM_MESSAGE_ACTION_KEY = "action";
}
