package id.co.veritrans.sdk.coreflow.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.security.cert.CertPathValidatorException;
import java.util.ArrayList;

import javax.net.ssl.SSLHandshakeException;

import id.co.veritrans.sdk.coreflow.R;
import id.co.veritrans.sdk.coreflow.callback.CardRegistrationCallback;
import id.co.veritrans.sdk.coreflow.callback.CheckoutCallback;
import id.co.veritrans.sdk.coreflow.callback.GetCardCallback;
import id.co.veritrans.sdk.coreflow.callback.GetCardTokenCallback;
import id.co.veritrans.sdk.coreflow.callback.PaymentOptionCallback;
import id.co.veritrans.sdk.coreflow.callback.SaveCardCallback;
import id.co.veritrans.sdk.coreflow.callback.TransactionCallback;
import id.co.veritrans.sdk.coreflow.callback.exception.CardRegistrationError;
import id.co.veritrans.sdk.coreflow.callback.exception.CheckoutError;
import id.co.veritrans.sdk.coreflow.callback.exception.ErrorType;
import id.co.veritrans.sdk.coreflow.callback.exception.GetCardTokenError;
import id.co.veritrans.sdk.coreflow.callback.exception.PaymentOptionError;
import id.co.veritrans.sdk.coreflow.callback.exception.BaseError;
import id.co.veritrans.sdk.coreflow.callback.exception.TransactionFailure;
import id.co.veritrans.sdk.coreflow.eventbus.events.Events;
import id.co.veritrans.sdk.coreflow.models.CardRegistrationResponse;
import id.co.veritrans.sdk.coreflow.models.CardTokenRequest;
import id.co.veritrans.sdk.coreflow.models.SaveCardRequest;
import id.co.veritrans.sdk.coreflow.models.SaveCardResponse;
import id.co.veritrans.sdk.coreflow.models.TokenRequestModel;
import id.co.veritrans.sdk.coreflow.models.TokenDetailsResponse;
import id.co.veritrans.sdk.coreflow.models.TransactionResponse;
import id.co.veritrans.sdk.coreflow.models.snap.Token;
import id.co.veritrans.sdk.coreflow.models.snap.Transaction;
import id.co.veritrans.sdk.coreflow.models.snap.payment.BankTransferPaymentRequest;
import id.co.veritrans.sdk.coreflow.models.snap.payment.BasePaymentRequest;
import id.co.veritrans.sdk.coreflow.models.snap.payment.CreditCardPaymentRequest;
import id.co.veritrans.sdk.coreflow.models.snap.payment.IndosatDompetkuPaymentRequest;
import id.co.veritrans.sdk.coreflow.models.snap.payment.KlikBCAPaymentRequest;
import id.co.veritrans.sdk.coreflow.models.snap.payment.MandiriClickPayPaymentRequest;
import id.co.veritrans.sdk.coreflow.models.snap.payment.TelkomselEcashPaymentRequest;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ziahaqi on 7/18/16.
 */
public class SnapTransactionManager extends BaseTransactionManager {
    // Snap
    private static final String GET_SNAP_TRANSACTION_FAILED = "Failed Getting Snap Transaction";
    private static final String GET_SNAP_TRANSACTION_SUCCESS = "Success Getting Snap Transaction";
    private static final String PAYMENT_TYPE_SNAP = "snap";
    private static final String TAG = "TransactionManager";

    private SnapRestAPI snapRestAPI;

    SnapTransactionManager(Context context, SnapRestAPI snapRestAPI, MerchantRestAPI merchantApiClient, VeritransRestAPI veritransRestAPI) {
        this.context = context;
        this.snapRestAPI = snapRestAPI;
        this.merchantPaymentAPI = merchantApiClient;
        this.veritransPaymentAPI = veritransRestAPI;
    }

    protected void setRestApi(SnapRestAPI restApi) {
        this.snapRestAPI = restApi;
    }

    /**
     * This method will get snap token via merchant server.
     *
     * @param model Transaction details.
     * @param callback Checkout Callback
     */
    public void checkout(TokenRequestModel model, final CheckoutCallback callback) {
        merchantPaymentAPI.checkout(model, new Callback<Token>() {
            @Override
            public void success(Token snapTokenDetailResponse, Response response) {
                releaseResources();
                if (snapTokenDetailResponse != null) {
                    if (snapTokenDetailResponse.getTokenId() != null && !snapTokenDetailResponse.getTokenId().equals("")) {
                        callback.onSuccess(snapTokenDetailResponse);
                    } else {
                        callback.onFailure(snapTokenDetailResponse, context.getString(R.string.error_empty_response));
                    }
                } else {
                    callback.onError(new Throwable(context.getString(R.string.error_empty_response)));
                }
            }

            @Override
            public void failure(RetrofitError e) {
                releaseResources();

                if (e.getCause() instanceof SSLHandshakeException || e.getCause() instanceof CertPathValidatorException) {
                    Logger.i(TAG, "Error in SSL Certificate. " + e.getMessage());
                }
                callback.onError(new Throwable(e.getMessage(), e.getCause()));
            }
        });
    }


    /**
     * This will create a HTTP request to Snap to get transaction option.
     *
     * @param snapToken Snap Token.
     *@param  callback callback of payment option
     */
    public void getPaymentOption(@NonNull String snapToken, final PaymentOptionCallback callback) {
        final long start = System.currentTimeMillis();
            snapRestAPI.getPaymentOption(snapToken, new Callback<Transaction>() {
                @Override
                public void success(Transaction transaction, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();

                    if (transaction != null) {
                        if (response.getStatus() == 200 && !transaction.getTransactionData().getTransactionId().equals("")) {
                            callback.onSuccess(transaction);
                            // Track Mixpanel event
                            analyticsManager.trackMixpanel(GET_SNAP_TRANSACTION_SUCCESS, PAYMENT_TYPE_SNAP, end - start);
                        } else {
                            callback.onFailure(transaction, response.getReason());
                            // Track Mixpanel event
                            analyticsManager.trackMixpanel(GET_SNAP_TRANSACTION_FAILED, PAYMENT_TYPE_SNAP, end - start);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.error_empty_response)));
                        Logger.e(TAG, context.getString(R.string.error_empty_response));

                        // Track Mixpanel event
                        analyticsManager.trackMixpanel(GET_SNAP_TRANSACTION_FAILED, PAYMENT_TYPE_SNAP, end - start);
                    }
                }

                @Override
                public void failure(RetrofitError e) {
                    releaseResources();
                    long end = System.currentTimeMillis();

                    if (e.getCause() instanceof SSLHandshakeException || e.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + e.getMessage());
                    }
                    callback.onError(new Throwable(e.getMessage(), e.getCause()));
                    // Track Mixpanel event
                    analyticsManager.trackMixpanel(GET_SNAP_TRANSACTION_FAILED, PAYMENT_TYPE_SNAP, end - start);
                }
            });
    }

    /**
     * This method is used for card payment using snap backend.
     *
     * @param requestModel Payment details.
     * @param callback Transaction callback
     */

    public void paymentUsingCreditCard(CreditCardPaymentRequest requestModel, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (requestModel != null) {
            snapRestAPI.paymentUsingCreditCard(requestModel, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_CREDIT_CARD, end - start);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_CREDIT_CARD, end - start, transactionResponse.getStatusMessage());
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_CREDIT_CARD, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();

                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_CREDIT_CARD, end - start, error.getMessage());
                }
            });
        }else{
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     * This method is used for Payment Using Bank Transfer BCA
     *
     * @param paymentRequest Payment Details.
     * @param callback Transaction callback
     */
    public void paymentUsingBankTransferBCA(BankTransferPaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingBankTransferBCA(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_BANK_TRANSFER, BANK_BCA, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BANK_TRANSFER, BANK_BCA, end - start, transactionResponse.getStatusMessage());
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BANK_TRANSFER, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BANK_TRANSFER, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for Payment Using Bank Transfer Permata
     *
     * @param paymentRequest payment Details
     * @param callback transaction callback
     */
    public void paymentUsingBankTransferPermata(final BankTransferPaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingBankTransferPermata(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }

                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_BANK_TRANSFER, BANK_PERMATA, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BANK_TRANSFER, BANK_PERMATA, end - start, transactionResponse.getStatusMessage());
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BANK_TRANSFER, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BANK_TRANSFER, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     * This method is used for payment using Klik BCA.
     *
     * @param request Payment details
     * @param callback transaction callback
     */
    public void paymentUsingKlikBCA(KlikBCAPaymentRequest request, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (request != null) {
            snapRestAPI.paymentUsingKlikBCA(request, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();

                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }

                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_KLIK_BCA, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_KLIK_BCA, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_KLIK_BCA, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();

                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_KLIK_BCA, end - start, error.getMessage());
                }
            });

        }else{
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using BCA Klik Pay.
     *
     * @param paymentRequest payment request for BCA Klik pay
     * @param callback transaction callback
     */
    public void paymentUsingBCAKlikpay(BasePaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingBCAKlikPay(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();

                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }

                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_BCA_KLIKPAY, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BCA_KLIKPAY, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BCA_KLIKPAY, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BCA_KLIKPAY, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using Mandiri Bill Pay.
     *
     * @param paymentRequest payment request for Mandiri Bill pay
     * @param callback transaction callback
     */
    public void paymentUsingMandiriBillPay(BankTransferPaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingMandiriBillPay(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_MANDIRI_BILL_PAY, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_MANDIRI_BILL_PAY, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_MANDIRI_BILL_PAY, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_MANDIRI_BILL_PAY, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using Mandiri Click Pay.
     *
     * @param paymentRequest payment request for Mandiri Click Pay
     * @param callback transaction callback
     */
    public void paymentUsingMandiriClickPay(MandiriClickPayPaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingMandiriClickPay(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_MANDIRI_CLICKPAY, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_MANDIRI_CLICKPAY, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_MANDIRI_CLICKPAY, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_MANDIRI_CLICKPAY, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using CIMB Click.
     *
     * @param paymentRequest payment request for CIMB Click
     * @param callback transaction callback
     */
    public void paymentUsingCIMBClick(BasePaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingCIMBClick(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_CIMB_CLICK, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_CIMB_CLICK, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_CIMB_CLICK, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_CIMB_CLICK, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using BRI Epay.
     *
     * @param paymentRequest payment request for BRI Epay.
     * @param callback transaction callback
     */
    public void paymentUsingBRIEpay(BasePaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingBRIEpay(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_BRI_EPAY, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BRI_EPAY, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BRI_EPAY, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BRI_EPAY, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using Mandiri E-Cash
     *
     * @param paymentRequest payment request for Mandiri E-Cash
     * @param callback transaction callbaack
     */
    public void paymentUsingMandiriEcash(BasePaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingMandiriEcash(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_MANDIRI_ECASH, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_MANDIRI_ECASH, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_MANDIRI_ECASH, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_MANDIRI_ECASH, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using Telkomsel E-Cash
     *
     * @param paymentRequest payment request for Telkomsel E-Cash
     * @param callback transaction callback
     */
    public void paymentUsingTelkomselCash(TelkomselEcashPaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingTelkomselEcash(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_TELKOMSEL_ECASH, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_TELKOMSEL_ECASH, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_TELKOMSEL_ECASH, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_TELKOMSEL_ECASH, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using XL Tunai
     *
     * @param paymentRequest payment request for XL Tunai
     * @param callback transaction callback
     */
    public void paymentUsingXLTunai(BasePaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingXlTunai(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_XL_TUNAI, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_XL_TUNAI, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_XL_TUNAI, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_XL_TUNAI, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using Indomaret
     *
     * @param paymentRequest payment request for Indomaret
     * @param callback Transaction callback
     */
    public void paymentUsingIndomaret(BasePaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingIndomaret(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_INDOMARET, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_INDOMARET, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_INDOMARET, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_INDOMARET, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using Indosat Dompetku
     *
     * @param paymentRequest payment request for Indosat Dompetku
     * @param callback transaction callback
     */
    public void paymentUsingIndosatDompetku(IndosatDompetkuPaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingIndosatDompetku(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_INDOSAT_DOMPETKU, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_INDOSAT_DOMPETKU, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_INDOSAT_DOMPETKU, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_INDOSAT_DOMPETKU, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using Kiosan
     *
     * @param paymentRequest payment request for Kiosan
     * @param callback transaction callback
     */
    public void paymentUsingKiosan(BasePaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingKiosan(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_KIOSAN, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_KIOSAN, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_KIOSAN, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_KIOSAN, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used for payment using Bank Transfer
     *
     * @param paymentRequest payment request for Bank Transfer
     * @param callback transaction callback
     */
    public void paymentUsingBankTransferAllBank(BankTransferPaymentRequest paymentRequest, final TransactionCallback callback) {
        final long start = System.currentTimeMillis();
        if (paymentRequest != null) {
            snapRestAPI.paymentUsingBankTransferAllBank(paymentRequest, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse, Response response) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (isSDKLogEnabled) {
                        displayResponse(transactionResponse);
                    }
                    if (transactionResponse != null) {
                        if (transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_200))
                                || transactionResponse.getStatusCode().equals(context.getString(R.string.success_code_201))) {
                            callback.onSuccess(transactionResponse);
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_SUCCESS, PAYMENT_TYPE_BANK_TRANSFER, ALL_BANK, end - start, Events.SNAP_PAYMENT);
                        } else {
                            callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                            analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BANK_TRANSFER, end - start, Events.SNAP_PAYMENT);
                        }
                    } else {
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                        analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BANK_TRANSFER, end - start, context.getString(R.string.error_empty_response));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    long end = System.currentTimeMillis();
                    if (error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException) {
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                    analyticsManager.trackMixpanel(KEY_TRANSACTION_FAILED, PAYMENT_TYPE_BANK_TRANSFER, ALL_BANK, end - start, error.getMessage());
                }
            });
        } else {
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *This method is used to save credit cards to merchant server
     *
     * @param cardRequests credit card Request model
     * @param userId unique id for every user
     * @param callback save card callback
     */
    public void saveCards(String userId, ArrayList<SaveCardRequest> cardRequests, final SaveCardCallback callback){
        if(cardRequests != null){
            merchantPaymentAPI.saveCards(userId, cardRequests, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    releaseResources();
                    SaveCardResponse saveCardResponse = new SaveCardResponse();
                    saveCardResponse.setCode(response.getStatus());
                    saveCardResponse.setMessage(response.getReason());
                    if(response.getStatus() == 200 || response.getStatus() == 201){
                        callback.onSuccess(saveCardResponse);
                    }else{
                        callback.onFailure(response.getReason());
                    }
                }
                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    if(error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException){
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                }
            });
        }else{
            releaseResources();
            callback.onError(new Throwable(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     * this method is used to get saved card list on merchant server
     *
     * @param userId unique id to each user
     * @param callback Transaction callback
     */
    public void getCards(String userId, final GetCardCallback callback){
            merchantPaymentAPI.getCards(userId, new Callback<ArrayList<SaveCardRequest>>() {
                @Override
                public void success(ArrayList<SaveCardRequest> cardsResponses, Response response) {
                    releaseResources();
                    if(cardsResponses != null && cardsResponses.size() > 0){
                        if(response.getStatus() == 200 || response.getStatus() == 201){
                            callback.onSuccess(cardsResponses);
                        }else{
                            callback.onError(new Throwable(response.getReason()));
                        }
                    }else{
                        callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    releaseResources();
                    if(error.getCause() instanceof SSLHandshakeException || error.getCause() instanceof CertPathValidatorException){
                        Logger.i(TAG, "Error in SSL Certificate. " + error.getMessage());
                    }
                    callback.onError(new Throwable(error.getMessage(), error.getCause()));
                }
            });
    }


    /*
     * PAPI STUFF
     */

    /**
     * It will execute API call to get token from Veritrans that can be used later.
     *
     * @param cardNumber   credit card number
     * @param cardCvv      credit card CVV number
     * @param cardExpMonth credit card expired month
     * @param cardExpYear  credit card expired year
     * @param callback     card transaction callback
     */
    public void cardRegistration(String cardNumber,
                                 String cardCvv,
                                 String cardExpMonth,
                                 String cardExpYear, String clientKey, final CardRegistrationCallback callback) {
        veritransPaymentAPI.registerCard(cardNumber, cardCvv, cardExpMonth, cardExpYear, clientKey, new Callback<CardRegistrationResponse>() {
            @Override
            public void success(CardRegistrationResponse cardRegistrationResponse, Response response) {
                releaseResources();

                if (cardRegistrationResponse != null) {
                    if (cardRegistrationResponse.getStatusCode().equals(context.getString(R.string.success_code_200))) {
                        callback.onSuccess(cardRegistrationResponse);
                    } else {
                        callback.onFailure(cardRegistrationResponse, cardRegistrationResponse.getStatusMessage());
                    }
                } else {
                    callback.onError(new Throwable(context.getString(R.string.empty_transaction_response)));
                }
            }

            @Override
            public void failure(RetrofitError e) {
                releaseResources();
                if (e.getCause() instanceof SSLHandshakeException || e.getCause() instanceof CertPathValidatorException) {
                    Logger.i(TAG,"Error in SSL Certificate. " + e.getMessage());
                }
                callback.onError(new Throwable(e.getMessage(), e.getCause()));
            }
        });
    }

    /**
     * It will execute an api call to get token from server, and after completion of request it
     *
     * @param cardTokenRequest information about credit card.
     * @param callback get creditcard token callback
     */
    public void getToken(@NonNull CardTokenRequest cardTokenRequest, @NonNull  final GetCardTokenCallback callback) {
        final long start = System.currentTimeMillis();
        if (cardTokenRequest.isTwoClick()) {
            if (cardTokenRequest.isInstalment()) {
                veritransPaymentAPI.getTokenInstalmentOfferTwoClick(
                        cardTokenRequest.getCardCVV(),
                        cardTokenRequest.getSavedTokenId(),
                        cardTokenRequest.isTwoClick(),
                        cardTokenRequest.isSecure(),
                        cardTokenRequest.getGrossAmount(),
                        cardTokenRequest.getBank(),
                        cardTokenRequest.getClientKey(),
                        cardTokenRequest.isInstalment(),
                        cardTokenRequest.getFormattedInstalmentTerm(), new Callback<TokenDetailsResponse>() {
                            @Override
                            public void success(TokenDetailsResponse tokenDetailsResponse, Response response) {
                                consumeTokenSuccesResponse( start, tokenDetailsResponse, callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                consumeTokenErrorResponse(start, error, callback);
                            }
                        });
            } else {
                veritransPaymentAPI.getTokenTwoClick(
                        cardTokenRequest.getCardCVV(),
                        cardTokenRequest.getSavedTokenId(),
                        cardTokenRequest.isTwoClick(),
                        cardTokenRequest.isSecure(),
                        cardTokenRequest.getGrossAmount(),
                        cardTokenRequest.getBank(),
                        cardTokenRequest.getClientKey(), new Callback<TokenDetailsResponse>() {
                            @Override
                            public void success(TokenDetailsResponse tokenDetailsResponse, Response response) {
                                consumeTokenSuccesResponse(start, tokenDetailsResponse, callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                consumeTokenErrorResponse(start, error, callback);
                            }
                        });
            }

        } else {
            if (cardTokenRequest.isInstalment()) {
                veritransPaymentAPI.get3DSTokenInstalmentOffers(cardTokenRequest.getCardNumber(),
                        cardTokenRequest.getCardCVV(),
                        cardTokenRequest.getCardExpiryMonth(), cardTokenRequest
                                .getCardExpiryYear(),
                        cardTokenRequest.getClientKey(),
                        cardTokenRequest.getBank(),
                        cardTokenRequest.isSecure(),
                        cardTokenRequest.isTwoClick(),
                        cardTokenRequest.getGrossAmount(),
                        cardTokenRequest.isInstalment(),
                        cardTokenRequest.getFormattedInstalmentTerm(), new Callback<TokenDetailsResponse>() {
                            @Override
                            public void success(TokenDetailsResponse tokenDetailsResponse, Response response) {
                                consumeTokenSuccesResponse(start, tokenDetailsResponse, callback);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                consumeTokenErrorResponse(start, error, callback);
                            }
                        });
            } else {
                //normal request
                if (!cardTokenRequest.isSecure()) {
                    veritransPaymentAPI.getToken(
                            cardTokenRequest.getCardNumber(),
                            cardTokenRequest.getCardCVV(),
                            cardTokenRequest.getCardExpiryMonth(),
                            cardTokenRequest.getCardExpiryYear(),
                            cardTokenRequest.getClientKey(),
                            new Callback<TokenDetailsResponse>() {
                                @Override
                                public void success(TokenDetailsResponse tokenDetailsResponse, Response response) {
                                    consumeTokenSuccesResponse(start, tokenDetailsResponse);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    consumeTokenErrorResponse(start, error);
                                }
                            }
                    );
                } else {
                    veritransPaymentAPI.get3DSToken(cardTokenRequest.getCardNumber(),
                            cardTokenRequest.getCardCVV(),
                            cardTokenRequest.getCardExpiryMonth(),
                            cardTokenRequest.getCardExpiryYear(),
                            cardTokenRequest.getClientKey(),
                            cardTokenRequest.getBank(),
                            cardTokenRequest.isSecure(),
                            cardTokenRequest.isTwoClick(),
                            cardTokenRequest.getGrossAmount(), new Callback<TokenDetailsResponse>() {
                                @Override
                                public void success(TokenDetailsResponse tokenDetailsResponse, Response response) {
                                    consumeTokenSuccesResponse(start, tokenDetailsResponse);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    consumeTokenErrorResponse(start, error);
                                }
                            });
                }
            }

        }
    }

    private  void consumeTokenSuccesResponse(long start, TokenDetailsResponse tokenDetailsResponse, GetCardTokenCallback callback) {
        releaseResources();

        long end = System.currentTimeMillis();

        if (tokenDetailsResponse != null) {
            if (isSDKLogEnabled) {
                displayTokenResponse(tokenDetailsResponse);
            }
            if (tokenDetailsResponse.getStatusCode().trim().equalsIgnoreCase(context.getString(R.string.success_code_200))) {
                callback.onSuccess(tokenDetailsResponse);

                // Track Mixpanel event
                analyticsManager.trackMixpanel(KEY_TOKENIZE_SUCCESS, PAYMENT_TYPE_CREDIT_CARD, end - start);
            } else {
                if (!TextUtils.isEmpty(tokenDetailsResponse.getStatusMessage())) {
                    callback.onFailure(tokenDetailsResponse, tokenDetailsResponse.getStatusMessage());

                    // Track Mixpanel event
                    analyticsManager.trackMixpanel(KEY_TOKENIZE_FAILED, PAYMENT_TYPE_CREDIT_CARD, end - start, tokenDetailsResponse.getStatusMessage());
                } else {
                    callback.onFailure(tokenDetailsResponse,
                            context.getString(R.string.error_empty_response));

                    analyticsManager.trackMixpanel(KEY_TOKENIZE_FAILED, PAYMENT_TYPE_CREDIT_CARD, end - start,
                            context.getString(R.string.error_empty_response));
                }
            }
        } else {
            callback.onError(new Throwable(context.getString(R.string.error_empty_response)));

            analyticsManager.trackMixpanel(KEY_TOKENIZE_FAILED, PAYMENT_TYPE_CREDIT_CARD, end - start,
                    context.getString(R.string.error_empty_response));

            Logger.e(TAG, context.getString(R.string.error_empty_response));
        }
    }

    private void consumeTokenErrorResponse(long start, RetrofitError e, GetCardTokenCallback callback) {
        releaseResources();
        long end = System.currentTimeMillis();

        if (e.getCause() instanceof SSLHandshakeException || e.getCause() instanceof CertPathValidatorException) {
            Logger.i("Error in SSL Certificate. " + e.getMessage());
        }
        callback.onError(new Throwable(e.getMessage(), e.getCause()));
        // Track Mixpanel event
        analyticsManager.trackMixpanel(KEY_TOKENIZE_FAILED, PAYMENT_TYPE_CREDIT_CARD, end - start, e.getMessage());
    }
}
