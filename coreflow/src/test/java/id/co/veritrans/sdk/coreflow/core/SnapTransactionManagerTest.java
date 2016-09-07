package id.co.veritrans.sdk.coreflow.core;

import android.content.Context;
import android.content.res.Resources;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.cert.CertPathValidatorException;
import java.util.ArrayList;
import java.util.Collections;

import javax.net.ssl.SSLHandshakeException;

import com.midtrans.sdk.coreflow.R;
import id.co.veritrans.sdk.coreflow.SDKConfigTest;
import com.midtrans.sdk.coreflow.analytics.MixpanelApi;
import com.midtrans.sdk.coreflow.core.Logger;
import com.midtrans.sdk.coreflow.core.MerchantRestAPI;
import com.midtrans.sdk.coreflow.core.MixpanelAnalyticsManager;
import com.midtrans.sdk.coreflow.core.SdkCoreFlowBuilder;
import com.midtrans.sdk.coreflow.core.SnapRestAPI;
import com.midtrans.sdk.coreflow.core.SnapTransactionManager;
import com.midtrans.sdk.coreflow.core.VeritransRestAPI;
import com.midtrans.sdk.coreflow.core.VeritransSDK;
import com.midtrans.sdk.coreflow.models.CardRegistrationResponse;
import com.midtrans.sdk.coreflow.models.CardTokenRequest;
import com.midtrans.sdk.coreflow.models.SaveCardRequest;
import com.midtrans.sdk.coreflow.models.TokenDetailsResponse;
import com.midtrans.sdk.coreflow.models.TokenRequestModel;
import com.midtrans.sdk.coreflow.models.TransactionResponse;
import com.midtrans.sdk.coreflow.models.snap.Token;
import com.midtrans.sdk.coreflow.models.snap.Transaction;
import com.midtrans.sdk.coreflow.models.snap.TransactionData;
import com.midtrans.sdk.coreflow.models.snap.payment.BankTransferPaymentRequest;
import com.midtrans.sdk.coreflow.models.snap.payment.BasePaymentRequest;
import com.midtrans.sdk.coreflow.models.snap.payment.CreditCardPaymentRequest;
import com.midtrans.sdk.coreflow.models.snap.payment.IndosatDompetkuPaymentRequest;
import com.midtrans.sdk.coreflow.models.snap.payment.KlikBCAPaymentRequest;
import com.midtrans.sdk.coreflow.models.snap.payment.MandiriClickPayPaymentRequest;
import com.midtrans.sdk.coreflow.models.snap.payment.TelkomselEcashPaymentRequest;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by ziahaqi on 7/18/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, TextUtils.class ,Logger.class, Looper.class, Base64.class})
@PowerMockIgnore("javax.net.ssl.*")
public class SnapTransactionManagerTest {

    protected String sampleJsonResponse = "{\"response\":\"response\"}";

    protected Response retrofitResponse = new Response("URL", 200, "success", Collections.EMPTY_LIST,
            new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

    // transaction properties
    @Mock
    protected CallbackCollaborator callbackCollaborator;
    @InjectMocks
    protected CallbackImplementSample callbackImplement;
    @Mock
    private Context contextMock;
    @Mock
    private Resources resourcesMock;
    private VeritransSDK veritransSDK;
    @Mock
    private MixpanelAnalyticsManager mixpanelAnalyticsManagerMock;
    @Mock
    private MixpanelApi mixpanelApiMock;
    private SnapTransactionManager transactionManager;

    @Captor
    private ArgumentCaptor<Callback<Token>> callbackSnapTokenResponseCaptor;

    @Mock
    private Token snapTokenMock;
    private String tokenId = "aa3afad7-a346-4db6-9cb3-737f24e4fc56";
    @Mock
    private MerchantRestAPI merchantApi;
    @Mock
    private RetrofitError retrofitError;
    @Mock
    private CertPathValidatorException errorInvalidCertPatMock;
    @Mock
    private SSLHandshakeException errorInvalidSSLException;
    @Mock
    private SnapRestAPI snapAPI;
    @Captor
    private ArgumentCaptor<Callback<Transaction>> transactionCaptorMock;
    @Mock
    private Transaction transactionMock;
    @Mock
    private java.lang.Throwable errorGeneralMock;
    @Captor
    private ArgumentCaptor<String> tokenIdCaptor;
    @Captor
    private ArgumentCaptor<TokenRequestModel> snapTokenRequestModelCaptor;
    @Mock
    private TokenRequestModel snapTokenRequestModelMock;
    @Mock
    private TransactionData transactionDataMock;
    private String transactionId = "trans_id";

    @Mock
    private CreditCardPaymentRequest creditcardRequestMock;
    @Captor
    private ArgumentCaptor<CreditCardPaymentRequest> creditCardRequestCaptor;
    @Mock
    private CreditCardPaymentRequest transactionRequestMock;
    @Captor
    private ArgumentCaptor<Callback<TransactionResponse>> transactionResponseCallbackCaptor;
    @Mock
    private TransactionResponse transactionResponseMock;
    @Mock
    private BankTransferPaymentRequest bankTransferRequestMock;
    @Captor
    private ArgumentCaptor<BankTransferPaymentRequest> bankTransferRequestCaptor;
    @Mock
    private KlikBCAPaymentRequest klikBCARequestMock;
    @Mock
    private BasePaymentRequest basePaymentRequestMock;
    @Captor
    private ArgumentCaptor<KlikBCAPaymentRequest> klikBCARequestCaptor;

    @Captor
    private ArgumentCaptor<BasePaymentRequest> BCAKlikpayRequestCaptor;
    @Mock
    private MandiriClickPayPaymentRequest mandiriClickPayPaymentRequestMock;
    @Captor
    private ArgumentCaptor<MandiriClickPayPaymentRequest> mandirClickPayPaymentRequestCaptor;
    @Captor
    private ArgumentCaptor<BasePaymentRequest> basePaymentRequestCaptor;
    @Mock
    private TelkomselEcashPaymentRequest telkomselEcashPaymentRequestMock;
    @Captor
    private ArgumentCaptor<TelkomselEcashPaymentRequest> telkomselEcashPaymentRequestCaptor;
    @Mock
    private IndosatDompetkuPaymentRequest indosatDompetkuPaymentRequestMock;
    @Captor
    private ArgumentCaptor<IndosatDompetkuPaymentRequest> indosatDompetKuPaymentRequestCaptor;
    private String sampleUserId = "user_id214";
    @Mock
    private ArrayList<SaveCardRequest> cardRequestListMock;
    @Captor
    private ArgumentCaptor<ArrayList<SaveCardRequest>> cardRequestListCaptor;
    @Captor
    private ArgumentCaptor<Callback<String>> saveCardCallbackCaptor;
    private String saveCardResponse = "Created";
    @Captor
    private ArgumentCaptor<String> sampleUserIdCaptor;
    @Captor
    private ArgumentCaptor<Callback<ArrayList<SaveCardRequest>>> getCardCallbackCaptor;
    @Mock
    private ArrayList<SaveCardRequest> getCardResponseMock;
    @Mock
    private VeritransRestAPI veritransApi;

    // get card token properties

    @Captor
    private ArgumentCaptor<Callback<TokenDetailsResponse>> callbackgetTokenArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> cardNumberCaptor;
    @Captor
    private ArgumentCaptor<String> cardCVVCaptor;
    @Captor
    private ArgumentCaptor<String> cardExpMonthCaptor;
    @Captor
    private ArgumentCaptor<String> cardExpYearCaptor;
    @Captor
    private ArgumentCaptor<String> clientKeyCaptor;
    @Captor
    private ArgumentCaptor<String> bankCaptor;
    @Captor
    private ArgumentCaptor<Boolean> instalmentCaptor;
    @Captor
    private ArgumentCaptor<String> instalmentTermCaptor;
    @Captor
    private ArgumentCaptor<Boolean> scureCaptor;
    @Captor
    private ArgumentCaptor<Boolean> twoClickCaptor;
    @Captor
    private ArgumentCaptor<Double> grossAmountCaptor;

    CardTokenRequest cardTokenRequest = new CardTokenRequest();

    //card registration properties

    @Captor
    private ArgumentCaptor<Callback<CardRegistrationResponse>> callbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> callbackArgumentCaptorCardNumber;
    @Captor
    private ArgumentCaptor<String> callbackArgumentCaptorCardCVV;
    @Captor
    private ArgumentCaptor<String> callbackArgumentCaptorCardYear;
    @Captor
    private ArgumentCaptor<String> callbackArgumentCaptorCardMonth;
    @Captor
    private ArgumentCaptor<String> callbackArgumentCaptorCar;
    @Captor
    private ArgumentCaptor<String> calbackArgumentCatorClientKey;

    private String mAuthToken = "VT-1dqwd34dwed23e2dw";

    public static final String CARD_NUMBER = "4811111111111114";
    public static final String CARD_CVV = "123";
    public static final String CARD_EXP_MONTH = "123";
    public static final String CARD_EXP_YEAR = "123";


    private Response retrofitResponseError = new Response("URL", 300, "success", Collections.EMPTY_LIST,
            new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));;

    @Before
    public void setup() {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(Looper.class);
        PowerMockito.mockStatic(Base64.class);
        PowerMockito.mockStatic(Logger.class);

        Mockito.when(contextMock.getResources()).thenReturn(resourcesMock);
        Mockito.when(contextMock.getApplicationContext()).thenReturn(contextMock);
        Mockito.when(contextMock.getString(R.string.success_code_200)).thenReturn("200");
        Mockito.when(contextMock.getString(R.string.success_code_201)).thenReturn("201");
        Mockito.when(contextMock.getString(R.string.success)).thenReturn("success");

        veritransSDK = SdkCoreFlowBuilder.init(contextMock, SDKConfigTest.CLIENT_KEY, SDKConfigTest.MERCHANT_BASE_URL)
                .setDefaultText("open_sans_regular.ttf")
                .setSemiBoldText("open_sans_semibold.ttf")
                .setBoldText("open_sans_bold.ttf")
                .buildSDK();

        mixpanelAnalyticsManagerMock.setMixpanelApi(mixpanelApiMock);
        transactionManager = veritransSDK.getmSnapTransactionManager();
        transactionManager.setAnalyticsManager(mixpanelAnalyticsManagerMock);
        transactionManager.setSDKLogEnabled(false);
        callbackImplement.setTransactionManager(transactionManager, merchantApi, veritransApi, snapAPI);
    }
/*
    /**
     * checkout
     */

    @Test
    public void sampleTest(){
        Assert.assertTrue(true);
    }

    @Test
    public void checkoutSuccess() {
        Mockito.when(snapTokenMock.getTokenId()).thenReturn(tokenId);
        callbackImplement.checkout(snapTokenRequestModelMock);

        Mockito.verify(merchantApi, Mockito.times(1)).checkout(snapTokenRequestModelCaptor.capture(),
                callbackSnapTokenResponseCaptor.capture());

        callbackSnapTokenResponseCaptor.getValue().success(snapTokenMock, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onCheckoutSuccess();
    }

    @Test
    public void checkoutError_whenTokenNull() {
        Mockito.when(snapTokenMock.getTokenId()).thenReturn("");
        callbackImplement.checkout(snapTokenRequestModelMock);

        Mockito.verify(merchantApi, Mockito.times(1)).checkout(snapTokenRequestModelCaptor.capture(),
                callbackSnapTokenResponseCaptor.capture());
        callbackSnapTokenResponseCaptor.getValue().success(snapTokenMock, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onCheckoutFailure();
    }


    @Test
    public void checkoutError_whenResponseNull() {
        Mockito.when(snapTokenMock.getTokenId()).thenReturn(tokenId);
        callbackImplement.checkout(snapTokenRequestModelMock);

        Mockito.verify(merchantApi, Mockito.times(1)).checkout(snapTokenRequestModelCaptor.capture(),
                callbackSnapTokenResponseCaptor.capture());
        callbackSnapTokenResponseCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();
    }

    @Test
    public void checkoutError_whenValidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.checkout(snapTokenRequestModelMock);
        Mockito.verify(merchantApi, Mockito.times(1)).checkout(snapTokenRequestModelCaptor.capture(),
                callbackSnapTokenResponseCaptor.capture());

        callbackSnapTokenResponseCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();
    }

    @Test
    public void checkoutError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.checkout(snapTokenRequestModelMock);
        Mockito.verify(merchantApi, Mockito.times(1)).checkout(snapTokenRequestModelCaptor.capture(),
                callbackSnapTokenResponseCaptor.capture());

        callbackSnapTokenResponseCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();
    }

    @Test
    public void checkoutError() {
        callbackImplement.checkout(snapTokenRequestModelMock);
        Mockito.verify(merchantApi, Mockito.times(1)).checkout(snapTokenRequestModelCaptor.capture(),
                callbackSnapTokenResponseCaptor.capture());
        callbackSnapTokenResponseCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();
    }


    /**
     * Get Transaction Options
     */

    @Test
    public void getTransactionOptionError_whenResponseNull() {
        callbackImplement.getPaymentOption(tokenId);
        Mockito.verify(snapAPI).getPaymentOption(tokenIdCaptor.capture(), transactionCaptorMock.capture());
        transactionCaptorMock.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void getTransactionOptionSuccess() {
        Mockito.when(transactionMock.getTransactionData()).thenReturn(transactionDataMock);
        Mockito.when(transactionDataMock.getTransactionId()).thenReturn(transactionId);

        callbackImplement.getPaymentOption(tokenId);
        Mockito.verify(snapAPI).getPaymentOption(tokenIdCaptor.capture(), transactionCaptorMock.capture());
        transactionCaptorMock.getValue().success(transactionMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onGetPaymentOptionSuccess();
    }

    @Test
    public void getTransactionOptionError_whenTransIdEmpty() {
        Mockito.when(transactionMock.getTransactionData()).thenReturn(transactionDataMock);
        Mockito.when(transactionDataMock.getTransactionId()).thenReturn("");

        callbackImplement.getPaymentOption(tokenId);
        Mockito.verify(snapAPI).getPaymentOption(tokenIdCaptor.capture(), transactionCaptorMock.capture());
        transactionCaptorMock.getValue().success(transactionMock, retrofitResponse);

        Mockito.verify(callbackCollaborator).onGetPaymentOptionFailure();
    }

    @Test
    public void getSnapTransactionError_whenCodeNot200() {
        retrofitResponse = retrofitResponseError;
        callbackImplement.getPaymentOption(tokenId);
        Mockito.verify(snapAPI).getPaymentOption(tokenIdCaptor.capture(), transactionCaptorMock.capture());
        transactionCaptorMock.getValue().success(transactionMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onGetPaymentOptionFailure();
    }

    @Test
    public void getSnapTransactionError_whenResponseNull() {
        retrofitResponse = retrofitResponseError;
        callbackImplement.getPaymentOption(tokenId);
        Mockito.verify(snapAPI).getPaymentOption(tokenIdCaptor.capture(), transactionCaptorMock.capture());
        transactionCaptorMock.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void getTransactionOptoins_whenCertPathInvalid() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.getPaymentOption(tokenId);

        Mockito.verify(snapAPI).getPaymentOption(tokenIdCaptor.capture(), transactionCaptorMock.capture());
        transactionCaptorMock.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void getTransactionOptoins_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.getPaymentOption(tokenId);
        Mockito.verify(snapAPI).getPaymentOption(tokenIdCaptor.capture(), transactionCaptorMock.capture());
        transactionCaptorMock.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void getTransactionOptoins_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.getPaymentOption(tokenId);
        Mockito.verify(snapAPI).getPaymentOption(tokenIdCaptor.capture(), transactionCaptorMock.capture());
        transactionCaptorMock.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    /**
     * Payment using Credit Card.
     */

    @Test
    public void paymentUsingCreditCard_whenRequestNull() {
        callbackImplement.paymentUsingCreditCard(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingCreditCardSuccess_whenResponseNull() {
        callbackImplement.paymentUsingCreditCard(creditcardRequestMock);
        Mockito.verify(snapAPI).paymentUsingCreditCard(creditCardRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingCreditCardSuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingCreditCard(transactionRequestMock);
        Mockito.verify(snapAPI).paymentUsingCreditCard(creditCardRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingCreditCardSuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");

        callbackImplement.paymentUsingCreditCard(transactionRequestMock);
        Mockito.verify(snapAPI).paymentUsingCreditCard(creditCardRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingCreditCardError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);

        callbackImplement.paymentUsingCreditCard(transactionRequestMock);
        Mockito.verify(snapAPI).paymentUsingCreditCard(creditCardRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingCreditCardError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);

        callbackImplement.paymentUsingCreditCard(transactionRequestMock);
        Mockito.verify(snapAPI).paymentUsingCreditCard(creditCardRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingCreditCardError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);

        callbackImplement.paymentUsingCreditCard(transactionRequestMock);
        Mockito.verify(snapAPI).paymentUsingCreditCard(creditCardRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment Using Bank Transfer BCA
     */
    @Test
    public void paymentUsingBankTransferBCA_whenRequestNull() {
        callbackImplement.paymentUsingSnapBankTransferBCA(null);
        Mockito.verify(callbackCollaborator).onError();
    }


    @Test
    public void paymentUsingBankTransferBCA_whenResponseNull() {
        callbackImplement.paymentUsingSnapBankTransferBCA(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferBCA(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBankTransferSuccess() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapBankTransferBCA(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferBCA(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingBankTransferBCA_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapBankTransferBCA(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferBCA(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingBankTransferBCA_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapBankTransferBCA(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferBCA(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBankTransferBCA_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapBankTransferBCA( bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferBCA(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingBankTransferBCA_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapBankTransferBCA(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferBCA(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment Using Bank Transfer Permata.
     */
    @Test
    public void paymentUsingBankTransferPermata_whenRequestNull() {
        callbackImplement.paymentUsingSnapBankTransferPermata(null);
        Mockito.verify(callbackCollaborator).onError();
    }


    @Test
    public void paymentUsingBankTransferPermataSuccess_whenResponseNull() {
        callbackImplement.paymentUsingSnapBankTransferPermata(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferPermata(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBankTransferPermataSuccess_whenCode200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        transactionManager.setSDKLogEnabled(true);
        callbackImplement.paymentUsingSnapBankTransferPermata(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferPermata(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingBankTransferPermata_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");

        callbackImplement.paymentUsingSnapBankTransferPermata(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferPermata(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingBankTransferPermata_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapBankTransferPermata(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferPermata(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBankTransferPermata_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapBankTransferBCA(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferBCA(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingBankTransferPermata_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapBankTransferPermata(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferPermata(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using KlikBCA
     */
    @Test
    public void paymentUsingKlikBCA_whenRequestNull() {
        callbackImplement.paymentUsingKlikBCA(null);
        Mockito.verify(callbackCollaborator).onError();
    }


    @Test
    public void paymentUsingKlikBCA_whenResponseNull() {
        callbackImplement.paymentUsingKlikBCA(klikBCARequestMock);
        Mockito.verify(snapAPI).paymentUsingKlikBCA(klikBCARequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingKlikBCA_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingKlikBCA(klikBCARequestMock);
        Mockito.verify(snapAPI).paymentUsingKlikBCA(klikBCARequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingKlikBCASuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingKlikBCA(klikBCARequestMock);
        Mockito.verify(snapAPI).paymentUsingKlikBCA(klikBCARequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingKlikBCAError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingKlikBCA(klikBCARequestMock);
        Mockito.verify(snapAPI).paymentUsingKlikBCA(klikBCARequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingKlikBCAError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingKlikBCA(klikBCARequestMock);
        Mockito.verify(snapAPI).paymentUsingKlikBCA(klikBCARequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingKlikBCAError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingKlikBCA(klikBCARequestMock);
        Mockito.verify(snapAPI).paymentUsingKlikBCA(klikBCARequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using BCA Klikpay
     */
    @Test
    public void paymentUsingBCAKlikpay_whenRequestNull() {
        callbackImplement.paymentUsingBCAKlikpay(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBCAKlikpaySuccess_whenResponseNull() {
        callbackImplement.paymentUsingBCAKlikpay(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBCAKlikPay(BCAKlikpayRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBCAKlikpaySuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingBCAKlikpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBCAKlikPay(BCAKlikpayRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingBCAKlikpay_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingBCAKlikpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBCAKlikPay(BCAKlikpayRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingBCAKlikpay_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingBCAKlikpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBCAKlikPay(BCAKlikpayRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBCAKlikpayError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingBCAKlikpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBCAKlikPay(BCAKlikpayRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingBCAKlikpayError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingBCAKlikpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBCAKlikPay(BCAKlikpayRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using mandiri billpay
     */

    @Test
    public void paymentUsingMandiriBillpay_whenRequestNull() {
        callbackImplement.paymentUsingSnapMandiriBillpay(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingMandiriBillpay_whenResponseNull() {
        callbackImplement.paymentUsingSnapMandiriBillpay(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriBillPay(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingMandiriBillpaySuccess() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapMandiriBillpay(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriBillPay(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingMandirBillpay_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapMandiriBillpay(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriBillPay(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingMandiriBillPayError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapMandiriBillpay(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriBillPay(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingMandiriBillPayError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapMandiriBillpay(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriBillPay(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingMandiriBillpayError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapMandiriBillpay(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriBillPay(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using mandiri clickpay
     */

    @Test
    public void paymentUsingMandiriClickpay_whenRequestNull() {
        callbackImplement.paymentUsingSnapMandiriClickPay(null);
        Mockito.verify(callbackCollaborator).onError();
    }
    @Test
    public void paymentUsingMandiriClickpaySuccess_whenResponseNull() {
        callbackImplement.paymentUsingSnapMandiriClickPay(mandiriClickPayPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriClickPay(mandirClickPayPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingMandiriClickPaySuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapMandiriClickPay(mandiriClickPayPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriClickPay(mandirClickPayPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingMandirClickPaySuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapMandiriClickPay(mandiriClickPayPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriClickPay(mandirClickPayPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingMandiriClickPayError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapMandiriClickPay(mandiriClickPayPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriClickPay(mandirClickPayPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingMandiriClickpayError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapMandiriClickPay(mandiriClickPayPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriClickPay(mandirClickPayPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingMandiriClickPayError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapMandiriClickPay(mandiriClickPayPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriClickPay(mandirClickPayPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using cimbclick
     */

    @Test
    public void paymentCIMBClick_whenRequestNull() {
        callbackImplement.paymentUsingSnapCIMBClick(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingCIMBClickpay_whenResponseNull() {
        callbackImplement.paymentUsingSnapCIMBClick(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingCIMBClick(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingCIMBClickSuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapCIMBClick(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingCIMBClick(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingCIMBClick_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapCIMBClick(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingCIMBClick(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingCIMBClickError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapCIMBClick(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingCIMBClick(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingCIMBClickError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapCIMBClick(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingCIMBClick(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingCIMBClickError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapCIMBClick(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingCIMBClick(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Mockito.anyString(), Mockito.anyString());
    }

    /**
     * Payment using bri epay
     */
    @Test
    public void paymentEpayBRI_whenRequestNull() {
        callbackImplement.paymentUsingSnapBRIEpay(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBRIEpaySuccess_whenResponseNull() {
        callbackImplement.paymentUsingSnapBRIEpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBRIEpay(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBRIEpaySuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapBRIEpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBRIEpay(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingBRIEpayuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapBRIEpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBRIEpay(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingBRIEpayError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapBRIEpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBRIEpay(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBRIEpayError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapBRIEpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBRIEpay(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingBRIEpayError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapBRIEpay(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingBRIEpay(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using mandiri ecash
     */

    @Test
    public void paymentUsingMandiriEcash_whenRequestNull() {
        callbackImplement.paymentUsingSnapMandirEcash(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingMandiriEcashSuccess_whenResponseNull() {
        callbackImplement.paymentUsingSnapMandirEcash(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriEcash(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingMandiriEcashSuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapMandirEcash(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriEcash(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingMandiriEcashSuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapMandirEcash(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriEcash(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingMandiriEcashError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapMandirEcash(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriEcash(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingMandiriEcashError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapMandirEcash(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriEcash(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingMandirEcashError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapMandirEcash(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingMandiriEcash(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using telkomsel ecash
     */

    @Test
    public void paymentUsingtelkomselEcash_whenRequestNull() {
        callbackImplement.paymentUsingSnapTelkomselEcash(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingTelkomselEcashSuccess_whenResponseNull() {
        callbackImplement.paymentUsingSnapTelkomselEcash(telkomselEcashPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingTelkomselEcash(telkomselEcashPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingTelkomselEcashSuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapTelkomselEcash(telkomselEcashPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingTelkomselEcash(telkomselEcashPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingTelkomselEcashSuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapTelkomselEcash(telkomselEcashPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingTelkomselEcash(telkomselEcashPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingTelkomselEcashError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapTelkomselEcash(telkomselEcashPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingTelkomselEcash(telkomselEcashPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingTelkomselEcashError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapTelkomselEcash( telkomselEcashPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingTelkomselEcash(telkomselEcashPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingTelkomselEcashError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapTelkomselEcash(telkomselEcashPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingTelkomselEcash(telkomselEcashPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using xl tunai
     */

    @Test
    public void paymentUsingXLTunai_whenRequestNull() {
        callbackImplement.paymentUsingSnapXLTunai(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingXLTunaiSuccess_whenResponseNull() {
        callbackImplement.paymentUsingSnapXLTunai(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingXlTunai(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingXLTunaiSuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapXLTunai(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingXlTunai(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingXLTunaiSuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapXLTunai(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingXlTunai(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingXLTunaiError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapXLTunai(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingXlTunai(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingXLTunaiError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapXLTunai(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingXlTunai(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingXLTunaiEcashError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapXLTunai(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingXlTunai(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }


    /**
     * Payment using indomaret
     */

    @Test
    public void paymentUsingIndomaret_whenRequestNull() {
        callbackImplement.paymentUsingSnapIndomaret(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingIndomaretuccess_whenResponseNull() {
        callbackImplement.paymentUsingSnapIndomaret(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndomaret(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingIndomaretSuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapIndomaret(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndomaret(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingIndomaretSuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapIndomaret(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndomaret(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingIndomaretrror_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapIndomaret(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndomaret(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingIndomaretError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapIndomaret(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndomaret(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingIndomaretEcashError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapIndomaret(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndomaret(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using indosat dompetku
     */
    @Test
    public void paymentUsingIndosatDompetku_whenRequestNull() {
        callbackImplement.paymentUsingSnapIndosatDompetku(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingIndosatDompetkuSuccess_whenResponseNull() {
        callbackImplement.paymentUsingSnapIndosatDompetku(indosatDompetkuPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndosatDompetku(indosatDompetKuPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingIndosatDompetkuSuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapIndosatDompetku(indosatDompetkuPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndosatDompetku(indosatDompetKuPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymetUsingIndosatDompetkuSuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapIndosatDompetku(indosatDompetkuPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndosatDompetku(indosatDompetKuPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingIndosatDompetkuError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapIndosatDompetku(indosatDompetkuPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndosatDompetku(indosatDompetKuPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingIndosatDompetkuError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapIndosatDompetku(indosatDompetkuPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndosatDompetku(indosatDompetKuPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingIndosatDompetku_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapIndosatDompetku(indosatDompetkuPaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingIndosatDompetku(indosatDompetKuPaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using kiosan
     */

    @Test
    public void paymentUsingKiosan_whenRequestNull() {
        callbackImplement.paymentUsingSnapKiosan(null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingKiosanSuccess_whenResponseNull() {
        callbackImplement.paymentUsingSnapKiosan(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingKiosan(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingKiosanSuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapKiosan(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingKiosan(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymetUsingKiosanSuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapKiosan(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingKiosan(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingKiosanError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapKiosan(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingKiosan(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingKiosanError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapKiosan(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingKiosan(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingKiosanError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapKiosan(basePaymentRequestMock);
        Mockito.verify(snapAPI).paymentUsingKiosan(basePaymentRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /**
     * Payment using Bank Transfer All Bank.
     */

    @Test
    public void paymentUsingBankTransferAllBank_whenRequestNull() {
        callbackImplement.paymentUsingSnapBankTransferAllBank(null);
        Mockito.verify(callbackCollaborator).onError();
    }


    @Test
    public void paymentUsingBankTransferAllBankSuccess_whenResponseNull() {
        callbackImplement.paymentUsingSnapBankTransferAllBank(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferAllBank(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBankTransferAllBankSuccess_whenCode200() {
        transactionManager.setSDKLogEnabled(true);
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("200");
        callbackImplement.paymentUsingSnapBankTransferAllBank(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferAllBank(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        PowerMockito.verifyStatic(Mockito.times(5));
        Logger.d(Matchers.anyString(), Matchers.anyString());
        Mockito.verify(callbackCollaborator).onTransactionSuccess();
    }

    @Test
    public void paymentUsingBankTransferAllBankSuccess_whenCodeNot200() {
        Mockito.when(transactionResponseMock.getStatusCode()).thenReturn("300");
        callbackImplement.paymentUsingSnapBankTransferAllBank(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferAllBank(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().success(transactionResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onTransactionFailure();
    }

    @Test
    public void paymentUsingBankTransferAllBankError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.paymentUsingSnapBankTransferAllBank(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferAllBank(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void paymentUsingBankTransferAllBankError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.paymentUsingSnapBankTransferAllBank(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferAllBank(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void paymentUsingBankTransferAllBankError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.paymentUsingSnapBankTransferAllBank(bankTransferRequestMock);
        Mockito.verify(snapAPI).paymentUsingBankTransferAllBank(bankTransferRequestCaptor.capture(), transactionResponseCallbackCaptor.capture());
        transactionResponseCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /*
     * save card to merchant server
     */

    @Test
    public void savecard_whenRequestNull() {
        callbackImplement.saveCards(sampleUserId, null);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void savecardSuccess_whenCode200or201(){
        callbackImplement.saveCards(sampleUserId, cardRequestListMock);
        Mockito.verify(merchantApi).saveCards(sampleUserIdCaptor.capture(), cardRequestListCaptor.capture(), saveCardCallbackCaptor.capture());
        saveCardCallbackCaptor.getValue().success(saveCardResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator).onSaveCardsSuccess();
    }

    @Test
    public void savecard_whenFailure(){
        retrofitResponse = new Response("URL", 210, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

        callbackImplement.saveCards(sampleUserId, cardRequestListMock);
        Mockito.verify(merchantApi).saveCards(sampleUserIdCaptor.capture(), cardRequestListCaptor.capture(), saveCardCallbackCaptor.capture());
        saveCardCallbackCaptor.getValue().success(saveCardResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator).onSaveCardsFailure();
    }

    @Test
    public void saveCardError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.saveCards(sampleUserId, cardRequestListMock);
        Mockito.verify(merchantApi).saveCards(sampleUserIdCaptor.capture(), cardRequestListCaptor.capture(), saveCardCallbackCaptor.capture());
        saveCardCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void saveCardError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.saveCards(sampleUserId, cardRequestListMock);
        Mockito.verify(merchantApi).saveCards(sampleUserIdCaptor.capture(), cardRequestListCaptor.capture(), saveCardCallbackCaptor.capture());
        saveCardCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void saveCardError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.saveCards(sampleUserId, cardRequestListMock);
        Mockito.verify(merchantApi).saveCards(sampleUserIdCaptor.capture(), cardRequestListCaptor.capture(), saveCardCallbackCaptor.capture());
        saveCardCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /*
     * get cards from merchant server
     */


    @Test
    public void getCardsSuccess_whenCode200or201(){
        Mockito.when(getCardResponseMock.size()).thenReturn(1);
        callbackImplement.snapGetCards(sampleUserId);
        Mockito.verify(merchantApi).getCards(sampleUserIdCaptor.capture(), getCardCallbackCaptor.capture());
        getCardCallbackCaptor.getValue().success(getCardResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onGetCardsSuccess();
    }

    @Test
    public void getCardSuccess_whenCodeNot200orNot201(){
        retrofitResponse = new Response("URL", 210, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));
        Mockito.when(getCardResponseMock.size()).thenReturn(1);
        callbackImplement.snapGetCards(sampleUserId);
        Mockito.verify(merchantApi).getCards(sampleUserIdCaptor.capture(),  getCardCallbackCaptor.capture());
        getCardCallbackCaptor.getValue().success(getCardResponseMock, retrofitResponse);
        Mockito.verify(callbackCollaborator).onSaveCardsFailure();
    }

    @Test
    public void getCardSuccess_whenResponseNull(){
        callbackImplement.snapGetCards(sampleUserId);
        Mockito.verify(merchantApi).getCards(sampleUserIdCaptor.capture(),  getCardCallbackCaptor.capture());
        getCardCallbackCaptor.getValue().success(null, retrofitResponse);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void getCardCardError_whenGeneralError() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorGeneralMock);
        callbackImplement.snapGetCards(sampleUserId);
        Mockito.verify(merchantApi).getCards(sampleUserIdCaptor.capture(), getCardCallbackCaptor.capture());
        getCardCallbackCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator).onError();
    }

    @Test
    public void getCardError_whenInvalidSSL() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackImplement.snapGetCards(sampleUserId);
        Mockito.verify(merchantApi).getCards(sampleUserIdCaptor.capture(), getCardCallbackCaptor.capture());
        getCardCallbackCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    @Test
    public void getCardError_whenInvalidCertPath() {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackImplement.snapGetCards(sampleUserId);
        Mockito.verify(merchantApi).getCards(sampleUserIdCaptor.capture(), getCardCallbackCaptor.capture());
        getCardCallbackCaptor.getValue().failure(retrofitError);
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /*
     * card registration test
     */

    @Test
    public void testCardRegistrationSuccess() throws Exception {
        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));
        CardRegistrationResponse registrationResponse = new CardRegistrationResponse();


        //registration success
        registrationResponse.setStatusCode("200");
        callbackImplement.cardRegistration(CARD_NUMBER, CARD_CVV, CARD_EXP_MONTH,
                CARD_EXP_YEAR, mAuthToken);
        Mockito.verify(veritransApi, Mockito.times(1)).registerCard(callbackArgumentCaptorCardNumber.capture(),
                callbackArgumentCaptorCardCVV.capture(),
                callbackArgumentCaptorCardMonth.capture(),
                callbackArgumentCaptorCardYear.capture(),
                calbackArgumentCatorClientKey.capture(),
                callbackArgumentCaptor.capture());
        callbackArgumentCaptor.getValue().success(registrationResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onCardRegistrationSuccess();
    }

    @Test
    public void testCardRegistration_whenResponseNull() throws Exception {
        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));
        CardRegistrationResponse registrationResponse = null;

        //retrofitResponse success but transacation not success
        callbackImplement.cardRegistration(CARD_NUMBER, CARD_CVV, CARD_EXP_MONTH,
                CARD_EXP_YEAR, mAuthToken);
        Mockito.verify(veritransApi, Mockito.times(1)).registerCard(callbackArgumentCaptorCardNumber.capture(),
                callbackArgumentCaptorCardCVV.capture(),
                callbackArgumentCaptorCardMonth.capture(),
                callbackArgumentCaptorCardYear.capture(),
                calbackArgumentCatorClientKey.capture(),
                callbackArgumentCaptor.capture());
        callbackArgumentCaptor.getValue().success(registrationResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();
    }

    @Test
    public void testCardRegistration_whenResponseSuccess_codeNot200() throws Exception {
        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));
        CardRegistrationResponse registrationResponse = new CardRegistrationResponse();
        registrationResponse.setStatusCode("212");

        //retrofitResponse success but transacation not success
        callbackImplement.cardRegistration(CARD_NUMBER, CARD_CVV, CARD_EXP_MONTH,
                CARD_EXP_YEAR, mAuthToken);
        Mockito.verify(veritransApi, Mockito.times(1)).registerCard(callbackArgumentCaptorCardNumber.capture(),
                callbackArgumentCaptorCardCVV.capture(),
                callbackArgumentCaptorCardMonth.capture(),
                callbackArgumentCaptorCardYear.capture(),
                calbackArgumentCatorClientKey.capture(),
                callbackArgumentCaptor.capture());
        callbackArgumentCaptor.getValue().success(registrationResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onCardRegistrationFailed();
    }


    @Test
    public void testCardRegistrationError_whenValidSSL() throws Exception {
        CardRegistrationResponse registrationResponse = new CardRegistrationResponse();
        registrationResponse.setStatusCode("400");
        callbackImplement.cardRegistration(CARD_NUMBER, CARD_CVV, CARD_EXP_MONTH,
                CARD_EXP_YEAR, mAuthToken);
        Mockito.verify(veritransApi, Mockito.times(1)).registerCard(callbackArgumentCaptorCardNumber.capture(),
                callbackArgumentCaptorCardCVV.capture(),
                callbackArgumentCaptorCardMonth.capture(),
                callbackArgumentCaptorCardYear.capture(),
                calbackArgumentCatorClientKey.capture(),
                callbackArgumentCaptor.capture());
        callbackArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();
    }

    @Test
    public void testCardRegistrationError_whenInValidSSLErrorAndCertPathError() throws Exception {
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);

        CardRegistrationResponse registrationResponse = new CardRegistrationResponse();
        registrationResponse.setStatusCode("400");
        callbackImplement.cardRegistration(CARD_NUMBER, CARD_CVV, CARD_EXP_MONTH,
                CARD_EXP_YEAR, mAuthToken);
        Mockito.verify(veritransApi, Mockito.times(1)).registerCard(callbackArgumentCaptorCardNumber.capture(),
                callbackArgumentCaptorCardCVV.capture(),
                callbackArgumentCaptorCardMonth.capture(),
                callbackArgumentCaptorCardYear.capture(),
                calbackArgumentCatorClientKey.capture(),
                callbackArgumentCaptor.capture());
        callbackArgumentCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());

        //when certificate path error
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);

        callbackArgumentCaptor.getValue().failure(retrofitError);
        PowerMockito.verifyStatic(Mockito.times(2));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /*
     * get card token test
     */

        /*
     * get3DSToken
     */

    @Test public void testGetTokenSuccess_getTokenSuccess(){
        TokenDetailsResponse tokenDetailsResponse = new TokenDetailsResponse();
        tokenDetailsResponse.setStatusCode("200");

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getToken(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), callbackgetTokenArgumentCaptor.capture());

        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onGetCardTokenSuccess();

    }

    @Test public void testGetTokenSuccess_getToken_whenResponseNotNull(){
        TokenDetailsResponse tokenDetailsResponse = new TokenDetailsResponse();
        tokenDetailsResponse.setStatusCode("200");

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getToken(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), callbackgetTokenArgumentCaptor.capture());

        //when retrofitResponse not 200
        tokenDetailsResponse.setStatusCode("212");
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onGetCardTokenFailed();

        tokenDetailsResponse.setStatusMessage(null);
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(2)).onGetCardTokenFailed();
    }

    @Test public void testGetTokenSuccess_getToken_whenresponseNull(){
        TokenDetailsResponse tokenDetailsResponse = null;

        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getToken(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), callbackgetTokenArgumentCaptor.capture());

        // when retrofitResponse null
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();

    }


    @Test
    public void testGetTokenError_getToken(){
        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getToken(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), callbackgetTokenArgumentCaptor.capture());

        //when valid certification
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();

        // when invalid certification
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(2)).onError();
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());

        // when invalid certification path
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(3)).onError();
        PowerMockito.verifyStatic(Mockito.times(2));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /*
     * get3DSToken
     */

    @Test public void testGetTokenSuccess_get3DSTokenSuccess(){
        TokenDetailsResponse tokenDetailsResponse = new TokenDetailsResponse();
        tokenDetailsResponse.setStatusCode("200");
        cardTokenRequest.setSecure(true);
        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).get3DSToken(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), bankCaptor.capture(),
                scureCaptor.capture(), twoClickCaptor.capture(),
                grossAmountCaptor.capture(), callbackgetTokenArgumentCaptor.capture());

        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onGetCardTokenSuccess();

    }

    @Test public void testGetTokenSuccess_get3DSToken_whenResponseNotNull(){
        TokenDetailsResponse tokenDetailsResponse = new TokenDetailsResponse();
        tokenDetailsResponse.setStatusCode("200");
        cardTokenRequest.setSecure(true);

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).get3DSToken(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), bankCaptor.capture(),
                scureCaptor.capture(), twoClickCaptor.capture(),
                grossAmountCaptor.capture(), callbackgetTokenArgumentCaptor.capture());

        //when retrofitResponse not 200
        tokenDetailsResponse.setStatusCode("212");
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onGetCardTokenFailed();

        tokenDetailsResponse.setStatusMessage(null);
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(2)).onGetCardTokenFailed();
    }

    @Test public void testGetTokenSuccess_get3DSToken_whenresponseNull(){
        TokenDetailsResponse tokenDetailsResponse = null;
        cardTokenRequest.setSecure(true);

        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).get3DSToken(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), bankCaptor.capture(),
                scureCaptor.capture(), twoClickCaptor.capture(),
                grossAmountCaptor.capture(), callbackgetTokenArgumentCaptor.capture());

        // when retrofitResponse null
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();

    }


    @Test
    public void testGetTokenError_get3DSToken(){
        cardTokenRequest.setSecure(true);
        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).get3DSToken(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), bankCaptor.capture(),
                scureCaptor.capture(), twoClickCaptor.capture(),
                grossAmountCaptor.capture(), callbackgetTokenArgumentCaptor.capture());

        //when valid certification
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();

        // when invalid certification
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(2)).onError();
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());

        // when invalid certification path
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(3)).onError();
        PowerMockito.verifyStatic(Mockito.times(2));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /*
     * getToken Instalment Offer TwoClick
     */


    @Test public void testGetTokenSuccess_get3DSTokenInstalmentOffers(){
        TokenDetailsResponse tokenDetailsResponse = new TokenDetailsResponse();
        tokenDetailsResponse.setStatusCode("200");

        cardTokenRequest.setInstalment(true);

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).get3DSTokenInstalmentOffers(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), bankCaptor.capture(),
                scureCaptor.capture(), twoClickCaptor.capture(),
                grossAmountCaptor.capture(),
                instalmentCaptor.capture(),
                instalmentTermCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onGetCardTokenSuccess();
    }

    @Test public void testGetTokenSuccess_get3DSTokenInstalmentOffers_whenResponseNotNull(){
        TokenDetailsResponse tokenDetailsResponse = new TokenDetailsResponse();
        tokenDetailsResponse.setStatusCode("200");

        cardTokenRequest.setInstalment(true);

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).get3DSTokenInstalmentOffers(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), bankCaptor.capture(),
                scureCaptor.capture(), twoClickCaptor.capture(),
                grossAmountCaptor.capture(),
                instalmentCaptor.capture(),
                instalmentTermCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());


        //when retrofitResponse not 200
        tokenDetailsResponse.setStatusCode("212");
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onGetCardTokenFailed();

        tokenDetailsResponse.setStatusMessage("");
        when(TextUtils.isEmpty(Matchers.anyString())).thenReturn(false);
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, retrofitResponse);
        Mockito.verify(callbackCollaborator, Mockito.times(2)).onGetCardTokenFailed();
    }


    @Test public void testGetTokenSuccess_get3DSTokenInstalmentOffers_whenresponseNull(){
        TokenDetailsResponse tokenDetailsResponse = null;

        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

        cardTokenRequest.setInstalment(true);
        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).get3DSTokenInstalmentOffers(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), bankCaptor.capture(),
                scureCaptor.capture(), twoClickCaptor.capture(),
                grossAmountCaptor.capture(),
                instalmentCaptor.capture(),
                instalmentTermCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        // when retrofitResponse null
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();
    }


    @Test
    public void testGetTokenError_get3DSTokenInstalmentOffers(){
        cardTokenRequest.setInstalment(true);
        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).get3DSTokenInstalmentOffers(cardNumberCaptor.capture(),
                cardCVVCaptor.capture(), cardExpMonthCaptor.capture(),
                cardExpYearCaptor.capture(), clientKeyCaptor.capture(), bankCaptor.capture(),
                scureCaptor.capture(), twoClickCaptor.capture(),
                grossAmountCaptor.capture(),
                instalmentCaptor.capture(),
                instalmentTermCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        //when valid certification
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();

        // when invalid certification
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(2)).onError();
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());

        // when invalid certification path
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(3)).onError();
        PowerMockito.verifyStatic(Mockito.times(2));
    }

    /*
     * getTokenTwoClick
     */
    @Test public void testGetTokenSuccess_getTokenTwoClick(){
        TokenDetailsResponse tokenDetailsResponse = new TokenDetailsResponse();
        tokenDetailsResponse.setStatusCode("200");

        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

        cardTokenRequest.setInstalment(false);
        cardTokenRequest.setTwoClick(true);

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getTokenTwoClick(
                cardCVVCaptor.capture(),
                tokenIdCaptor.capture(),
                twoClickCaptor.capture(),
                scureCaptor.capture(),
                grossAmountCaptor.capture(),
                bankCaptor.capture(),
                clientKeyCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        // when retrofitResponse 20
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onGetCardTokenSuccess();
    }

    @Test public void testGetTokenSuccess_getTokenTwoClick_whenResponseNotNull(){
        TokenDetailsResponse tokenDetailsResponse = new TokenDetailsResponse();
        tokenDetailsResponse.setStatusCode("200");

        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

        cardTokenRequest.setInstalment(false);
        cardTokenRequest.setTwoClick(true);

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getTokenTwoClick(
                cardCVVCaptor.capture(),
                tokenIdCaptor.capture(),
                twoClickCaptor.capture(),
                scureCaptor.capture(),
                grossAmountCaptor.capture(),
                bankCaptor.capture(),
                clientKeyCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        //when retrofitResponse not 200
        tokenDetailsResponse.setStatusCode("212");
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onGetCardTokenFailed();

        tokenDetailsResponse.setStatusMessage(null);
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(2)).onGetCardTokenFailed();
    }

    @Test public void testGetTokenSuccess_getTokenTwoClick_whenresponseNull(){
        TokenDetailsResponse tokenDetailsResponse = null;

        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

        cardTokenRequest.setInstalment(false);
        cardTokenRequest.setTwoClick(true);

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getTokenTwoClick(
                cardCVVCaptor.capture(),
                tokenIdCaptor.capture(),
                twoClickCaptor.capture(),
                scureCaptor.capture(),
                grossAmountCaptor.capture(),
                bankCaptor.capture(),
                clientKeyCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        // when retrofitResponse null
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();

    }


    @Test
    public void testGetTokenError_getTokenTwoClick(){
        cardTokenRequest.setInstalment(false);
        cardTokenRequest.setTwoClick(true);
        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getTokenTwoClick(
                cardCVVCaptor.capture(),
                tokenIdCaptor.capture(),
                twoClickCaptor.capture(),
                scureCaptor.capture(),
                grossAmountCaptor.capture(),
                bankCaptor.capture(),
                clientKeyCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        //when valid certification
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();

        // when invalid certification
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(2)).onError();
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());

        // when invalid certification path
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(3)).onError();
        PowerMockito.verifyStatic(Mockito.times(2));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }

    /*
     * getToken Instalment Offer on TwoClick
     */
    @Test
    public void testGetTokenSuccess_getTokenInstalmentOfferTwoClick(){
        TokenDetailsResponse tokenDetailsResponse = new TokenDetailsResponse();
        tokenDetailsResponse.setStatusCode("200");

        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

        cardTokenRequest.setInstalment(true);
        cardTokenRequest.setTwoClick(true);

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getTokenInstalmentOfferTwoClick(
                cardCVVCaptor.capture(),
                tokenIdCaptor.capture(),
                twoClickCaptor.capture(),
                scureCaptor.capture(),
                grossAmountCaptor.capture(),
                bankCaptor.capture(),
                clientKeyCaptor.capture(),
                instalmentCaptor.capture(),
                instalmentTermCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        // when retrofitResponse 20
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onGetCardTokenSuccess();

    }

    @Test public void testGetToken_getTokenInstalmentOfferTwoClick_whenresponseNotNull(){
        TokenDetailsResponse tokenDetailsResponse = new TokenDetailsResponse();

        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

        cardTokenRequest.setInstalment(true);
        cardTokenRequest.setTwoClick(true);
        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getTokenInstalmentOfferTwoClick(
                cardCVVCaptor.capture(),
                tokenIdCaptor.capture(),
                twoClickCaptor.capture(),
                scureCaptor.capture(),
                grossAmountCaptor.capture(),
                bankCaptor.capture(),
                clientKeyCaptor.capture(),
                instalmentCaptor.capture(),
                instalmentTermCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        //when retrofitResponse not 200
        tokenDetailsResponse.setStatusCode("212");
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onGetCardTokenFailed();

        tokenDetailsResponse.setStatusMessage(null);
        callbackgetTokenArgumentCaptor.getValue().success(tokenDetailsResponse, response);
        Mockito.verify(callbackCollaborator, Mockito.times(2)).onGetCardTokenFailed();
    }

    @Test public void testGetToken_getTokenInstalmentOfferTwoClick_whenresponseNull(){
        Response response = new Response("URL", 200, "success", Collections.EMPTY_LIST,
                new TypedByteArray("application/sampleJsonResponse", sampleJsonResponse.getBytes()));

        cardTokenRequest.setInstalment(true);
        cardTokenRequest.setTwoClick(true);
        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getTokenInstalmentOfferTwoClick(
                cardCVVCaptor.capture(),
                tokenIdCaptor.capture(),
                twoClickCaptor.capture(),
                scureCaptor.capture(),
                grossAmountCaptor.capture(),
                bankCaptor.capture(),
                clientKeyCaptor.capture(),
                instalmentCaptor.capture(),
                instalmentTermCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        callbackgetTokenArgumentCaptor.getValue().success(null, response);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();

    }


    @Test
    public void testGetTokenError_getTokenInstalmentOfferTwoClick(){
        cardTokenRequest.setInstalment(true);
        cardTokenRequest.setTwoClick(true);

        callbackImplement.getToken(cardTokenRequest);

        Mockito.verify(veritransApi, Mockito.times(1)).getTokenInstalmentOfferTwoClick(
                cardCVVCaptor.capture(),
                tokenIdCaptor.capture(),
                twoClickCaptor.capture(),
                scureCaptor.capture(),
                grossAmountCaptor.capture(),
                bankCaptor.capture(),
                clientKeyCaptor.capture(),
                instalmentCaptor.capture(),
                instalmentTermCaptor.capture(),
                callbackgetTokenArgumentCaptor.capture());

        //when valid certification
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(1)).onError();

        // when invalid certification
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidSSLException);
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(2)).onError();
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString(), Matchers.anyString());

        // when invalid certification path
        Mockito.when(retrofitError.getCause()).thenReturn(errorInvalidCertPatMock);
        callbackgetTokenArgumentCaptor.getValue().failure(retrofitError);
        Mockito.verify(callbackCollaborator, Mockito.times(3)).onError();
        PowerMockito.verifyStatic(Mockito.times(2));
        Logger.e(Matchers.anyString(), Matchers.anyString());
    }
}
