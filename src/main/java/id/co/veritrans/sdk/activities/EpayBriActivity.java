package id.co.veritrans.sdk.activities;

import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.Subscribe;

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
import id.co.veritrans.sdk.fragments.InstructionEpayBriFragment;
import id.co.veritrans.sdk.fragments.PaymentTransactionStatusFragment;
import id.co.veritrans.sdk.models.TransactionResponse;

public class EpayBriActivity extends AppCompatActivity implements View.OnClickListener, TransactionBusCallback {

    private static final int PAYMENT_WEB_INTENT = 150;
    private static int RESULT_CODE = RESULT_CANCELED;
    private Button btConfirmPayment = null;
    private Toolbar toolbar = null;
    private VeritransSDK veritransSDK = null;
    private InstructionEpayBriFragment instructionEpayBriFragment;
    private TransactionResponse transactionResponse;
    private FragmentManager fragmentManager;
    private String currentFragmentName = "";
    private String errorMessage;
    private TransactionResponse transactionResponseFromMerchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        setContentView(R.layout.activity_epay_bri);
        veritransSDK = VeritransSDK.getVeritransSDK();
        if (veritransSDK == null) {
            SdkUtil.showSnackbar(EpayBriActivity.this, Constants
                    .ERROR_SDK_IS_NOT_INITIALIZED);
            finish();
        }

        initializeViews();
        setUpFragment();
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

    private void initializeViews() {
        btConfirmPayment = (Button) findViewById(R.id.btn_confirm_payment);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btConfirmPayment.setVisibility(View.VISIBLE);
        btConfirmPayment.setOnClickListener(this);
    }

    private void setUpFragment() {

        // setup  fragment
        instructionEpayBriFragment = new InstructionEpayBriFragment();
        replaceFragment(instructionEpayBriFragment, true, false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_confirm_payment) {
            makeTransaction();
        }
    }

    private void makeTransaction() {

        SdkUtil.showProgressDialog(this, getString(R.string.processing_payment), false);
        veritransSDK.paymentUsingEpayBri();
        /*veritransSDK.paymentUsingEpayBri(EpayBriActivity.this,
                new TransactionCallback() {

                    @Override
                    public void onFailure(String errorMessage, TransactionResponse
                            transactionResponse) {
                        SdkUtil.hideProgressDialog();
                        SdkUtil.showApiFailedMessage(EpayBriActivity.this, errorMessage);
                        EpayBriActivity.this.errorMessage = errorMessage;

                    }

                    @Override
                    public void onSuccess(TransactionResponse transactionResponse) {
                        SdkUtil.hideProgressDialog();
                        if (transactionResponse != null &&
                                !TextUtils.isEmpty(transactionResponse.getRedirectUrl())) {
                            EpayBriActivity.this.transactionResponse = transactionResponse;
                            Intent intentPaymentWeb = new Intent(EpayBriActivity.this, PaymentWebActivity.class);
                            intentPaymentWeb.putExtra(Constants.WEBURL, transactionResponse.getRedirectUrl());
                            startActivityForResult(intentPaymentWeb, PAYMENT_WEB_INTENT);
                        } else {
                            SdkUtil.showApiFailedMessage(EpayBriActivity.this, getString(R.string.empty_transaction_response));
                        }

                    }
                });*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i("reqCode:" + requestCode + ",res:" + resultCode);
        if (resultCode == RESULT_OK && data!=null ) {
            String responseStr = data.getStringExtra(getString(R.string.payment_response));
            if(TextUtils.isEmpty(responseStr)){
                return;
            }
            Logger.i("responseStr:"+responseStr);
            Gson gson = new Gson();
            transactionResponseFromMerchant = gson.fromJson(responseStr, TransactionResponse.class);
            PaymentTransactionStatusFragment paymentTransactionStatusFragment =
                    PaymentTransactionStatusFragment.newInstance(transactionResponseFromMerchant);
            replaceFragment(paymentTransactionStatusFragment, true, false);
            btConfirmPayment.setVisibility(View.GONE);
            /*veritransSDK.getPaymentStatus(this, transactionResponse.getTransactionId(), new PaymentStatusCallback() {
                @Override
                public void onFailure(String errorMessage, TransactionStatusResponse transactionStatusResponse) {
                    PaymentTransactionStatusFragment paymentTransactionStatusFragment =
                            PaymentTransactionStatusFragment.newInstance(transactionResponse);
                    replaceFragment(paymentTransactionStatusFragment, true, false);
                }

                @Override
                public void onSuccess(TransactionStatusResponse transactionStatusResponse) {
                    PaymentTransactionStatusFragment paymentTransactionStatusFragment =
                            PaymentTransactionStatusFragment.newInstance(transactionResponse);
                    replaceFragment(paymentTransactionStatusFragment, true, false);
                }
            });*/
        }
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, boolean clearBackStack) {
        if (fragment != null) {
            Logger.i("replace freagment");
            boolean fragmentPopped = false;
            String backStateName = fragment.getClass().getName();

            if (clearBackStack) {
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {
                fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
            }

            if (!fragmentPopped) { //fragment not in back stack, create it.
                Logger.i("fragment not in back stack, create it");
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.bri_container_layout, fragment, backStateName);
                if (addToBackStack) {
                    ft.addToBackStack(backStateName);
                }
                ft.commit();
                currentFragmentName = backStateName;
                //currentFragment = fragment;
            }
        }
    }

    public void setResultAndFinish(){
        Intent data = new Intent();
        if (transactionResponseFromMerchant != null) {
            Logger.i("transactionResponseFromMerchant:"+transactionResponseFromMerchant.getString());
            data.putExtra(getString(R.string.transaction_response), transactionResponseFromMerchant);
        }
        data.putExtra(getString(R.string.error_transaction), errorMessage);
        setResult(RESULT_CODE, data);
        finish();
    }

    public void setResultCode(int resultCode) {
        RESULT_CODE = resultCode;
    }

    @Subscribe
    @Override
    public void onEvent(TransactionSuccessEvent event) {
        SdkUtil.hideProgressDialog();
        if (event.getResponse() != null &&
                !TextUtils.isEmpty(event.getResponse().getRedirectUrl())) {
            EpayBriActivity.this.transactionResponse = event.getResponse();
            Intent intentPaymentWeb = new Intent(EpayBriActivity.this, PaymentWebActivity.class);
            intentPaymentWeb.putExtra(Constants.WEBURL, event.getResponse().getRedirectUrl());
            startActivityForResult(intentPaymentWeb, PAYMENT_WEB_INTENT);
        } else {
            SdkUtil.showApiFailedMessage(EpayBriActivity.this, getString(R.string.empty_transaction_response));
        }
    }

    @Subscribe
    @Override
    public void onEvent(TransactionFailedEvent event) {
        EpayBriActivity.this.errorMessage = event.getMessage();

        SdkUtil.hideProgressDialog();
        SdkUtil.showApiFailedMessage(EpayBriActivity.this, errorMessage);
    }

    @Subscribe
    @Override
    public void onEvent(NetworkUnavailableEvent event) {
        EpayBriActivity.this.errorMessage = getString(R.string.no_network_msg);

        SdkUtil.hideProgressDialog();
        SdkUtil.showApiFailedMessage(EpayBriActivity.this, errorMessage);
    }

    @Subscribe
    @Override
    public void onEvent(GeneralErrorEvent event) {
        EpayBriActivity.this.errorMessage = event.getMessage();

        SdkUtil.hideProgressDialog();
        SdkUtil.showApiFailedMessage(EpayBriActivity.this, event.getMessage());
    }
}