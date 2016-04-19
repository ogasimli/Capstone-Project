package org.ogasimli.manat.fragment;

import com.github.mikephil.charting.charts.LineChart;

import org.joda.time.DateTime;
import org.ogasimli.manat.chart.ChartMaker;
import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.helper.Utilities;
import org.ogasimli.manat.object.Currency;
import org.ogasimli.manat.retrofit.ApiService;
import org.ogasimli.manat.retrofit.RetrofitAdapter;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import manat.ogasimli.org.manat.R;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ogasimli on 10.01.2016.
 */
public class DetailActivityFragment extends Fragment {

    private String mCurrencyCode;

    private String mCurrencyName;

    private ArrayList<Currency> mMainCurrencyList;

    private ArrayList<Currency> mSecondaryCurrencyList;

    private ProgressDialog mProgressDialog;

    private String mQueryString;

    private String mTillDateString;

    private int mPressedBtnNum = 0;

    @Bind(R.id.linechart)
    LineChart mChart;

    @Bind(R.id.toolbar_detail)
    Toolbar mToolbar;

    @Bind(R.id.fab_details)
    FloatingActionButton mFab;

    @Bind(R.id.detail_result_view)
    CoordinatorLayout mResultView;

    @Bind(R.id.detail_error_view)
    LinearLayout mErrorView;

    @Bind(R.id.detail_current_rate_textview)
    TextView mCurrentRateTextView;

    @Bind(R.id.detail_average_rate_textview)
    TextView mAverageRateTextView;

    @Bind(R.id.detail_max_rate_textview)
    TextView mMaxRateTextView;

    @Bind(R.id.detail_min_rate_textview)
    TextView mMinRateTextView;

    @Bind(R.id.detail_statistic_label_text_view)
    TextView mStatisticsLabelTextView;

    @Bind(R.id.weekly_btn)
    Button mWeeklyBtn;

    @Bind(R.id.monthly_btn)
    Button mMonthlyBtn;

    @Bind(R.id.quarterly_btn)
    Button mQuarterlyBtn;

    @Bind(R.id.semi_annually_btn)
    Button mSemiAnnuallyBtn;

    @Bind(R.id.yearly_btn)
    Button mYearlyBtn;

    @BindDrawable(R.drawable.text_view_border_color_accent)
    Drawable mSelectedBackground;

    @BindDrawable(R.drawable.ripple_border_color_primary)
    Drawable mUnselectedBackground;

    @BindColor(R.color.colorPrimary)
    ColorStateList mColorPrimary;

    @BindColor(R.color.colorAccent)
    ColorStateList mColorAccent;

    public DetailActivityFragment() {
    }

    public static DetailActivityFragment getInstance(String code) {
        DetailActivityFragment fragment = new DetailActivityFragment();
        Bundle args = new Bundle();
        args.putString(Constants.SELECTED_CODE_KEY, code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int state = Constants.VIEW_STATE_RESULTS;
        if (mErrorView.getVisibility() == View.VISIBLE) {
            state = Constants.VIEW_STATE_ERROR;
        }

        outState.putInt(Constants.VIEW_STATE_KEY, state);
        outState.putInt(Constants.BUTTON_STATE_KEY, mPressedBtnNum);
        outState.putParcelableArrayList(Constants.LIST_STATE_KEY, mMainCurrencyList);
        outState.putParcelableArrayList(Constants.SECONDARY_LIST_STATE_KEY, mSecondaryCurrencyList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mCurrencyCode = getArguments().getString(Constants.SELECTED_CODE_KEY);
        if (mCurrencyCode == null) {
            throw new NullPointerException("Currency code should be put into fragment arguments.");
        }
        mCurrencyName = Utilities.getCurrencyName(getActivity(), mCurrencyCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_currency_detail, container, false);
        ButterKnife.bind(this, rootView);

        //initToolbar();
        initToolbar();

        mStatisticsLabelTextView.setText(getString(R.string.detail_statistics_label,
                "Weekly"));

        /*
        * loadData if savedInstanceState is null, load from already fetched data
        * if savedInstanceSate is not null
        */
        if (savedInstanceState == null || !savedInstanceState.containsKey(Constants.LIST_STATE_KEY)
                || !savedInstanceState.containsKey(Constants.SECONDARY_LIST_STATE_KEY)
                || !savedInstanceState.containsKey(Constants.VIEW_STATE_KEY)
                || !savedInstanceState.containsKey(Constants.BUTTON_STATE_KEY)) {
            loadData();
        } else {
            int state = savedInstanceState.getInt(Constants.VIEW_STATE_KEY,
                    Constants.VIEW_STATE_ERROR);
            switch (state) {
                case Constants.VIEW_STATE_ERROR:
                    showErrorView();
                    break;
                case Constants.VIEW_STATE_RESULTS:
                    mMainCurrencyList = savedInstanceState.getParcelableArrayList
                            (Constants.LIST_STATE_KEY);
                    mSecondaryCurrencyList = savedInstanceState.getParcelableArrayList
                            (Constants.SECONDARY_LIST_STATE_KEY);

                    showResultView();
                    break;
            }
        }

        ChartMaker chartMaker = new ChartMaker(getActivity(), mChart);
        chartMaker.createChart();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /*Initialize Toolbar*/
    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        if (mToolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mCurrencyName);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Helper method to load rates
     */
    private void loadData() {
        //Show ProgressDialog if not visible
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            showProgressDialog(true);
        }

        //Build query string
        buildQueryString();

        //Load data from API
        RestAdapter adapter = RetrofitAdapter.getRestAdapter();
        ApiService service = adapter.create(ApiService.class);
        service.getCurrencyByPeriod(mQueryString, Constants.API_EXCLUDE_FIELD_STRING,
                new Callback<ArrayList<Currency>>() {
                    @Override
                    public void success(ArrayList<Currency> currencyList, Response response) {
                        mMainCurrencyList = new ArrayList<>();
                        mMainCurrencyList = currencyList;
                        mSecondaryCurrencyList = Utilities.getDataForPeriod(mMainCurrencyList,
                                mPressedBtnNum);
                        if (mSecondaryCurrencyList != null && mSecondaryCurrencyList.size() > 0) {
                            showResultView();
                        } else {
                            showErrorView();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        showErrorView();
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                        Log.d("RetrofitError", error.toString());
                    }
                });
    }

    /**
     * Helper method to build query string before loading
     */
    private void buildQueryString() {
        DateTime tillDate = new DateTime();
        DateTime fromDate = tillDate.minusYears(1);
        String fromDateString = Constants.DATE_FORMATTER_WITH_DASH.print(fromDate)
                + Constants.DATE_APPENDIX;
        mTillDateString = Constants.DATE_FORMATTER_WITH_DASH.print(tillDate)
                + Constants.DATE_APPENDIX;
        mQueryString = Utilities.buildQueryString(fromDateString, mTillDateString, mCurrencyCode);
    }

    /**
     * Helper method to construct views if data is loaded
     */
    private void showResultView() {

        //View and hide relevant LinearLayouts
        mResultView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);

        //Analyze and set values to TextViews
        HashMap<String, String> map = Utilities.processData(mSecondaryCurrencyList, mTillDateString);
        mCurrentRateTextView.setText(map.get(Constants.CURRENT_RATE_KEY));
        mAverageRateTextView.setText(map.get(Constants.AVERAGE_RATE_KEY));
        mMaxRateTextView.setText(map.get(Constants.MAX_RATE_KEY));
        mMinRateTextView.setText(map.get(Constants.MIN_RATE_KEY));

        //Hide ProgressDialog
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            showProgressDialog(false);
        }
    }

    /**
     * Helper method to show error notification if it is failed to load data
     */
    private void showErrorView() {

        //View and hide relevant LinearLayouts
        mResultView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);

        //Hide ProgressDialog
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            showProgressDialog(false);
        }
    }

    /*
    * Helper method to show and hide ProgressDialog
    */
    private void showProgressDialog(boolean show) {
        if (show) {
            mProgressDialog = ProgressDialog.show(getActivity(),
                    getActivity().getString(R.string.progress_dialog_title),
                    getActivity().getString(R.string.progress_dialog_content), true, false);
        } else {
            mProgressDialog.dismiss();
        }
    }

    /*
    * Helper method to change Button states when one of them pressed
    */
    private void changeBtnState(int state) {
        //Create ArrayList from buttons
        ArrayList<Button> buttonList = new ArrayList<>();
        buttonList.add(mWeeklyBtn);
        buttonList.add(mMonthlyBtn);
        buttonList.add(mQuarterlyBtn);
        buttonList.add(mSemiAnnuallyBtn);
        buttonList.add(mYearlyBtn);

        String statisticsLabel = "";

        switch (state) {
            case Constants.WEEKLY_BTN_SELECTED:
                for (Button button : buttonList) {
                    if (button.getText().equals("Week")) {
                        //Make weekly_btn selected
                        setDrawable(button, mSelectedBackground);
                        button.setTextColor(mColorPrimary);
                    } else {
                        //Make other buttons unselected
                        setDrawable(button, mUnselectedBackground);
                        button.setTextColor(mColorAccent);
                    }
                }
                statisticsLabel = getActivity().getString(R.string.statistics_label_weekly);
                break;
            case Constants.MONTHLY_BTN_SELECTED:
                for (Button button : buttonList) {
                    if (button.getText().equals("Month")) {
                        //Make monthly_btn selected
                        setDrawable(button, mSelectedBackground);
                        button.setTextColor(mColorPrimary);
                    } else {
                        //Make other buttons unselected
                        setDrawable(button, mUnselectedBackground);
                        button.setTextColor(mColorAccent);
                    }
                }
                statisticsLabel = getActivity().getString(R.string.statistics_label_monthly);
                break;
            case Constants.QUARTERLY_BTN_SELECTED:
                for (Button button : buttonList) {
                    if (button.getText().equals("3 M")) {
                        //Make quarterly_btn selected
                        setDrawable(button, mSelectedBackground);
                        button.setTextColor(mColorPrimary);
                    } else {
                        //Make other buttons unselected
                        setDrawable(button, mUnselectedBackground);
                        button.setTextColor(mColorAccent);
                    }
                }
                statisticsLabel = getActivity().getString(R.string.statistics_label_quarterly);
                break;
            case Constants.SEMI_ANNUALLY_BTN_SELECTED:
                for (Button button : buttonList) {
                    if (button.getText().equals("6 M")) {
                        //Make semi_annually_btn selected
                        setDrawable(button, mSelectedBackground);
                        button.setTextColor(mColorPrimary);
                    } else {
                        //Make other buttons unselected
                        setDrawable(button, mUnselectedBackground);
                        button.setTextColor(mColorAccent);
                    }
                }
                statisticsLabel = getActivity().getString(R.string.statistics_label_semi_annually);
                break;
            case Constants.YEARLY_BTN_SELECTED:
                for (Button button : buttonList) {
                    if (button.getText().equals("Year")) {
                        //Make yearly_btn selected
                        setDrawable(button, mSelectedBackground);
                        button.setTextColor(mColorPrimary);
                    } else {
                        //Make other buttons unselected
                        setDrawable(button, mUnselectedBackground);
                        button.setTextColor(mColorAccent);
                    }
                }
                statisticsLabel = getActivity().getString(R.string.statistics_label_yearly);
                break;
        }

        mStatisticsLabelTextView.setText(getString(R.string.detail_statistics_label,
                statisticsLabel));
    }

    /*
    * Helper method to set background drawables for buttons
    */
    private void setDrawable(View view, Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    @OnClick(R.id.reload_text)
    public void reloadTextViewClick(TextView texView) {
        loadData();
    }

    @OnClick({R.id.weekly_btn, R.id.monthly_btn, R.id.quarterly_btn,
            R.id.semi_annually_btn, R.id.yearly_btn})
    public void showStatistics(Button button) {
        //Show ProgressDialog if not visible
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            showProgressDialog(true);
        }

        switch (button.getText().toString()) {
            case "Week":
                mPressedBtnNum = Constants.WEEKLY_BTN_SELECTED;
                break;
            case "Month":
                mPressedBtnNum = Constants.MONTHLY_BTN_SELECTED;
                break;
            case "3 M":
                mPressedBtnNum = Constants.QUARTERLY_BTN_SELECTED;
                break;
            case "6 M":
                mPressedBtnNum = Constants.SEMI_ANNUALLY_BTN_SELECTED;
                break;
            case "Year":
                mPressedBtnNum = Constants.YEARLY_BTN_SELECTED;
                break;
            default:
                mPressedBtnNum = Constants.WEEKLY_BTN_SELECTED;
                break;
        }

        changeBtnState(mPressedBtnNum);
        mSecondaryCurrencyList = Utilities.getDataForPeriod(mMainCurrencyList, mPressedBtnNum);
        if (mSecondaryCurrencyList != null && mSecondaryCurrencyList.size() > 0) {
            showResultView();
        } else {
            showErrorView();
        }
    }
}
