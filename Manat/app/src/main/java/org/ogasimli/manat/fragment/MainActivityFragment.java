package org.ogasimli.manat.fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;

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
import org.ogasimli.manat.model.Currency;
import org.ogasimli.manat.retrofit.ApiService;
import org.ogasimli.manat.retrofit.RetrofitAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import manat.ogasimli.org.manat.R;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * MainActivityFragment class
 *
 * Created by Orkhan Gasimli on 10.01.2016.
 */
public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Currency>>,
        GoogleApiClient.OnConnectionFailedListener {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private CurrencyListAdapter mCurrencyListAdapter;

    private ArrayList<Currency> mCurrencyList;

    private ProgressDialog mProgressDialog;

    private String mDateString;

    private String mSelectedCode;

    private String mDateBeforeChange;

    private String[] mCodes;

    private boolean mIgnoreChange = false;

    private int mAmountField = 0;

    private int mSwapOrder = 0;

    private Unbinder mUnbinder;

    private FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.main_result_view)
    LinearLayout mResultView;

    @BindView(R.id.main_error_view)
    LinearLayout mErrorView;

    @BindView(R.id.main_converter_root_view)
    LinearLayout mRootView;

    @BindView(R.id.main_foreign_linear_layout)
    LinearLayout mForeignLinerLayout;

    @BindView(R.id.main_azn_linear_layout)
    LinearLayout mAznLinearLayout;

    @BindView(R.id.recyclerview_main)
    RecyclerView mRecyclerView;

    @BindView(R.id.main_foreign_amount_textview)
    TextView mMainForeignAmountTextView;

    @BindView(R.id.main_azn_amount_textview)
    TextView mMainAznAmountTextView;

    @BindView(R.id.main_foreign_currency_text)
    TextView mMainForeignCurrencyText;

    @BindView(R.id.main_date_text)
    TextView mMainDateTextView;

    @BindView(R.id.main_rate_text)
    TextView mMainRateTextView;

    @BindView(R.id.main_swap_fab)
    FloatingActionButton mSwapFab;

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;

    @BindView(R.id.snackBar_layout)
    FrameLayout mFrameLayout;

    @Nullable
    @BindView(R.id.adView)
    AdView mAdView;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        // Create an auto-managed GoogleApiClient with access to App Invites.
        new GoogleApiClient.Builder(getActivity())
                .addApi(AppInvite.API)
                .enableAutoManage(getActivity(), this)
                .build();

        // Initialize the FirebaseAnalytics object
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        //Initialize Toolbar
        initToolbar();

        //Load add banner
        loadAd(mAdView);

        //Set date
        DateTime date = new DateTime();
        String dateText = Constants.DATE_FORMATTER_DMMMMYYYY.print(date);
        mMainDateTextView.setText(dateText);

        //Format amount to have default locale decimal separator
        String foreignAmount = mMainForeignAmountTextView.getText().toString();
        String aznAmount = mMainAznAmountTextView.getText().toString();
        mIgnoreChange = true;
        mMainForeignAmountTextView.setText(foreignAmount.replace(",", Utilities.getDecimalSeparator()));
        mIgnoreChange = true;
        mMainAznAmountTextView.setText(aznAmount.replace(",", Utilities.getDecimalSeparator()));

        //Set TextWatchers
        mMainDateTextView.addTextChangedListener(mDateWatcher);
        mMainRateTextView.addTextChangedListener(mRateWatcher);
        mMainForeignAmountTextView.addTextChangedListener(mForeignAmountWatcher);
        mMainAznAmountTextView.addTextChangedListener(mAznAmountWatcher);

        //Set selected currency
        mSelectedCode = Utilities.getSelectedCode(getActivity());
        mMainForeignCurrencyText.setText(mSelectedCode);

        //Instantiate RecyclerView adapter
        mCurrencyListAdapter = new CurrencyListAdapter(getActivity());

        //Instantiate RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mCurrencyListAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mCurrencyListAdapter.setOnItemClickListener(itemClickListener);

        /*
        * loadData if savedInstanceState is null, load from already fetched data
        * if savedInstanceSate is not null
        */
        if (savedInstanceState == null || !savedInstanceState.containsKey(Constants.LIST_STATE_KEY)
                || !savedInstanceState.containsKey(Constants.VIEW_STATE_KEY)
                || !savedInstanceState.containsKey(Constants.PRESSED_AMOUNT_FIELD_KEY)) {
            loadData();
        } else {
            restoreInstanceState(savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int state = Constants.VIEW_STATE_RESULTS;
        if (mErrorView.getVisibility() == View.VISIBLE) {
            state = Constants.VIEW_STATE_ERROR;
        }

        outState.putInt(Constants.VIEW_STATE_KEY, state);
        outState.putInt(Constants.PRESSED_AMOUNT_FIELD_KEY, mAmountField);
        outState.putBoolean(Constants.IGNORE_CHANGE_KEY, mIgnoreChange);
        outState.putInt(Constants.SWAP_ORDER_KEY, mSwapOrder);
        outState.putParcelableArrayList(Constants.LIST_STATE_KEY, mCurrencyList);
    }

    @Override
    public void onDestroyView() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroyView();

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mUnbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        switch (requestCode) {
            case Constants.SELECT_CURRENCY_DIALOG_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mSelectedCode = bundle.getString(Constants.CODE);
                    mMainForeignCurrencyText.setText(mSelectedCode);
                    mMainRateTextView.setText(Utilities.getRateByCode(mCurrencyList, mSelectedCode));
                    Log.d(LOG_TAG, "Selected code is: " + mSelectedCode);
                } else {
                    Log.d(LOG_TAG, "Unable to get selected code");
                }
                break;
            case Constants.CALCULATOR_DIALOG_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String amount = bundle.getString(Constants.AMOUNT);
                    String resultString = null;
                    if (amount != null) {
                        resultString = Utilities.reformatAmount(amount);
                    }
                    Double result = 0.0;
                    try {
                        result = Double.parseDouble(resultString);
                    } catch (NumberFormatException e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                    resultString = String.format(Locale.getDefault(), "%,.2f", result);
                    mAmountField = bundle.getInt(Constants.BUTTON_KEY);
                    switch (mAmountField) {
                        case 0:
                            mMainForeignAmountTextView.setText(resultString);
                            break;
                        case 1:
                            mMainAznAmountTextView.setText(resultString);
                            break;
                        default:
                            mMainForeignAmountTextView.setText(resultString);
                    }
                    Log.d(LOG_TAG, "Amount is: " + resultString);
                } else {
                    Log.d(LOG_TAG, "Unable to get selected code");
                }
                break;
            case Constants.INVITATION_REQUEST_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    // Check how many invitations were sent and log a message
                    // The ids array contains the unique invitation ids for each invitation sent
                    // (one for each contact select by the user). You can use these for analytics
                    // as the ID will be consistent on the sending and receiving devices.
                    String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                    Log.d(LOG_TAG, getString(R.string.sent_invitations_fmt, ids.length));
                } else {
                    // Sending failed or it was canceled, show failure message to the user
                    showMessage(getString(R.string.send_failed));
                }
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
            case R.id.menu_refresh:
                refreshData();
                break;
            case R.id.menu_invite:
                onInviteClicked();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to show SnackBar
     */
    private void showMessage(String msg) {
        final Snackbar snackbar = Snackbar.make(mFrameLayout, msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        ViewGroup viewGroup = (ViewGroup) snackbar.getView();
        viewGroup.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        snackbar.show();
    }

    /**
     * Helper method to invite friends
     */
    private void onInviteClicked() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(getActivity());
        if (code == ConnectionResult.SUCCESS) {
            Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                    .setMessage(getString(R.string.invitation_message))
                    .setCustomImage(Uri.parse(getString(R.string.invitation_image)))
                    .setCallToActionText(getString(R.string.invitation_cta))
                    .build();
            startActivityForResult(intent, Constants.INVITATION_REQUEST_RESULT);
        } else {
            AlertDialog alertDialog =
                    new AlertDialog.Builder(getActivity(), R.style.DatePickerDialogStyle).setMessage(
                            getActivity().getString(R.string.google_play_missing_error_message))
                            .create();
            alertDialog.show();
        }
    }

    /**
     * Initialize Toolbar
     */
    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        if (mToolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * TextWatcher for mMainDateTextView
     */
    private final TextWatcher mDateWatcher = new TextWatcher() {
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
    };

    /**
     * TextWatcher for mMainRateTextView
     */
    private final TextWatcher mRateWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mIgnoreChange = true;
            String fAmountString = mMainForeignAmountTextView.getText().toString();
            String aAmountString = mMainAznAmountTextView.getText().toString();
            String rateString;
            if (mMainRateTextView.getText().toString().isEmpty() ||
                    mMainRateTextView.getText().toString().equals("")) {
                rateString = "1";
            } else {
                rateString = mMainRateTextView.getText().toString();
            }
            double fAmount = Double.parseDouble(Utilities.reformatAmount(fAmountString));
            double aAmount = Double.parseDouble(Utilities.reformatAmount(aAmountString));
            double rate = Double.parseDouble(Utilities.reformatAmount(rateString));
            double result;
            String resultString;
            switch (mSwapOrder) {
                case 0:
                    result = fAmount * rate;
                    resultString = String.format(Locale.getDefault(), "%,.2f", result);
                    mMainAznAmountTextView.setText(resultString);
                    break;
                case 1:
                    result = aAmount / rate;
                    resultString = String.format(Locale.getDefault(), "%,.2f", result);
                    mMainForeignAmountTextView.setText(resultString);
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * TextWatcher for mMainAznAmountTextView
     */
    private final TextWatcher mAznAmountWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!mIgnoreChange) {
                mIgnoreChange = true;
                String amountString = mMainAznAmountTextView.getText().toString();
                String rateString;
                if (mMainRateTextView.getText().toString().isEmpty() ||
                        mMainRateTextView.getText().toString().equals("")) {
                    rateString = "1";
                } else {
                    rateString = mMainRateTextView.getText().toString();
                }
                double amount = Double.parseDouble(Utilities.reformatAmount(amountString));
                double rate = Double.parseDouble(Utilities.reformatAmount(rateString));
                double result = amount / rate;
                String resultString = String.format(Locale.getDefault(), "%,.2f", result);
                mMainForeignAmountTextView.setText(resultString);
            } else {
                mIgnoreChange = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * TextWatcher for mMainForeignAmountTextView
     */
    private final TextWatcher mForeignAmountWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!mIgnoreChange) {
                mIgnoreChange = true;
                String amountString = mMainForeignAmountTextView.getText().toString();
                String rateString;
                if (mMainRateTextView.getText().toString().isEmpty() ||
                        mMainRateTextView.getText().toString().equals("")) {
                    rateString = "1";
                } else {
                    rateString = mMainRateTextView.getText().toString();
                }
                double amount = Double.parseDouble(Utilities.reformatAmount(amountString));
                double rate = Double.parseDouble(Utilities.reformatAmount(rateString));
                double result = amount * rate;
                String resultString = String.format(Locale.getDefault(), "%,.2f", result);
                mMainAznAmountTextView.setText(resultString);
            } else {
                mIgnoreChange = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
        service.getCurrencyByDate(mDateString, new Callback<ArrayList<Currency>>() {
            @SuppressWarnings("unchecked")
            @Override
            public void success(ArrayList<Currency> currencyList, Response response) {
                mCurrencyList = new ArrayList<>();
                mCurrencyList = currencyList;
                if (mCurrencyList != null && mCurrencyList.size() == 44) {
                    //Sort list in desired order
                    mCurrencyList = Utilities.sortList(mCurrencyList);
                    Log.d(LOG_TAG, "Loaded from API");
                    //Delete old data and insert new
                    new CurrencySaver(getActivity(), mDateString).execute(mCurrencyList);
                    Log.d(LOG_TAG, "Inserted into DB");
                    showResultView();

                    //Update widget if today rates are updated
                    DateTime dateTime = new DateTime();
                    String dateString = Constants.DATE_FORMATTER_WITH_DASH.print(dateTime)
                            + Constants.DATE_APPENDIX;
                    if (dateString.equals(mDateString)) {
                        updateWidgets();
                    }
                } else {
                    showErrorView();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showErrorView();
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

    /*
    * Helper method to load ad banner
    */
    private void loadAd(AdView adView) {
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build() to get test
        // ads on this device."
        AdRequest adRequest = new AdRequest.Builder().build();
        if (adView != null) {
            adView.loadAd(adRequest);
        }
    }

    /*
    * Helper method to restore instance state
    */
    private void restoreInstanceState(Bundle savedInstanceState) {
        mAmountField = savedInstanceState.getInt(Constants.PRESSED_AMOUNT_FIELD_KEY);
        mIgnoreChange = savedInstanceState.getBoolean(Constants.IGNORE_CHANGE_KEY);
        mSwapOrder = savedInstanceState.getInt(Constants.SWAP_ORDER_KEY);
        int state = savedInstanceState.getInt(Constants.VIEW_STATE_KEY,
                Constants.VIEW_STATE_ERROR);

        switch (state) {
            case Constants.VIEW_STATE_ERROR:
                showErrorView();
                break;
            case Constants.VIEW_STATE_RESULTS:
                mCurrencyList = savedInstanceState.getParcelableArrayList(Constants.LIST_STATE_KEY);
                if (mCurrencyList != null && mCurrencyList.size() == 44) {
                    showResultView();
                } else {
                    loadData();
                }
                break;
        }

        if (mSwapOrder == 1) {
            TransitionManager.beginDelayedTransition(mRootView, new ChangeBounds());
            mRootView.removeView(mForeignLinerLayout);
            mRootView.removeView(mAznLinearLayout);

            //swap layouts
            mRootView.addView(mForeignLinerLayout, mRootView.getChildCount());
            mRootView.addView(mAznLinearLayout, 1);

            //enable/disable ripple effects
            mMainForeignAmountTextView.setBackgroundResource(0);
            setRipple(mMainAznAmountTextView);
        }

        //Set RecyclerView unfocusable on orientation change in order to prevent converter from
        // scrolling up
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mRecyclerView.setFocusable(false);
        }
    }

    /*
    * Helper method to set ripple effect to view
    */
    private void setRipple(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue tValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, tValue, true);
            view.setBackgroundResource(tValue.resourceId);
        }
    }

    /*
    * Helper method to update app widget
    */
    private void updateWidgets() {
        Context context = getContext();
        Intent dataUpdatedIntent = new Intent(Constants.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    @OnClick(R.id.main_swap_fab)
    public void swapLayouts(FloatingActionButton swapFab) {
        //Animate fab
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AnimatedVectorDrawable fabRotation = (AnimatedVectorDrawable) getActivity().
                    getDrawable(R.drawable.fab_anim_vector_drawable);
            mSwapFab.setImageDrawable(fabRotation);
            if (fabRotation != null) {
                fabRotation.start();
            }
        }

        //Swap layouts
        TransitionManager.beginDelayedTransition(mRootView, new ChangeBounds());
        mRootView.removeView(mForeignLinerLayout);
        mRootView.removeView(mAznLinearLayout);

        switch (mSwapOrder) {
            case 0:
                //swap layouts
                mRootView.addView(mForeignLinerLayout, mRootView.getChildCount());
                mRootView.addView(mAznLinearLayout, 1);

                //enable/disable ripple effects
                mMainForeignAmountTextView.setBackgroundResource(0);
                setRipple(mMainAznAmountTextView);

                //set swap order
                mSwapOrder++;
                break;
            case 1:
                //swap layouts
                mRootView.addView(mForeignLinerLayout, 1);
                mRootView.addView(mAznLinearLayout, mRootView.getChildCount());

                //enable/disable ripple effects
                mMainAznAmountTextView.setBackgroundResource(0);
                setRipple(mMainForeignAmountTextView);

                //set swap order
                mSwapOrder--;
                break;
        }
    }

    @OnClick(R.id.reload_text)
    public void reloadTextViewClick(Button button) {
        refreshData();
    }

    @OnClick(R.id.main_date_text)
    public void dateTextViewClick(TextView textView) {
        showDatePickerDialog();
    }

    @OnClick(R.id.main_foreign_amount_textview)
    public void foreignAmountTextViewClick(TextView textView) {
        if (mSwapOrder == 0) {
            mAmountField = 0;
            showCalculatorDialog(mAmountField);
        }
    }

    @OnClick(R.id.main_azn_amount_textview)
    public void aznAmountTextViewClick(TextView textView) {
        if (mSwapOrder == 1) {
            mAmountField = 1;
            showCalculatorDialog(mAmountField);
        }
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
        datePickerDialog.show(fm, Constants.DATA_PICKER_DIALOG_FRAGMENT_TAG);
    }

    private void showCalculatorDialog(int key) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CALCULATOR_BUNDLE_KEY, key);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        CalculatorDialogFragment calculatorDialog = CalculatorDialogFragment.newInstance();
        calculatorDialog.setArguments(bundle);
        calculatorDialog.setTargetFragment(this, Constants.CALCULATOR_DIALOG_RESULT);
        calculatorDialog.show(fm, Constants.CALCULATOR_DIALOG_FRAGMENT_TAG);
    }

    private void showSelectCurrencyDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        SelectCurrencyDialogFragment selectCurrencyDialog = SelectCurrencyDialogFragment.newInstance();
        selectCurrencyDialog.setTargetFragment(this, Constants.SELECT_CURRENCY_DIALOG_RESULT);
        selectCurrencyDialog.show(fm, Constants.SELECT_CURRENCY_DIALOG_FRAGMENT_TAG);
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

            trackCurrencyClicks(code);
        }

        @Override
        public void onItemLongClick(int position, View v) {
            //TODO: Implement long click on list item
        }
    };

    /**
     * Track currency item clicks
     */
    private void trackCurrencyClicks(String selectedCode) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, selectedCode);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showMessage(getString(R.string.google_play_services_error));
    }
}
