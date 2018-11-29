package com.midtrans.sdk.corekit.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.midtrans.sdk.corekit.base.callback.MidtransCallback;
import com.midtrans.sdk.corekit.base.enums.Environment;
import com.midtrans.sdk.corekit.core.merchant.MerchantApiManager;
import com.midtrans.sdk.corekit.core.merchant.model.checkout.request.CheckoutTransaction;
import com.midtrans.sdk.corekit.core.merchant.model.checkout.response.CheckoutResponse;
import com.midtrans.sdk.corekit.core.snap.SnapApiManager;
import com.midtrans.sdk.corekit.core.snap.model.pay.request.CustomerDetailPayRequest;
import com.midtrans.sdk.corekit.core.snap.model.pay.request.mandiriclick.MandiriClickpayParams;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.BasePaymentResponse;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.epaybri.BriEpayPaymentResponse;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.va.BcaPaymentResponse;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.va.BniPaymentResponse;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.va.OtherPaymentResponse;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.va.PermataPaymentResponse;
import com.midtrans.sdk.corekit.core.snap.model.transaction.response.PaymentInfoResponse;
import com.midtrans.sdk.corekit.utilities.Constants;
import com.midtrans.sdk.corekit.utilities.Logger;
import com.midtrans.sdk.corekit.utilities.NetworkHelper;

import static android.webkit.URLUtil.isValidUrl;
import static com.midtrans.sdk.corekit.utilities.Constants.ERROR_SDK_CLIENT_KEY_AND_CONTEXT_PROPERLY;
import static com.midtrans.sdk.corekit.utilities.Constants.ERROR_SDK_IS_NOT_INITIALIZE_PROPERLY;
import static com.midtrans.sdk.corekit.utilities.Constants.ERROR_SDK_MERCHANT_BASE_URL_PROPERLY;

public class MidtransSdk {

    private static final String TAG = "MidtransSdk";

    /**
     * Instance variable.
     */
    private static volatile MidtransSdk SINGLETON_INSTANCE = null;
    private final int apiRequestTimeOut = 30;
    private final String BASE_URL_SANDBOX = "https://api.sandbox.midtrans.com/v2/";
    private final String BASE_URL_PRODUCTION = "https://api.midtrans.com/v2/";
    private final String SNAP_BASE_URL_SANDBOX = "https://app.sandbox.midtrans.com/snap/";
    private final String SNAP_BASE_URL_PRODUCTION = "https://app.midtrans.com/snap/";
    private final String PROMO_BASE_URL_SANDBOX = "https://promo.vt-stage.info/";
    private final String PROMO_BASE_URL_PRODUCTION = "https://promo.vt-stage.info/";
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
    /**
     * Mandatory checkout property.
     */
<<<<<<< HEAD
    private CheckoutTransaction checkoutTransaction = null;
=======
    private TransactionRequest checkoutTransaction = null;
>>>>>>> feature/revamp
    private MerchantApiManager merchantApiManager;
    private SnapApiManager snapApiManager;

    private MidtransSdk(Context context,
                        String clientId,
                        String merchantUrl,
                        Environment environment) {
        this.context = context.getApplicationContext();
        this.merchantClientId = clientId;
        this.merchantBaseUrl = merchantUrl;
        this.midtransEnvironment = environment;
        this.merchantApiManager = NetworkHelper.newMerchantServiceManager(merchantBaseUrl, apiRequestTimeOut);
        String snapBaseUrl;
        if (this.midtransEnvironment == Environment.SANDBOX) {
            snapBaseUrl = SNAP_BASE_URL_SANDBOX;
        } else {
            snapBaseUrl = SNAP_BASE_URL_PRODUCTION;
        }
        this.snapApiManager = NetworkHelper.newSnapServiceManager(snapBaseUrl, apiRequestTimeOut);
    }

    /**
     * Initializer.
     *
     * @param context     Context, mandatory not null.
     * @param clientId    ClientId or Merchant Client Key, mandatory not null.
     * @param merchantUrl MerchantUrl or Merchant Base Url, mandatory not null.
     * @return Builder.
     */
    public static Builder builder(@NonNull Context context,
                                  @NonNull String clientId,
                                  @NonNull String merchantUrl) {

        return new Builder(context,
                clientId,
                merchantUrl);
    }

    /**
     * Returns instance of midtrans sdk.
     *
     * @return MidtransSdk instance.
     */
    public synchronized static MidtransSdk getInstance() {
        if (SINGLETON_INSTANCE == null) {
            String message = "MidtransSdk isn't initialized. Please use MidtransSdk.builder() to initialize.";
            RuntimeException runtimeException = new RuntimeException(message);
            Logger.error(message, runtimeException);
        }
        return SINGLETON_INSTANCE;
    }

    /**
     * Returns value of merchant url.
     *
     * @return merchant url value.
     */
    public Environment getEnvironment() {
        return midtransEnvironment;
    }

    /**
     * Returns value of instance context.
     *
     * @return context value.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Returns value of merchant client id.
     *
     * @return merchant client id value.
     */
    public String getMerchantClientId() {
        return merchantClientId;
    }

    /**
     * Returns value of merchant base url.
     *
     * @return merchant base url value.
     */
    public String getMerchantBaseUrl() {
        return merchantBaseUrl;
    }

    /**
     * Returns value of timeout.
     *
     * @return timeout value.
     */
    public int getApiRequestTimeOut() {
        return apiRequestTimeOut;
    }

    /**
     * Returns value of transaction request.
     *
     * @return transaction request object.
     */
<<<<<<< HEAD
    public CheckoutTransaction getCheckoutTransaction() {
=======
    public TransactionRequest getCheckoutTransaction() {
>>>>>>> feature/revamp
        return checkoutTransaction;
    }

    /**
     * Set value to transaction request for begin checkout.
     */
<<<<<<< HEAD
    public void setCheckoutTransaction(CheckoutTransaction checkoutTransaction) {
=======
    public void setCheckoutTransaction(TransactionRequest checkoutTransaction) {
>>>>>>> feature/revamp
        this.checkoutTransaction = checkoutTransaction;
    }

    /**
     * Begin checkout for all Payment Method that merchant activate in MAP
     * for use this method you have to set checkoutTransaction first.
     *
     * @param callback for receiving callback from request.
     */
    public void checkout(final MidtransCallback<CheckoutResponse> callback) {
        checkout(this.checkoutTransaction, callback);
    }

    /**
     * Begin checkout for all Payment Method that merchant activate in MAP
     * for use this method you must to include checkoutTransaction as parameter.
     *
     * @param checkoutTransaction transaction request for making checkout.
     * @param callback            for receiving callback from request.
     */
<<<<<<< HEAD
    public void checkout(@NonNull final CheckoutTransaction checkoutTransaction,
                         final MidtransCallback<CheckoutResponse> callback) {
        if (callback == null) {
            Logger.error(TAG, Constants.MESSAGE_ERROR_CALLBACK_UNIMPLEMENTED);
            return;
        }
        if (isNetworkAvailable()) {
            merchantApiManager.checkout(checkoutTransaction, callback);
        } else {
            callback.onFailed(new Throwable(Constants.MESSAGE_ERROR_FAILED_TO_CONNECT_TO_SERVER));
=======
    public void checkout(@NonNull final TransactionRequest checkoutTransaction,
                         final MidtransCallback<CheckoutResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            merchantApiManager.checkout(checkoutTransaction, callback);
>>>>>>> feature/revamp
        }
    }

    /**
     * Getting Payment Info including enabled payment method and others information.
     *
     * @param snapToken token after making checkout.
     * @param callback  for receiving callback from request.
     */
    public void getPaymentInfo(final String snapToken,
                               final MidtransCallback<PaymentInfoResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            snapApiManager.getPaymentInfo(snapToken, callback);
        }
    }

    /**
     * Start payment using bank transfer and va with BCA.
     *
     * @param snapToken       token after making checkout.
     * @param customerDetails for putting bank transfer request.
     * @param callback        for receiving callback from request.
     */
    public void paymentUsingBankTransferVaBca(final String snapToken,
                                              final CustomerDetailPayRequest customerDetails,
                                              final MidtransCallback<BcaPaymentResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            snapApiManager.paymentUsingBankTransferVaBca(snapToken, customerDetails, callback);
        }
    }

    /**
     * Start payment using bank transfer and va with BNI.
     *
     * @param snapToken       token after making checkout.
     * @param customerDetails for putting bank transfer request.
     * @param callback        for receiving callback from request.
     */
    public void paymentUsingBankTransferVaBni(final String snapToken,
                                              final CustomerDetailPayRequest customerDetails,
                                              final MidtransCallback<BniPaymentResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            snapApiManager.paymentUsingBankTransferVaBni(snapToken, customerDetails, callback);
        }
    }

    /**
     * Start payment using bank transfer and va with PERMATA.
     *
     * @param snapToken       token after making checkout.
     * @param customerDetails for putting bank transfer request.
     * @param callback        for receiving callback from request.
     */
    public void paymentUsingBankTransferVaPermata(final String snapToken,
                                                  final CustomerDetailPayRequest customerDetails,
                                                  final MidtransCallback<PermataPaymentResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            snapApiManager.paymentUsingBankTransferVaPermata(snapToken, customerDetails, callback);
        }
    }

    /**
     * Start payment using bank transfer and va with Other Bank.
     *
     * @param snapToken       token after making checkout.
     * @param customerDetails for putting bank transfer request.
     * @param callback        for receiving callback from request.
     */
    public void paymentUsingBankTransferVaOther(final String snapToken,
                                                final CustomerDetailPayRequest customerDetails,
                                                final MidtransCallback<OtherPaymentResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            snapApiManager.paymentUsingBankTransferVaOther(snapToken, customerDetails, callback);
        }
    }

    /**
     * Start payment using bank transfer and va with Mandiri Ecash.
     *
     * @param snapToken                token after making checkout.
     * @param customerDetailPayRequest for putting bank transfer request.
     * @param callback                 for receiving callback from request.
     */
    public void paymentUsingMandiriEcash(final String snapToken,
                                         final CustomerDetailPayRequest customerDetailPayRequest,
                                         final MidtransCallback<BasePaymentResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            snapApiManager.paymentUsingMandiriEcash(snapToken, customerDetailPayRequest, callback);
        }
    }

    /**
     * Start payment using bank transfer and va with Mandiri Ecash.
     *
     * @param snapToken             token after making checkout.
     * @param mandiriClickpayParams for putting bank transfer request.
     * @param callback              for receiving callback from request.
     */
    public void paymentUsingMandiriClickPayt(final String snapToken,
                                             final MandiriClickpayParams mandiriClickpayParams,
                                             final MidtransCallback<BasePaymentResponse> callback) {
        if (callback == null) {
            Logger.error(TAG, Constants.MESSAGE_ERROR_CALLBACK_UNIMPLEMENTED);
            return;
        }
        if (isNetworkAvailable()) {
            snapApiManager.paymentUsingMandiriClickPay(snapToken, mandiriClickpayParams, callback);
        } else {
            callback.onFailed(new Throwable(Constants.MESSAGE_ERROR_FAILED_TO_CONNECT_TO_SERVER));
        }
    }

    /**
     * Start payment using bank transfer and va with CIMB Clicks.
     *
     * @param snapToken token after making checkout.
     * @param callback  for receiving callback from request.
     */
    public void paymentUsingCimbClicks(final String snapToken,
                                       final MidtransCallback<BasePaymentResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            snapApiManager.paymentUsingCimbClick(snapToken, callback);
        }
    }

    /**
     * Start payment using bank transfer and va with BRI Epay.
     *
     * @param snapToken token after making checkout.
     * @param callback  for receiving callback from request.
     */
    public void paymentUsingBriEpay(final String snapToken,
                                    final MidtransCallback<BriEpayPaymentResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            snapApiManager.paymentUsingBriEpay(snapToken, callback);
        }
    }

    /**
     * Start payment using bank transfer and va with Klik BCA.
     *
     * @param snapToken token after making checkout.
     * @param callback  for receiving callback from request.
     */
    public void paymentUsingKlikBca(final String snapToken,
                                    final String klikBcaUserId,
                                    final MidtransCallback<BasePaymentResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            snapApiManager.paymentUsingKlikBca(snapToken, klikBcaUserId, callback);
        }
    }

    /**
     * Open utils for checking network status
     *
     * @return boolean based on network status
     */
    public boolean isNetworkAvailable() {
        return NetworkHelper.isNetworkAvailable(this.context);
    }

    /**
     * Builder class extends BaseSdkBuilder
     * contain Constructor for set data to BaseSdkBuilder.
     */
    public static class Builder {

        protected String merchantClientId;
        protected Context context;
        protected boolean enableLog = false;
        protected String merchantBaseUrl;
        protected Environment midtransEnvironment = Environment.SANDBOX;

        private Builder(Context context, String clientId, String merchantUrl) {
            this.context = context;
            this.merchantClientId = clientId;
            this.merchantBaseUrl = merchantUrl;
        }

        /**
         * set Logger visible or not.
         */
        public Builder setLogEnabled(boolean logEnabled) {
            this.enableLog = logEnabled;
            Logger.enabled = this.enableLog;
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
         * This method will start payment flow if you have set useUi field to true.
         *
         * @return it returns fully initialized object of midtrans sdk.
         */
        public MidtransSdk build() {
            if (isValidData()) {
                SINGLETON_INSTANCE = new MidtransSdk(context,
                        merchantClientId,
                        merchantBaseUrl,
                        midtransEnvironment);
                return SINGLETON_INSTANCE;
            } else {
                Logger.error(ERROR_SDK_IS_NOT_INITIALIZE_PROPERLY);
            }
            return null;
        }

        private boolean isValidData() {
            if (merchantClientId == null || context == null) {
                RuntimeException runtimeException = new RuntimeException(ERROR_SDK_CLIENT_KEY_AND_CONTEXT_PROPERLY);
                Logger.error(ERROR_SDK_CLIENT_KEY_AND_CONTEXT_PROPERLY, runtimeException);
            }

            if (TextUtils.isEmpty(merchantBaseUrl) && isValidUrl(merchantBaseUrl)) {
                RuntimeException runtimeException = new RuntimeException(ERROR_SDK_MERCHANT_BASE_URL_PROPERLY);
                Logger.error(ERROR_SDK_MERCHANT_BASE_URL_PROPERLY, runtimeException);
            }
            return true;
        }
    }

    private <T> Boolean isValidForNetworkCall(MidtransCallback<T> callback) {
        if (callback == null) {
            Logger.error(TAG, Constants.MESSAGE_ERROR_CALLBACK_UNIMPLEMENTED);
            return false;
        }
        if (!isNetworkAvailable()) {
            callback.onFailed(new Throwable(Constants.MESSAGE_ERROR_FAILED_TO_CONNECT_TO_SERVER));
            return false;
        } else {
            return true;
        }
    }

}