package org.ogasimli.manat.dialog;

import com.udojava.evalex.Expression;

import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.helper.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import manat.ogasimli.org.manat.R;
import me.grantland.widget.AutofitTextView;

/**
 * DialogFragment class for Calculator
 *
 * Created by Orkhan Gasimli on 08.01.2016.
 */
public class CalculatorDialogFragment extends DialogFragment {

    private static final String LOG_TAG = CalculatorDialogFragment.class.getSimpleName();

    private int mButtonKey = 0;

    @BindView(R.id.calc_edit_textview)
    AutofitTextView mEditTextView;

    private Unbinder mUnbinder;

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
        mUnbinder = ButterKnife.bind(this, rootView);

        mButtonKey = getArguments().getInt(Constants.CALCULATOR_BUNDLE_KEY);

        //Prevent mEditTextView from resizing
        mEditTextView.setMaxWidth(mEditTextView.getWidth());
        mEditTextView.setMaxHeight(mEditTextView.getHeight());
        mEditTextView.setMovementMethod(new ScrollingMovementMethod());

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick({R.id.calc_one_textview, R.id.calc_two_textview, R.id.calc_three_textview,
            R.id.calc_four_textview, R.id.calc_five_textview, R.id.calc_six_textview,
            R.id.calc_seven_textview, R.id.calc_eight_textview, R.id.calc_nine_textview,
            R.id.calc_zero_textview, R.id.calc_divide_textview, R.id.calc_multiply_textview,
            R.id.calc_plus_textview, R.id.calc_minus_textview, R.id.calc_delimiter_textview})
    public void editTextClick(Button button) {
        String buttonText = button.getText().toString();
        String editText = mEditTextView.getText().toString();
        double zero = 0;
        String zeroStr = String.format(Locale.getDefault(), "%.2f", zero);
        if (!editText.equals(zeroStr)) {
            mEditTextView.append(buttonText);
        } else {
            mEditTextView.setText(buttonText);
        }
    }

    @OnClick(R.id.calc_backspace_textview)
    public void backspaceClick(Button button) {
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
    public void clearClick(Button button) {
        String editText = mEditTextView.getText().toString();
        double zero = 0;
        String zeroStr = String.format(Locale.getDefault(), "%.2f", zero);
        if (!editText.equals(zeroStr)) {
            editText = zeroStr;
        }
        mEditTextView.setText(editText);
    }

    @OnClick(R.id.calc_equals_textview)
    public void equalsClick(Button button) {
        String editText = mEditTextView.getText().toString();
        try {
            editText = Utilities.evalMathString(editText);
            mEditTextView.setText(editText);
        } catch (Expression.ExpressionException expressException) {
            Log.d(LOG_TAG, expressException.toString());
            Toast.makeText(getActivity(), R.string.express_exception_message, Toast
                    .LENGTH_LONG).show();
        } catch (ArithmeticException arithmeticException) {
            Log.d(LOG_TAG, arithmeticException.toString());
            Toast.makeText(getActivity(), R.string.arithmetical_exception_message, Toast
                    .LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.calc_cancel_btn)
    public void dismissDialogClick(Button button) {
        getDialog().dismiss();
    }

    @OnClick(R.id.calc_ok_btn)
    public void sendResultClick(Button button) {
        String editText = mEditTextView.getText().toString();
        try {
            editText = Utilities.evalMathString(editText);
            //Go back to MainActivityFragment
            Intent intent = new Intent();
            intent.putExtra(Constants.AMOUNT, editText);
            intent.putExtra(Constants.BUTTON_KEY, mButtonKey);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            getDialog().dismiss();
        } catch (Expression.ExpressionException expressException) {
            Log.d(LOG_TAG, expressException.toString());
            Toast.makeText(getActivity(), R.string.express_exception_message, Toast
                    .LENGTH_LONG).show();
        } catch (ArithmeticException arithmeticException) {
            Log.d(LOG_TAG, arithmeticException.toString());
            Toast.makeText(getActivity(), R.string.arithmetical_exception_message, Toast
                    .LENGTH_LONG).show();
        }
    }
}
