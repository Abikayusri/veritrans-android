package com.midtrans.sdk.corekit.utilities;

public class Constants {

    public static final String TAG = "MidtransSdk";


    public static final String USER_AGENT = "User-Agent";
    public static final String APPLICATION_JSON_FORMAT = "application/json";
    public static final String AUTHORIZATION_HEADER = "Authentication";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";

    public static final int PAYMENT_METHOD_NOT_SELECTED = -1;

    public static final int PAYMENT_METHOD_CREDIT_OR_DEBIT = 1;
    public static final int PAYMENT_METHOD_MANDIRI_CLICK_PAY = 2;

    public static final int PAYMENT_METHOD_INDOSAT_DOMPETKU = 6;
    public static final int PAYMENT_METHOD_MANDIRI_ECASH = 7;
    public static final int PAYMENT_METHOD_PERMATA_VA_BANK_TRANSFER = 8;

    public static final int PAYMENT_METHOD_MANDIRI_BILL_PAYMENT = 9;
    public static final int PAYMENT_METHOD_INDOMARET = 10;
    public static final int PAYMENT_METHOD_KLIKBCA = 11;
    public static final int PAYMENT_METHOD_TELKOMSEL_CASH = 12;
    public static final int PAYMENT_METHOD_XL_TUNAI = 13;
    public static final int PAYMENT_METHOD_BANK_TRANSFER_ALL_BANK = 14;
    public static final int PAYMENT_METHOD_KIOSON = 15;
    public static final int PAYMENT_METHOD_GCI = 16;

    public static final int BANK_TRANSFER_BCA = 1001;
    public static final int BANK_TRANSFER_PERMATA = 1003;
    public static final int BANK_TRANSFER_BNI = 1004;

    public static final int PHONE_NUMBER_LENGTH = 9;
    public static final int PHONE_NUMBER_MAX_LENGTH = 15;
    public static final int ZIPCODE_LENGTH = 5;


    /**
     * constant to indicate billing address
     */
    public static final int ADDRESS_TYPE_BILLING = 1;
    /**
     * constant to indicate shipping address
     */
    public static final int ADDRESS_TYPE_SHIPPING = 2;

    /**
     * constant to indicate that this address will be used for both billing and shipping purpose.
     */
    public static final int ADDRESS_TYPE_BOTH = 3;

    /**
     * regex for email id.
     */
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * result code used for payment transfer activities
     */
    public static final int RESULT_CODE_PAYMENT_TRANSFER = 5102;
    public static final long FADE_IN_FORM_TIME = 300;

    /**
     * result code used for payment transfer activities
     */
    public static final String STATUS_CODE_200 = "200";
    public static final String STATUS_CODE_201 = "201";
    public static final String STATUS_CODE_400 = "400";

    /**
     * Error Message
     * Midtrans SDK instance
     */
    public static final String ERROR_SDK_IS_NOT_INITIALIZE_PROPERLY = "Midtrans SDK is not initialize properly, please use MidtransSdk.builder().";
    public static final String ERROR_SDK_CLIENT_KEY_AND_CONTEXT_PROPERLY = "Client key and context cannot be null or empty. Please set the client key and context.";
    public static final String ERROR_SDK_MERCHANT_BASE_URL_PROPERLY = "Merchant base url cannot be null or empty (required) and must url valid format. Please set your merchant base url.";
    public static final String MESSAGE_ERROR_CALLBACK_UNIMPLEMENTED = "Callback Unimplemented, please put callback.";

    /**
     * Error Message
     * Api Service Validation
     */
    public static final String MESSAGE_ERROR_SNAP_TOKEN = "Snap Token must not empty.";
    public static final String MESSAGE_ERROR_EMPTY_MERCHANT_URL = "Merchant base url is empty. Please set merchant base url on SDK";

    /**
     * Error Message
     * Network Call
     */
    public static final String MESSAGE_ERROR_EMPTY_RESPONSE = "Failed to retrieve response from server.";
    public static final String MESSAGE_ERROR_FAILURE_RESPONSE = "Error message not catchable";
    public static final String MESSAGE_ERROR_FAILED_TO_CONNECT_TO_SERVER = "Failed to connect to server.";

    public static final String KEY_PREFERENCES_VERSION = "preferences.version";
    public static final String USER_DETAILS = "user_details";

}