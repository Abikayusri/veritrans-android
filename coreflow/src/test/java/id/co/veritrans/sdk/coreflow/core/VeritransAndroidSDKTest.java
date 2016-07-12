package id.co.veritrans.sdk.coreflow.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

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

import java.util.ArrayList;

import id.co.veritrans.sdk.coreflow.APIClientMain;
import id.co.veritrans.sdk.coreflow.R;
import id.co.veritrans.sdk.coreflow.SDKConfig;
import id.co.veritrans.sdk.coreflow.eventbus.bus.VeritransBus;
import id.co.veritrans.sdk.coreflow.eventbus.bus.VeritransBusProvider;
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
import id.co.veritrans.sdk.coreflow.models.PermataBankTransfer;
import id.co.veritrans.sdk.coreflow.models.SaveCardRequest;
import id.co.veritrans.sdk.coreflow.transactionmanager.BusCollaborator;
import id.co.veritrans.sdk.coreflow.transactionmanager.EventBustImplementSample;
import id.co.veritrans.sdk.coreflow.utilities.Utils;

/**
 * Created by ziahaqi on 24/06/2016.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SdkUtil.class, Looper.class, Utils.class,Log.class, TextUtils.class, Logger.class  })

public class VeritransAndroidSDKTest {


    @Mock
    Context context;
    @Mock
    TransactionManager transactionManager;
    @Mock
    MerchantRestAPI merchantRestAPI;
    @Mock
    VeritransRestAPI veritransRestAPI;
    @Mock
    Resources resources;
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

    @Before
    public void setup(){
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(Looper.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(SdkUtil.class);

        eventBustImplementSample.registerBus(busMock);

        Mockito.when(context.getApplicationContext()).thenReturn(context);
        Mockito.when(context.getString(R.string.error_unable_to_connect)).thenReturn("not connected");
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Mockito.when(networkInfo.isConnected()).thenReturn(false);
        Mockito.when(context.getSharedPreferences("local.data", Context.MODE_PRIVATE)).thenReturn(preference);

        VeritransSDK veritransSDK = (new SdkCoreFlowBuilder(context, SDKConfig.CLIENT_KEY, SDKConfig.MERCHANT_BASE_URL)
                .enableLog(true)
                .setDefaultText("open_sans_regular.ttf")
                .setSemiBoldText("open_sans_semibold.ttf")
                .setBoldText("open_sans_bold.ttf")
                .setMerchantName("Veritrans Example Merchant")
                .buildSDK());
        Mockito.when(veritransSDK.readAuthenticationToken()).thenReturn(sdkTokenMock);
        veritransSDK.setTransactionManager(transactionManager);
        veritransSDKSSpy = spy(veritransSDK);

    }

    /*
     * getToken cases
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

    @Test public void deleteCard_whenCreditCardNull(){
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
        veritransSDKSSpy.deleteCard(savecardMock);
        Assert.assertFalse(veritransSDKSSpy.isRunning);
        busCollaborator.onGeneralErrorEvent();
    }



    //////////////////////////////////////////////////
    @Test
    public void getOverLimit_whenNoetworkAvailable() throws Exception {
        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(true);

        veritransSDKSSpy.getOffersList();
        Mockito.verify(transactionManager,Mockito.times(1)).getOffers(sdkTokenMock);
    }

//    @Test
//    public void getOverLimit_whenNetworkUnAvailable() throws Exception {
//        when(veritransSDKSSpy.isNetworkAvailable()).thenReturn(false);
//        veritransSDKSSpy.getOffersList();
//        Assert.assertEquals(false, veritransSDK.isRunning);
//    }


//    @Test
//    public void getOverLimit() throws Exception {
//        Mockito.when(veritransSDK.isAvailableForTransaction("event")).thenReturn(true);
//        Assert.assertEquals(1,1);
//        when(veritransSDK.isSDKAvailable("args")).thenReturn(true);
//        veritransSDK.getOffersList();
//        Mockito.verify(transactionManager).getOffers("token");
//    }

//    private void setIsNetworkAvailableReturnTrue() throws Exception {
//        when(veritransSDK, method(VeritransSDK.class, "isNetworkAvailable", String.class))
//                .withArguments(Matchers.anyString())
//
//                .thenReturn(true);
//    }
//
//    private void setIsSDKAvailableReturnTrue() throws Exception {
//
//        when(veritransSDK, method(VeritransSDK.class, "isSDKAvailable", String.class))
//                .withArguments(Matchers.anyString())
//                .thenReturn(true);
//    }
//
//    private void setIsSDKAvailableReturnFalse() throws Exception {
//        when(veritransSDK, method(VeritransSDK.class, "isSDKAvailable", String.class))
//                .withArguments(Matchers.anyString())
//                .thenReturn(false);
//    }
//
//    private void setIsNetworkAvailableReturnFalse() throws Exception {
//        when(veritransSDK, method(VeritransSDK.class, "isNetworkAvailable", String.class))
//                .withArguments(Matchers.anyString())
//                .thenReturn(false);
//    }


}
