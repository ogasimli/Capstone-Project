package org.ogasimli.manat.fragment;

import org.joda.time.DateTime;
import org.ogasimli.manat.activity.DetailActivity;
import org.ogasimli.manat.adapter.CurrencyListAdapter;
import org.ogasimli.manat.asynctask.CurrencyLoader;
import org.ogasimli.manat.asynctask.CurrencySaver;
import org.ogasimli.manat.dialog.CalculatorDialogFragment;
import org.ogasimli.manat.dialog.DatePickerDialogFragment;
import org.ogasimli.manat.dialog.SelectCurrencyDialogFragment;
import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.helper.Utilities;
import org.ogasimli.manat.object.Currency;
import org.ogasimli.manat.retrofit.ApiService;
import org.ogasimli.manat.retrofit.RetrofitAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import manat.ogasimli.org.manat.R;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Currency>> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private CurrencyListAdapter mCurrencyListAdapter;

    private ArrayList<Currency> mCurrencyList;

    private ProgressDialog mProgressDialog;

    private String mDateString;

    private String mSelectedCode;

    private String mDateBeforeChange;

    private boolean mIsConnected;

    private String mQueryString;

    private String[] mCodes;

    float viewHeight;
    boolean noSwap = true;
    private static int ANIMATION_DURATION = 300;

    @Bind(R.id.main_result_view)
    LinearLayout mResultView;

    @Bind(R.id.main_error_view)
    LinearLayout mErrorView;

    @Bind(R.id.main_foreign_linear_layout)
    LinearLayout mForeignLinerLayout;

    @Bind(R.id.main_azn_linear_layout)
    LinearLayout mAznLinearLayout;

    @Bind(R.id.recyclerview_main)
    RecyclerView mRecyclerView;

    @Bind(R.id.main_foreign_amount_textview)
    TextView mMainForeignAmountTextView;

    @Bind(R.id.main_azn_amount_textview)
    TextView mMainAznAmountTextView;

    @Bind(R.id.main_foreign_currency_text)
    TextView mMainForeignCurrencyText;

    @Bind(R.id.main_date_text)
    TextView mMainDateTextView;

    @Bind(R.id.main_rate_text)
    TextView mMainRateTextView;


    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int state = Constants.VIEW_STATE_RESULTS;
        if (mErrorView.getVisibility() == View.VISIBLE) {
            state = Constants.VIEW_STATE_ERROR;
        }

        outState.putInt(Constants.VIEW_STATE_KEY, state);
        outState.putParcelableArrayList(Constants.LIST_STATE_KEY, mCurrencyList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        //Set date
        DateTime date = new DateTime();
        String dateText = Constants.DATE_FORMATTER_DMMMMYYYY.print(date);
        mMainDateTextView.setText(dateText);

        //Set amounts equal to zero
        double zero = 0;
        String zeroStr = String.format(Locale.getDefault(), "%.2f", zero);
        mMainForeignAmountTextView.setText(zeroStr);
        mMainAznAmountTextView.setText(zeroStr);

        //Set selected currency
        mSelectedCode = Utilities.getSelectedCode(getActivity());
        mMainForeignCurrencyText.setText(mSelectedCode);

        //Instantiate RecyclerView adapter
        mCurrencyListAdapter = new CurrencyListAdapter(getActivity());

        //Instantiate RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mCurrencyListAdapter.setOnItemClickListener(itemClickListener);

        /*
        * loadData if savedInstanceState is null, load from already fetched data
        * if savedInstanceSate is not null
        */
        if (savedInstanceState == null || !savedInstanceState.containsKey(Constants.LIST_STATE_KEY)
                || !savedInstanceState.containsKey(Constants.VIEW_STATE_KEY)) {
            loadData();
        } else {
            restoreInstanceState(savedInstanceState);
        }

/*        ViewTreeObserver viewTreeObserver = mMainAznAmountTextView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mMainAznAmountTextView.getViewTreeObserver().addOnGlobalLayoutListener(this);
                    viewHeight = (float) (mMainAznAmountTextView.getHeight() * 3.5);
                    mMainAznAmountTextView.getLayoutParams();
                }
            });
        }*/

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Reload data if date was changed
        mMainDateTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mDateBeforeChange = mMainDateTextView.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mDateBeforeChange.equals(mMainDateTextView.getText().toString())) {
                    loadData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.SELECT_CURRENCY_DIALOG_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mSelectedCode = bundle.getString(Constants.CODE);
                    mMainForeignCurrencyText.setText(mSelectedCode);
                    mMainRateTextView.setText(Utilities.getRateByCode(mCurrencyList, mSelectedCode));
                    Log.d(LOG_TAG, "Selected code is: " + mSelectedCode);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d(LOG_TAG, "Unable to get selected code");
                }
                break;
            case Constants.CALCULATOR_DIALOG_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String amount = bundle.getString(Constants.AMOUNT);
                    int key = bundle.getInt(Constants.BUTTON_KEY);
                    switch (key) {
                        case 0:
                            mMainForeignAmountTextView.setText(amount);
                            break;
                        case 1:
                            mMainAznAmountTextView.setText(amount);
                            break;
                        default:
                            mMainForeignAmountTextView.setText(amount);
                    }
                    Log.d(LOG_TAG, "Amount is: " + amount);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d(LOG_TAG, "Unable to get selected code");
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:
                return true;
            case R.id.menu_refresh:
                refreshData();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to load rates
     */
    private void loadData() {
        initializeData();
        showProgressDialog(true);
        //First try to check if DB has corresponding entries
        getActivity()
                .getSupportLoaderManager()
                .restartLoader(Constants.CURRENCY_LOADER_ID, null, this);

    }

    /**
     * Helper method to refresh rates
     */
    private void refreshData() {
        //Show ProgressDialog if not visible
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            showProgressDialog(true);
        }

        //Load data from API
        RestAdapter adapter = RetrofitAdapter.getRestAdapter();
        ApiService service = adapter.create(ApiService.class);
        service.getCurrencyByDate(mQueryString, new Callback<ArrayList<Currency>>() {
            @Override
            public void success(ArrayList<Currency> currencyList, Response response) {
                mCurrencyList = new ArrayList<>();
                mCurrencyList = currencyList;
                if (mCurrencyList != null && mCurrencyList.size() > 0) {
                    //Sort list in desired order
                    mCurrencyList = Utilities.sortList(mCurrencyList);
                    Log.d(LOG_TAG, "Loaded from API");
                    //Delete old data and insert new
                    new CurrencySaver(getActivity(), mDateString).execute(mCurrencyList);
                    Log.d(LOG_TAG, "Inserted into DB");
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
     * Helper method to get necessary data before loading
     */
    private void initializeData() {
        mDateString = Utilities.modifyDateString(mMainDateTextView.getText().toString(),
                Constants.DATE_FORMATTER_DDMMMYYYY,
                Constants.DATE_FORMATTER_WITH_DASH,
                Constants.DATE_APPENDIX);
        mQueryString = Utilities.buildQueryString(mDateString, null, null);
        mCodes = getResources().getStringArray(R.array.currency_codes);
    }

    /**
     * Helper method to construct views if data is loaded
     */
    private void showResultView() {

        //View and hide relevant LinearLayouts
        mResultView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);

        //Set adapter to RecyclerView
        mCurrencyListAdapter.setCurrencyList(mCurrencyList);
        mRecyclerView.setAdapter(mCurrencyListAdapter);

        //Set main rate text
        mMainRateTextView.setText(Utilities.getRateByCode(mCurrencyList, mSelectedCode));

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

    private void restoreInstanceState(Bundle savedInstanceState) {
        int state = savedInstanceState.getInt(Constants.VIEW_STATE_KEY,
                Constants.VIEW_STATE_ERROR);
        switch (state) {
            case Constants.VIEW_STATE_ERROR:
                showErrorView();
                break;
            case Constants.VIEW_STATE_RESULTS:
                mCurrencyList = savedInstanceState.getParcelableArrayList
                        (Constants.LIST_STATE_KEY);

                showResultView();
                break;
        }
    }

    //TODO: Enhance swap animation
    @OnClick(R.id.main_swap_fab)
    public void swapLayouts(FloatingActionButton swapFab) {
/*        if (noSwap) {
            TranslateAnimation ta1 = new TranslateAnimation(0, 0, 0, viewHeight);
            ta1.setDuration(ANIMATION_DURATION);
            ta1.setFillAfter(true);
            mForeignLinerLayout.startAnimation(ta1);
            mForeignLinerLayout.bringToFront();

            TranslateAnimation ta2 = new TranslateAnimation(0, 0, 0, -viewHeight);
            ta2.setDuration(ANIMATION_DURATION);
            ta2.setFillAfter(true);
            mAznLinearLayout.startAnimation(ta2);
            mAznLinearLayout.bringToFront();

            noSwap = false;
        } else {
            TranslateAnimation ta1 = new TranslateAnimation(0, 0, viewHeight, 0);
            ta1.setDuration(ANIMATION_DURATION);
            ta1.setFillAfter(true);
            mForeignLinerLayout.startAnimation(ta1);
            mForeignLinerLayout.bringToFront();

            TranslateAnimation ta2 = new TranslateAnimation(0, 0, -viewHeight, 0);
            ta2.setDuration(ANIMATION_DURATION);
            ta2.setFillAfter(true);
            mAznLinearLayout.startAnimation(ta2);
            mAznLinearLayout.bringToFront();

            noSwap = true;
        }*/
    }

    @OnClick(R.id.reload_text)
    public void reloadTextViewClick(TextView texView) {
        refreshData();
    }

    @OnClick(R.id.main_date_text)
    public void dateTextViewClick(TextView textView) {
        showDatePickerDialog();
    }

    @OnClick(R.id.main_foreign_amount_textview)
    public void foreignAmountTextViewClick(TextView textView) {
        showCalculatorDialog(0);
    }

    @OnClick(R.id.main_azn_amount_textview)
    public void aznAmountTextViewClick(TextView textView) {
        showCalculatorDialog(1);
    }

    @OnClick(R.id.main_foreign_currency_text)
    public void currencyCodeTextViewClick(TextView textView) {
        showSelectCurrencyDialog();
    }

    private void showDatePickerDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATE_PICKER_BUNDLE_KEY, mMainDateTextView.getText().toString());
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogFragment datePickerDialog = DatePickerDialogFragment.newInstance();
        datePickerDialog.setArguments(bundle);
        datePickerDialog.show(fm, Constants.DATA_PICKER_DIALOG_FRAGMENT);
    }

    private void showCalculatorDialog(int key) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CALCULATOR_BUNDLE_KEY, key);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        CalculatorDialogFragment calculatorDialog = CalculatorDialogFragment.newInstance();
        calculatorDialog.setArguments(bundle);
        calculatorDialog.setTargetFragment(this, Constants.CALCULATOR_DIALOG_RESULT);
        calculatorDialog.show(fm, Constants.CALCULATOR_DIALOG_FRAGMENT);
    }

    private void showSelectCurrencyDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        SelectCurrencyDialogFragment selectCurrencyDialog = SelectCurrencyDialogFragment.newInstance();
        selectCurrencyDialog.setTargetFragment(this, Constants.SELECT_CURRENCY_DIALOG_RESULT);
        selectCurrencyDialog.show(fm, Constants.SELECT_CURRENCY_DIALOG_FRAGMENT);
    }

    /* ItemClickListener for currency list items */
    private final CurrencyListAdapter.ClickListener itemClickListener
            = new CurrencyListAdapter.ClickListener() {
        @Override
        public void onItemClick(int position, View v) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            String code = mCurrencyList.get(position).getCode();
            intent.putExtra(Constants.SELECTED_CODE_KEY, code);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(int position, View v) {
            //TODO: Implement long click on list item
        }
    };

    /* Callbacks to query data from movie table */
    @Override
    public Loader<ArrayList<Currency>> onCreateLoader(int id, Bundle args) {
        return new CurrencyLoader(getActivity(), mDateString, mCodes);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Currency>> loader, ArrayList<Currency> data) {
        mCurrencyList = data;
        //If there is no corresponding entries in DB then make API call and write to DB
        if (mCurrencyList != null && mCurrencyList.size() == 44) {
            Log.d(LOG_TAG, "Loaded from DB");
            showResultView();
        } else {
            refreshData();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Currency>> loader) {

    }
}
