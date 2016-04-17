package org.ogasimli.manat.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import manat.ogasimli.org.manat.R;

/**
 * Created by ogasimli on 08.01.2016.
 */
public class CalculatorDialogFragment extends DialogFragment {

    public CalculatorDialogFragment() {
    }

    public static CalculatorDialogFragment newInstance() {
        return new CalculatorDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.CalculatorDialogStyle);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.calculator_fragment, null))
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
    }

    @Override
    public void onStart() {
        super.onStart();
    }


}
