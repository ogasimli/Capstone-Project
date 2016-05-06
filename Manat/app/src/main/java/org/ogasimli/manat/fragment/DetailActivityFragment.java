package org.ogasimli.manat.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.github.mikephil.charting.charts.LineChart;

import org.joda.time.DateTime;
import org.ogasimli.manat.ManatApplication;
import org.ogasimli.manat.chart.ChartMaker;
import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.helper.Utilities;
import org.ogasimli.manat.model.Currency;
import org.ogasimli.manat.retrofit.ApiService;
import org.ogasimli.manat.retrofit.RetrofitAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import manat.ogasimli.org.manat.R;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * DetailActivityFragment class
 *
 * Created by Orkhan Gasimli on 10.01.2016.
 */
public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private String mCurrencyCode;

    private String mCurrencyName;

    private ArrayList<Currency> mMainCurrencyList;

    private ArrayList<Currency> mSecondaryCurrencyList;

    private ProgressDialog mProgressDialog;

    private String mQueryString;

    private String mTillDateString;

    private int mPressedBtnNum = 0;

    private Tracker mTracker;

    @BindView(R.id.linechart)
    LineChart mChart;

    @BindView(R.id.toolbar_detail)
    Toolbar mToolbar;

    @Nullable
    @BindView(R.id.fab_share_details)
    FloatingActionButton mFab;

    @BindView(R.id.detail_result_view)
    LinearLayout mResultView;

    @BindView(R.id.detail_error_view)
    LinearLayout mErrorView;

    @BindView(R.id.detail_current_rate_textview)
    TextView mCurrentRateTextView;

    @BindView(R.id.detail_average_rate_textview)
    TextView mAverageRateTextView;

    @BindView(R.id.detail_max_rate_textview)
    TextView mMaxRateTextView;

    @BindView(R.id.detail_min_rate_textview)
    TextView mMinRateTextView;

    @BindView(R.id.detail_statistic_label_text_view)
    TextView mStatisticsLabelTextView;

    @BindView(R.id.weekly_btn)
    Button mWeeklyBtn;

    @BindView(R.id.monthly_btn)
    Button mMonthlyBtn;

    @BindView(R.id.quarterly_btn)
    Button mQuarterlyBtn;

    @BindView(R.id.semi_annually_btn)
    Button mSemiAnnuallyBtn;

    @BindView(R.id.yearly_btn)
    Button mYearlyBtn;

    @BindColor(R.color.colorPrimary)
    ColorStateList mColorPrimary;

    @BindColor(R.color.colorAccent)
    ColorStateList mColorAccent;

    private Unbinder mUnbinder;

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

        outState.putInt(Constants.BUTTON_STATE_KEY, mPressedBtnNum);
        outState.putInt(Constants.VIEW_STATE_KEY, state);
        outState.putParcelableArrayList(Constants.LIST_STATE_KEY, mMainCurrencyList);
        outState.putParcelableArrayList(Constants.SECONDARY_LIST_STATE_KEY, mSecondaryCurrencyList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mCurrencyCode = getArguments().getString(Constants.SELECTED_CODE_KEY);
        if (mCurrencyCode == null) {
            throw new NullPointerException("Currency code should be put into fragment arguments.");
        }
        mCurrencyName = Utilities.getCurrencyName(getActivity(), mCurrencyCode);

        // Obtain the shared Tracker instance.
        ManatApplication application = (ManatApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        //initToolbar();
        initToolbar();

        //Set statistics label
        mStatisticsLabelTextView.setText(getString(R.string.detail_statistics_label,
                getString(R.string.statistics_label_weekly)));

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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Setting screen name: " + Constants.DETAIL_ACTIVITY_SCREEN_NAME);
        mTracker.setScreenName("Screen:" + Constants.DETAIL_ACTIVITY_SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            menu.findItem(R.id.menu_share).setVisible(true);
        } else {
            menu.findItem(R.id.menu_share).setVisible(false);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_share:
                trackShare();
                shareRate();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Track invite clicks
     */
    private void trackShare() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(Constants.ACTION_TYPE)
                .setAction(Constants.SHARE_ACTION)
                .build());
    }

    /**
     * Initialize Toolbar
     */
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

        //Instantiate and create LineChart
        ChartMaker chartMaker = new ChartMaker(getActivity(), mChart, mSecondaryCurrencyList);
        chartMaker.createChart();

        //Analyze and set values to TextViews
        HashMap<String, String> map = Utilities.processData(mSecondaryCurrencyList, mTillDateString);
        mCurrentRateTextView.setText(map.get(Constants.CURRENT_RATE_KEY));
        mAverageRateTextView.setText(map.get(Constants.AVERAGE_RATE_KEY));
        mMaxRateTextView.setText(map.get(Constants.MAX_RATE_KEY));
        mMinRateTextView.setText(map.get(Constants.MIN_RATE_KEY));

        //View and hide relevant LinearLayouts
        mToolbar.setVisibility(View.VISIBLE);
        mResultView.setVisibility(View.VISIBLE);

        if (mFab != null) {
            if (mFab.getVisibility() == View.GONE) {
                mFab.setVisibility(View.VISIBLE);
                //set animation for share fab
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.grow_fab);
                mFab.startAnimation(animation);
            }
        }

        mErrorView.setVisibility(View.GONE);

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
        mToolbar.setVisibility(View.GONE);
        mResultView.setVisibility(View.GONE);
        if (mFab != null) {
            mFab.setVisibility(View.GONE);
        }
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
                    getString(R.string.progress_dialog_title),
                    getString(R.string.progress_dialog_content), true, false);
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
                    if (button.getId() == R.id.weekly_btn) {
                        //Make weekly_btn selected
                        setButtonStyle(button, true);
                    } else {
                        //Make other buttons unselected
                        setButtonStyle(button, false);
                    }
                }
                statisticsLabel = getString(R.string.statistics_label_weekly);
                break;
            case Constants.MONTHLY_BTN_SELECTED:
                for (Button button : buttonList) {
                    if (button.getId() == R.id.monthly_btn) {
                        //Make monthly_btn selected
                        setButtonStyle(button, true);
                    } else {
                        //Make other buttons unselected
                        setButtonStyle(button, false);
                    }
                }
                statisticsLabel = getString(R.string.statistics_label_monthly);
                break;
            case Constants.QUARTERLY_BTN_SELECTED:
                for (Button button : buttonList) {
                    if (button.getId() == R.id.quarterly_btn) {
                        //Make quarterly_btn selected
                        setButtonStyle(button, true);
                    } else {
                        //Make other buttons unselected
                        setButtonStyle(button, false);
                    }
                }
                statisticsLabel = getString(R.string.statistics_label_quarterly);
                break;
            case Constants.SEMI_ANNUALLY_BTN_SELECTED:
                for (Button button : buttonList) {
                    if (button.getId() == R.id.semi_annually_btn) {
                        //Make semi_annually_btn selected
                        setButtonStyle(button, true);
                    } else {
                        //Make other buttons unselected
                        setButtonStyle(button, false);
                    }
                }
                statisticsLabel = getString(R.string.statistics_label_semi_annually);
                break;
            case Constants.YEARLY_BTN_SELECTED:
                for (Button button : buttonList) {
                    if (button.getId() == R.id.yearly_btn) {
                        //Make yearly_btn selected
                        setButtonStyle(button, true);
                    } else {
                        //Make other buttons unselected
                        setButtonStyle(button, false);
                    }
                }
                statisticsLabel = getString(R.string.statistics_label_yearly);
                break;
        }

        mStatisticsLabelTextView.setText(getString(R.string.detail_statistics_label,
                statisticsLabel));
    }

    /*
    * Helper method to change state of buttons
    */
    private void setButtonStyle(Button button, boolean selected) {
        if (selected) {
            //Make button selected
            button.setTextColor(mColorPrimary);
            button.setEnabled(false);
        } else {
            //Make button unselected
            button.setTextColor(mColorAccent);
            button.setEnabled(true);
        }
    }

    @OnClick(R.id.reload_text)
    public void reloadTextViewClick(Button button) {
        loadData();
    }

    @Optional
    @OnClick(R.id.fab_share_details)
    public void shareStatistics(FloatingActionButton fab) {
        trackShare();
        shareRate();
    }

    private void shareRate() {
        int period = Utilities.determinePeriod(mPressedBtnNum);
        DateTime tillDate = new DateTime();
        DateTime fromDate = tillDate.minusDays(period);
        String fromDateString = Constants.DATE_FORMATTER_DMMMMYYYY.print(fromDate);
        String tillDateString = Constants.DATE_FORMATTER_DMMMMYYYY.print(tillDate);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_statistics_text,
                mCurrencyName, fromDateString, tillDateString,
                mCurrentRateTextView.getText().toString(),
                mAverageRateTextView.getText().toString(),
                mMaxRateTextView.getText().toString(),
                mMinRateTextView.getText().toString()));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share_rate_statistics_title)));
    }

    //TODO: Optimize data processing
    @OnClick({R.id.weekly_btn, R.id.monthly_btn, R.id.quarterly_btn,
            R.id.semi_annually_btn, R.id.yearly_btn})
    public void showStatistics(Button button) {
        //Show ProgressDialog if not visible
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            showProgressDialog(true);
        }

        switch (button.getId()) {
            case R.id.weekly_btn:
                mPressedBtnNum = Constants.WEEKLY_BTN_SELECTED;
                break;
            case R.id.monthly_btn:
                mPressedBtnNum = Constants.MONTHLY_BTN_SELECTED;
                break;
            case R.id.quarterly_btn:
                mPressedBtnNum = Constants.QUARTERLY_BTN_SELECTED;
                break;
            case R.id.semi_annually_btn:
                mPressedBtnNum = Constants.SEMI_ANNUALLY_BTN_SELECTED;
                break;
            case R.id.yearly_btn:
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
