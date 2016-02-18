package id.co.veritrans.sdk.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.Subscribe;

import id.co.veritrans.sdk.BuildConfig;
import id.co.veritrans.sdk.R;
import id.co.veritrans.sdk.core.Constants;
import id.co.veritrans.sdk.core.Logger;
import id.co.veritrans.sdk.core.SdkUtil;
import id.co.veritrans.sdk.core.VeritransSDK;
import id.co.veritrans.sdk.eventbus.bus.VeritransBusProvider;
import id.co.veritrans.sdk.eventbus.callback.TransactionBusCallback;
import id.co.veritrans.sdk.eventbus.events.GeneralErrorEvent;
import id.co.veritrans.sdk.eventbus.events.NetworkUnavailableEvent;
import id.co.veritrans.sdk.eventbus.events.TransactionFailedEvent;
import id.co.veritrans.sdk.eventbus.events.TransactionSuccessEvent;
import id.co.veritrans.sdk.fragments.BBMMoneyPaymentFragment;
import id.co.veritrans.sdk.fragments.BBMMoneyPaymentStatusFragment;
import id.co.veritrans.sdk.fragments.BankTransferFragment;
import id.co.veritrans.sdk.fragments.InstructionBBMMoneyFragment;
import id.co.veritrans.sdk.models.TransactionResponse;
import id.co.veritrans.sdk.utilities.Utils;
import id.co.veritrans.sdk.widgets.TextViewFont;
import id.co.veritrans.sdk.widgets.VeritransDialog;

/**
 * Created by Ankit on 12/3/15.
 */
public class BBMMoneyActivity extends AppCompatActivity implements View.OnClickListener, TransactionBusCallback {

    public static final String HOME_FRAGMENT = "home";
    public static final String PAYMENT_FRAGMENT = "payment";
    public static final String STATUS_FRAGMENT = "transaction_status";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong";
    public String currentFragment = "home";

    private TextViewFont textViewOrderId = null;
    private TextViewFont textViewAmount = null;
    private Button buttonConfirmPayment = null;
    private AppBarLayout appBarLayout = null;
    private TextViewFont textViewTitle = null;
    private LinearLayout layoutPayWithBBM = null;

    private VeritransSDK veritransSDK = null;
    private Toolbar toolbar = null;

    private InstructionBBMMoneyFragment instructionBBMMoneyFragment = null;
    private BBMMoneyPaymentFragment bbmMoneyPaymentFragment = null;
    private TransactionResponse transactionResponse = null;
    private String errorMessage = null;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;

    private int position = Constants.PAYMENT_METHOD_BBM_MONEY;
    private int RESULT_CODE = RESULT_CANCELED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbm_money);

        veritransSDK = VeritransSDK.getVeritransSDK();

        // get position of selected payment method
        Intent data = getIntent();
        if (data != null) {
            position = data.getIntExtra(getString(R.string.position), Constants
                    .PAYMENT_METHOD_BBM_MONEY);
        } else {
            SdkUtil.showSnackbar(BBMMoneyActivity.this, getString(R.string.error_something_wrong));
            finish();
        }

        initializeView();
        bindDataToView();
        setUpHomeFragment();
        if (!VeritransBusProvider.getInstance().isRegistered(this)) {
            VeritransBusProvider.getInstance().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (VeritransBusProvider.getInstance().isRegistered(this)) {
            VeritransBusProvider.getInstance().unregister(this);
        }
        super.onDestroy();
    }


    /**
     * set up {@link BankTransferFragment} to display payment instructions.
     */
    private void setUpHomeFragment() {
        // setup home fragment

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        instructionBBMMoneyFragment = new InstructionBBMMoneyFragment();

        fragmentTransaction.add(R.id.bbm_money_container,
                instructionBBMMoneyFragment, HOME_FRAGMENT);
        fragmentTransaction.commit();

        currentFragment = HOME_FRAGMENT;
    }

    @Override
    public void onBackPressed() {
        setResultAndFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return false;
    }

    private void initializeView() {
        textViewOrderId = (TextViewFont) findViewById(R.id.text_order_id);
        textViewAmount = (TextViewFont) findViewById(R.id.text_amount);
        textViewTitle = (TextViewFont) findViewById(R.id.text_title);
        buttonConfirmPayment = (Button) findViewById(R.id.btn_confirm_payment);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);
        layoutPayWithBBM = (LinearLayout) findViewById(R.id.layout_pay_with_bbm);

        //setup tool bar
        toolbar.setTitle(""); // disable default Text
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void bindDataToView() {
        if (veritransSDK != null) {
            textViewAmount.setText(getString(R.string.prefix_money, Utils.getFormattedAmount(veritransSDK.getTransactionRequest().getAmount())));
            textViewOrderId.setText("" + veritransSDK.getTransactionRequest().getOrderId());
            buttonConfirmPayment.setTypeface(veritransSDK.getTypefaceOpenSansSemiBold());
            buttonConfirmPayment.setOnClickListener(this);
            layoutPayWithBBM.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_confirm_payment) {

            if (SdkUtil.isBBMMoneyInstalled(BBMMoneyActivity.this)) {

                if (currentFragment.equalsIgnoreCase(HOME_FRAGMENT)) {

                    performTransaction();

                } else if (currentFragment.equalsIgnoreCase(PAYMENT_FRAGMENT)) {

                    appBarLayout.setExpanded(true);

                    if (transactionResponse != null) {
//                        setUpTransactionStatusFragment(transactionResponse);

                    } else {
                        RESULT_CODE = RESULT_OK;
                        SdkUtil.showSnackbar(BBMMoneyActivity.this, SOMETHING_WENT_WRONG);
                        onBackPressed();
                    }
                } else {
                    RESULT_CODE = RESULT_OK;
                    onBackPressed();
                }

            } else {
                Logger.i("BBM Not present");
                VeritransDialog veritransDialog = new VeritransDialog(BBMMoneyActivity.this,
                        getString(R.string.title_bbm_not_found),
                        getString(R.string.message_bbm_not_found), getString(R.string.got_it), "");
                veritransDialog.show();
            }
        }

        if (view.getId() == R.id.layout_pay_with_bbm) {

            if (veritransSDK != null && veritransSDK.getBBMCallBackUrl() != null) {

                String encodedUrl = SdkUtil.createEncodedUrl(bbmMoneyPaymentFragment.PERMATA_VA,
                        veritransSDK.getBBMCallBackUrl().getCheckStatus(),
                        veritransSDK.getBBMCallBackUrl().getBeforePaymentError(),
                        veritransSDK.getBBMCallBackUrl().getUserCancel());

                String feedUrl = BuildConfig.BBM_PREFIX_URL + encodedUrl;

                if (SdkUtil.isBBMMoneyInstalled(BBMMoneyActivity.this)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(feedUrl)));
                } else {
                    instructionBBMMoneyFragment.openPlayStore();
                }
            } else {

            }
        }
    }

    private void performTransaction() {

        SdkUtil.showProgressDialog(BBMMoneyActivity.this, false);

        //Execute transaction
        veritransSDK.paymentUsingBBMMoney();
    }

    private void setUpTransactionStatusFragment(final TransactionResponse transactionResponse) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        currentFragment = STATUS_FRAGMENT;
        buttonConfirmPayment.setVisibility(View.VISIBLE);
        layoutPayWithBBM.setVisibility(View.GONE);
        buttonConfirmPayment.setText(R.string.done);

        collapsingToolbarLayout.setVisibility(View.GONE);
        toolbar.setNavigationIcon(R.drawable.ic_close);

        BBMMoneyPaymentStatusFragment bbmMoneyPaymentStatusFragment =
                BBMMoneyPaymentStatusFragment.newInstance(transactionResponse, false);

        // setup transaction status fragment
        fragmentTransaction.replace(R.id.bbm_money_container,
                bbmMoneyPaymentStatusFragment, STATUS_FRAGMENT);
        fragmentTransaction.addToBackStack(STATUS_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void setUpTransactionFragment(final TransactionResponse transactionResponse) {
        if (transactionResponse != null) {
            // setup transaction fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            bbmMoneyPaymentFragment =
                    BBMMoneyPaymentFragment.newInstance(transactionResponse);
            fragmentTransaction.replace(R.id.bbm_money_container,
                    bbmMoneyPaymentFragment, PAYMENT_FRAGMENT);
            fragmentTransaction.addToBackStack(PAYMENT_FRAGMENT);
            fragmentTransaction.commit();
            buttonConfirmPayment.setVisibility(View.GONE);
            layoutPayWithBBM.setVisibility(View.VISIBLE);
            currentFragment = PAYMENT_FRAGMENT;
        } else {
            SdkUtil.showSnackbar(BBMMoneyActivity.this, SOMETHING_WENT_WRONG);
            onBackPressed();
        }
    }

    public int getPosition() {
        return position;
    }

    public void activateRetry() {

        if (buttonConfirmPayment != null) {
            buttonConfirmPayment.setText(getResources().getString(R.string.retry));
        }
    }

    private void setResultAndFinish() {
        Intent data = new Intent();
        data.putExtra(getString(R.string.transaction_response), transactionResponse);
        data.putExtra(getString(R.string.error_transaction), errorMessage);
        setResult(RESULT_CODE, data);
        finish();
    }

    @Subscribe
    @Override
    public void onEvent(TransactionSuccessEvent event) {
        SdkUtil.hideProgressDialog();

        if (transactionResponse != null) {
            BBMMoneyActivity.this.transactionResponse = event.getResponse();
            appBarLayout.setExpanded(true);
            setUpTransactionFragment(transactionResponse);
        } else {
            onBackPressed();
        }
    }

    @Subscribe
    @Override
    public void onEvent(TransactionFailedEvent event) {
        try {
            BBMMoneyActivity.this.errorMessage = event.getMessage();
            BBMMoneyActivity.this.transactionResponse = event.getResponse();

            SdkUtil.hideProgressDialog();
            SdkUtil.showSnackbar(BBMMoneyActivity.this, "" + errorMessage);
        } catch (NullPointerException ex) {
            Logger.e("transaction error is " + errorMessage);
        }
    }

    @Subscribe
    @Override
    public void onEvent(NetworkUnavailableEvent event) {
        BBMMoneyActivity.this.errorMessage = getString(R.string.no_network_msg);

        SdkUtil.hideProgressDialog();
        SdkUtil.showSnackbar(BBMMoneyActivity.this, "" + errorMessage);
    }

    @Subscribe
    @Override
    public void onEvent(GeneralErrorEvent event) {
        BBMMoneyActivity.this.errorMessage = event.getMessage();

        SdkUtil.hideProgressDialog();
        SdkUtil.showSnackbar(BBMMoneyActivity.this, "" + errorMessage);
    }
}
