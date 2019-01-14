package com.midtrans.sdk.corekit.core.payment;

import android.support.annotation.NonNull;

import com.midtrans.sdk.corekit.base.callback.MidtransCallback;
import com.midtrans.sdk.corekit.core.api.snap.model.pay.response.ConvenienceStoreIndomaretPaymentResponse;

public class ConvenienceStoreCharge extends BaseGroupPayment {

    /**
     * Start payment using bank transfer and va with Indomaret.
     *
     * @param snapToken token after making checkoutWithTransaction.
     * @param callback  for receiving callback from request.
     */
    public static void paymentUsingIndomaret(@NonNull final String snapToken,
                                             @NonNull final MidtransCallback<ConvenienceStoreIndomaretPaymentResponse> callback) {
        if (isValidForNetworkCall(callback)) {
            getSnapApiManager().paymentUsingIndomaret(snapToken, callback);
        }
    }
}