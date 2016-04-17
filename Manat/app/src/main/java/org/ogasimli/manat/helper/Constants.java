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
    public static final int SELECT_CURRENCY_DIALOG_RESULT = 1;
    public static final String DATA_PICKER_DIALOG_FRAGMENT = "DatePickerDialogFragment";
    public static final String CALCULATOR_DIALOG_FRAGMENT = "CalculatorDialogFragment";
    public static final String SELECT_CURRENCY_DIALOG_FRAGMENT = "SelectCurrencyDialogFragment";

    //Date constants
    public static final DateTimeFormatter DATE_FORMATTER_NORMAL = DateTimeFormat.forPattern
            ("dd MM yyyy");
    public static final DateTimeFormatter DATE_FORMATTER_MONTH_STRING_D = DateTimeFormat.forPattern
            ("d MMMM yyyy");
    public static final DateTimeFormatter DATE_FORMATTER_MONTH_STRING_DD = DateTimeFormat.forPattern
            ("dd MMMM yyyy");
    public static final DateTimeFormatter DATE_FORMATTER_WITH_DASH = DateTimeFormat.forPattern
            ("yyyy-MM-dd");
    public static final String DATE_APPENDIX = "T00:00:00.000+04:00";

    //SharedPreference keys
    public static final String PREFERENCE_FILE_KEY = "PREFERENCE_FILE_KEY";
    public static final String SELECTED_CODE_KEY = "SELECTED_CODE_KEY";

    //Bundle keys
    public static final String DATE_PICKER_BUNDLE_KEY = "DATE_PICKER_BUNDLE_KEY";
    public static final String LIST_STATE_KEY = "LIST_STATE_KEY";
    public static final String VIEW_STATE_KEY = "VIEW_STATE_KEY";

    //Activity state keys
    public final static int VIEW_STATE_ERROR = 0;
    public final static int VIEW_STATE_RESULTS = 1;
    public final static int WEEKLY_BTN_SELECTED = 0;
    public final static int MONTHLY_BTN_SELECTED = 1;
    public final static int QUARTERLY_BTN_SELECTED = 2;
    public final static int SEMI_ANNUALLY_BTN_SELECTED = 3;
    public final static int YEARLY_BTN_SELECTED = 4;


    //Database constants
    public static String DB_NAME = "fxrates";
    public static int DB_VERSION = 1;

    //HashMap keys
    public static String CURRENT_RATE_KEY = "CURRENT_RATE";
    public static String AVERAGE_RATE_KEY = "AVERAGE_RATE";
    public static String MAX_RATE_KEY = "MAX_RATE";
    public static String MIN_RATE_KEY = "MIN_RATE";
}
