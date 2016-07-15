package id.co.veritrans.sdk.coreflow.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import   org.powermock.api.support.membermodification.MemberModifier;
import java.util.ArrayList;
import java.util.Iterator;

import id.co.veritrans.sdk.coreflow.R;
import id.co.veritrans.sdk.coreflow.SDKConfigTest;
import id.co.veritrans.sdk.coreflow.models.BCAKlikPayDescriptionModel;
import id.co.veritrans.sdk.coreflow.models.BillInfoModel;
import id.co.veritrans.sdk.coreflow.models.BillingAddress;
import id.co.veritrans.sdk.coreflow.models.CardPaymentDetails;
import id.co.veritrans.sdk.coreflow.models.CstoreEntity;
import id.co.veritrans.sdk.coreflow.models.CustomerDetails;
import id.co.veritrans.sdk.coreflow.models.DescriptionModel;
import id.co.veritrans.sdk.coreflow.models.ItemDetails;
import id.co.veritrans.sdk.coreflow.models.KlikBCADescriptionModel;
import id.co.veritrans.sdk.coreflow.models.MandiriClickPayModel;
import id.co.veritrans.sdk.coreflow.models.ShippingAddress;
import id.co.veritrans.sdk.coreflow.models.UserAddress;
import id.co.veritrans.sdk.coreflow.models.UserDetail;
import id.co.veritrans.sdk.coreflow.utilities.Utils;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;


/**
 * Created by ziahaqi on 7/13/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LocalDataHandler.class, Utils.class,Log.class, TextUtils.class, Logger.class  })

public class SDKUtilsTest {

    @Mock
    private TransactionRequest transactionRequestMock;

    @Mock
    private BillInfoModel billingInfoModelMock;
    @Mock
    private ArrayList<ItemDetails> itemDetailMock;
    @Mock
    private ArrayList<BillingAddress> billingAddressMock;
    @Mock
    private ArrayList<ShippingAddress> shippingAddressMock;
    private java.lang.String orderId = "01";
    private java.lang.Double amount = 20.0;
    @Mock
    private CustomerDetails costumerDetailMock;
    @Mock
    private id.co.veritrans.sdk.coreflow.models.MandiriBillPayTransferModel modelMock;
    @Mock
    private MandiriClickPayModel mandiriClickPayModelMock;
    @Mock
    private KlikBCADescriptionModel klikBCAModelMock;
    @Mock
    private String descriptionMock;
    @Mock
    private BCAKlikPayDescriptionModel bcaKlikPayMock;
    @Mock
    private Context contextMock;

    private VeritransSDK veritransSDK;
    private String paymermataName;
    @Mock
    private CstoreEntity cstoreMock;
    @Mock
    private DescriptionModel descriptionCIMBMock;
    @Mock
    private CardPaymentDetails cardPaymentDetailMock;
    private String msisdn = "msisdn";
    @Mock
    private TransactionRequest transactionRequestChangedMock;
    @Mock
    private TransactionManager transactionManagerMock;
    @Mock
    private Resources resourceMock;
    private String userDetail = "user_details";
    @Mock
    private UserDetail userDetailMock;
    @Mock
    private SharedPreferences mpreferenceMock;
    private String fullname = "fullname";
    private Iterator<UserAddress> userAddressIterator;
    @Mock
    private UserAddress userAddressMock1;
    @Mock
    private UserAddress userAddressMock2;
    @Mock
    private UserAddress userAddressMock3;
    private ArrayList<UserAddress> userAddressListMock;

    @Before
    public void setup(){
        userAddressIterator = mock(Iterator.class);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(Logger.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(LocalDataHandler.class);
//        PowerMockito.mockStatic(SdkUtil.class);


        Mockito.when(transactionRequestMock.getBillInfoModel()).thenReturn(billingInfoModelMock);
        Mockito.when(transactionRequestMock.getItemDetails()).thenReturn(itemDetailMock);
        Mockito.when(transactionRequestMock.getBillingAddressArrayList()).thenReturn(billingAddressMock);
        Mockito.when(transactionRequestMock.getShippingAddressArrayList()).thenReturn(shippingAddressMock);
        Mockito.when(transactionRequestMock.getCustomerDetails()).thenReturn(costumerDetailMock);
        Mockito.when(transactionRequestMock.getOrderId()).thenReturn(orderId);
        Mockito.when(transactionRequestMock.getAmount()).thenReturn(amount);

    }

    private void initSDK(){
        Mockito.when(klikBCAModelMock.getDescription()).thenReturn(descriptionMock);
        Mockito.when(contextMock.getApplicationContext()).thenReturn(contextMock);
        Mockito.when(contextMock.getResources()).thenReturn(resourceMock);

        VeritransSDK veritransSDK = (new SdkCoreFlowBuilder(contextMock, SDKConfigTest.CLIENT_KEY, SDKConfigTest.MERCHANT_BASE_URL)
                .enableLog(true)
                .setDefaultText("open_sans_regular.ttf")
                .setSemiBoldText("open_sans_semibold.ttf")
                .setBoldText("open_sans_bold.ttf")
                .setMerchantName("Veritrans Example Merchant")
                .buildSDK());
        veritransSDK.setTransactionManager(transactionManagerMock);
        veritransSDK = spy(veritransSDK);

        when(contextMock.getString(R.string.payment_permata)).thenReturn(paymermataName);
    }


    @Test
    public void getMandiriBillPayModel() throws Exception {
        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        mockStatic(SdkUtil.class);
//        when(sdkUtil, method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).withArguments(transactionRequestMock).thenReturn(transactionRequestChangedMock);
//        when(SdkUtil.getUserDetails(transactionRequestMock)).thenReturn(transactionRequestChangedMock);
//        when(SdkUtil.initializeUserInfo(transactionRequestMock)).thenReturn(transactionRequestChangedMock);

        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(null);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertEquals(billingInfoModelMock, SdkUtil.getMandiriBillPayModel(transactionRequestMock).getBillInfoModel());
        verifyStatic();
    }

    @Test
    public void getMandiriClickPayRequestModel() throws ClassNotFoundException {
        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertEquals(mandiriClickPayModelMock, SdkUtil.getMandiriClickPayRequestModel(transactionRequestMock, mandiriClickPayModelMock).getMandiriClickPayModel());
    }

    @Test
    public void getKlikBCAModelTest() throws ClassNotFoundException {
        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertNotNull(SdkUtil.getKlikBCAModel(transactionRequestMock, klikBCAModelMock).getDescriptionModel());
    }

    @Test
    public void getBCAKlickPayModelTest() throws ClassNotFoundException {
        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertNotNull( SdkUtil.getBCAKlikPayModel(transactionRequestMock, bcaKlikPayMock).getTransactionDetails());
    }

    @Test
    public void getPermataBankModelTest() throws ClassNotFoundException {
        initSDK();

        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertEquals(itemDetailMock, SdkUtil.getPermataBankModel(transactionRequestMock).getItemDetails());

    }


    @Test
    public void getPermataBankModelTest_whenSDKNull() throws ClassNotFoundException {

        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());
        Assert.assertEquals(itemDetailMock, SdkUtil.getPermataBankModel(transactionRequestMock).getItemDetails());
        verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString());

    }


    @Test
    public void getBcaBankTransferRequest() throws ClassNotFoundException {
        initSDK();

        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertEquals(itemDetailMock, SdkUtil.getBcaBankTransferRequest(transactionRequestMock).getItemDetails());

    }


    @Test
    public void getBcaBankTransferRequest_whenSDKNull() throws ClassNotFoundException {

        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());
        Assert.assertEquals(itemDetailMock, SdkUtil.getBcaBankTransferRequest(transactionRequestMock).getItemDetails());
        verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString());

    }


    @Test
    public void getIndomaretRequestModel() throws ClassNotFoundException {
        initSDK();

        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertNotNull(SdkUtil.getIndomaretRequestModel(transactionRequestMock,cstoreMock).getCustomerDetails());

    }


    @Test
    public void getIndomaretRequestModel_whenSDKNull() throws ClassNotFoundException {

        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());
        Assert.assertNotNull(SdkUtil.getIndomaretRequestModel(transactionRequestMock, cstoreMock).getCustomerDetails());
        verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString());

    }


    @Test
    public void getBBMMoneyRequestModel() throws ClassNotFoundException {
        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertNotNull(SdkUtil.getBBMMoneyRequestModel(transactionRequestMock).getTransactionDetails());
    }

    @Test
    public void getCIMBClickPayModelTest() throws ClassNotFoundException {
        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertEquals(billingAddressMock, SdkUtil.getCIMBClickPayModel(transactionRequestMock, descriptionCIMBMock).getBillingAddresses());
    }

    @Test
    public void getMandiriECashModel() throws ClassNotFoundException {
        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());
        Assert.assertEquals(billingAddressMock, SdkUtil.getMandiriECashModel(transactionRequestMock, descriptionCIMBMock).getBillingAddresses());
    }

    @Test
    public void getCardTransferModel() throws ClassNotFoundException {
        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());
        Assert.assertEquals(billingAddressMock, SdkUtil.getCardTransferModel(transactionRequestMock, cardPaymentDetailMock).getBillingAddresses());
    }

    @Test
    public void getEpayBriBankModel() throws ClassNotFoundException {
        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());
        Assert.assertEquals(billingAddressMock, SdkUtil.getEpayBriBankModel(transactionRequestMock).getBillingAddresses());
    }

    @Test
    public void getIndosatDompetkuRequestModel() throws ClassNotFoundException {
        initSDK();

        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertEquals(itemDetailMock, SdkUtil.getIndosatDompetkuRequestModel(transactionRequestMock, msisdn).getItemDetails());

    }

    @Test
    public void getIndosatDompetkuRequestModel_whenMSISDNNull() throws ClassNotFoundException {
        initSDK();

        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertEquals(itemDetailMock, SdkUtil.getIndosatDompetkuRequestModel(transactionRequestMock, null).getItemDetails());

    }

    @Test
    public void getIndosatDompetkuRequestModel_whenMSISDNEmpty() throws ClassNotFoundException {
        initSDK();

        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());

        Assert.assertEquals(itemDetailMock, SdkUtil.getIndosatDompetkuRequestModel(transactionRequestMock, "").getItemDetails());

    }

    @Test
    public void getIndosatDompetkuRequestModel_whenSDKNull() throws ClassNotFoundException {
        veritransSDK = null;
        Mockito.when(transactionRequestMock.isUiEnabled()).thenReturn(true);
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "initializeUserInfo", TransactionRequest.class)).toReturn(transactionRequestMock);
        Assert.assertNotNull(transactionRequestMock.getBillInfoModel());
        Assert.assertEquals(itemDetailMock, SdkUtil.getIndosatDompetkuRequestModel(transactionRequestMock,msisdn).getItemDetails());
        verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString());

    }

    @Test
    public void initializeUserInfo() throws ClassNotFoundException {
        MemberModifier.stub(MemberMatcher.method(SdkUtil.class, "getUserDetails", TransactionRequest.class)).toReturn(transactionRequestMock);

        Assert.assertEquals(transactionRequestMock, SdkUtil.initializeUserInfo(transactionRequestMock));

    }

    @Test
    public void getUserDetailTest(){
        initSDK();
        VeritransSDK.setmPreferences(mpreferenceMock);
        mockStatic(LocalDataHandler.class);
        when(LocalDataHandler.readObject(userDetail, UserDetail.class)).thenReturn(userDetailMock);
        when(contextMock.getString(R.string.user_details)).thenReturn(userDetail);
        when(userDetailMock.getUserFullName()).thenReturn(fullname);
        SdkUtil.getUserDetails(transactionRequestMock);

        verifyStatic(Mockito.times(1));
        Logger.e(Matchers.anyString());

    }

    @Test
    public void extractUserAddress_whenBoth(){
        when(userAddressMock1.getAddressType()).thenReturn(Constants.ADDRESS_TYPE_BOTH);
        when(userAddressMock1.getAddress()).thenReturn("address1");
        when(userAddressMock1.getCity()).thenReturn("city1");
        when(userAddressMock1.getCountry()).thenReturn("indonesia1");
        when(userDetailMock.getUserFullName()).thenReturn("fullname");
        when(userDetailMock.getPhoneNumber()).thenReturn("phoneNumber");
        userAddressListMock =  new ArrayList<>();
        TransactionRequest transactionRequest = new TransactionRequest(orderId, amount, Constants.PAYMENT_METHOD_NOT_SELECTED);

        userAddressListMock.add(userAddressMock1);
        TransactionRequest request = SdkUtil.extractUserAddress(userDetailMock, userAddressListMock, transactionRequest);
        Assert.assertEquals(1, request.getBillingAddressArrayList().size());
        Assert.assertEquals(1, request.getShippingAddressArrayList().size());

    }


//    @Test
//    public void extractUserAddress_whenBoth(){
//        when(userAddressMock1.getAddressType()).thenReturn(Constants.ADDRESS_TYPE_BOTH);
//        when(userAddressMock1.getAddress()).thenReturn("address>address");
//
//        when(userAddressIterator.hasNext()).thenReturn(true).thenReturn(false);
//        when(userAddressIterator.next()).thenReturn(userAddressMock1);
//        when(userAddressListMock.iterator()).thenReturn(userAddressIterator);
//        TransactionRequest request = SdkUtil.extractUserAddress(userDetailMock, userAddressListMock, transactionRequestMock);
//        Assert.assertEquals(1, request.getBillingAddressArrayList().size());
//        Assert.assertEquals(1, request.getShippingAddressArrayList().size());
//
//    }

    @Test
    public void extractUserAddress_whenTypeAddressShipping(){
        when(userAddressMock1.getAddressType()).thenReturn(Constants.ADDRESS_TYPE_SHIPPING);
        when(userAddressMock1.getAddress()).thenReturn("address1");
        when(userAddressMock1.getCity()).thenReturn("city1");
        when(userAddressMock1.getCountry()).thenReturn("indonesia1");
        when(userDetailMock.getUserFullName()).thenReturn("fullname");
        when(userDetailMock.getPhoneNumber()).thenReturn("phoneNumber");
        userAddressListMock =  new ArrayList<>();
        TransactionRequest transactionRequest = new TransactionRequest(orderId, amount, Constants.PAYMENT_METHOD_NOT_SELECTED);
        userAddressListMock.add(userAddressMock1);

        TransactionRequest request = SdkUtil.extractUserAddress(userDetailMock, userAddressListMock, transactionRequest);
        Assert.assertEquals(0, request.getBillingAddressArrayList().size());
        Assert.assertEquals(1, request.getShippingAddressArrayList().size());
    }

    @Test
    public void extractUserAddress_whenBillingTypeShipping(){
        when(userAddressMock1.getAddressType()).thenReturn(Constants.ADDRESS_TYPE_BILLING);
        when(userAddressMock1.getAddress()).thenReturn("address1");
        when(userAddressMock1.getCity()).thenReturn("city1");
        when(userAddressMock1.getCountry()).thenReturn("indonesia1");
        when(userDetailMock.getUserFullName()).thenReturn("fullname");
        when(userDetailMock.getPhoneNumber()).thenReturn("phoneNumber");
        userAddressListMock =  new ArrayList<>();
        TransactionRequest transactionRequest = new TransactionRequest(orderId, amount, Constants.PAYMENT_METHOD_NOT_SELECTED);
        userAddressListMock.add(userAddressMock1);

        TransactionRequest request = SdkUtil.extractUserAddress(userDetailMock, userAddressListMock, transactionRequest);
        Assert.assertEquals(1, request.getBillingAddressArrayList().size());
        Assert.assertEquals(0, request.getShippingAddressArrayList().size());
    }
}
