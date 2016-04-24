package org.ogasimli.manat.dialog;

import org.ogasimli.manat.customview.MyTextView;
import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.helper.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import manat.ogasimli.org.manat.R;

/**
 * Created by ogasimli on 08.01.2016.
 */
public class CalculatorDialogFragment extends DialogFragment {

    private int mButtonKey = 0;

    @Bind(R.id.calc_backspace_textview)
    MyTextView mBackspaceTextView;

    @Bind(R.id.calc_clear_textview)
    MyTextView mClearTextView;

    @Bind(R.id.calc_equals_textview)
    MyTextView mEqualsTextView;

    @Bind(R.id.calc_edit_textview)
    MyTextView mEditTextView;

    @Bind(R.id.calc_ok_btn)
    Button mOkBtn;

    @Bind(R.id.calc_cancel_btn)
    Button mCancelBtn;

    public CalculatorDialogFragment() {
    }

    public static CalculatorDialogFragment newInstance() {
        return new CalculatorDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_calculator, container, false);
        ButterKnife.bind(this, rootView);

        mButtonKey = getArguments().getInt(Constants.CALCULATOR_BUNDLE_KEY);

        //Prevent mEditTextView from resizing
        mEditTextView.setMaxWidth(mEditTextView.getWidth());
        mEditTextView.setMaxHeight(mEditTextView.getHeight());

        //Set mEditTextView text to zero
        double zero = 0;
        String zeroStr = String.format(Locale.getDefault(), "%.2f", zero);
        mEditTextView.setText(zeroStr);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove TitleBar
        int style = DialogFragment.STYLE_NO_TITLE;
        int theme = R.style.CalculatorDialogStyle;
        setStyle(style, theme);
    }



/*    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.CalculatorDialogStyle);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.fragment_calculator, null))
                .setPositiveButton(getActivity().getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .setNegativeButton(getActivity().getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }*/

    @Override
    public void onStart() {
        super.onStart();
    }

    @OnClick({R.id.calc_one_textview, R.id.calc_two_textview, R.id.calc_three_textview,
            R.id.calc_four_textview, R.id.calc_five_textview, R.id.calc_six_textview,
            R.id.calc_seven_textview, R.id.calc_eight_textview, R.id.calc_nine_textview,
            R.id.calc_zero_textview, R.id.calc_divide_textview, R.id.calc_multiply_textview,
            R.id.calc_plus_textview, R.id.calc_minus_textview, R.id.calc_delimiter_textview})
    public void editTextClick(MyTextView textView) {
        String buttonText = textView.getText().toString();
        String editText = mEditTextView.getText().toString();
        double zero = 0;
        String zeroStr = String.format(Locale.getDefault(), "%.2f", zero);
        if (!editText.equals(zeroStr)) {
            editText = editText + buttonText;
        } else {
            editText = buttonText;
        }
        mEditTextView.setText(editText);
    }

    @OnClick(R.id.calc_backspace_textview)
    public void backspaceClick(TextView textView) {
        String editText = mEditTextView.getText().toString();
        double zero = 0;
        String zeroStr = String.format(Locale.getDefault(), "%.2f", zero);
        if (!editText.equals(zeroStr)) {
            if (editText.length() > 1) {
                editText = Utilities.deleteLastChar(editText);
            } else {
                editText = zeroStr;
            }
        }
        mEditTextView.setText(editText);
    }

    @OnClick(R.id.calc_clear_textview)
    public void clearClick(TextView textView) {
        String editText = mEditTextView.getText().toString();
        double zero = 0;
        String zeroStr = String.format(Locale.getDefault(), "%.2f", zero);
        if (!editText.equals(zeroStr)) {
            editText = zeroStr;
        }
        mEditTextView.setText(editText);
    }

    @OnClick(R.id.calc_equals_textview)
    public void equalsClick(TextView textView) {
        String editText = mEditTextView.getText().toString();
        char firstChar = editText.charAt(0);
        char lastChar = editText.charAt(editText.length() - 1);
        if (Character.isDigit(firstChar) && Character.isDigit(lastChar)) {
            editText = Utilities.evalMathString(editText);
            mEditTextView.setText(editText);
        } else {
            Toast.makeText(getActivity(), "Invalid input! Enter correct values.", Toast
                    .LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.calc_cancel_btn)
    public void dismissDialogClick(Button button) {
        getDialog().dismiss();
    }

    @OnClick(R.id.calc_ok_btn)
    public void sendResultClick(Button button) {
        //Determine the state of String
        String editText = mEditTextView.getText().toString();
        char firstChar = editText.charAt(0);
        char lastChar = editText.charAt(editText.length() - 1);
        if (Character.isDigit(firstChar) && Character.isDigit(lastChar)) {
            editText = Utilities.evalMathString(editText);
            //Go back to MainActivityFragment
            Intent intent = new Intent();
            intent.putExtra(Constants.AMOUNT, editText);
            intent.putExtra(Constants.BUTTON_KEY, mButtonKey);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            getDialog().dismiss();
        } else {
            Toast.makeText(getActivity(), "Invalid input! Enter correct values.", Toast
                    .LENGTH_LONG).show();
        }
    }
}
