package org.ogasimli.manat.ui.adapter;

import org.ogasimli.manat.helper.Utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import manat.ogasimli.org.manat.R;

/**
 * RecyclerViewAdapter for select currency list
 *
 * Created by Orkhan Gasimli on 06.01.2016.
 */
public class SelectCurrencyAdapter extends RecyclerView.Adapter<SelectCurrencyAdapter.ViewHolder> {

    final private Context mContext;

    private final ArrayList<String> currencyCodes;

    private static ClickListener mClickListener;

    public SelectCurrencyAdapter(Context context) {
        mContext = context;
        currencyCodes = new ArrayList<>(Arrays.asList(context.getResources()
                .getStringArray(R.array.currency_codes)));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.select_currency_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        String code = currencyCodes.get(i);
        holder.mCountryFlag.setImageResource(Utilities.getCurrencyImage(mContext, code));
        holder.mCurrency.setText(code);
        holder.mCountryName.setText(Utilities.getCurrencyName(mContext, code));
    }

    @Override
    public int getItemCount() {
        return currencyCodes == null ? 0 : currencyCodes.size();
    }

    public String selectCurrency(int i) {
        return currencyCodes.get(i);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.select_list_item_country_flag)
        ImageView mCountryFlag;

        @BindView(R.id.select_list_item_currency_name_textview)
        TextView mCountryName;

        @BindView(R.id.select_list_item_currency_textview)
        TextView mCurrency;

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
    }
}
