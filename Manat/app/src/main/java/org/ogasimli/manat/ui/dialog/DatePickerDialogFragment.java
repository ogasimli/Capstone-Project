package org.ogasimli.manat.ui.dialog;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.helper.Utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import manat.ogasimli.org.manat.R;

/**
 * DataPickerDialogFragment class
 *
 * Created by Orkhan Gasimli on 10.01.2016.
 */
public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String LOG_TAG = DatePickerDialogFragment.class.getSimpleName();

    @BindView(R.id.main_date_text)
    TextView mMainDateTextView;

    private Unbinder mUnbinder;

    public DatePickerDialogFragment() {
    }

    public static DatePickerDialogFragment newInstance() {
        return new DatePickerDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mUnbinder = ButterKnife.bind(this, getActivity());

        String dateString = getArguments().getString(Constants.DATE_PICKER_BUNDLE_KEY);
        LocalDateTime date = Constants.DATE_FORMATTER_DDMMMYYYY.parseLocalDateTime(dateString);
        int year = date.getYear();
        // Subtract 1 because JodaTime's month begins from 1, while Calendar's month from 0
        int month = date.getMonthOfYear() - 1;
        int day = date.getDayOfMonth();

        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                R.style.DatePickerDialogStyle, this, year, month, day);

        // Set min date
        DateTime minDate =  DateTime.parse(Constants.MIN_DATE,
                Constants.DATE_FORMATTER_DDMMYYYY);
        long minDateMillis = minDate.getMillis();
        dialog.getDatePicker().setMinDate(minDateMillis);

        // Set max date
        DateTime maxDate = new DateTime(DateTimeZone.getDefault())
                .withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59);
        long maxDateMillis = maxDate.getMillis();
        dialog.getDatePicker().setMaxDate(maxDateMillis);

        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String dateString = String.format("%s %s %s", String.valueOf(day),
                String.valueOf(month + 1), String.valueOf(year));
        dateString = Utilities.modifyDateString(dateString, Constants.DATE_FORMATTER_DDMMYYYY,
                Constants.DATE_FORMATTER_DMMMMYYYY, "");
        mMainDateTextView.setText(dateString);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }
}
