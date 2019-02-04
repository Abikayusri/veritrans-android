package com.midtrans.sdk.uikit;

import android.content.Context;
import android.text.TextUtils;

import com.midtrans.sdk.corekit.MidtransSdk;
import com.midtrans.sdk.corekit.base.enums.Environment;
import com.midtrans.sdk.corekit.utilities.Logger;

import static android.webkit.URLUtil.isValidUrl;
import static com.midtrans.sdk.corekit.utilities.Constants.ERROR_SDK_CLIENT_KEY_AND_CONTEXT_PROPERLY;
import static com.midtrans.sdk.corekit.utilities.Constants.ERROR_SDK_IS_NOT_INITIALIZE_PROPERLY;
import static com.midtrans.sdk.corekit.utilities.Constants.ERROR_SDK_MERCHANT_BASE_URL_PROPERLY;
import static com.midtrans.sdk.corekit.utilities.Constants.MESSAGE_INSTANCE_NOT_INITALIZE;

public class MidtransUi {

    private static volatile MidtransUi SINGLETON_INSTANCE = null;
    private static final int API_TIMEOUT_DEFAULT = 30;
    /**
     * Mandatory property.
     */
    private Context context;
    private String merchantClientId;
    private String merchantBaseUrl;
    /**
     * Optional property.
     */
    private Environment midtransEnvironment;
    private int apiRequestTimeOut;
    private boolean isBuiltinStorageEnabled;
    private boolean isLogEnabled;

    MidtransUi(
            Context context,
            String merchantClientId,
            String merchantBaseUrl,
            Environment midtransEnvironment,
            int apiRequestTimeOut,
            boolean isBuiltinStorageEnabled,
            boolean isLogEnabled
    ) {
        this.context = context;
        this.merchantClientId = merchantClientId;
        this.merchantBaseUrl = merchantBaseUrl;
        this.midtransEnvironment = midtransEnvironment;
        this.apiRequestTimeOut = apiRequestTimeOut;
        this.isBuiltinStorageEnabled = isBuiltinStorageEnabled;
        this.isLogEnabled = isLogEnabled;
        initMidtransSdk();
    }

    /**
     * Initializer.
     *
     * @param context     Context, mandatory not null.
     * @param clientId    ClientId or Merchant Client Key, mandatory not null.
     * @param merchantUrl MerchantUrl or Merchant Base Url, mandatory not null.
     * @return Builder.
     */
    public static Builder builder(final Context context,
                                  final String clientId,
                                  final String merchantUrl) {

        return new Builder(
                context,
                clientId,
                merchantUrl
        );
    }

    /**
     * Returns value of merchant url.
     *
     * @return merchant url value.
     */
    public Environment getEnvironment() {
        if (doCheckSdkInitialization(midtransEnvironment)) {
            return midtransEnvironment;
        }
        return null;
    }

    /**
     * Returns value of instance context.
     *
     * @return context value.
     */
    public Context getContext() {
        if (doCheckSdkInitialization(context)) {
            return context;
        }
        return null;
    }

    /**
     * Returns value of merchant client id.
     *
     * @return merchant client id value.
     */
    public String getMerchantClientId() {
        if (doCheckSdkInitialization(merchantClientId)) {
            return merchantClientId;
        }
        return null;
    }

    /**
     * Returns value of merchant base url.
     *
     * @return merchant base url value.
     */
    public String getMerchantBaseUrl() {
        if (doCheckSdkInitialization(merchantBaseUrl)) {
            return merchantBaseUrl;
        }
        return null;
    }

    /**
     * Returns value of timeout.
     *
     * @return timeout value.
     */
    public int getApiRequestTimeOut() {
        if (doCheckSdkInitialization(apiRequestTimeOut)) {
            return apiRequestTimeOut;
        }
        return API_TIMEOUT_DEFAULT;
    }

    /**
     * Returns value of builtinstorage
     *
     * @return builtinstorage value
     */
    public boolean isBuiltinStorageEnabled() {
        if (doCheckSdkInitialization(isBuiltinStorageEnabled)) {
            return isBuiltinStorageEnabled;
        }
        return true;
    }

    /**
     * Returns instance of midtrans sdk.
     *
     * @return MidtransSdk instance.
     */
    public synchronized static MidtransUi getInstance() {
        if (doCheckSdkInitialization(SINGLETON_INSTANCE)) {
            return SINGLETON_INSTANCE;
        }
        return null;
    }

    private void initMidtransSdk() {
        MidtransSdk
                .builder(
                        this.context,
                        this.merchantClientId,
                        this.merchantBaseUrl
                )
                .setApiRequestTimeOut(this.apiRequestTimeOut)
                .setBuiltinStorageEnabled(this.isBuiltinStorageEnabled)
                .setEnvironment(this.midtransEnvironment)
                .setLogEnabled(this.isLogEnabled)
                .build();
    }

    public static final class Builder {
        private String merchantClientId;
        private Context context;
        private String merchantBaseUrl;
        private Environment midtransEnvironment = Environment.SANDBOX;
        private int apiRequestTimeOut = API_TIMEOUT_DEFAULT;
        private boolean isLogEnabled = false;
        private boolean isBuiltinStorageEnabled = true;

        private Builder(
                final Context context,
                final String clientId,
                final String merchantUrl
        ) {
            this.context = context;
            this.merchantClientId = clientId;
            this.merchantBaseUrl = merchantUrl;
        }

        /**
         * set Logger visible or not.
         */
        public Builder setLogEnabled(
                final boolean isLogEnabled
        ) {
            this.isLogEnabled = isLogEnabled;
            return this;
        }

        public Builder setBuiltinStorageEnabled(
                final boolean isBuiltinStorageEnabled
        ) {
            this.isBuiltinStorageEnabled = isBuiltinStorageEnabled;
            return this;
        }

        /**
         * set Logger visible or not.
         */
        public Builder setEnvironment(
                final Environment environment
        ) {
            this.midtransEnvironment = environment;
            return this;
        }

        /**
         * set Logger visible or not.
         */
        public Builder setApiRequestTimeOut(
                final int apiRequestTimeOutInSecond
        ) {
            this.apiRequestTimeOut = apiRequestTimeOutInSecond;
            return this;
        }

        /**
         * This method will start payment flow if you have set useUi field to true.
         *
         * @return it returns fully initialized object of midtrans sdk.
         */
        public MidtransUi build() {
            if (isValidData()) {
                SINGLETON_INSTANCE = new MidtransUi(
                        context,
                        merchantClientId,
                        merchantBaseUrl,
                        midtransEnvironment,
                        apiRequestTimeOut,
                        isBuiltinStorageEnabled,
                        isLogEnabled
                );
                return SINGLETON_INSTANCE;
            } else {
                Logger.error(ERROR_SDK_IS_NOT_INITIALIZE_PROPERLY);
            }
            return null;
        }

        private boolean isValidData() {
            if (merchantClientId == null || context == null) {
                RuntimeException runtimeException = new RuntimeException(ERROR_SDK_CLIENT_KEY_AND_CONTEXT_PROPERLY);
                Logger.error(runtimeException.getMessage());
            }

            if (TextUtils.isEmpty(merchantBaseUrl) && isValidUrl(merchantBaseUrl)) {
                RuntimeException runtimeException = new RuntimeException(ERROR_SDK_MERCHANT_BASE_URL_PROPERLY);
                Logger.error(runtimeException.getMessage());
            }
            return true;
        }
    }

    private static <T> boolean doCheckSdkInitialization(
            T itemForCheck
    ) {
        if (itemForCheck == null) {
            RuntimeException runtimeException = new RuntimeException(MESSAGE_INSTANCE_NOT_INITALIZE);
            Logger.error(runtimeException.getMessage());
            return false;
        }
        return true;
    }
}