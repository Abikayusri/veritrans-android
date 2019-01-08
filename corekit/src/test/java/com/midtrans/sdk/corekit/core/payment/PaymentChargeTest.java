package com.midtrans.sdk.corekit.core.payment;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.midtrans.sdk.corekit.SDKConfigTest;
import com.midtrans.sdk.corekit.base.callback.MidtransCallback;
import com.midtrans.sdk.corekit.base.enums.Environment;
import com.midtrans.sdk.corekit.base.network.MidtransRestAdapter;
import com.midtrans.sdk.corekit.MidtransSdk;
import com.midtrans.sdk.corekit.core.api.snap.model.pay.request.CustomerDetailPayRequest;
import com.midtrans.sdk.corekit.core.api.snap.model.pay.request.mandiriclick.MandiriClickpayParams;
import com.midtrans.sdk.corekit.core.api.snap.model.pay.response.BasePaymentResponse;
import com.midtrans.sdk.corekit.utilities.Logger;
import com.midtrans.sdk.corekit.utilities.NetworkHelper;
import com.midtrans.sdk.corekit.utilities.Helper;
import com.midtrans.sdk.corekit.utilities.ValidationHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NetworkHelper.class,
        Looper.class,
        Helper.class,
        Log.class,
        TextUtils.class,
        Logger.class,
        ValidationHelper.class})
public class PaymentChargeTest {

    @InjectMocks
    private BankTransferCharge bankTransferCharge;
    @InjectMocks
    private CardlessCreditCharge cardlessCreditCharge;
    @InjectMocks
    private DirectDebitCharge directDebitCharge;
    @InjectMocks
    private EWalletCharge eWalletCharge;
    @InjectMocks
    private OnlineDebitCharge onlineDebitCharge;
    @InjectMocks
    private ConvenienceStoreCharge convenienceStoreCharge;

    @Mock
    private Context contextMock;
    @Mock
    private CustomerDetailPayRequest customerDetailPayRequest;
    @Mock
    private BasePaymentResponse responseMock;
    @Mock
    private MidtransCallback<BasePaymentResponse> callbackMock;
    @Mock
    private String userId, customerNumber;
    @Mock
    private MandiriClickpayParams mandiriClickpayParams;

    @Before
    public void setup() {
        PowerMockito.mockStatic(MidtransCallback.class);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(Logger.class);
        PowerMockito.mockStatic(Looper.class);
        PowerMockito.mockStatic(Helper.class);
        PowerMockito.mockStatic(NetworkHelper.class);
        PowerMockito.mockStatic(ValidationHelper.class);

        Mockito.when(TextUtils.isEmpty(Matchers.anyString())).thenReturn(false);
        Mockito.when(TextUtils.isEmpty(null)).thenReturn(true);
        Mockito.when(TextUtils.isEmpty("")).thenReturn(true);

        MidtransSdk.builder(contextMock,
                SDKConfigTest.CLIENT_KEY,
                SDKConfigTest.MERCHANT_BASE_URL)
                .setLogEnabled(true)
                .setEnvironment(Environment.SANDBOX)
                .build();

        callbackMock.onSuccess(responseMock);
        callbackMock.onFailed(new Throwable());
    }

    @Test
    public void test_PaymentUsingBankTransferVaBca_positive() {
        bankTransferCharge.paymentUsingBankTransferVaBca(SDKConfigTest.SNAP_TOKEN, customerDetailPayRequest, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_PaymentUsingBankTransferVaBca_negative() {
        bankTransferCharge.paymentUsingBankTransferVaBca(SDKConfigTest.SNAP_TOKEN, customerDetailPayRequest, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_PaymentUsingBankTransferVaPermata_positive() {
        bankTransferCharge.paymentUsingBankTransferVaPermata(SDKConfigTest.SNAP_TOKEN, customerDetailPayRequest, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_PaymentUsingBankTransferVaPermata_negative() {
        bankTransferCharge.paymentUsingBankTransferVaPermata(SDKConfigTest.SNAP_TOKEN, customerDetailPayRequest, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_PaymentUsingBankTransferVaBni_positive() {
        bankTransferCharge.paymentUsingBankTransferVaBni(SDKConfigTest.SNAP_TOKEN, customerDetailPayRequest, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_PaymentUsingBankTransferVaBni_negative() {
        bankTransferCharge.paymentUsingBankTransferVaBni(SDKConfigTest.SNAP_TOKEN, customerDetailPayRequest, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_PaymentUsingCardlessCreditAkulaku_positive() {
        cardlessCreditCharge.paymentUsingAkulaku(SDKConfigTest.SNAP_TOKEN, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_PaymentUsingCardlessCreditAkulaku_negative() {
        cardlessCreditCharge.paymentUsingAkulaku(SDKConfigTest.SNAP_TOKEN, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_PaymentUsingDirectDebitKlikBca_positive(){
        directDebitCharge.paymentUsingKlikBca(SDKConfigTest.SNAP_TOKEN,userId, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_PaymentUsingDirectDebitKlikBca_negative(){
        directDebitCharge.paymentUsingKlikBca(SDKConfigTest.SNAP_TOKEN,userId, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void testPaymentUsingDirectDebitMandiriClickPay_positive(){
        directDebitCharge.paymentUsingMandiriClickPay(SDKConfigTest.SNAP_TOKEN,mandiriClickpayParams, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void testPaymentUsingDirectDebitMandiriClickPay_negative(){
        directDebitCharge.paymentUsingMandiriClickPay(SDKConfigTest.SNAP_TOKEN,mandiriClickpayParams, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_paymentUsingEwalletTelkkomselCash_positive(){
        eWalletCharge.paymentUsingTelkomselCash(SDKConfigTest.SNAP_TOKEN,customerNumber, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_paymentUsingEwalletTelkkomselCash_negative(){
        eWalletCharge.paymentUsingTelkomselCash(SDKConfigTest.SNAP_TOKEN,customerNumber, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_paymentUsingEwalletMandiriEcash_positive(){
        eWalletCharge.paymentUsingMandiriEcash(SDKConfigTest.SNAP_TOKEN,customerDetailPayRequest, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_paymentUsingEwalletMandiriEcash_negative(){
        eWalletCharge.paymentUsingMandiriEcash(SDKConfigTest.SNAP_TOKEN,customerDetailPayRequest, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_paymentUsingEwalletGopay_positive(){
        eWalletCharge.paymentUsingGopay(SDKConfigTest.SNAP_TOKEN,customerNumber, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_paymentUsingEwalletGopay_negative(){
        eWalletCharge.paymentUsingGopay(SDKConfigTest.SNAP_TOKEN,customerNumber, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_paymentUsingOnlineDebitChargeCimbClicks_positive(){
        onlineDebitCharge.paymentUsingCimbClicks(SDKConfigTest.SNAP_TOKEN, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_paymentUsingOnlineDebitChargeCimbClicks_negative(){
        onlineDebitCharge.paymentUsingCimbClicks(SDKConfigTest.SNAP_TOKEN, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_paymentUsingOnlineDebitChargeBcaClickPay_positive(){
        onlineDebitCharge.paymentUsingBcaClickPay(SDKConfigTest.SNAP_TOKEN, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_paymentUsingOnlineDebitChargeBcaClickPay_negative(){
        onlineDebitCharge.paymentUsingBcaClickPay(SDKConfigTest.SNAP_TOKEN, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_paymentUsingOnlineDebitChargeBriEpay_positive(){
        onlineDebitCharge.paymentUsingBriEpay(SDKConfigTest.SNAP_TOKEN, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_paymentUsingOnlineDebitChargeBriEpay_negative(){
        onlineDebitCharge.paymentUsingBriEpay(SDKConfigTest.SNAP_TOKEN, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

    @Test
    public void test_paymentUsingStoreChange_positive(){
        convenienceStoreCharge.paymentUsingIndomaret(SDKConfigTest.SNAP_TOKEN, callbackMock);
        Mockito.verify(callbackMock).onSuccess(Matchers.any(BasePaymentResponse.class));
    }

    @Test
    public void test_paymentUsingStoreChange_negative(){
        convenienceStoreCharge.paymentUsingIndomaret(SDKConfigTest.SNAP_TOKEN, callbackMock);
        Mockito.verify(callbackMock).onFailed(Matchers.any(Throwable.class));
    }

}