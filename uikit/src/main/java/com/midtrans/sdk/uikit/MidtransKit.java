package com.midtrans.sdk.uikit;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.midtrans.sdk.corekit.MidtransSdk;
import com.midtrans.sdk.corekit.base.enums.Environment;
import com.midtrans.sdk.corekit.base.enums.PaymentType;
import com.midtrans.sdk.corekit.core.api.merchant.model.checkout.request.CheckoutTransaction;
import com.midtrans.sdk.corekit.utilities.Logger;
import com.midtrans.sdk.uikit.base.callback.PaymentResult;

import androidx.annotation.NonNull;

import static android.webkit.URLUtil.isValidUrl;
import static com.midtrans.sdk.corekit.utilities.Constants.ERROR_SDK_CLIENT_KEY_AND_CONTEXT_PROPERLY;
import static com.midtrans.sdk.corekit.utilities.Constants.ERROR_SDK_IS_NOT_INITIALIZE_PROPERLY;
import static com.midtrans.sdk.corekit.utilities.Constants.ERROR_SDK_MERCHANT_BASE_URL_PROPERLY;
import static com.midtrans.sdk.corekit.utilities.Constants.MESSAGE_INSTANCE_NOT_INITALIZE;

public class MidtransKit {

    private static final int API_TIMEOUT_DEFAULT = 30;
    private static volatile MidtransKit SINGLETON_INSTANCE = null;
    /**
     * Mandatory property.
     */
    private Context context;
    private String merchantClientId;
    private String merchantBaseUrl;
    /**
     * Optional property.
     */
    private Environment midtransEnvironment;
    private int apiRequestTimeOut;
    private boolean isBuiltinStorageEnabled;
    private boolean isLogEnabled;
    private MidtransKitConfig midtransKitConfig;
    private PaymentResult callback;

    MidtransKit(
            Context context,
            String merchantClientId,
            String merchantBaseUrl,
            Environment midtransEnvironment,
            int apiRequestTimeOut,
            boolean isBuiltinStorageEnabled,
            boolean isLogEnabled,
            MidtransKitConfig midtransKitConfig
    ) {
        this.context = context;
        this.merchantClientId = merchantClientId;
        this.merchantBaseUrl = merchantBaseUrl;
        this.midtransEnvironment = midtransEnvironment;
        this.apiRequestTimeOut = apiRequestTimeOut;
        this.isBuiltinStorageEnabled = isBuiltinStorageEnabled;
        this.isLogEnabled = isLogEnabled;
        this.midtransKitConfig = midtransKitConfig;
        initMidtransSdk();
    }

    /**
     * Initializer.
     *
     * @param context     Context, mandatory not null.
     * @param clientId    ClientId or Merchant Client Key, mandatory not null.
     * @param merchantUrl MerchantUrl or Merchant Base Url, mandatory not null.
     * @return Builder.
     */
    public static Builder builder(
            final Context context,
            final String clientId,
            final String merchantUrl
    ) {
        return new Builder(
                context,
                clientId,
                merchantUrl
        );
    }

    /**
     * Returns instance of midtrans sdk.
     *
     * @return MidtransSdk instance.
     */
    public synchronized static MidtransKit getInstance() {
        if (doCheckSdkInitialization(SINGLETON_INSTANCE)) {
            return SINGLETON_INSTANCE;
        }
        return null;
    }

    private static <T> boolean doCheckSdkInitialization(T itemForCheck) {
        if (itemForCheck == null) {
            RuntimeException runtimeException = new RuntimeException(MESSAGE_INSTANCE_NOT_INITALIZE);
            Logger.error(runtimeException.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Returns value of merchant url.
     *
     * @return merchant url value.
     */
    public Environment getEnvironment() {
        if (doCheckSdkInitialization(midtransEnvironment)) {
            return midtransEnvironment;
        }
        return null;
    }

    /**
     * Returns value of instance context.
     *
     * @return context value.
     */
    public Context getContext() {
        if (doCheckSdkInitialization(context)) {
            return context;
        }
        return null;
    }

    /**
     * Returns value of merchant client id.
     *
     * @return merchant client id value.
     */
    public String getMerchantClientId() {
        if (doCheckSdkInitialization(merchantClientId)) {
            return merchantClientId;
        }
        return null;
    }

    /**
     * Returns value of merchant base url.
     *
     * @return merchant base url value.
     */
    public String getMerchantBaseUrl() {
        if (doCheckSdkInitialization(merchantBaseUrl)) {
            return merchantBaseUrl;
        }
        return null;
    }

    /**
     * Returns value of timeout.
     *
     * @return timeout value.
     */
    public int getApiRequestTimeOut() {
        if (doCheckSdkInitialization(apiRequestTimeOut)) {
            return apiRequestTimeOut;
        }
        return API_TIMEOUT_DEFAULT;
    }

    /**
     * Returns value of builtinstorage
     *
     * @return builtinstorage value
     */
    public boolean isBuiltinStorageEnabled() {
        if (doCheckSdkInitialization(isBuiltinStorageEnabled)) {
            return isBuiltinStorageEnabled;
        }
        return true;
    }

    /**
     * Returns value of midtransEnvironment
     *
     * @return enum for midtransEnvironment
     */
    public Environment getMidtransEnvironment() {
        return midtransEnvironment;
    }

    /**
     * Returns value of isLogEnabled
     *
     * @return boolean for isLogEnabled
     */
    public boolean isLogEnabled() {
        return isLogEnabled;
    }

    /**
     * Returns value of MidtransKitConfig
     *
     * @return object for midtransKitConfig
     */
    public MidtransKitConfig getMidtransKitConfig() {
        return midtransKitConfig;
    }

    /**
     * Start public API from MidtransKit, it use for UI Stuff and Starting payment
     *
     * Payment API can contain callback or without callback,
     * If user use without callback method, user must set the callback separately
     * This public API same with Midtrans SDK v1.x.x
     */
    public void setMidtransKitConfig(@NonNull MidtransKitConfig midtransKitConfig) {
        this.midtransKitConfig = midtransKitConfig;
    }

    public void setTransactionFinishedCallback(@NonNull PaymentResult callback) {
        this.callback = callback;
    }

    public void startPaymentUiFlow(
            @NonNull Activity context,
            @NonNull String token
    ) {
        startPaymentUiFlow(context, token, callback);
    }

    public void startPaymentUiFlow(
            @NonNull Activity context,
            @NonNull String token,
            @NonNull @PaymentType String paymentType
    ) {
        startPaymentUiFlow(context, token, paymentType, callback);
    }

    public void startPaymentUiFlow(
            @NonNull Activity context,
            @NonNull String token,
            @NonNull PaymentResult callback
    ) {
        MidtransKitFlow.paymentWithTokenFlow(context, token, callback);
    }

    public void startPaymentUiFlow(
            @NonNull Activity context,
            @NonNull String token,
            @NonNull @PaymentType String paymentType,
            @NonNull PaymentResult callback
    ) {
        MidtransKitFlow.directPaymentWithTokenFlow(context, token, paymentType, callback);
    }

    public void startPaymentUiFlow(
            @NonNull Activity context,
            @NonNull CheckoutTransaction checkoutTransaction
    ) {
        startPaymentUiFlow(context, checkoutTransaction, callback);
    }

    public void startPaymentUiFlow(
            @NonNull Activity context,
            @NonNull CheckoutTransaction checkoutTransaction,
            @NonNull @PaymentType String paymentType
    ) {
        startPaymentUiFlow(context, checkoutTransaction, paymentType, callback);
    }

    public void startPaymentUiFlow(
            @NonNull Activity context,
            @NonNull CheckoutTransaction checkoutTransaction,
            @NonNull PaymentResult callback
    ) {
        MidtransKitFlow.paymentWithTransactionFlow(context, checkoutTransaction, callback);
    }

    public void startPaymentUiFlow(
            @NonNull Activity context,
            @NonNull CheckoutTransaction checkoutTransaction,
            @NonNull @PaymentType String paymentType,
            @NonNull PaymentResult callback
    ) {
        MidtransKitFlow.directPaymentWithTransactionFlow(context, checkoutTransaction, paymentType, callback);
    }

    private void initMidtransSdk() {
        MidtransSdk
                .builder(
                        this.context,
                        this.merchantClientId,
                        this.merchantBaseUrl
                )
                .setApiRequestTimeOut(this.apiRequestTimeOut)
                .setBuiltinStorageEnabled(this.isBuiltinStorageEnabled)
                .setEnvironment(this.midtransEnvironment)
                .setLogEnabled(this.isLogEnabled)
                .build();
    }

    public static final class Builder {
        private String merchantClientId;
        private Context context;
        private String merchantBaseUrl;
        private Environment midtransEnvironment = Environment.SANDBOX;
        private int apiRequestTimeOut = API_TIMEOUT_DEFAULT;
        private boolean isLogEnabled = false;
        private boolean isBuiltinStorageEnabled = true;
        private MidtransKitConfig midtransKitConfig;

        private Builder(
                Context context,
                String clientId,
                String merchantUrl
        ) {
            this.context = context;
            this.merchantClientId = clientId;
            this.merchantBaseUrl = merchantUrl;
        }

        /**
         * set Logger visible or not.
         */
        public Builder setLogEnabled(boolean isLogEnabled) {
            this.isLogEnabled = isLogEnabled;
            return this;
        }

        public Builder setBuiltinStorageEnabled(boolean isBuiltinStorageEnabled) {
            this.isBuiltinStorageEnabled = isBuiltinStorageEnabled;
            return this;
        }

        /**
         * set Logger visible or not.
         */
        public Builder setEnvironment(Environment environment) {
            this.midtransEnvironment = environment;
            return this;
        }

        /**
         * set Logger visible or not.
         */
        public Builder setApiRequestTimeOut(int apiRequestTimeOutInSecond) {
            this.apiRequestTimeOut = apiRequestTimeOutInSecond;
            return this;
        }

        /**
         * set Custom Kit Setting.
         */
        public Builder setMidtransKitConfig(MidtransKitConfig midtransKitConfig) {
            this.midtransKitConfig = midtransKitConfig;
            return this;
        }

        /**
         * This method will start payment flow if you have set useUi field to true.
         *
         * @return it returns fully initialized object of midtrans sdk.
         */
        public MidtransKit build() {
            if (isValidData()) {
                SINGLETON_INSTANCE = new MidtransKit(
                        context,
                        merchantClientId,
                        merchantBaseUrl,
                        midtransEnvironment,
                        apiRequestTimeOut,
                        isBuiltinStorageEnabled,
                        isLogEnabled,
                        midtransKitConfig
                );
                return SINGLETON_INSTANCE;
            } else {
                Logger.error(ERROR_SDK_IS_NOT_INITIALIZE_PROPERLY);
            }
            return null;
        }

        private boolean isValidData() {
            if (merchantClientId == null || context == null) {
                RuntimeException runtimeException = new RuntimeException(ERROR_SDK_CLIENT_KEY_AND_CONTEXT_PROPERLY);
                Logger.error(runtimeException.getMessage());
            }

            if (TextUtils.isEmpty(merchantBaseUrl) && isValidUrl(merchantBaseUrl)) {
                RuntimeException runtimeException = new RuntimeException(ERROR_SDK_MERCHANT_BASE_URL_PROPERLY);
                Logger.error(runtimeException.getMessage());
            }
            return true;
        }
    }
}