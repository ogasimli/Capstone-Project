package org.ogasimli.manat.dialog;

import org.ogasimli.manat.adapter.SelectCurrencyAdapter;
import org.ogasimli.manat.helper.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import manat.ogasimli.org.manat.R;

/**
 * DialogFragment class for selection of currency
 *
 * Created by Orkhan Gasimli on 12.04.2016.
 */
public class SelectCurrencyDialogFragment extends DialogFragment {

    @Bind(R.id.select_currency_recyclerview)
    RecyclerView mRecyclerView;

    private SelectCurrencyAdapter mSelectCurrencyAdapter;

    public SelectCurrencyDialogFragment() {
    }

    public static SelectCurrencyDialogFragment newInstance() {
        return new SelectCurrencyDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.SelectCurrencyDialogStyle_DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_select_currency, container, false);

        ButterKnife.bind(this, rootView);

        mSelectCurrencyAdapter = new SelectCurrencyAdapter(getActivity());
        mSelectCurrencyAdapter.setOnItemClickListener(itemClickListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSelectCurrencyAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.select_currency_cancel_btn)
    public void dismissDialog(Button button) {
        getDialog().dismiss();
    }

    /*ItemClickListener for currency code*/
    private final SelectCurrencyAdapter.ClickListener itemClickListener
            = new SelectCurrencyAdapter.ClickListener() {
        @Override
        public void onItemClick(int position, View v) {
            //Write result to SharedPreferences
            String code = mSelectCurrencyAdapter.selectCurrency(position);
            SharedPreferences sharedPref = getActivity().getSharedPreferences
                    (Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.SELECTED_CODE_KEY, code);
            editor.apply();

            //Go back to MainActivityFragment
            Intent intent = new Intent();
            intent.putExtra(Constants.CODE, code);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            getDialog().dismiss();
        }
    };
}
