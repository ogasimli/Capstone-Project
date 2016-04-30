package org.ogasimli.manat.adapter;

import org.ogasimli.manat.customview.MyTextViewLight;
import org.ogasimli.manat.helper.Utilities;
import org.ogasimli.manat.model.Currency;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import manat.ogasimli.org.manat.R;

/**
 * RecyclerViewAdapter for main currency list
 *
 * Created by Orkhan Gasimli on 06.01.2016.
 */
public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ViewHolder> {

    final private Context mContext;

    private static ClickListener mClickListener;

    private ArrayList<Currency> mCurrencyList;

    public CurrencyListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_cards, parent,
                false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        Currency currency = mCurrencyList.get(i);
        currency.setName(Utilities.getCurrencyName(mContext, currency.getCode()));
        holder.mCountryFlag.setImageResource(Utilities.getCurrencyImage(mContext, currency.getCode()));
        holder.mCurrency.setText(currency.getCode());
        if (Integer.valueOf(currency.getNominal()) != 1) {
            holder.mCountryName.setText(String.format("%s %s",
                    currency.getNominal(), currency.getName()));
        } else {
            holder.mCountryName.setText(currency.getName());
        }
        double value =0;
        if (!currency.getValue().equals("")) {
            value = Double.parseDouble(currency.getValue());
        }
        holder.mRate.setText(String.format(Locale.getDefault(), "%.4f", value));
        holder.mRateTrend.setImageResource(Utilities.getTrendImage(currency.getTrend()));
    }

    public void setCurrencyList(ArrayList<Currency> currencyList) {
        mCurrencyList = currencyList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCurrencyList == null ? 0 : mCurrencyList.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    /*Movie view holder class*/
    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        @Bind(R.id.list_item_country_flag)
        ImageView mCountryFlag;

        @Bind(R.id.list_item_currency_name_textview)
        TextView mCountryName;

        @Bind(R.id.list_item_currency_textview)
        TextView mCurrency;

        @Bind(R.id.list_item_rate_textview)
        MyTextViewLight mRate;

        @Bind(R.id.list_item_trend_imageview)
        ImageView mRateTrend;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(getAdapterPosition(), v);
            }
        }

        public boolean onLongClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemLongClick(getAdapterPosition(), v);
            }
            return false;
        }
    }
}
