package com.midtrans.sdk.corekit.core.payment;

import android.support.annotation.NonNull;

import com.midtrans.sdk.corekit.base.callback.MidtransCallback;
import com.midtrans.sdk.corekit.core.api.midtrans.model.cardtoken.CardTokenRequest;
import com.midtrans.sdk.corekit.core.api.midtrans.model.cardregistration.CardRegistrationResponse;
import com.midtrans.sdk.corekit.core.api.merchant.model.savecard.SaveCardResponse;
import com.midtrans.sdk.corekit.core.api.midtrans.model.tokendetails.TokenDetailsResponse;
import com.midtrans.sdk.corekit.core.api.snap.model.pay.request.CustomerDetailPayRequest;
import com.midtrans.sdk.corekit.core.api.snap.model.pay.request.creditcard.CreditCardPaymentParams;
import com.midtrans.sdk.corekit.core.api.snap.model.pay.request.creditcard.SaveCardRequest;
import com.midtrans.sdk.corekit.core.api.snap.model.pay.response.BasePaymentResponse;
import com.midtrans.sdk.corekit.utilities.Constants;

import java.util.ArrayList;

import static com.midtrans.sdk.corekit.utilities.ValidationHelper.isNotEmpty;

public class CreditCardCharge extends BaseGroupPayment {

    /**
     * It will run backgrond task to charge payment using Credit Card
     *
     * @param snapToken authentication token
     * @param callback  transaction callback
     */
    public static void paymentUsingCard(@NonNull final String snapToken,
                                        @NonNull final CreditCardPaymentParams creditCardPaymentParams,
                                        @NonNull final CustomerDetailPayRequest customerDetailPayRequest,
                                        MidtransCallback<BasePaymentResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            getSnapApiManager().paymentUsingCreditCard(snapToken,
                    creditCardPaymentParams,
                    customerDetailPayRequest,
                    callback);
        }
    }

    /**
     * It will run backround task to register card PAPI(Payment API) Backend
     *
     * @param cardNumber   credit card number
     * @param cardCvv      credit card cvv
     * @param cardExpMonth credit card expired month
     * @param cardExpYear  credit card expired year
     * @param callback     Credit card registration callback
     */
    public static void cardRegistration(@NonNull final String cardNumber,
                                        @NonNull final String cardCvv,
                                        @NonNull final String cardExpMonth,
                                        @NonNull final String cardExpYear,
                                        @NonNull final MidtransCallback<CardRegistrationResponse> callback) {

        if (isValidForNetworkCall(callback)) {
            getMidtransServiceManager().cardRegistration(cardNumber,
                    cardCvv,
                    cardExpMonth,
                    cardExpYear,
                    getClientKey(),
                    callback);
        }
    }

    /**
     * It will execute an api request to retrieve a authentication token.
     *
     * @param cardTokenRequest get card token  request object
     * @param callback         get card token callback
     */
    public static void getCardToken(@NonNull final CardTokenRequest cardTokenRequest,
                                    @NonNull final MidtransCallback<TokenDetailsResponse> callback) {

        if (isNotEmpty(cardTokenRequest)) {
            if (isValidForNetworkCall(callback)) {
                getMidtransServiceManager().getToken(cardTokenRequest, callback);
            }
        } else {
            callback.onFailed(new Throwable(Constants.MESSAGE_ERROR_INVALID_DATA_SUPPLIED));
        }
    }

    /**
     * It will run backround task to save card to merchant server
     *
     * @param userId   id user
     * @param requests save card request model
     * @param callback save card callback
     */
    public static void saveCards(@NonNull final String userId,
                                 @NonNull final ArrayList<SaveCardRequest> requests,
                                 @NonNull final MidtransCallback<SaveCardResponse> callback) {
        if (isNotEmpty(requests)) {
            if (isValidForNetworkCall(callback)) {
                getMerchantApiManager().saveCards(userId, requests, callback);
            }
        } else {
            callback.onFailed(new Throwable(Constants.MESSAGE_ERROR_INVALID_DATA_SUPPLIED));
        }
    }
}