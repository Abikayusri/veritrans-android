package id.co.veritrans.sdk.coreflow.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;

import java.util.ArrayList;

import id.co.veritrans.sdk.coreflow.R;
import id.co.veritrans.sdk.coreflow.eventbus.bus.VeritransBusProvider;
import id.co.veritrans.sdk.coreflow.eventbus.events.Events;
import id.co.veritrans.sdk.coreflow.eventbus.events.GeneralErrorEvent;
import id.co.veritrans.sdk.coreflow.models.BBMCallBackUrl;
import id.co.veritrans.sdk.coreflow.models.BBMMoneyRequestModel;
import id.co.veritrans.sdk.coreflow.models.BCABankTransfer;
import id.co.veritrans.sdk.coreflow.models.BCAKlikPayDescriptionModel;
import id.co.veritrans.sdk.coreflow.models.BCAKlikPayModel;
import id.co.veritrans.sdk.coreflow.models.BankTransferModel;
import id.co.veritrans.sdk.coreflow.models.CIMBClickPayModel;
import id.co.veritrans.sdk.coreflow.models.CardTokenRequest;
import id.co.veritrans.sdk.coreflow.models.CardTransfer;
import id.co.veritrans.sdk.coreflow.models.CstoreEntity;
import id.co.veritrans.sdk.coreflow.models.DescriptionModel;
import id.co.veritrans.sdk.coreflow.models.EpayBriTransfer;
import id.co.veritrans.sdk.coreflow.models.IndomaretRequestModel;
import id.co.veritrans.sdk.coreflow.models.IndosatDompetkuRequest;
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
import id.co.veritrans.sdk.coreflow.utilities.Utils;

//import id.co.veritrans.sdk.activities.SaveCreditCardActivity;
//import id.co.veritrans.sdk.activities.UserDetailsActivity;

/**
 * Created by shivam on 10/19/15.
 */
public class VeritransSDK {

    public static final String BILL_INFO_AND_ITEM_DETAILS_ARE_NECESSARY = "bill info and item " +
            "details are necessary.";
    private static final String ADD_TRANSACTION_DETAILS = "Add transaction request details.";

    private static final String LOCAL_DATA_PREFERENCES = "local.data";
    private static Context context = null;
    private static String themeColorString = null;
    private static int themeColor;

    private static VeritransSDK veritransSDK = new VeritransSDK();

    private static boolean isLogEnabled = true;
    /*private static String serverKey = null;*/
    private static String clientKey = null;
    private static String merchantServerUrl = null;
    private static SharedPreferences mPreferences = null;
    private static String defaultText = null;
    private static String boldText = null;
    private static String semiBoldText = null;
    private static String merchantName = null;
    private static ISdkFlow uiflow;
    private static IScanner externalScanner;
    private static TransactionManager mTransactionManager;


    protected boolean isRunning = false;
    private TransactionRequest transactionRequest = null;
    private ArrayList<PaymentMethodsModel> selectedPaymentMethods = new ArrayList<>();
    private String TRANSACTION_RESPONSE_NOT_AVAILABLE = "Transaction response not available.";
    private BBMCallBackUrl mBBMCallBackUrl = null;

    private VeritransSDK() {
    }



    protected static VeritransSDK getInstance(SdkCoreFlowBuilder sdkBuilder) {

        if (sdkBuilder != null) {
            context = sdkBuilder.context;
            isLogEnabled = sdkBuilder.enableLog;
            /*serverKey = sdkBuilder.serverKey;*/
            clientKey = sdkBuilder.clientKey;
            merchantServerUrl = sdkBuilder.merchantServerUrl;
            if (sdkBuilder.colorTheme != null) {
                themeColorString = sdkBuilder.colorTheme;
            }
            if (sdkBuilder.merchantName != null) {
                merchantName = sdkBuilder.merchantName;
            }
            if(sdkBuilder.defaultText != null){
              defaultText = sdkBuilder.defaultText;
            }
            if(sdkBuilder.semiBoldText != null){
                semiBoldText = sdkBuilder.semiBoldText;
            }
            if(sdkBuilder.boldText != null){
                boldText = sdkBuilder.boldText;
            }
            if(sdkBuilder.sdkFlow != null){
                uiflow = sdkBuilder.sdkFlow;
            }
            if(sdkBuilder.externalScanner != null){
                externalScanner = sdkBuilder.externalScanner;
            }
            initializeTheme();
            initializeSharedPreferences();
            mTransactionManager = new TransactionManager(context, VeritransRestAdapter.getVeritransApiClient(), VeritransRestAdapter.getMerchantApiClient(merchantServerUrl));
            return veritransSDK;
        } else {
            return null;
        }
    }

    private boolean isNetworkAvailable(){
        if(Utils.isNetworkAvailable(context)) {
            return true;
        }
        Logger.e(veritransSDK.getContext().getString(R.string.error_unable_to_connect));
        return false;
    }

    private boolean isSDKAvailable(String eventSource){
        if(veritransSDK != null){
            return true;
        }
        Logger.e(Constants.ERROR_SDK_IS_NOT_INITIALIZED);
        return false;
    }


    private static void initializeSharedPreferences() {
        mPreferences = context.getSharedPreferences(LOCAL_DATA_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * Returns instance of veritrans sdk.
     *
     * @return VeritransSDK instance
     */
    public static VeritransSDK getVeritransSDK() {

        /*if (serverKey != null && context != null && clientKey != null) {
            // created to get access of already created instance of sdk.
            // This instance contains information about transaction.
            return veritransSDK;
        }*/
        if (context != null) {
            return veritransSDK;
        }
        Logger.e(Constants.ERROR_SDK_IS_NOT_INITIALIZED);
        return null;
    }

    private static void initializeTheme() {
        if (themeColorString != null) {
            themeColor = Color.parseColor(themeColorString);
        } else {
            themeColor = context.getResources().getColor(R.color.colorPrimary);
        }
    }

    public static SharedPreferences getmPreferences() {
        return mPreferences;
    }

    public static String getDefaultText() {
        return VeritransSDK.defaultText;
    }

    public void setDefaultText(String defaultText) {
        VeritransSDK.defaultText = defaultText;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public String getBoldText() {
        return VeritransSDK.boldText;
    }

    public void setBoldText(String boldText) {
        VeritransSDK.boldText = boldText;
    }

    public String getSemiBoldText() {
        return VeritransSDK.semiBoldText;
    }

    public void setSemiBoldText(String semiBoldText) {
        VeritransSDK.semiBoldText = semiBoldText;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isLogEnabled() {
        return isLogEnabled;
    }

    public Context getContext() {
        return context;
    }

    public String getMerchantToken() {
        UserDetail userDetail = null;
        try {
            userDetail = LocalDataHandler.readObject(context.getString(R.string.user_details), UserDetail.class);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        String merchantToken = userDetail.getMerchantToken();
        Logger.i("merchantToken:" + merchantToken);
        return merchantToken;
        //return serverKey;
    }

    public String readAuthenticationToken() {
        return LocalDataHandler.readString(Constants.AUTH_TOKEN);
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getMerchantServerUrl(){
        return merchantServerUrl;
    }

    public ArrayList<BankTransferModel> getBankTransferList() {
        ArrayList<BankTransferModel> models = new ArrayList<>();
        models.add(new BankTransferModel(context.getString(R.string.bca_bank_transfer), R.drawable.ic_bca, true));
        models.add(new BankTransferModel(context.getString(R.string.permata_bank_transfer), R.drawable.ic_permata, true));
        return models;
    }

    public ArrayList<PaymentMethodsModel> getSelectedPaymentMethods() {
        return selectedPaymentMethods;
    }

    public void setSelectedPaymentMethods(ArrayList<PaymentMethodsModel> selectedPaymentMethods) {
        this.selectedPaymentMethods = selectedPaymentMethods;
    }

    /**
     * It will execute an api request to retrieve a token.
     *
     * @param cardTokenRequest  token request object
     */
    public void getToken(CardTokenRequest cardTokenRequest) {

        if (cardTokenRequest != null && isNetworkAvailable()) {
            isRunning = true;
            mTransactionManager.getToken(cardTokenRequest);
        } else {
            Logger.e(context.getString(R.string.error_invalid_data_supplied));
            isRunning = false;
        }
    }

    /**
     * It will execute an api request to register credit card info.
     *
     * @param cardTokenRequest  request token object
     */
    public void registerCard(CardTokenRequest cardTokenRequest, String userId) {

        if (cardTokenRequest != null && isNetworkAvailable()) {

            isRunning = true;
            mTransactionManager.registerCard(cardTokenRequest, userId);
        } else {
            Logger.e(context.getString(R.string.error_invalid_data_supplied));
            isRunning = false;
        }
    }


    /**
     * It will execute an transaction for permata bank .
     *
     */
    public void paymentUsingPermataBank() {

        if (transactionRequest != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_PERMATA_VA_BANK_TRANSFER;

                PermataBankTransfer permataBankTransfer = SdkUtil.getPermataBankModel
                        (transactionRequest);

                isRunning = true;
                mTransactionManager.paymentUsingPermataBank(permataBankTransfer);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     * It will execute an transaction for bca bank .
     *
     */
    public void paymentUsingBcaBankTransfer() {

        if (transactionRequest != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_PERMATA_VA_BANK_TRANSFER;

                BCABankTransfer bcaBankTransfer = SdkUtil.getBcaBankTransferRequest(transactionRequest);

                isRunning = true;
                mTransactionManager.paymentUsingBCATransfer(bcaBankTransfer);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     * It will execute an transaction using credit card .
     *
     * @param cardTransfer Card transfer details
     */
    public void paymentUsingCard(CardTransfer cardTransfer) {
        if (transactionRequest != null
                && cardTransfer != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_CREDIT_OR_DEBIT;

                isRunning = true;
                mTransactionManager.paymentUsingCard(cardTransfer);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     * It will execute an transaction for mandiri click pay.
     *
     * @param mandiriClickPayModel       information about mandiri clickpay
     */
    public void paymentUsingMandiriClickPay(MandiriClickPayModel mandiriClickPayModel) {

        if (transactionRequest != null
                && mandiriClickPayModel != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_MANDIRI_CLICK_PAY;

                MandiriClickPayRequestModel mandiriClickPayRequestModel =
                        SdkUtil.getMandiriClickPayRequestModel(transactionRequest,
                                mandiriClickPayModel);

                isRunning = true;

                mTransactionManager.paymentUsingMandiriClickPay(mandiriClickPayRequestModel);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {

            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     * It will execute an transaction for mandiri click pay.
     *
     * @param descriptionModel       information about BCA Klikpay
     */
    public void paymentUsingBCAKlikPay(BCAKlikPayDescriptionModel descriptionModel) {

        if (transactionRequest != null && descriptionModel != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_MANDIRI_CLICK_PAY;
                BCAKlikPayModel bcaKlikPayModel = SdkUtil.getBCAKlikPayModel(transactionRequest, descriptionModel);
                isRunning = true;
                mTransactionManager.paymentUsingBCAKlikPay(bcaKlikPayModel);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    public void paymentUsingKlikBCA(KlikBCADescriptionModel descriptionModel) {
        if (transactionRequest != null && descriptionModel != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_MANDIRI_CLICK_PAY;
                KlikBCAModel klikBCAModel = SdkUtil.getKlikBCAModel(transactionRequest, descriptionModel);
                isRunning = true;
                mTransactionManager.paymentUsingKlikBCA(klikBCAModel);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     * It will execute an transaction for mandiri bill pay.
     *
     */
    public void paymentUsingMandiriBillPay() {
        if (transactionRequest != null) {
            if (transactionRequest.getBillInfoModel() != null
                    && transactionRequest.getItemDetails() != null) {
                if(isNetworkAvailable()){
                    transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_MANDIRI_BILL_PAYMENT;

                    MandiriBillPayTransferModel mandiriBillPayTransferModel =
                            SdkUtil.getMandiriBillPayModel(transactionRequest);

                    isRunning = true;
                    mTransactionManager.paymentUsingMandiriBillPay(mandiriBillPayTransferModel);
                }else{
                    isRunning = false;
                    VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
                }

            } else {
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(BILL_INFO_AND_ITEM_DETAILS_ARE_NECESSARY));
                Logger.e("Error: " + BILL_INFO_AND_ITEM_DETAILS_ARE_NECESSARY);
            }
        } else {
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));

        }
    }

    /**
     * It will execute an transaction for CIMB click pay.
     *
     * @param descriptionModel           contains description about the cimb payment
     */

    public void paymentUsingCIMBClickPay(DescriptionModel descriptionModel) {

        if (transactionRequest != null
                && descriptionModel != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_CIMB_CLICKS;

                CIMBClickPayModel cimbClickPayModel = SdkUtil.getCIMBClickPayModel
                        (transactionRequest, descriptionModel);
                isRunning = true;
                mTransactionManager.paymentUsingCIMBPay(cimbClickPayModel);
            }
            else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            if (descriptionModel == null) {
                Logger.e(context.getString(R.string.error_description_required));
            } else {
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
            }
        }
    }

    /**
     * It will execute an transaction for Mandiri E Cash.
     *
     * @param descriptionModel           Description about Mandiri E cash payment.
     */

    public void paymentUsingMandiriECash(DescriptionModel descriptionModel) {
        if (transactionRequest != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_MANDIRI_ECASH;

                MandiriECashModel mandiriECashModel = SdkUtil.getMandiriECashModel
                        (transactionRequest, descriptionModel);
                isRunning = true;
                mTransactionManager.paymentUsingMandiriECash(mandiriECashModel);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            if (descriptionModel == null) {
                Logger.e(context.getString(R.string.error_description_required));
            } else {
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
            }
        }
    }

    public TransactionRequest getTransactionRequest() {
        return transactionRequest;
    }

    /**
     * Set transaction information that you want to execute.
     *
     * @param transactionRequest    request token object
     */
    public void setTransactionRequest(TransactionRequest transactionRequest) {

        if (!isRunning) {

            if (transactionRequest != null) {
                this.transactionRequest = transactionRequest;
            } else {
                Logger.e(ADD_TRANSACTION_DETAILS);
            }

        } else {
            Logger.e(context.getString(R.string.error_already_running));
        }

    }

    private void showError(TransactionRequest transactionRequest) {
        if (transactionRequest == null) {
            Logger.e(ADD_TRANSACTION_DETAILS);
        }

        Logger.e(context.getString(R.string.error_invalid_data_supplied));
    }

    /**
     * This will start actual execution of save card UI flow.
     *
     * @param context current activity.
     */
    public void startRegisterCardUIFlow(Context context) {
        if(uiflow != null){
            uiflow.runRegisterCard(context);
        }
    }
    /**
     * This will start actual execution of transaction. if you have enabled an ui then it will
     * start activity according to it.
     * @param context current activity.
     */

    public void startPaymentUiFlow(Context context) {

        if (transactionRequest != null && !isRunning) {

            if (transactionRequest.getPaymentMethod() == Constants
                    .PAYMENT_METHOD_NOT_SELECTED) {
                transactionRequest.enableUi(true);
                if(uiflow != null){
                    uiflow.runUIFlow(context);
                }
            } 

        } else {
            if (transactionRequest == null) {
                Logger.e(ADD_TRANSACTION_DETAILS);
            } else {
                Logger.e(context.getString(R.string.error_already_running));
            }
        }
    }


    /**
     * It will execute an transaction for epay bri .
     *
     */
    public void paymentUsingEpayBri() {
        if (transactionRequest != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_EPAY_BRI;

            /*PermataBankTransfer permataBankTransfer = SdkUtil.getPermataBankModel
                    (transactionRequest);*/
                EpayBriTransfer epayBriTransfer = SdkUtil.getEpayBriBankModel(transactionRequest);

                isRunning = true;
                mTransactionManager.paymentUsingEpayBri(epayBriTransfer);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     * It will execute an transaction for permata bank .
     *
     * @param msisdn                registered mobile number of user.
     */
    public void paymentUsingIndosatDompetku(String msisdn) {
        if (transactionRequest != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_INDOSAT_DOMPETKU;

                IndosatDompetkuRequest indosatDompetkuRequest =
                        SdkUtil.getIndosatDompetkuRequestModel(transactionRequest, msisdn);

                isRunning = true;
                mTransactionManager.paymentUsingIndosatDompetku(indosatDompetkuRequest);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    public void getPaymentStatus(String transactionId) {
        if (TextUtils.isEmpty(transactionId)) {
            if(isNetworkAvailable()){
                mTransactionManager.getPaymentStatus(transactionId);
            }else{
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }

        }
    }

    /**
     * It will execute an transaction for Indomaret .
     *
     * @param cstoreEntity transaction details
     */
    public void paymentUsingIndomaret(CstoreEntity cstoreEntity) {

        if (transactionRequest != null
                && cstoreEntity != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_INDOSAT_DOMPETKU;

                IndomaretRequestModel indomaretRequestModel =
                        SdkUtil.getIndomaretRequestModel(transactionRequest, cstoreEntity);

                isRunning = true;
                mTransactionManager.paymentUsingIndomaret(indomaretRequestModel);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    /**
     *
     * It will fetch saved cards from merchant server.
     *
     */
    public void getSavedCard() {
        if(isNetworkAvailable()){
            mTransactionManager.getCards();
        }else{
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.GET_CARD));
        }
    }

    /**
     * It will  save cards to merchant server.
     *
     * @param cardTokenRequest card details
     */
    public void saveCards(SaveCardRequest cardTokenRequest) {
        if (cardTokenRequest != null) {
            if(isNetworkAvailable()){
                mTransactionManager.saveCards(cardTokenRequest);
            }else{
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.REGISTER_CARD));
            }
        }
    }

    /**
     * It will execute an transaction for BBMMoney.
     *
     */
    public void paymentUsingBBMMoney() {

        if (transactionRequest != null) {
            if(isNetworkAvailable()){
                transactionRequest.paymentMethod = Constants.PAYMENT_METHOD_BBM_MONEY;

                BBMMoneyRequestModel bbmMoneyRequestModel =
                        SdkUtil.getBBMMoneyRequestModel(transactionRequest);

                isRunning = true;
                mTransactionManager.paymentUsingBBMMoney(bbmMoneyRequestModel);
            }else{
                isRunning = false;
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(veritransSDK.getContext().getString(R.string.error_unable_to_connect), Events.PAYMENT));
            }
        } else {
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_invalid_data_supplied)));
        }
    }

    public void deleteCard(SaveCardRequest creditCard) {
        if (creditCard != null) {
            if(isNetworkAvailable()){
                mTransactionManager.deleteCard(creditCard);
            }else{
                VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.DELETE_CARD));
            }
        }
    }

    public void cardRegistration(String cardNumber,
                                 String cardCvv, String cardExpMonth,
                                 String cardExpYear) {
        if(isNetworkAvailable()){
            mTransactionManager.cardRegistration(cardNumber, cardCvv, cardExpMonth, cardExpYear);
            isRunning = true;
        }else{
            isRunning = false;
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(veritransSDK.getContext().getString(R.string.error_empty_response), Events.CARD_REGISTRATION));
        }
    }
    public BBMCallBackUrl getBBMCallBackUrl() {
        return mBBMCallBackUrl;
    }

    //unused
    public void setBBMCallBackUrl(BBMCallBackUrl BBMCallBackUrl) {
        mBBMCallBackUrl = BBMCallBackUrl;
    }

    /**
     * It will fetch the Offers from merchant server.
     *
     */
    public void getOffersList() {
        if(isNetworkAvailable()){
            mTransactionManager.getOffers();
        }else{
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.GET_OFFER));
        }
    }

    /**
     * It will run background task to get authentication token.
     */
    public void getAuthenticationToken() {
        if(isNetworkAvailable()){
            mTransactionManager.getAuthenticationToken();
        }else{
            VeritransBusProvider.getInstance().post(new GeneralErrorEvent(context.getString(R.string.error_unable_to_connect), Events.AUTHENTICATION));
        }
    }

    public IScanner getExternalScanner() {
        return externalScanner;
    }

    public  TransactionManager getTransactionManager() {
        return mTransactionManager;
    }
}