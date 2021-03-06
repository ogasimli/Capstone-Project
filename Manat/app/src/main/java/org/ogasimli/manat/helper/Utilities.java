package org.ogasimli.manat.helper;

import com.udojava.evalex.Expression;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.ogasimli.manat.model.Currency;

import android.content.Context;
import android.content.SharedPreferences;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import manat.ogasimli.org.manat.R;

/**
 * Helper class for holding methods
 *
 * Created by Orkhan Gasimli on 03.04.2016.
 */
public class Utilities {

    /*
    * Helper method to get decimal separators of amounts
    */
    public static String getDecimalSeparator() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        DecimalFormatSymbols symbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        return String.valueOf(symbols.getDecimalSeparator());
    }

    /*
    * Helper method to get decimal separators of amounts
    */
    private static String getThousandsSeparator() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        DecimalFormatSymbols symbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        return String.valueOf(symbols.getGroupingSeparator());
    }

    /*
    * Helper method to reformat amounts to be able to make calculations on it
    */
    public static String reformatAmount(String amount) {
        return convertToEnglishDigits(amount
                .trim()
                .replace(getThousandsSeparator(),"")
                .replace(getDecimalSeparator(),"."));
    }

    /*
    * Helper method to convert hindi digits to english
    */
    private static String convertToEnglishDigits(String value)
    {
                //Hindi digits
        return value.replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4")
                .replace("٥", "5").replace("٦", "6").replace("٧", "7").replace("٨", "8")
                .replace("٩", "9").replace("٠", "0")

                //Persian & Urdu digits
                .replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4")
                .replace("۵", "5").replace("۶", "6").replace("۷", "7").replace("۸", "8")
                .replace("۹", "9").replace("۰", "0")

                //Devanagari digits
                .replace("१", "1").replace("२", "2").replace("३", "3").replace("४", "4")
                .replace("५", "5").replace("६", "6").replace("७", "7").replace("८", "8")
                .replace("९", "9").replace("०", "0")

                //Tamil digits
                .replace("௧", "1").replace("௨", "2").replace("௩", "3").replace("௪", "4")
                .replace("௫", "5").replace("௬", "6").replace("௭", "7").replace("௮", "8")
                .replace("௯", "9").replace("௰", "10").replace("௱", "100").replace("௲", "1000");
    }

    /*
    * Helper method to evaluate math string
    */
    public static String evalMathString(String string) {
        BigDecimal result;
        Expression expression = new Expression(reformatAmount(string));
        result = expression.eval();
        return String.format(Locale.getDefault(), "%,.2f", result);
    }

    /*
    * Helper method to delete last char of a string
    */
    public static String deleteLastChar(String str) {
        String subStr = null;
        if (str != null && str.length() > 0) {
            subStr = str.substring(0, str.length() - 1);
        }
        return subStr;
    }

    /*
    * Helper method to get currency name by code
    */
    public static String getCurrencyName(Context context, String code) {
        int i = -1;
        for (String codes : context.getResources().getStringArray(R.array.currency_codes)) {
            i++;
            if (codes.equals(code))
                break;
        }
        return context.getResources().getStringArray(R.array.currency_names)[i];
    }

    /*
    * Helper method to get currency image resource by code
    */
    public static int getCurrencyImage(Context context, String code) {
        switch (code) {
            // Special case for TRY in order to escape java's built-in try statement
            case "TRY":
                return context.getResources().getIdentifier("trl", "drawable",
                        context.getPackageName());
            default:
                return context.getResources().getIdentifier(code.toLowerCase(Locale.ENGLISH),
                        "drawable", context.getPackageName());
        }
    }

    /*
    * Helper method to get trend image resource by currency trend
    */
    public static int getTrendImage(String trendString) {
        int trend = Integer.parseInt(trendString);
        switch (trend) {
            case -1:
                return R.drawable.ic_trending_down;
            case 0:
                return R.drawable.ic_trending_flat;
            case 1:
                return R.drawable.ic_trending_up;
            default:
                return R.drawable.ic_trending_flat;
        }
    }

    /*
    * Helper method to get trend image content description by currency trend
    */
    public static String getTrendImageDescription(Context context, String trendString) {
        int trend = Integer.parseInt(trendString);
        switch (trend) {
            case -1:
                return context.getString(R.string.trend_down_image_description);
            case 0:
                return context.getString(R.string.trend_flat_image_description);
            case 1:
                return context.getString(R.string.trend_up_image_description);
            default:
                return context.getString(R.string.trend_down_image_description);
        }
    }
    /*
    * Helper method to modify date string to use in query
    */
    public static String modifyDateString(String dateString, DateTimeFormatter beforeFormatter,
                                          DateTimeFormatter afterFormatter, String appendix) {
        LocalDateTime date = beforeFormatter.parseLocalDateTime(dateString);
        return afterFormatter.print(date) + appendix;
    }

    /*
    * Helper method to get currency rate by currency code
    */
    public static String getRateByCode(ArrayList<Currency> currencyList, String code) {
        double rate = 0.00;
        if (currencyList != null && currencyList.size() > 0) {
            for (Currency currency : currencyList) {
                if (currency.getCode().equals(code)) {
                    double value = Double.parseDouble(currency.getValue());
                    double nominal = Double.parseDouble(currency.getNominal());
                    rate = value / nominal;
                }
            }
        }
        return String.format(Locale.getDefault(), "%.4f", rate);
    }

    /**
     * Helper method to retrieve selected currency code from SharedPreferences
     */
    public static String getSelectedCode(Context context) {
        SharedPreferences sharedPref = context
                .getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        String[] codes = context.getResources().getStringArray(R.array.currency_codes);
        String defaultValue = codes[0];
        return sharedPref.getString(Constants.SELECTED_CODE_KEY, defaultValue);
    }

    /**
     * Helper method to set Date property of Currency object
     */
    public static ArrayList<Currency> getDataForPeriod(ArrayList<Currency> currencyList,
                                                       int pressedBtnNum) {
        ArrayList<Currency> newCurrencyList = new ArrayList<>();
        DateTime tillDate = new DateTime();
        int period = determinePeriod(pressedBtnNum);

        //Create new Currency list based on old Currency list containing dates
        for (int i = period; i >= 0; i--) {
            for (Currency currency : currencyList) {
                String fromDateString = Constants.DATE_FORMATTER_WITH_DASH
                        .print(tillDate.minusDays(i));
                String date = currency.getDate();
                String currencyDateString = date.substring(0, date.indexOf("T"));
                if (currencyDateString.equals(fromDateString)) {
                    newCurrencyList.add(currency);
                }
            }
        }

        return newCurrencyList;
    }

    /**
     * Helper method to determine beginning date of period based on the user selection
     */
    public static int determinePeriod(int pressedBtnNum) {
        DateTime tillDate = new DateTime();
        DateTime fromDate;

        //Identify period
        switch (pressedBtnNum) {
            case Constants.WEEKLY_BTN_SELECTED:
                fromDate = tillDate.minusWeeks(1);
                break;
            case Constants.MONTHLY_BTN_SELECTED:
                fromDate = tillDate.minusMonths(1);
                break;
            case Constants.QUARTERLY_BTN_SELECTED:
                fromDate = tillDate.minusMonths(3);
                break;
            case Constants.SEMI_ANNUALLY_BTN_SELECTED:
                fromDate = tillDate.minusMonths(6);
                break;
            case Constants.YEARLY_BTN_SELECTED:
                fromDate = tillDate.minusYears(1);
                break;
            default:
                fromDate = tillDate.minusWeeks(1);
                break;
        }

        //Get days between dates
        Days days = Days.daysBetween(fromDate, tillDate);

        return days.getDays() - 1;
    }

    /**
     * Helper method to find current, average, max and min rates from set of rates
     */
    public static HashMap<String, String> processData(ArrayList<Currency> currencyList, String
            dateString) {

        String currentRate = "";
        String averageRate;
        String maxRate;
        String minRate;
        double sum = 0;
        List<Double> values = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();

        for (Currency currency : currencyList) {
            if (currency.getDate().equals(dateString)) {
                currentRate = currency.getValue();
            }

            sum += Double.parseDouble(currency.getValue());
            values.add(Double.parseDouble(currency.getValue()));
        }

        if (!currentRate.isEmpty()) {
            currentRate = String.format(Locale.getDefault(), "%.4f", Double.parseDouble(currentRate));
        }

        averageRate = String.format(Locale.getDefault(), "%.4f", sum / currencyList.size());
        maxRate = String.format(Locale.getDefault(), "%.4f", Collections.max(values));
        minRate = String.format(Locale.getDefault(), "%.4f", Collections.min(values));

        map.put(Constants.CURRENT_RATE_KEY, currentRate);
        map.put(Constants.AVERAGE_RATE_KEY, averageRate);
        map.put(Constants.MAX_RATE_KEY, maxRate);
        map.put(Constants.MIN_RATE_KEY, minRate);

        return map;
    }

    /**
     * Helper method to sort currency list
     */
    public static ArrayList<Currency> sortList(ArrayList<Currency> currencyList) {
        Currency currency;
        //Sort list by code
        if (currencyList.size() >= 41) {
            //USD
            currency = currencyList.get(41);
            currencyList.remove(41);
            currencyList.add(0, currency);
            //EUR
            currency = currencyList.get(13);
            currencyList.remove(13);
            currencyList.add(1, currency);
            //GBP
            currency = currencyList.get(14);
            currencyList.remove(14);
            currencyList.add(2, currency);
            //RUB
            currency = currencyList.get(33);
            currencyList.remove(33);
            currencyList.add(3, currency);
            //TRY
            currency = currencyList.get(39);
            currencyList.remove(39);
            currencyList.add(4, currency);
        }

        return currencyList;
    }
}