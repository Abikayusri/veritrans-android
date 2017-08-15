package com.midtrans.sdk.uikit.views.banktransfer.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.midtrans.sdk.corekit.core.PaymentType;
import com.midtrans.sdk.corekit.models.TransactionResponse;
import com.midtrans.sdk.uikit.R;
import com.midtrans.sdk.uikit.abstracts.BasePaymentActivity;
import com.midtrans.sdk.uikit.adapters.InstructionPagerAdapter;
import com.midtrans.sdk.uikit.constants.AnalyticsEventName;
import com.midtrans.sdk.uikit.fragments.BankTransferFragment;
import com.midtrans.sdk.uikit.utilities.SdkUIFlowUtil;
import com.midtrans.sdk.uikit.utilities.UiKitConstants;
import com.midtrans.sdk.uikit.views.banktransfer.status.BankTransferStatusActivity;
import com.midtrans.sdk.uikit.widgets.DefaultTextView;
import com.midtrans.sdk.uikit.widgets.FancyButton;

/**
 * Created by ziahaqi on 8/9/17.
 */

public class BankTransferPaymentActivity extends BasePaymentActivity implements BankTransferPaymentView {

    public static final String EXTRA_BANK_TYPE = "bank.type";
    private BankTransferPaymentPresenter presenter;

    private ViewPager pagerInstruction;
    private TabLayout tabInstruction;
    private AppCompatEditText editEmail;
    private FancyButton buttonPay;
    private ImageView imageProgress;

    private TextInputLayout containerEmail;
    private LinearLayout containerProgress;

    private DefaultTextView textTitle;
    private DefaultTextView textNotificationToken;
    private DefaultTextView textNotificationOtp;


    private String paymentType;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer_payment);
        initProperties();
        trackPage();
        initTabPager();
        initPaymentButton();
        initProgressLayout();
        initData();
    }

    private void initPaymentButton() {
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SdkUIFlowUtil.hideKeyboard(BankTransferPaymentActivity.this);

                String email = editEmail.getText().toString().trim();
                if (checkEmailValidity(email)) {
                    showProgressLayout();
                    presenter.startPayment(paymentType, email);
                }
            }
        });
    }

    private boolean checkEmailValidity(String email) {
        boolean valid = true;

        if (presenter.isEmailValid(email)) {
            containerEmail.setError("");
        } else {
            containerEmail.setError(getString(R.string.error_invalid_email_id));
            valid = false;
        }

        return valid;
    }

    private void initProperties() {
        presenter = new BankTransferPaymentPresenter(this);

        paymentType = getIntent().getStringExtra(EXTRA_BANK_TYPE);
    }

    private void trackPage() {
        switch (paymentType) {
            case PaymentType.BCA_VA:
                presenter.trackEvent(AnalyticsEventName.PAGE_BCA_VA);
                break;
            case PaymentType.PERMATA_VA:
                presenter.trackEvent(AnalyticsEventName.PAGE_PERMATA_VA);
                break;
            case PaymentType.ALL_VA:
                presenter.trackEvent(AnalyticsEventName.PAGE_PERMATA_VA);
                break;
            case PaymentType.E_CHANNEL:
                presenter.trackEvent(AnalyticsEventName.PAGE_MANDIRI_BILL);
                break;
            case PaymentType.BNI_VA:
                presenter.trackEvent(AnalyticsEventName.PAGE_BNI_VA);
                break;
        }
    }

    private void initProgressLayout() {
        Glide.with(this)
                .load(R.drawable.midtrans_loader)
                .asGif()
                .into(imageProgress);
    }


    @Override
    public void bindViews() {
        pagerInstruction = (ViewPager) findViewById(R.id.tab_view_pager);
        tabInstruction = (TabLayout) findViewById(R.id.tab_instructions);
        editEmail = (AppCompatEditText) findViewById(R.id.edit_email);
        imageProgress = (ImageView) findViewById(R.id.progress_bar_image);
        buttonPay = (FancyButton) findViewById(R.id.btn_pay_now);

        textTitle = (DefaultTextView) findViewById(R.id.text_page_title);
        textNotificationToken = (DefaultTextView) findViewById(R.id.text_notificationToken);
        textNotificationOtp = (DefaultTextView) findViewById(R.id.text_notificationOtp);

        containerEmail = (TextInputLayout) findViewById(R.id.container_email);
        containerProgress = (LinearLayout) findViewById(R.id.progress_container);
    }

    @Override
    public void initTheme() {
        tabInstruction.setSelectedTabIndicatorColor(getPrimaryColor());
        setPrimaryBackgroundColor(buttonPay);
        setBackgroundTintList(editEmail);
        setTextInputlayoutFilter(containerEmail);
    }


    private void initTabPager() {

        tabInstruction.setSelectedTabIndicatorColor(getPrimaryColor());
        pagerInstruction.setPageMargin(getResources().getDimensionPixelSize(R.dimen.twenty_dp));

        int pageNumber;
        String title;
        String bankType = paymentType;
        switch (bankType) {
            case PaymentType.BCA_VA:
                title = getString(R.string.bank_bca_transfer);
                pageNumber = 3;

                //track page bca va overview
                presenter.trackEvent(AnalyticsEventName.PAGE_BCA_VA_OVERVIEW);
                break;
            case PaymentType.E_CHANNEL:
                title = getString(R.string.mandiri_bill_transfer);
                pageNumber = 2;

                //track page mandiri bill overview
                presenter.trackEvent(AnalyticsEventName.PAGE_MANDIRI_BILL_OVERVIEW);
                break;
            case PaymentType.PERMATA_VA:
                title = getString(R.string.bank_permata_transfer);
                pageNumber = 2;

                //track page permata va overview
                presenter.trackEvent(AnalyticsEventName.PAGE_PERMATA_VA_OVERVIEW);
                break;
            case PaymentType.ALL_VA:
                title = getString(R.string.other_bank_transfer);
                pageNumber = 3;

                //track page other bank va overview
                presenter.trackEvent(AnalyticsEventName.PAGE_OTHER_BANK_VA_OVERVIEW);
                break;
            case PaymentType.BNI_VA:
                title = getString(R.string.bank_bni_transfer);
                pageNumber = 3;

                // track page bni va overview
                presenter.trackEvent(AnalyticsEventName.PAGE_OTHER_BANK_VA_OVERVIEW);
                break;
            default:
                title = getString(R.string.bank_transfer);
                pageNumber = 0;
                break;
        }

        setPageTitle(title);
        InstructionPagerAdapter adapter = new InstructionPagerAdapter(this, bankType, getSupportFragmentManager(), pageNumber);
        pagerInstruction.setAdapter(adapter);
        setUpTabLayout();
    }

    private void initData() {
        editEmail.setText(presenter.getUserEmail());
        editEmail.clearFocus();
    }

    public void setPageTitle(String pageTitle) {
        this.textTitle.setText(pageTitle);
    }


    private void setUpTabLayout() {

        tabInstruction.setupWithViewPager(pagerInstruction);
        tabInstruction.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pagerInstruction.setCurrentItem(tab.getPosition());
                initTopNotification(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void showProgressLayout() {
        containerProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgressLayout() {
        containerProgress.setVisibility(View.GONE);
    }

    private void initPaymentStatus(TransactionResponse response) {
        if (!TextUtils.isEmpty(paymentType) && paymentType.equals(PaymentType.E_CHANNEL)) {
            showEchannelStatusPage(response);
        } else {
            showBankTransferStatusPage(response);
        }
    }

    private void showEchannelStatusPage(TransactionResponse response) {
        Intent intent = new Intent(this, BankTransferStatusActivity.class);
        intent.putExtra(BankTransferStatusActivity.EXTRA_PAYMENT_RESULT, response);
        intent.putExtra(BankTransferStatusActivity.EXTRA_BANK_TYPE, paymentType);
        startActivityForResult(intent, UiKitConstants.INTENT_CODE_PAYMENT_STATUS);
    }

    private void showBankTransferStatusPage(TransactionResponse response) {
        Intent intent = new Intent(this, BankTransferStatusActivity.class);
        intent.putExtra(BankTransferStatusActivity.EXTRA_PAYMENT_RESULT, response);
        intent.putExtra(BankTransferStatusActivity.EXTRA_BANK_TYPE, paymentType);
        startActivityForResult(intent, UiKitConstants.INTENT_CODE_PAYMENT_STATUS);
    }

    private void finishPayment(int resultCode) {
        Intent data = new Intent();
        data.putExtra(getString(R.string.transaction_response), presenter.getTransactionResponse());
        setResult(resultCode, data);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UiKitConstants.INTENT_CODE_PAYMENT_STATUS) {
            finishPayment(RESULT_OK);
        }
    }

    @Override
    public void onPaymentSuccess(TransactionResponse response) {
        hideProgressLayout();
        initPaymentStatus(response);
    }

    @Override
    public void onPaymentFailure(TransactionResponse response) {
        hideProgressLayout();
        initPaymentStatus(response);
    }

    @Override
    public void onPaymentError(Throwable error) {
        hideProgressLayout();
    }

    @Override
    public void onBankTranferPaymentUnavailable(String bankType) {
        hideProgressLayout();
    }

    private void initTopNotification(int position) {
        if (!TextUtils.isEmpty(paymentType)) {
            if (paymentType.equals(PaymentType.BCA_VA)) {
                if (position == 1) {
                    showTokenNotification(true);
                } else {
                    showTokenNotification(false);
                }
            } else if (paymentType.equals(BankTransferFragment.TYPE_BNI)) {
                if (position == 1) {
                    showOtpNotification(true);
                } else {
                    showOtpNotification(false);
                }
            } else {
                showOtpNotification(false);
                showTokenNotification(false);
            }
        }

    }

    private void showTokenNotification(boolean show) {
        if (show) {
            textNotificationToken.setVisibility(View.VISIBLE);
            final Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
            textNotificationToken.startAnimation(animation);

        } else {
            textNotificationToken.setVisibility(View.GONE);
            textNotificationToken.setAnimation(null);
        }
    }

    private void showOtpNotification(boolean show) {
        if (show) {
            textNotificationOtp.setVisibility(View.VISIBLE);
            final Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
            textNotificationOtp.startAnimation(animation);

        } else {
            textNotificationOtp.setVisibility(View.GONE);
            textNotificationOtp.setAnimation(null);
        }
    }
}
