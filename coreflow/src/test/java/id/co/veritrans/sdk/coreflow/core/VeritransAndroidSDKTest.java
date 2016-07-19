package id.co.veritrans.sdk.coreflow.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.powermock.api.mockito.PowerMockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;

import id.co.veritrans.sdk.coreflow.APIClientMain;
import id.co.veritrans.sdk.coreflow.R;
import id.co.veritrans.sdk.coreflow.SDKConfigTest;
import id.co.veritrans.sdk.coreflow.eventbus.bus.VeritransBus;
import id.co.veritrans.sdk.coreflow.eventbus.bus.VeritransBusProvider;
import id.co.veritrans.sdk.coreflow.eventbus.callback.CardRegistrationBusCallback;
import id.co.veritrans.sdk.coreflow.eventbus.callback.DeleteCardBusCallback;
import id.co.veritrans.sdk.coreflow.eventbus.callback.GetAuthenticationBusCallback;
import id.co.veritrans.sdk.coreflow.eventbus.callback.GetCardBusCallback;
import id.co.veritrans.sdk.coreflow.eventbus.callback.GetOfferBusCallback;
import id.co.veritrans.sdk.coreflow.eventbus.callback.GetSnapTokenCallback;
import id.co.veritrans.sdk.coreflow.eventbus.callback.GetSnapTransactionCallback;
import id.co.veritrans.sdk.coreflow.eventbus.callback.HttpErrorCallback;
import id.co.veritrans.sdk.coreflow.eventbus.callback.SaveCardBusCallback;
import id.co.veritrans.sdk.coreflow.eventbus.callback.TokenBusCallback;
import id.co.veritrans.sdk.coreflow.eventbus.callback.TransactionBusCallback;
import id.co.veritrans.sdk.coreflow.eventbus.events.AuthenticationEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.CardRegistrationFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.CardRegistrationSuccessEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.DeleteCardFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.DeleteCardSuccessEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.GeneralErrorEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.GetCardFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.GetCardsSuccessEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.GetOfferFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.GetOfferSuccessEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.GetTokenFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.GetTokenSuccessEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.NetworkUnavailableEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.SSLErrorEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.SaveCardFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.SaveCardSuccessEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.TransactionFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.TransactionSuccessEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.snap.GetSnapTokenFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.snap.GetSnapTokenSuccessEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.snap.GetSnapTransactionFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.snap.GetSnapTransactionSuccessEvent;
import id.co.veritrans.sdk.coreflow.models.BBMCallBackUrl;
import id.co.veritrans.sdk.coreflow.models.BBMMoneyRequestModel;
import id.co.veritrans.sdk.coreflow.models.BCABankTransfer;
import id.co.veritrans.sdk.coreflow.models.BCAKlikPayDescriptionModel;
import id.co.veritrans.sdk.coreflow.models.BCAKlikPayModel;
import id.co.veritrans.sdk.coreflow.models.CIMBClickPayModel;
import id.co.veritrans.sdk.coreflow.models.CardTokenRequest;
import id.co.veritrans.sdk.coreflow.models.CardTransfer;
import id.co.veritrans.sdk.coreflow.models.CstoreEntity;
import id.co.veritrans.sdk.coreflow.models.DescriptionModel;
import id.co.veritrans.sdk.coreflow.models.EpayBriTransfer;
import id.co.veritrans.sdk.coreflow.models.IndomaretRequestModel;
import id.co.veritrans.sdk.coreflow.models.IndosatDompetkuRequest;
import id.co.veritrans.sdk.coreflow.models.ItemDetails;
import id.co.veritrans.sdk.coreflow.models.KlikBCADescriptionModel;
import id.co.veritrans.sdk.coreflow.models.KlikBCAModel;
import id.co.veritrans.sdk.coreflow.models.MandiriBillPayTransferModel;
import id.co.veritrans.sdk.coreflow.models.MandiriClickPayModel;
import id.co.veritrans.sdk.coreflow.models.MandiriClickPayRequestModel;
import id.co.veritrans.sdk.coreflow.models.MandiriECashModel;
import id.co.veritrans.sdk.coreflow.models.PaymentMethodsModel;
import id.co.veritrans.sdk.coreflow.models.PermataBankTransfer;
import id.co.veritrans.sdk.coreflow.models.SaveCardRequest;
import id.co.veritrans.sdk.coreflow.models.UserDetail;
import id.co.veritrans.sdk.coreflow.transactionmanager.BusCollaborator;
import id.co.veritrans.sdk.coreflow.utilities.Utils;

/**
 * Created by ziahaqi on 24/06/2016.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocalDataHandler.class, SdkUtil.class, Looper.class, Utils.class,Log.class, TextUtils.class, Logger.class  })

public class VeritransAndroidSDKTest {

    @Mock
    Context contextMock;
    @Mock
    TransactionManager transactionManager;
    @Mock
    MerchantRestAPI merchantRestAPI;
    @Mock
    VeritransRestAPI veritransRestAPI;
    @Mock
    Resources resourceMock;
    @Mock
    ConnectivityManager connectivityManager;
    @Mock
    NetworkInfo networkInfo;
    @Mock
    boolean isconnected;
    @Mock
    SharedPreferences preference;
    @Mock
    private String sdkTokenMock;
    @Mock
    private boolean isRunningMock;
    @Mock
    private java.lang.Boolean isconnectedMock;
    @Mock
    private CardTokenRequest cardTokenRequestMock;
    @Mock
    private VeritransBus busMock;

    @Mock
    protected BusCollaborator busCollaborator;

    @InjectMocks
    protected EventBustImplementSample eventBustImplementSample;

    private VeritransSDK veritransSDKSSpy;
    @Mock
    private TransactionRequest transactionRequestMock;
    @Mock
    private PermataBankTransfer permatabankTransferMok;
    @Mock
    private BCABankTransfer bcaTransferMok;
    @Mock
    private CardTransfer cardTransferMock;
    @Mock
    private BCAKlikPayDescriptionModel bcaklikpayDescModelMock;
    @Mock
    private BCAKlikPayModel bcaKlikPayModelMock;
    @Mock
    private KlikBCADescriptionModel klikbcaDescModelMock;
    @Mock
    private KlikBCAModel klikBCAModelMock;
    @Mock
    private id.co.veritrans.sdk.coreflow.models.BillInfoModel billInfoModelMock;
    @Mock
    private ArrayList<ItemDetails> itemDetailMock;
    @Mock
    private MandiriBillPayTransferModel mandiriBillPayTransModelMock;
    @Mock
    private DescriptionModel descriptionModelMock;
    @Mock
    private CIMBClickPayModel cimbClickPayModel;
    @Mock
    private MandiriECashModel mandiriEcashModelMock;

    String msisdnMock = "msisdnmock";
    @Mock
    IndosatDompetkuRequest indosatDompetkuRequestMock;
    @Mock
    private EpayBriTransfer epayBriTransferMock;
    @Mock
    private CstoreEntity cstoreEntityMock;
    @Mock
    private IndomaretRequestModel indomaretRequestModelMock;
    @Mock
    private SaveCardRequest savecardMock;
    @Mock
    private SaveCardRequest saveCardRequestMock;
    @Mock
    private MandiriClickPayModel mandiriClickPayModelMock;
    @Mock
    private MandiriClickPayRequestModel mandiriClickPayRequestModelMock;
    @Mock
    private BBMMoneyRequestModel bbmMoneyRequestModelMock;
    private String userId ;
    private String transactionId = "A1";
    @Mock
    private ISdkFlow uiflowMock;

    Log fakeLog;
    private String exceptionMock;
    @Mock
    private Integer drawableIntCostumMock ;
    @Mock
    private Drawable drawableCostumMock;
    @Mock
    private Integer drawableIntDefaultMock;
    @Mock
    private Drawable drawableDefaultMock;
    private String merchantLogoMock = "merchantLogo";
    @Mock
    private BBMCallBackUrl bbmCallbackUrlMock;
    @Mock
    private ArrayList<PaymentMethodsModel> paymentMethodMock;
    @Mock
    private UserDetail userDetailMock;
    @Mock
    private MixpanelAnalyticsManager mixpanelMock;

    @Before
    public void setup(){
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(Logger.class);
        PowerMockito.mockStatic(Looper.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(SdkUtil.class);

        eventBustImplementSample.registerBus(busMock);

        Mockito.when(contextMock.getApplicationContext()).thenReturn(contextMock);
        Mockito.when(contextMock.getString(R.string.error_unable_to_connect)).thenReturn("not connected");
        Mockito.when(contextMock.getResources()).thenReturn(resourceMock);
        Mockito.when(contextMock.getResources().getDrawable(drawableIntDefaultMock)).thenReturn(drawableDefaultMock);

        Mockito.when(contextMock.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Mockito.when(networkInfo.isConnected()).thenReturn(false);
        Mockito.when(contextMock.getSharedPreferences("local.data", Context.MODE_PRIVATE)).thenReturn(preference);

        VeritransSDK veritransSDK = (new SdkCoreFlowBuilder(contextMock, SDKConfigTest.CLIENT_KEY, SDKConfigTest.MERCHANT_BASE_URL)
                .enableLog(true)
                .setDefaultText("open_sans_regular.ttf")
                .setSemiBoldText("open_sans_semibold.ttf")
                .setBoldText("open_sans_bold.ttf")
                .setMerchantName("Veritrans Example Merchant")
                .buildSDK());
        Mockito.when(veritransSDK.readAuthenticationToken()).thenReturn(sdkTokenMock);
        veritransSDK.setTransactionManager(transactionManager);
        transactionManager.setAnalyticsManager(mixpanelMock);
        veritransSDKSSpy = spy(veritransSDK);

    }

    /*
     * getSnapToken cases
     */
    @Test
    public void getToken_whenCardTokenRequestNull(){
        veritransSDKSSpy.getToken(null);
        Assert.assertEquals(false, veritransSDKSSpy.isRunning);
    }

    @Test
    public void getToken_whenCardTokenRequestNotNull_networkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);

        veritransSDKSSpy.getToken(cardTokenRequestMock);
        Assert.assertEquals(true, veritransSDKSSpy.isRunning);
    }

    @Test
    public void getToken_whenCardTokenRequestNotNull_networkUnavailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);

        veritransSDKSSpy.getToken(cardTokenRequestMock);
        Assert.assertEquals(false, veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }



    /*
     * paymentUsingPermataBank
     */

    @Test public void paymentUsingPermataBank_whenTransactionRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingPermataBank();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }


    @Test public void paymentUsingPermataBank_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getPermataBankModel(transactionRequestMock)).thenReturn(permatabankTransferMok);
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingPermataBank();
        Mockito.verify(transactionManager).paymentUsingPermataBank(permatabankTransferMok, "");
    }


    @Test public void paymentUsingPermataBank_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingPermataBank();
        Assert.assertEquals(false, veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

        /*
     *  paymentUsingBCATransfer
     *
     */

    @Test public void paymentUsingBCATransfer_whenTransactonRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingBcaBankTransfer();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingBCATransfer_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getBcaBankTransferRequest(transactionRequestMock)).thenReturn(bcaTransferMok);
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingBcaBankTransfer();
        Mockito.verify(transactionManager).paymentUsingBCATransfer(bcaTransferMok, "");
    }

    @Test public void paymentUsingBCATransfer_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingBcaBankTransfer();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     *  paymentUsingCard
     *
     */

    @Test public void paymentUsingCard_whenTransactonRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingCard(cardTransferMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingCard_whenTransactoncardTransferNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingCard(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingCard_whencardTransferNull(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingCard(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingCard_whenNetworkAvailable(){

        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingCard(cardTransferMock);
        Mockito.verify(transactionManager).paymentUsingCard(cardTransferMock, "");
    }


    @Test public void paymentUsingCard_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingCard(cardTransferMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     *  paymentUsingBCAKlikPay
     *
     */


    @Test public void paymentUsingBCAKlikPay_whenTransactonRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingBCAKlikPay(bcaklikpayDescModelMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingBCAKlikPay_whenTransactoncardTransferNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingBCAKlikPay(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingBCAKlikPay_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getBCAKlikPayModel(transactionRequestMock, bcaklikpayDescModelMock)).thenReturn(bcaKlikPayModelMock);

        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingBCAKlikPay(bcaklikpayDescModelMock);
        Mockito.verify(transactionManager).paymentUsingBCAKlikPay(bcaKlikPayModelMock, "");
    }


    @Test public void paymentUsingBCAKlikPay_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingBCAKlikPay(bcaklikpayDescModelMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }


    /*
     *  paymentUsingKlikBCA
     *
     */


    @Test public void paymentKlikBCA_whenTransactonRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingKlikBCA(klikbcaDescModelMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingKlikBCA_whenTransactoncardTransferNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingKlikBCA(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingKlikBCA_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getKlikBCAModel(transactionRequestMock, klikbcaDescModelMock)).thenReturn(klikBCAModelMock);

        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingKlikBCA(klikbcaDescModelMock);
        Mockito.verify(transactionManager).paymentUsingKlikBCA(klikBCAModelMock);
    }

    @Test public void paymentUsingKlikBCA_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingKlikBCA(klikbcaDescModelMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * paymentUsingMandiriBillPay
     *
     */

    @Test public void paymentUsingMandiriBillPay_whenTransactonRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingMandiriBillPay();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }
    @Test public void paymentUsingMandiriBillPay_whenBillInfoModelNull(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingMandiriBillPay();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingMandiriBillPay_whenItemDetailNull(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingMandiriBillPay();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }


    @Test public void paymentUsingMandiriBillPay_whenNetworkAvailable(){

        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(transactionRequestMock.getBillInfoModel()).thenReturn(billInfoModelMock);
        when(transactionRequestMock.getItemDetails()).thenReturn(itemDetailMock);
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);

        when(SdkUtil.getMandiriBillPayModel(transactionRequestMock)).thenReturn(mandiriBillPayTransModelMock);

        veritransSDKSSpy.paymentUsingMandiriBillPay();
        Mockito.verify(transactionManager).paymentUsingMandiriBillPay(mandiriBillPayTransModelMock, "");
    }

    @Test public void paymentUsingMandiriBillPay_whenNetworkUnAvailable(){
        when(transactionRequestMock.getBillInfoModel()).thenReturn(billInfoModelMock);
        when(transactionRequestMock.getItemDetails()).thenReturn(itemDetailMock);
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingMandiriBillPay();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * paymentUsingCIMBClickPay
     *
     */
    @Test public void paymentUsingCIMBClickPay_whenTransactonRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingCIMBClickPay(descriptionModelMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingCIMBClickPay_whenDescriptionModelNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingCIMBClickPay(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
    }

    @Test public void paymentUsingCIMBClickPay_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getCIMBClickPayModel(transactionRequestMock, descriptionModelMock)).thenReturn(cimbClickPayModel);

        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingCIMBClickPay(descriptionModelMock);
        Mockito.verify(transactionManager).paymentUsingCIMBPay(cimbClickPayModel, "");
    }

    @Test public void paymentUsingCIMBClickPay_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingCIMBClickPay(descriptionModelMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * paymentUsingMandiriECash
     *
     */

    @Test public void paymentUsingMandiriEcash_whenTransactonRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingMandiriECash(descriptionModelMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingMandiriEcash_whenDescriptionModelNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingMandiriECash(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
    }

    @Test public void paymentUsingMandiriEcash_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getMandiriECashModel(transactionRequestMock, descriptionModelMock)).thenReturn(mandiriEcashModelMock);
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingMandiriECash(descriptionModelMock);
        Mockito.verify(transactionManager).paymentUsingMandiriECash(mandiriEcashModelMock, "");
    }

    @Test public void paymentUsingMandiriEcash_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingMandiriECash(descriptionModelMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }
    /*
     * paymentUsingIndosatDompetku
     *
     */
    @Test public void paymentUsingIndosatDompetku_whenTransactonRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingIndosatDompetku(msisdnMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingIndosatDompetku_whenmsisdnNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingIndosatDompetku(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();

    }

    @Test public void paymentUsingIndosatDompetku_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getIndosatDompetkuRequestModel(transactionRequestMock, msisdnMock)).thenReturn(indosatDompetkuRequestMock);
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingIndosatDompetku(msisdnMock);
        Mockito.verify(transactionManager).paymentUsingIndosatDompetku(indosatDompetkuRequestMock, "");
    }

    @Test public void paymentUsingIndonsatDompetku_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingIndosatDompetku(msisdnMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * paymentUsingEpayBri
     *
     */

    @Test public void paymentUsingEpayBri_whenTransactionRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingEpayBri();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }


    @Test public void paymentUsingEpayBri_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getEpayBriBankModel(transactionRequestMock)).thenReturn(epayBriTransferMock);
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingEpayBri();
        Mockito.verify(transactionManager).paymentUsingEpayBri(epayBriTransferMock, "");
    }

    @Test public void paymentUsingEpayBri_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingEpayBri();
        Assert.assertEquals(false, veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }


    /*
     * paymentUsingIndomaret
     */

    @Test public void paymentUsingIndomaret_whenTransactionRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingIndomaret(cstoreEntityMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingIndomaret_whenCsStoreEntity(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingIndomaret(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingIndomaret_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getIndomaretRequestModel(transactionRequestMock, cstoreEntityMock)).thenReturn(indomaretRequestModelMock);
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingIndomaret(cstoreEntityMock);
        Mockito.verify(transactionManager).paymentUsingIndomaret(indomaretRequestModelMock, "");
    }

    @Test public void paymentUsingIndomaret_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingIndomaret(cstoreEntityMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }
       /*
        * getSavedCard
        *
        */


    @Test public void getSavedCard_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        veritransSDKSSpy.getSavedCard();
        Mockito.verify(transactionManager).getCards("");
    }

    @Test public void getSavedCard_whenNetworkUnAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.getSavedCard();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * savecards
     */

    @Test public void saveCards_whenSaveCardRequestNull(){
        veritransSDKSSpy.saveCards(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
    }


    @Test public void saveCards_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        veritransSDKSSpy.saveCards(saveCardRequestMock);
        Mockito.verify(transactionManager).saveCards(saveCardRequestMock, "");
    }

    @Test public void saveCards_whenNetworkUnAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.saveCards(savecardMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

        /*
     * paymentUsingMandiriClickPay
     *
     */

    @Test public void paymentUsingMandiriClickPay_whenTransactonRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingMandiriClickPay(mandiriClickPayModelMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingMandiriClickPay_whenMandiriClickPayModelNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingMandiriClickPay(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingMandiriClickPay_whenMandiriJustClickPayModelNull(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.paymentUsingMandiriClickPay(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void paymentUsingMandiriClickPay_whenNetworkAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getMandiriClickPayRequestModel(transactionRequestMock, mandiriClickPayModelMock)).thenReturn(mandiriClickPayRequestModelMock);

        veritransSDKSSpy.paymentUsingMandiriClickPay(mandiriClickPayModelMock);
        Mockito.verify(transactionManager).paymentUsingMandiriClickPay(mandiriClickPayRequestModelMock, "");
    }


    @Test public void paymentUsingMandiriClickPay_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingMandiriClickPay(mandiriClickPayModelMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * paymentUsingBBMMoney
     *
     */

    @Test public void paymentUsingBBMMoney_whenTransactonRequestNull(){
        veritransSDKSSpy.setTransactionRequest(null);
        veritransSDKSSpy.paymentUsingBBMMoney();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }


    @Test public void paymentUsingBBMMoney_whenNetworkAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        when(SdkUtil.getBBMMoneyRequestModel(transactionRequestMock)).thenReturn(bbmMoneyRequestModelMock);
        veritransSDKSSpy.paymentUsingBBMMoney();
        Mockito.verify(transactionManager).paymentUsingBBMMoney(bbmMoneyRequestModelMock, "");
    }


    @Test public void paymentUsingBBMMoney_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.paymentUsingBBMMoney();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * cardRegistration
     *
     */
    @Test public void cardRegistration_whenNetworkAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        veritransSDKSSpy.cardRegistration(APIClientMain.CARD_NUMBER, APIClientMain.CARD_CVV,
                APIClientMain.CARD_EXP_MONTH, APIClientMain.CARD_EXP_YEAR);
        Mockito.verify(transactionManager).cardRegistration(APIClientMain.CARD_NUMBER, APIClientMain.CARD_CVV,
                APIClientMain.CARD_EXP_MONTH, APIClientMain.CARD_EXP_YEAR, veritransSDKSSpy.getClientKey());
    }


    @Test public void cardRegistration_whenNetworkUnAvailable(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.cardRegistration(APIClientMain.CARD_NUMBER, APIClientMain.CARD_CVV,
                APIClientMain.CARD_EXP_MONTH, APIClientMain.CARD_EXP_YEAR);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * deleteCard
     *
     */

    @Test public void deleteCard_whenCreditCardNullNetorkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        veritransSDKSSpy.deleteCard(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        Mockito.verify(busCollaborator).onGeneralErrorEvent();
    }

    @Test public void deleteCard_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        veritransSDKSSpy.deleteCard(savecardMock);
        Mockito.verify(transactionManager).deleteCard(savecardMock, "");
    }


    @Test public void deleteCard_whenNetworkUnAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.deleteCard(null);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * getAuthenticationToken
     *
     */


    @Test public void getAuthenticationToken_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        veritransSDKSSpy.getAuthenticationToken();
        Mockito.verify(transactionManager).getAuthenticationToken();
    }

    @Test public void getAuthenticationToken_whenNetworkUnAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.getAuthenticationToken();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * getOffersList
     *
     */

    @Test public void getOffersList_whenNetworkAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);
        veritransSDKSSpy.getOffersList();
        Mockito.verify(transactionManager).getOffers("");
        Assert.assertTrue(veritransSDKSSpy.isRunning);
    }

    @Test public void getOffersList_whenNetworkUnAvailable(){
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
        veritransSDKSSpy.getAuthenticationToken();
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }

    /*
     * startRegisterCardUIFlow
     *
     */

    @Test public void startRegisterCardUIFlow_whenUIFlowNotNull(){
        veritransSDKSSpy.uiflow = uiflowMock;
        veritransSDKSSpy.startRegisterCardUIFlow(contextMock);
        Mockito.verify(uiflowMock).runRegisterCard(contextMock);
    }

    /*
     * startRegisterCardUIFlow
     *
     */


    @Test public void startPaymentUiFlow_whenUIFlowNotNull(){
        when(transactionRequestMock.getPaymentMethod()).thenReturn(Constants.PAYMENT_METHOD_NOT_SELECTED);
        veritransSDKSSpy.uiflow = uiflowMock;
        veritransSDKSSpy.isRunning = false;
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);

        veritransSDKSSpy.startPaymentUiFlow(contextMock);
        Mockito.verify(veritransSDKSSpy).startPaymentUiFlow(contextMock);
    }


    @Test public void startPaymentUiFlow_whenTransactionRequestNull(){
        veritransSDKSSpy.uiflow = uiflowMock;
        veritransSDKSSpy.isRunning = true;
        veritransSDKSSpy.setTransactionRequest(null);

        veritransSDKSSpy.startPaymentUiFlow(contextMock);
        verifyStatic(Mockito.times(2));
        Logger.e(Matchers.anyString());

    }

    @Test public void startPaymentUiFlow_whenSdkRunning(){
        veritransSDKSSpy.uiflow = uiflowMock;
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        veritransSDKSSpy.isRunning = true;

        veritransSDKSSpy.startPaymentUiFlow(contextMock);
        verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString());

    }

    @Test
    public void getMerhcantToken(){
        PowerMockito.mockStatic(LocalDataHandler.class);
        when(LocalDataHandler.readObject(contextMock.getString(R.string.user_details), UserDetail.class)).thenReturn(userDetailMock);
        when(userDetailMock.getMerchantToken()).thenReturn("token");
        Assert.assertEquals("token", veritransSDKSSpy.getMerchantToken());

    }

    @Test public void initializeLogo_whenLogoIdNot0() throws Exception {
        drawableIntCostumMock = 1;

        veritransSDKSSpy.setMerchantLogoResourceId(drawableIntCostumMock);
        Assert.assertEquals((Integer)1, (Integer)veritransSDKSSpy.getMerchantLogoResourceId());

        WhiteboxImpl.invokeMethod(veritransSDKSSpy, "initializeLogo");
        Mockito.verify(contextMock, Mockito.times(1)).getResources();
    }

    @Test
    public void isLogEnabled(){
        Assert.assertTrue( veritransSDKSSpy.isLogEnabled());
    }

    @Test
    public void getMerchantUrl(){
        Assert.assertEquals(SDKConfigTest.MERCHANT_BASE_URL, veritransSDKSSpy.getMerchantServerUrl());
    }

    @Test public void initializeLogo_whenLogoId0() throws Exception {
        veritransSDKSSpy.setMerchantLogoResourceId(0);

        veritransSDKSSpy.setMerchantLogo(merchantLogoMock);
        Assert.assertEquals(merchantLogoMock, veritransSDKSSpy.getMerchantLogo());

        WhiteboxImpl.invokeMethod(veritransSDKSSpy, "initializeLogo");
        Mockito.verify(contextMock, Mockito.times(1)).getResources();

    }

    @Test public void getInstanceTest(){
        VeritransSDK.getInstance(null);
        verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString());
    }

    @Test
    public void getDefaultText(){
        String defaultText = "text";
        veritransSDKSSpy.setDefaultText(defaultText);
        Assert.assertEquals(defaultText, veritransSDKSSpy.getDefaultText());
    }

    @Test
    public void boldtext(){
        String boldtext = "text";
        veritransSDKSSpy.setBoldText(boldtext);
        Assert.assertEquals(boldtext, veritransSDKSSpy.getBoldText());

    }
     @Test
    public void semiboldtext(){
        String text = "text";
        veritransSDKSSpy.setSemiBoldText(text);
        Assert.assertEquals(text, veritransSDKSSpy.getSemiBoldText());

    }

    @Test
    public void isRunningTest(){
        veritransSDKSSpy.isRunning = true;
        Assert.assertEquals(true, veritransSDKSSpy.isRunning());

    }


    @Test
    public void merchantLogo(){
        String merchantLogo = "merchantLogo";
        veritransSDKSSpy.setMerchantLogo(merchantLogo);
        Assert.assertEquals(merchantLogo, veritransSDKSSpy.getMerchantLogo());

    }

    @Test
    public void paymentMethod(){

        veritransSDKSSpy.setSelectedPaymentMethods(paymentMethodMock);
        Assert.assertEquals(paymentMethodMock, veritransSDKSSpy.getSelectedPaymentMethods());
    }
    //////////////////////////////////////////////////
    @Test
    public void getOverLimit_whenNoetworkAvailable() throws Exception {
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);

        veritransSDKSSpy.getOffersList();
        Mockito.verify(transactionManager,Mockito.times(1)).getOffers(sdkTokenMock);
        Assert.assertTrue(veritransSDKSSpy.isRunning);
    }

    /*
     * setget
     */

    @Test
    public void getBBMCallBackUrlTest(){
        veritransSDKSSpy.setBBMCallBackUrl(bbmCallbackUrlMock);
        Assert.assertEquals(bbmCallbackUrlMock, veritransSDKSSpy.getBBMCallBackUrl());
    }

    @Test
    public void TransactionRequestTest(){
        veritransSDKSSpy.setTransactionRequest(transactionRequestMock);
        Assert.assertEquals(transactionRequestMock, veritransSDKSSpy.getTransactionRequest());
    }


    /**
     * Created by ziahaqi on 25/06/2016.
     */
    public static class EventBustImplementSample implements GetAuthenticationBusCallback, DeleteCardBusCallback,
            CardRegistrationBusCallback, SaveCardBusCallback, HttpErrorCallback,
            TokenBusCallback, TransactionBusCallback, GetCardBusCallback,
            GetOfferBusCallback, GetSnapTokenCallback, GetSnapTransactionCallback {
        BusCollaborator busCollaborator;
        public String onsuccessStatusCode;
        private TransactionManager transactionManager;
        private VeritransBus bus;
        private SnapTransactionManager snapTransactionManager;


        public void registerBus(VeritransBus veritransBus) {
            VeritransBus.setBus(veritransBus);
            VeritransBusProvider.getInstance().register(this);
        }

        public void setTransactionManager(SnapTransactionManager transactionManager) {
            this.snapTransactionManager = transactionManager;
        }

        public void setTransactionManager(TransactionManager transactionManager) {
            this.transactionManager = transactionManager;
        }
        public void cardRegistration(VeritransRestAPI registered, String cardNumber, String cardCvv, String cardExpMonth, String cardExpYear, String authtoken) throws InterruptedException {

            transactionManager.setVeritransPaymentAPI(registered);
            transactionManager.cardRegistration(cardNumber,
                    cardCvv,
                    cardExpMonth,
                    cardExpYear, authtoken);
        }

        public VeritransBus getBus(){
            return this.bus;
        }

        public void getToken(VeritransRestAPI veritransRestAPI, CardTokenRequest cardTokenRequest) {
            transactionManager.setVeritransPaymentAPI(veritransRestAPI);
            transactionManager.getToken(cardTokenRequest);
        }

        public void paymentUsingPermataBank(MerchantRestAPI merchantRestAPI, PermataBankTransfer transfer, String token) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPI);
            transactionManager.paymentUsingPermataBank(transfer, token);
        }


        public void paymentUsingPermataBCA(MerchantRestAPI merchantRestAPI, BCABankTransfer transfer, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPI);
            transactionManager.paymentUsingBCATransfer(transfer, mToken);
        }

        public void paymentUsingCard(MerchantRestAPI merchantRestAPIMock, String xAuth, CardTransfer transfer, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingCard(transfer, mToken);
        }

        public void paymentUsingMandiriClickPay(MerchantRestAPI merchantRestAPIMock, String xAuth, MandiriClickPayRequestModel requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingMandiriClickPay(requestModel, xAuth);
        }

        public void paymentUsingBCAClickPay(MerchantRestAPI merchantRestAPIMock, String xAuth, BCAKlikPayModel requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingBCAKlikPay(requestModel, xAuth);
        }

        public void paymentUsingMandiriBillpay(MerchantRestAPI merchantRestAPIMock, String xAuth, MandiriBillPayTransferModel requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingMandiriBillPay(requestModel, xAuth);
        }

        public void paymentUsingCIMBPay(MerchantRestAPI merchantRestAPIMock, String xAuth, CIMBClickPayModel requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingCIMBPay(requestModel, xAuth);
        }



        public void paymentUsingIndosatDompetku(MerchantRestAPI merchantRestAPIMock, IndosatDompetkuRequest requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingIndosatDompetku(requestModel, mToken);
        }

        public void paymentUsingIndomaret(MerchantRestAPI merchantRestAPIMock, IndomaretRequestModel requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingIndomaret(requestModel, mToken);
        }

        public void paymentUsingBBMMoney(MerchantRestAPI merchantRestAPIMock, BBMMoneyRequestModel requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingBBMMoney(requestModel, mToken);
        }

        public void paymentUsingClickBCAModel(MerchantRestAPI merchantRestAPIMock, KlikBCAModel requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingKlikBCA( requestModel);
        }

        public void getAuthenticationToken(MerchantRestAPI veritransRestAPIMock) {
            transactionManager.setMerchantPaymentAPI(veritransRestAPIMock);
            transactionManager.getAuthenticationToken();
        }

        public void paymentUsingMandiriEcashPay(MerchantRestAPI merchantRestAPIMock, String xAuth, MandiriECashModel requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingMandiriECash(requestModel, xAuth);
        }

        public void paymentUsingBriEpay(MerchantRestAPI merchantRestAPIMock, String xAuth, EpayBriTransfer requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.paymentUsingEpayBri(requestModel, xAuth);
        }


        /*
         * snap
         */


        public void getSnapToken(MerchantRestAPI merchantRestAPI) {

            snapTransactionManager.setMerchantPaymentAPI(merchantRestAPI);
            snapTransactionManager.getSnapToken(model);

        }

        /*
         * Card Registration Callback stuff
         */

        @Override
        @Subscribe
        public void onEvent(CardRegistrationSuccessEvent event) {
            busCollaborator.onCardRegistrationSuccess();
            onsuccessStatusCode = event.getResponse().getStatusCode();
        }

        @Subscribe
        @Override
        public void onEvent(CardRegistrationFailedEvent event) {
            busCollaborator.onCardRegistrationFailed();
        }

        @Override
        @Subscribe
        public void onEvent(NetworkUnavailableEvent event) {
            busCollaborator.onNetworkUnAvailable();
        }

        @Override
        @Subscribe
        public void onEvent(GeneralErrorEvent event) {
            busCollaborator.onGeneralErrorEvent();
        }

        /*
         * token bus callback stuff
         */
        @Subscribe
        @Override
        public void onEvent(GetTokenSuccessEvent event) {
            busCollaborator.onGetTokenSuccessEvent();
        }

        @Subscribe
        @Override
        public void onEvent(GetTokenFailedEvent event) {
            busCollaborator.onGetTokenFailedEvent();
        }


        /*
         * transaction bus callbaack
         */
        @Subscribe
        @Override
        public void onEvent(TransactionSuccessEvent event) {
            busCollaborator.onTransactionSuccessEvent();
        }

        @Subscribe
        @Override
        public void onEvent(TransactionFailedEvent event) {
            busCollaborator.onTransactionFailedEvent();
        }


        @Subscribe
        @Override
        public void onEvent(AuthenticationEvent event) {
            busCollaborator.onAuthenticationEvent();
        }

        public void getOffers(MerchantRestAPI merchantRestAPIMock,  String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.getOffers(mToken);
        }

        @Override
        @Subscribe
        public void onEvent(GetOfferSuccessEvent event) {
            busCollaborator.onGetOfferSuccesEvent();

        }

        @Override
        @Subscribe
        public void onEvent(GetOfferFailedEvent event) {
            busCollaborator.onGetOfferFailedEvent();
        }

        public void deleteCard(MerchantRestAPI merchantRestAPIMock, SaveCardRequest requestModel, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.deleteCard(requestModel, mToken);
        }

        @Override
        @Subscribe
        public void onEvent(DeleteCardSuccessEvent event) {
            busCollaborator.onDeleteCardSuccessEvent();
        }

        @Override
        @Subscribe
        public void onEvent(DeleteCardFailedEvent event) {
            busCollaborator.onDeleteCardFailedEvent();
        }

        public void getCards(MerchantRestAPI merchantRestAPIMock, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.getCards(mToken);
        }

        @Subscribe
        @Override
        public void onEvent(GetCardsSuccessEvent event) {
            busCollaborator.onGetCardSuccess();
        }

        @Subscribe
        @Override
        public void onEvent(GetCardFailedEvent event) {
            busCollaborator.onGetCardFailed();
        }

        public void saveCard(MerchantRestAPI merchantRestAPIMock, SaveCardRequest request, String mToken) {
            transactionManager.setMerchantPaymentAPI(merchantRestAPIMock);
            transactionManager.saveCards(request, mToken);
        }

        @Override
        @Subscribe
        public void onEvent(SaveCardSuccessEvent event) {
            busCollaborator.onSaveCardSuccessEvent();
        }

        @Override
        @Subscribe
        public void onEvent(SaveCardFailedEvent event) {
            busCollaborator.onsaveCardFailedEvent();
        }

        @Override
        @Subscribe
        public void onEvent(SSLErrorEvent errorEvent) {
            busCollaborator.onSSLErrorEvent();
        }

        public void getPaymentType(SnapRestAPI restAPI, String snapToken) {
            snapTransactionManager.setRestApi(restAPI);
            snapTransactionManager.getSnapTransaction(snapToken);
        }

        @Subscribe
        @Override
        public void onEvent(GetSnapTokenSuccessEvent event) {
            busCollaborator.onGetSnapTokenSuccess();
        }

        @Subscribe
        @Override
        public void onEvent(GetSnapTokenFailedEvent event) {
            busCollaborator.onGetSnapTokenFailed();
        }

        @Subscribe
        @Override
        public void onEvent(GetSnapTransactionSuccessEvent event) {
            busCollaborator.onGetSnapTransactionSuccess();
        }

        @Subscribe
        @Override
        public void onEvent(GetSnapTransactionFailedEvent event) {
            busCollaborator.onGetSnapTransactionFailed();
        }
    }
}
