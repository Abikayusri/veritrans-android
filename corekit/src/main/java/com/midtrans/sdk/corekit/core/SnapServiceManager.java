package com.midtrans.sdk.corekit.core;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.midtrans.sdk.corekit.callback.BankBinsCallback;
import com.midtrans.sdk.corekit.callback.BanksPointCallback;
import com.midtrans.sdk.corekit.callback.DeleteCardCallback;
import com.midtrans.sdk.corekit.callback.GetTransactionStatusCallback;
import com.midtrans.sdk.corekit.callback.TransactionCallback;
import com.midtrans.sdk.corekit.callback.TransactionOptionsCallback;
import com.midtrans.sdk.corekit.models.TransactionResponse;
import com.midtrans.sdk.corekit.models.snap.BankBinsResponse;
import com.midtrans.sdk.corekit.models.snap.BanksPointResponse;
import com.midtrans.sdk.corekit.models.snap.Transaction;
import com.midtrans.sdk.corekit.models.snap.TransactionStatusResponse;
import com.midtrans.sdk.corekit.models.snap.payment.BankTransferPaymentRequest;
import com.midtrans.sdk.corekit.models.snap.payment.BasePaymentRequest;
import com.midtrans.sdk.corekit.models.snap.payment.CreditCardPaymentRequest;
import com.midtrans.sdk.corekit.models.snap.payment.DanamonOnlinePaymentRequest;
import com.midtrans.sdk.corekit.models.snap.payment.GCIPaymentRequest;
import com.midtrans.sdk.corekit.models.snap.payment.GoPayPaymentRequest;
import com.midtrans.sdk.corekit.models.snap.payment.IndosatDompetkuPaymentRequest;
import com.midtrans.sdk.corekit.models.snap.payment.KlikBCAPaymentRequest;
import com.midtrans.sdk.corekit.models.snap.payment.NewMandiriClickPayPaymentRequest;
import com.midtrans.sdk.corekit.models.snap.payment.TelkomselEcashPaymentRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ziahaqi on 3/27/18.
 */

public class SnapServiceManager extends BaseServiceManager {
    private static final String TAG = SnapServiceManager.class.getSimpleName();
    private static final String KEY_ERROR_MESSAGE = "error_messages";

    private SnapApiService snapApiService;

    SnapServiceManager(SnapApiService snapApiService) {
        this.snapApiService = snapApiService;
    }


    /**
     * This will create a HTTP request to Snap to get transaction option.
     *
     * @param snapToken Snap Token.
     * @param callback  callback of payment option
     */
    public void getTransactionOptions(@NonNull final String snapToken, final TransactionOptionsCallback callback) {

        Call<Transaction> call = snapApiService.getPaymentOption(snapToken);
        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                releaseResources();

                Transaction transaction = response.body();

                if (transaction != null) {
                    if (response.code() == 200 && !TextUtils.isEmpty(transaction.getToken())) {
                        callback.onSuccess(transaction);
                    } else {
                        callback.onFailure(transaction, response.message());
                    }

                    return;
                }

                try {
                    if (response.code() == 404 && response.errorBody() != null) {
                        String strErrorBody = String.valueOf(response.errorBody());

                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(strErrorBody, JsonObject.class);

                        if (jsonObject != null && jsonObject.getAsJsonArray(KEY_ERROR_MESSAGE) != null) {
                            JsonArray jsonArray = jsonObject.getAsJsonArray(KEY_ERROR_MESSAGE);
                            String errorMessage = response.message();
                            if (jsonArray.get(0) != null) {
                                errorMessage = jsonArray.get(0).toString();
                            }
                            callback.onError(new Throwable(errorMessage, new Resources.NotFoundException()));

                        } else {
                            callback.onError(new Throwable(response.message(), new Resources.NotFoundException()));
                        }

                    } else {
                        callback.onError(new Throwable(Constants.MESSAGE_ERROR_EMPTY_RESPONSE));
                        Logger.e(TAG, Constants.MESSAGE_ERROR_EMPTY_RESPONSE);
                    }

                } catch (Exception e) {
                    callback.onError(new Throwable(Constants.MESSAGE_ERROR_EMPTY_RESPONSE));
                    Logger.e(TAG, Constants.MESSAGE_ERROR_EMPTY_RESPONSE);
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });

    }

    /**
     * This method is used for card payment using snap backend.
     *
     * @param paymentRequest Payment details.
     * @param callback       Transaction callback
     */

    public void paymentUsingCreditCard(final String authenticationToken, CreditCardPaymentRequest paymentRequest, final TransactionCallback callback) {

        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingCreditCard(authenticationToken, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }


    /**
     * This method is used for Payment Using Bank Transfer BCA
     *
     * @param paymentRequest Payment Details.
     * @param callback       Transaction callback
     */
    public void paymentUsingVa(final String snapToken, BankTransferPaymentRequest paymentRequest, final TransactionCallback callback) {
        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingVa(snapToken, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    /**
     * This method is used for payment methods that using Base payment model
     * payment methods : kioson, BCA KlikPay, CIMB Click, BRI epay, Mandiri Ecash, XL Tunai, Indomaret
     *
     * @param snap
     * @param paymentRequest BasePaymentRequest
     * @param callback       transaction callback
     */
    public void paymentUsingBaseMethod(final String snap, BasePaymentRequest paymentRequest, final TransactionCallback callback) {

        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingBaseMethod(snap, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    /**
     * This method is used for payment using Mandiri Bill Pay.
     *
     * @param snapToken
     * @param paymentRequest payment request for Mandiri Bill pay
     * @param callback       transaction callback
     */
    public void paymentUsingMandiriBillPay(final String snapToken, BankTransferPaymentRequest paymentRequest, final TransactionCallback callback) {
        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingMandiriBillPay(snapToken, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    /**
     * This method is used for payment using new flow of Mandiri Click Pay.
     *
     * @param snapToken
     * @param paymentRequest payment request for Mandiri Click Pay
     * @param callback       transaction callback
     */
    public void paymentUsingMandiriClickPay(final String snapToken, NewMandiriClickPayPaymentRequest paymentRequest, final TransactionCallback callback) {
        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingMandiriClickPay(snapToken, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    /**
     * This method is used for payment using Telkomsel E-Cash
     *
     * @param paymentRequest payment request for Telkomsel E-Cash
     * @param callback       transaction callback
     */
    public void paymentUsingTelkomselCash(final String snapToken, TelkomselEcashPaymentRequest paymentRequest, final TransactionCallback callback) {

        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingTelkomselEcash(snapToken, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    /**
     * This method is used for payment using Indosat Dompetku
     *
     * @param snapToken
     * @param paymentRequest payment request for Indosat Dompetku
     * @param callback       transaction callback
     */
    public void paymentUsingIndosatDompetku(final String snapToken, IndosatDompetkuPaymentRequest paymentRequest, final TransactionCallback callback) {

        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingIndosatDompetku(snapToken, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    public void paymentUsingGci(final String authenticationToken, GCIPaymentRequest paymentRequest, final TransactionCallback callback) {

        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingGci(authenticationToken, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    /**
     * This method is used for payment using Klik BCA.
     *
     * @param paymentRequest Payment details
     * @param callback       transaction callback
     */
    public void paymentUsingKlikBca(final String authenticationToken, KlikBCAPaymentRequest paymentRequest, final TransactionCallback callback) {

        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingKlikBca(authenticationToken, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }


    /**
     * This method is used for payment using GoPay
     *
     * @param paymentRequest
     * @param callback
     */
    public void paymentUsingGoPay(String snapToken, GoPayPaymentRequest paymentRequest, final TransactionCallback callback) {

        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingGoPay(snapToken, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    /**
     * This method is used for payment using Danamon Online
     *
     * @param snapToken      SnapToken
     * @param paymentRequest DanamonOnlinePaymentRequest
     * @param callback       TransactionCallback
     */
    public void paymentUsingDanamonOnline(String snapToken, DanamonOnlinePaymentRequest paymentRequest, final TransactionCallback callback) {

        if (paymentRequest == null) {
            doOnInvalidDataSupplied(callback);
            return;
        }

        Call<TransactionResponse> call = snapApiService.paymentUsingDanamonOnline(snapToken, paymentRequest);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                doOnPaymentResponseSuccess(response, callback);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    private void doOnPaymentResponseSuccess(Response<TransactionResponse> response, TransactionCallback callback) {
        releaseResources();
        TransactionResponse transactionResponse = response.body();

        if (transactionResponse != null) {
            String statusCode = transactionResponse.getStatusCode();
            if (!TextUtils.isEmpty(statusCode)
                    && (transactionResponse.getStatusCode().equals(Constants.STATUS_CODE_200)
                    || transactionResponse.getStatusCode().equals(Constants.STATUS_CODE_201))) {

                callback.onSuccess(transactionResponse);

            } else {

                if (statusCode.equals(Constants.STATUS_CODE_400)) {
                    String message;
                    if (transactionResponse.getValidationMessages() != null && !transactionResponse.getValidationMessages().isEmpty()) {
                        message = transactionResponse.getValidationMessages().get(0);
                    } else {
                        message = transactionResponse.getStatusMessage();
                    }
                    callback.onFailure(transactionResponse, message);
                } else {
                    callback.onFailure(transactionResponse, transactionResponse.getStatusMessage());
                }

            }
        } else {
            callback.onError(new Throwable(Constants.MESSAGE_ERROR_EMPTY_RESPONSE));
        }
    }

    /**
     * It will delete saved credit card number on Snap Api
     *
     * @param authenticationToken
     * @param maskedCard          masked card number
     * @param callback            DeleteCardCallback
     */
    public void deleteCard(String authenticationToken, String maskedCard, final DeleteCardCallback callback) {

        Call<Void> call = snapApiService.deleteCard(authenticationToken, maskedCard);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                releaseResources();
                if (response.code() == 200 || response.code() == 201) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(response.body());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });

    }


    /**
     * it will be used to get bank bins on the snap API
     *
     * @param callback BankbinsCallback instance
     */
    public void getBankBins(final BankBinsCallback callback) {

        Call<List<BankBinsResponse>> call = snapApiService.getBankBins();
        call.enqueue(new Callback<List<BankBinsResponse>>() {
            @Override
            public void onResponse(Call<List<BankBinsResponse>> call, Response<List<BankBinsResponse>> response) {
                releaseResources();

                List<BankBinsResponse> bankBinsResponses = response.body();

                if (bankBinsResponses != null && !bankBinsResponses.isEmpty()) {
                    if (response.code() == 200 || response.code() == 201) {
                        callback.onSuccess(new ArrayList<>(bankBinsResponses));
                    } else {
                        callback.onFailure(response.message());
                    }
                } else {
                    callback.onError(new Throwable(Constants.MESSAGE_ERROR_EMPTY_RESPONSE));
                }
            }

            @Override
            public void onFailure(Call<List<BankBinsResponse>> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    /**
     * @param authenticationToken snap token
     * @param cardToken           credit card token
     * @param callback            BNI points callback instance
     */
    public void getBanksPoint(String authenticationToken, String cardToken, final BanksPointCallback callback) {

        Call<BanksPointResponse> call = snapApiService.getBanksPoint(authenticationToken, cardToken);
        call.enqueue(new Callback<BanksPointResponse>() {
            @Override
            public void onResponse(Call<BanksPointResponse> call, Response<BanksPointResponse> response) {
                releaseResources();
                BanksPointResponse bankPointResponse = response.body();

                if (bankPointResponse != null) {
                    if (bankPointResponse.getStatusCode() != null && bankPointResponse.getStatusCode().equals(Constants.STATUS_CODE_200)) {
                        callback.onSuccess(bankPointResponse);
                    } else {
                        callback.onFailure(response.message());
                    }
                } else {
                    callback.onError(new Throwable(Constants.MESSAGE_ERROR_EMPTY_RESPONSE));
                }
            }

            @Override
            public void onFailure(Call<BanksPointResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }

    public void getTransactionStatus(String snapToken, final GetTransactionStatusCallback callback) {

        Call<TransactionStatusResponse> call = snapApiService.getTransactionStatus(snapToken);
        call.enqueue(new Callback<TransactionStatusResponse>() {
            @Override
            public void onResponse(Call<TransactionStatusResponse> call, Response<TransactionStatusResponse> response) {
                releaseResources();
                TransactionStatusResponse transactionStatusResponse = response.body();

                if (transactionStatusResponse != null) {
                    if (transactionStatusResponse.getStatusCode() != null && transactionStatusResponse.getStatusCode().equals(Constants.STATUS_CODE_200)) {
                        callback.onSuccess(transactionStatusResponse);
                    } else {
                        callback.onFailure(transactionStatusResponse, response.message());
                    }
                } else {
                    callback.onError(new Throwable(Constants.MESSAGE_ERROR_EMPTY_RESPONSE));
                }
            }

            @Override
            public void onFailure(Call<TransactionStatusResponse> call, Throwable t) {
                doOnResponseFailure(t, callback);
            }
        });
    }
}
