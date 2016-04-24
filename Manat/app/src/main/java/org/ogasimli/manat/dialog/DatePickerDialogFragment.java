package org.ogasimli.manat.dialog;


import org.joda.time.DateTime;
import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.helper.Utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import manat.ogasimli.org.manat.R;

/**
 * Created by ogasimli on 10.01.2016.
 */
public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Bind(R.id.main_date_text)
    TextView mMainDateTextView;

    public DatePickerDialogFragment() {
    }

    public static DatePickerDialogFragment newInstance() {
        return new DatePickerDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ButterKnife.bind(this, getActivity());

        String dateString = getArguments().getString(Constants.DATE_PICKER_BUNDLE_KEY);
        DateTime date = DateTime.parse(dateString, Constants.DATE_FORMATTER_DDMMMYYYY);
        int year = date.getYear();
        //Subtract 1 because JodaTime's month begins from 1, while Calendar's month from 0
        int month = date.getMonthOfYear() - 1;
        int day = date.getDayOfMonth();

        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                R.style.DatePickerDialogStyle, this, year, month, day);

        //TODO: fix issue with selection of current date
        //Set min date
        DateTime minDate =  DateTime.parse("25 11 1993",
                Constants.DATE_FORMATTER_DDMMYYYY);
        long minDateMillis = minDate.getMillis();
        dialog.getDatePicker().setMinDate(minDateMillis);

        //Set max date
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

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
        ButterKnife.unbind(this);
    }
}
