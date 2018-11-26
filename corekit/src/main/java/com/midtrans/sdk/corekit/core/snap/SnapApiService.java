package com.midtrans.sdk.corekit.core.snap;

import com.midtrans.sdk.corekit.core.snap.model.pay.request.PaymentRequest;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.mandiriecash.MandiriEcashResponse;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.va.BcaPaymentResponse;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.va.BniPaymentResponse;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.va.OtherPaymentResponse;
import com.midtrans.sdk.corekit.core.snap.model.pay.response.va.PermataPaymentResponse;
import com.midtrans.sdk.corekit.core.snap.model.transaction.response.PaymentInfoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SnapApiService {

    String PAYMENT_INFO = "v1/transactions/{snap_token}";
    String PAYMENT_PAY = "v1/transactions/{snap_token}/pay";

    /**
     * Get transaction options using Snap with snap token.
     *
     * @param snapToken snap token
     */
    @GET(PAYMENT_INFO)
    Call<PaymentInfoResponse> getTransactionOptions(@Path("snap_token") String snapToken);

    /**
     * Charge payment using bank transfer Virtual account.
     *
     * @param paymentRequest Payment Request Details.
     */
    @POST(PAYMENT_PAY)
    Call<BcaPaymentResponse> paymentBankTransferBca(@Path("snap_token") String snapToken, @Body PaymentRequest paymentRequest);

    /**
     * Charge payment using bank transfer Virtual account.
     *
     * @param paymentRequest Payment Request Details.
     */
    @POST(PAYMENT_PAY)
    Call<BniPaymentResponse> paymentBankTransferBni(@Path("snap_token") String snapToken, @Body PaymentRequest paymentRequest);

    /**
     * Charge payment using bank transfer Virtual account.
     *
     * @param paymentRequest Payment Request Details.
     */
    @POST(PAYMENT_PAY)
    Call<PermataPaymentResponse> paymentBankTransferPermata(@Path("snap_token") String snapToken, @Body PaymentRequest paymentRequest);

    /**
     * Charge payment using bank transfer Virtual account.
     *
     * @param paymentRequest Payment Request Details.
     */
    @POST(PAYMENT_PAY)
    Call<OtherPaymentResponse> paymentBankTransferOther(@Path("snap_token") String snapToken, @Body PaymentRequest paymentRequest);

    /**
     * Charge payment using bank transfer Virtual account.
     *
     * @param paymentRequest Payment Request Details.
     */
    @POST(PAYMENT_PAY)
    Call<MandiriEcashResponse> paymentMandiriEcash(@Path("snap_token") String snapToken, @Body PaymentRequest paymentRequest);

}