package org.ogasimli.manat.helper;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Helper class to hold constants
 *
 * Created by Orkhan Gasimli on 03.04.2016.
 */
public class Constants {

    //MongoDB query constants
    public static final String BASE_URL = "https://api.mlab" +
            ".com/api/1/databases/fxrates/collections/";
    public static final String API_QUERY_PARAM = "q";
    public static final String API_EXCLUDE_FIELDS_PARAM = "f";
    public static final String API_EXCLUDE_FIELD_STRING = "{\"value\":1,\"date\":1}";
    public static final String API_KEY = "apiKey";

    //Intent extra keys
    public static final String CODE = "CODE";
    public static final String AMOUNT = "AMOUNT";
    public static final String BUTTON_KEY = "BUTTON_KEY";
    public static final int SELECT_CURRENCY_DIALOG_RESULT = 0;
    public static final int CALCULATOR_DIALOG_RESULT = 1;
    public static final int INVITATION_REQUEST_RESULT = 2;

    //Fragment tags
    public static final String MAIN_ACTIVITY_FRAGMENT_TAG = "MAIN_ACTIVITY_FRAGMENT";
    public static final String DETAIL_ACTIVITY_FRAGMENT_TAG = "DETAIL_ACTIVITY_FRAGMENT";
    public static final String DATA_PICKER_DIALOG_FRAGMENT_TAG = "DATA_PICKER_DIALOG_FRAGMENT";
    public static final String CALCULATOR_DIALOG_FRAGMENT_TAG = "CALCULATOR_DIALOG_FRAGMENT";
    public static final String SELECT_CURRENCY_DIALOG_FRAGMENT_TAG = "SELECT_CURRENCY_DIALOG_FRAGMENT";

    //Date constants
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
    public static final String DATE_APPENDIX = "T00:00:00.000+04:00";

    //SharedPreference keys
    public static final String PREFERENCE_FILE_KEY = "PREFERENCE_FILE_KEY";
    public static final String SELECTED_CODE_KEY = "SELECTED_CODE_KEY";

    //Bundle keys
    public static final String DATE_PICKER_BUNDLE_KEY = "DATE_PICKER_BUNDLE_KEY";
    public static final String CALCULATOR_BUNDLE_KEY = "CALCULATOR_BUNDLE_KEY";
    public static final String LIST_STATE_KEY = "LIST_STATE_KEY";
    public static final String SECONDARY_LIST_STATE_KEY = "SECONDARY_LIST_STATE_KEY";
    public static final String VIEW_STATE_KEY = "VIEW_STATE_KEY";
    public static final String PRESSED_AMOUNT_FIELD_KEY = "PRESSED_AMOUNT_FIELD_KEY";
    public static final String BUTTON_STATE_KEY = "BUTTON_STATE_KEY";

    //Activity state keys
    public static final int VIEW_STATE_ERROR = 0;
    public static final int VIEW_STATE_RESULTS = 1;
    public static final int WEEKLY_BTN_SELECTED = 0;
    public static final int MONTHLY_BTN_SELECTED = 1;
    public static final int QUARTERLY_BTN_SELECTED = 2;
    public static final int SEMI_ANNUALLY_BTN_SELECTED = 3;
    public static final int YEARLY_BTN_SELECTED = 4;

    //Database constants
    public static final String DB_NAME = "manat_db";
    public static final int DB_VERSION = 1;
    public static final String DB_AUTHORITY = "content://org.ogasimli.manat/";
    public static final int CURRENCY_LOADER_ID = 0;

    //HashMap keys
    public static final String CURRENT_RATE_KEY = "CURRENT_RATE";
    public static final String AVERAGE_RATE_KEY = "AVERAGE_RATE";
    public static final String MAX_RATE_KEY = "MAX_RATE";
    public static final String MIN_RATE_KEY = "MIN_RATE";

    //First available date
    public static final String MIN_DATE = "25 11 1993";

    //Google Analytics constants
    public static final String MAIN_ACTIVITY_SCREEN_NAME = "Main Activity";
    public static final String DETAIL_ACTIVITY_SCREEN_NAME = "Detail Activity";
    public static final String ACTION_TYPE = "Action";
    public static final String INVITE_ACTION = "Invite";
    public static final String SHARE_ACTION = "Share";
}
