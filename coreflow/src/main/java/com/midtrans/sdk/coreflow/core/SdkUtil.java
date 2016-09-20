package com.midtrans.sdk.coreflow.core;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.midtrans.sdk.coreflow.models.BCAKlikPayDescriptionModel;
import com.midtrans.sdk.coreflow.models.BillingAddress;
import com.midtrans.sdk.coreflow.models.CardTransfer;
import com.midtrans.sdk.coreflow.models.CstoreEntity;
import com.midtrans.sdk.coreflow.models.ShippingAddress;
import com.midtrans.sdk.coreflow.models.TokenRequestModel;

import java.util.ArrayList;

import com.midtrans.sdk.coreflow.R;
import com.midtrans.sdk.coreflow.models.BBMMoneyRequestModel;
import com.midtrans.sdk.coreflow.models.BCABankTransfer;
import com.midtrans.sdk.coreflow.models.BCAKlikPayModel;
import com.midtrans.sdk.coreflow.models.BankTransfer;
import com.midtrans.sdk.coreflow.models.CIMBClickPayModel;
import com.midtrans.sdk.coreflow.models.CardPaymentDetails;
import com.midtrans.sdk.coreflow.models.CustomerDetails;
import com.midtrans.sdk.coreflow.models.DescriptionModel;
import com.midtrans.sdk.coreflow.models.EpayBriTransfer;
import com.midtrans.sdk.coreflow.models.IndomaretRequestModel;
import com.midtrans.sdk.coreflow.models.IndosatDompetkuRequest;
import com.midtrans.sdk.coreflow.models.KlikBCADescriptionModel;
import com.midtrans.sdk.coreflow.models.KlikBCAModel;
import com.midtrans.sdk.coreflow.models.MandiriBillPayTransferModel;
import com.midtrans.sdk.coreflow.models.MandiriClickPayModel;
import com.midtrans.sdk.coreflow.models.MandiriClickPayRequestModel;
import com.midtrans.sdk.coreflow.models.MandiriECashModel;
import com.midtrans.sdk.coreflow.models.PermataBankTransfer;
import com.midtrans.sdk.coreflow.models.SnapTransactionDetails;
import com.midtrans.sdk.coreflow.models.TransactionDetails;
import com.midtrans.sdk.coreflow.models.UserAddress;
import com.midtrans.sdk.coreflow.models.UserDetail;
import com.midtrans.sdk.coreflow.models.snap.payment.BankTransferPaymentRequest;
import com.midtrans.sdk.coreflow.models.snap.payment.CreditCardPaymentRequest;
import com.midtrans.sdk.coreflow.models.snap.payment.KlikBCAPaymentRequest;
import com.midtrans.sdk.coreflow.models.snap.payment.MandiriClickPayPaymentRequest;
import com.midtrans.sdk.coreflow.models.snap.payment.PaymentDetails;

/**
 * Created by ziahaqi on 18/06/2016.
 */
public class SdkUtil {
    private static final String UNIT_MINUTES = "minutes";

    /**
     * helper method to extract {@link MandiriBillPayTransferModel} from {@link TransactionRequest}.
     *
     * @param request   transaction request object
     * @return Transfer model object
     */
    protected static MandiriBillPayTransferModel getMandiriBillPayModel(TransactionRequest
                                                                                request) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());
        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        MandiriBillPayTransferModel model =
                new MandiriBillPayTransferModel(request.getBillInfoModel(),
                        transactionDetails, request.getItemDetails(),
                        request.getBillingAddressArrayList(),
                        request.getShippingAddressArrayList(), request.getCustomerDetails());
        return model;
    }


    /**
     * helper method to extract {@link MandiriClickPayModel} from {@link TransactionRequest}.
     *
     * @param request   transaction request object
     * @return Transfer model object
     */
    protected static MandiriClickPayRequestModel getMandiriClickPayRequestModel(TransactionRequest
                                                                                        request,
                                                                                MandiriClickPayModel mandiriClickPayModel) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        MandiriClickPayRequestModel model =
                new MandiriClickPayRequestModel(mandiriClickPayModel,
                        transactionDetails, request.getItemDetails(),
                        request.getBillingAddressArrayList(),
                        request.getShippingAddressArrayList(), request.getCustomerDetails());
        return model;
    }

    protected static KlikBCAModel getKlikBCAModel(TransactionRequest request, KlikBCADescriptionModel descriptionModel) {
        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(), request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        return new KlikBCAModel(
                descriptionModel,
                transactionDetails,
                request.getItemDetails(),
                request.getBillingAddressArrayList(),
                request.getShippingAddressArrayList(),
                request.getCustomerDetails()
        );
    }

    protected static BCAKlikPayModel getBCAKlikPayModel(TransactionRequest request,
                                                        BCAKlikPayDescriptionModel descriptionModel) {
        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(), request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        return new BCAKlikPayModel(
                descriptionModel,
                transactionDetails,
                request.getItemDetails(),
                request.getBillingAddressArrayList(),
                request.getShippingAddressArrayList(),
                request.getCustomerDetails()
        );
    }


    /**
     * helper method to extract {@link PermataBankTransfer} from {@link TransactionRequest}.
     *
     * @param request   transaction request
     * @return Transfer model object
     */
    protected static PermataBankTransfer getPermataBankModel(TransactionRequest request) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        // bank name
        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBank(MidtransSDK.getInstance().getContext().getString(R.string.payment_permata));

        return new PermataBankTransfer(bankTransfer,
                transactionDetails, request.getItemDetails(),
                request.getBillingAddressArrayList(),
                request.getShippingAddressArrayList(),
                request.getCustomerDetails());

    }

    /**
     * helper method to extract {@link PermataBankTransfer} from {@link TransactionRequest}.
     *
     * @param request   Transaction request
     * @return Transfer model object
     */
    protected static BCABankTransfer getBcaBankTransferRequest(TransactionRequest request) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        // bank name
        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBank(MidtransSDK.getInstance().getContext().getString(R.string.payment_bca));


        BCABankTransfer model =
                new BCABankTransfer(bankTransfer,
                        transactionDetails, request.getItemDetails(),
                        request.getBillingAddressArrayList(),
                        request.getShippingAddressArrayList(),
                        request.getCustomerDetails());
        return model;

    }


    /**
     * helper method to extract {@link IndomaretRequestModel} from
     * {@link TransactionRequest}.
     *
     * @param request   transaction request object
     * @return transfer model object
     */
    protected static IndomaretRequestModel getIndomaretRequestModel(TransactionRequest request,
                                                                    CstoreEntity cstoreEntity) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        IndomaretRequestModel model =
                new IndomaretRequestModel();

        model.setPaymentType(MidtransSDK.getInstance().getContext().getString(R.string.payment_indomaret));

        model.setItem_details(request.getItemDetails());
        model.setCustomerDetails(request.getCustomerDetails());
        model.setTransactionDetails(transactionDetails);
        model.setCstore(cstoreEntity);

        return model;

    }

    protected static BBMMoneyRequestModel getBBMMoneyRequestModel(TransactionRequest request) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        BBMMoneyRequestModel model =
                new BBMMoneyRequestModel();
        model.setPaymentType("bbm_money");
        model.setTransactionDetails(transactionDetails);
        return model;
    }



    /**
     * helper method to extract {@link CIMBClickPayModel} from {@link TransactionRequest}.
     *
     * @param cimbDescription   CIMB bank description
     * @param request           transaction request
     * @return transfer model object
     */

    protected static CIMBClickPayModel getCIMBClickPayModel(TransactionRequest request,
                                                            DescriptionModel cimbDescription) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        CIMBClickPayModel model =
                new CIMBClickPayModel(cimbDescription, transactionDetails, request.getItemDetails(),
                        request.getBillingAddressArrayList(),
                        request.getShippingAddressArrayList(),
                        request.getCustomerDetails());
        return model;
    }

    /**
     * helper method to extract {@link MandiriECashModel} from {@link TransactionRequest}.
     *
     * @return Mandiri E Cash Model object
     */

    protected static MandiriECashModel getMandiriECashModel(TransactionRequest request,
                                                            DescriptionModel description) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        MandiriECashModel model =
                new MandiriECashModel(description, transactionDetails, request.getItemDetails(),
                        request.getBillingAddressArrayList(),
                        request.getShippingAddressArrayList(),
                        request.getCustomerDetails());
        return model;
    }

    /**
     * helper method to extract {@link CardTransfer} from {@link TransactionRequest}.
     *
     * @param request   transaction request
     * @param cardPaymentDetails   payment details
     * @return Card transfer model object
     */
    public static CardTransfer getCardTransferModel(TransactionRequest request,
                                                    CardPaymentDetails cardPaymentDetails) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        CardTransfer model =
                new CardTransfer(cardPaymentDetails,
                        transactionDetails, request.getItemDetails(),
                        request.getBillingAddressArrayList(),
                        request.getShippingAddressArrayList(),
                        request.getCustomerDetails());
        return model;
    }


    /**
     * helper method to extract {@link EpayBriTransfer} from {@link TransactionRequest}.
     *
     * @param request   transaction request object
     * @return E Pay BRI transfer model
     */
    protected static EpayBriTransfer getEpayBriBankModel(TransactionRequest request) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());

        if (request.isUiEnabled()) {
            //get user details only if using default ui.
            request = initializeUserInfo(request);
        }

        EpayBriTransfer model =
                new EpayBriTransfer(transactionDetails, request.getItemDetails(),
                        request.getBillingAddressArrayList(),
                        request.getShippingAddressArrayList(),
                        request.getCustomerDetails());
        return model;

    }

    /**
     * helper method to extract {@link IndosatDompetkuRequest} from
     * {@link TransactionRequest}.
     *
     * @param request   transaction request object
     * @return transfer model object
     */
    protected static IndosatDompetkuRequest getIndosatDompetkuRequestModel(TransactionRequest
                                                                                   request,
                                                                           String msisdn) {

        TransactionDetails transactionDetails = new TransactionDetails("" + request.getAmount(),
                request.getOrderId());

        //get user details only if using default ui.
        request = initializeUserInfo(request);

        IndosatDompetkuRequest model =
                new IndosatDompetkuRequest();

        model.setCustomerDetails(request.getCustomerDetails(), request
                .getShippingAddressArrayList(), request.getBillingAddressArrayList());
        model.setPaymentType(MidtransSDK.getInstance().getContext().getString(R.string.payment_indosat_dompetku));

        IndosatDompetkuRequest.IndosatDompetkuEntity entity = new IndosatDompetkuRequest
                .IndosatDompetkuEntity();
        entity.setMsisdn("" + msisdn);

        model.setIndosatDompetku(entity);
        model.setItemDetails(request.getItemDetails());
        model.setTransactionDetails(transactionDetails);

        return model;
    }

    /**
     * helper method to add {@link CustomerDetails} in {@link TransactionRequest}.
     *
     * @param transactionRequest    transaction request
     * @return transactionRequest with  {@link CustomerDetails}.
     */
    protected static TransactionRequest initializeUserInfo(TransactionRequest transactionRequest) {
        transactionRequest = getUserDetails(transactionRequest);
        return transactionRequest;
    }

    /**
     * it extracts customer information from TransactionRequest.
     *
     * @param request instance of TransactionRequest
     * @return transaction request with {@link UserDetail}
     */
     static TransactionRequest getUserDetails(TransactionRequest request) {

        UserDetail userDetail = null;
        CustomerDetails mCustomerDetails = null;

        try {
            userDetail = LocalDataHandler.readObject(MidtransSDK.getInstance().getContext().getString(R.string.user_details), UserDetail.class);

            if (userDetail != null && !TextUtils.isEmpty(userDetail.getUserFullName())) {
                ArrayList<UserAddress> userAddresses = userDetail.getUserAddresses();
                if (userAddresses != null && !userAddresses.isEmpty()) {
                    Logger.i("Found " + userAddresses.size() + " user addresses.");
                    mCustomerDetails = new CustomerDetails();
                    mCustomerDetails.setPhone(userDetail.getPhoneNumber());
                    mCustomerDetails.setFirstName(userDetail.getUserFullName());
                    mCustomerDetails.setLastName(null);
                    mCustomerDetails.setEmail(userDetail.getEmail());
                    //added email in performTransaction()
                    request.setCustomerDetails(mCustomerDetails);

                    request = extractUserAddress(userDetail, userAddresses, request);
                }

            } else {
                Logger.e("User details not available.");
            }
        } catch (Exception ex) {
            Logger.e("Error while fetching user details : " + ex.getMessage());
        }

        return request;
    }

    static TransactionRequest extractUserAddress(UserDetail userDetail,
                                                         ArrayList<UserAddress> userAddresses,
                                                         TransactionRequest request) {

        ArrayList<BillingAddress> billingAddressArrayList = new ArrayList<>();
        ArrayList<ShippingAddress> shippingAddressArrayList = new ArrayList<>();

        for (int i = 0; i < userAddresses.size(); i++) {

            UserAddress userAddress = userAddresses.get(i);

            if (userAddress.getAddressType() == Constants.ADDRESS_TYPE_BOTH) {

                BillingAddress billingAddress = getBillingAddress(userDetail, userAddress);
                billingAddressArrayList.add(billingAddress);
                ShippingAddress shippingAddress = getShippingAddress(userDetail, userAddress);
                shippingAddressArrayList.add(shippingAddress);
            } else if (userAddress.getAddressType() == Constants.ADDRESS_TYPE_SHIPPING) {
                ShippingAddress shippingAddress = getShippingAddress(userDetail, userAddress);
                shippingAddressArrayList.add(shippingAddress);
            } else {
                BillingAddress billingAddress = getBillingAddress(userDetail, userAddress);
                billingAddressArrayList.add(billingAddress);
            }

        }

        request.setBillingAddressArrayList(billingAddressArrayList);
        request.setShippingAddressArrayList(shippingAddressArrayList);

        CustomerDetails customerDetails = request.getCustomerDetails();

        if (customerDetails != null) {
            if (billingAddressArrayList.size() > 0 && billingAddressArrayList.get(0) != null) {
                customerDetails.setBillingAddress(billingAddressArrayList.get(0));
            }

            if (shippingAddressArrayList.size() > 0 && shippingAddressArrayList.get(0) != null) {
                customerDetails.setShippingAddress(shippingAddressArrayList.get(0));
            }
            request.setCustomerDetails(customerDetails);
        }
        return request;
    }

    @NonNull
    private static BillingAddress getBillingAddress(UserDetail userDetail, UserAddress
            userAddress) {
        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setCity(userAddress.getCity());
        billingAddress.setFirstName(userDetail.getUserFullName());
        billingAddress.setLastName("");
        billingAddress.setPhone(userDetail.getPhoneNumber());
        billingAddress.setCountryCode(userAddress.getCountry());
        billingAddress.setPostalCode(userAddress.getZipcode());
        return billingAddress;
    }


    @NonNull
    private static ShippingAddress getShippingAddress(UserDetail userDetail, UserAddress
            userAddress) {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setCity(userAddress.getCity());
        shippingAddress.setFirstName(userDetail.getUserFullName());
        shippingAddress.setLastName("");
        shippingAddress.setPhone(userDetail.getPhoneNumber());
        shippingAddress.setCountryCode(userAddress.getCountry());
        shippingAddress.setPostalCode(userAddress.getZipcode());
        return shippingAddress;
    }

    /**
     * Get device identifier using SDK context.
     *
     * @return device identifier
     */
    public static String getDeviceId() {
        String deviceId = "UNKNOWN";
        try {
            deviceId = Settings.Secure.getString(MidtransSDK.getInstance().getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ex) {
            Logger.e(ex.toString());
        }
        return deviceId;
    }

    public static TokenRequestModel getSnapTokenRequestModel(TransactionRequest transactionRequest) {

        if (transactionRequest.isUiEnabled()) {
            //get user details only if using default ui.
            transactionRequest = initializeUserInfo(transactionRequest);
        }

        SnapTransactionDetails details = new SnapTransactionDetails(transactionRequest.getOrderId(), (int) transactionRequest.getAmount());

        return new TokenRequestModel(
                details,
                transactionRequest.getItemDetails(),
                transactionRequest.getCustomerDetails(), transactionRequest.getCreditCard());
    }

    public static PaymentDetails initializePaymentDetails(TransactionRequest transactionRequest) {
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setFullName(transactionRequest.getCustomerDetails().getFirstName());
        paymentDetails.setPhone(transactionRequest.getCustomerDetails().getPhone());
        paymentDetails.setEmail(transactionRequest.getCustomerDetails().getEmail());
        return paymentDetails;
    }

    public static CreditCardPaymentRequest getCreditCardPaymentRequest(String cardToken, boolean saveCard, TransactionRequest transactionRequest,
                                                                       String tokenId, String paymentType) {
        if (transactionRequest.isUiEnabled()) {
            // get user details only if using default ui
            transactionRequest = initializeUserInfo(transactionRequest);
        }

        PaymentDetails paymentDetails = initializePaymentDetails(transactionRequest);

        CreditCardPaymentRequest paymentRequest = new CreditCardPaymentRequest();
        paymentRequest.setTokenId(cardToken);
        paymentRequest.setSaveCard(saveCard);
        paymentRequest.setPaymentDetails(paymentDetails);
        paymentRequest.setTransactionId(tokenId);
        paymentRequest.setPaymentType(paymentType);
        return paymentRequest;
    }

    public static BankTransferPaymentRequest getBankTransferPaymentRequest(String email,
                                                                           String tokenId, String paymentType) {
        BankTransferPaymentRequest paymentRequest = new BankTransferPaymentRequest();
        paymentRequest.setEmailAddress(email);
        paymentRequest.setTransactionId(tokenId);
        paymentRequest.setPaymentType(paymentType);
        return paymentRequest;
    }

    public static KlikBCAPaymentRequest getKlikBCAPaymentRequest(String userId, String tokenId, String paymentType) {
        KlikBCAPaymentRequest klikBCAPaymentRequest = new KlikBCAPaymentRequest();
        klikBCAPaymentRequest.setUserId(userId);
        klikBCAPaymentRequest.setTransactionId(tokenId);
        klikBCAPaymentRequest.setPaymentType(paymentType);
        return klikBCAPaymentRequest;
    }

    public static String getEmailAddress(TransactionRequest transactionRequest) {
        return transactionRequest.getCustomerDetails().getEmail();
    }

    public static MandiriClickPayPaymentRequest getMandiriClickPaymentRequest(String token, String mandiriCardNumber,
                                                                              String tokenResponse, String input3, String paymentType) {
        MandiriClickPayPaymentRequest request = new MandiriClickPayPaymentRequest();
        request.setTransactionId(token);
        request.setMandiriCardNumber(mandiriCardNumber);
        request.setTokenResponse(tokenResponse);
        request.setInput3(input3);
        request.setPaymentType(paymentType);
        return request;
    }

}
