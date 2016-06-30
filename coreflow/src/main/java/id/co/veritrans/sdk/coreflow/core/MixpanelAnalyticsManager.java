package id.co.veritrans.sdk.coreflow.core;

import android.os.Build;
import android.util.Base64;

import com.google.gson.Gson;

import id.co.veritrans.sdk.coreflow.BuildConfig;
import id.co.veritrans.sdk.coreflow.analytics.MixpanelApi;
import id.co.veritrans.sdk.coreflow.analytics.MixpanelEvent;
import id.co.veritrans.sdk.coreflow.analytics.MixpanelProperties;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author rakawm
 */
public class MixpanelAnalyticsManager {
    // Platform properties
    private static final String PLATFORM = "Android";
    /**
     * Track event for mixpanel.
     *
     * @param event Mixpanel parameter.
     */
    private static void trackEvent(MixpanelEvent event) {
        MixpanelApi api = VeritransRestAdapter.getMixpanelApi();

        if (api != null) {
            Gson gson = new Gson();
            String eventObject = gson.toJson(event);
            String data = Base64.encodeToString(eventObject.getBytes(), Base64.DEFAULT);
            api.trackEvent(data, new Callback<Integer>() {
                @Override
                public void success(Integer integer, Response response) {
//                    Logger.i("Response: " + Integer.toString(integer));

                }

                @Override
                public void failure(RetrofitError error) {
//                    Logger.i("Response>error: " + error.getMessage());

                }
            });
        } else {
//            Logger.e("No network connection");
        }
    }

    // Mixpanel event tracker
    public void trackMixpanel(String eventName, String paymentType, String bankType, long responseTime) {
        MixpanelEvent event = new MixpanelEvent();
        event.setEvent(eventName);

        MixpanelProperties properties = new MixpanelProperties();
        properties.setOsVersion(Build.VERSION.RELEASE);
        properties.setVersion(BuildConfig.VERSION_NAME);
        properties.setPlatform(PLATFORM);
        properties.setDeviceId(SdkUtil.getDeviceId());
        properties.setToken(BuildConfig.MIXPANEL_TOKEN);
        properties.setMerchant(VeritransSDK.getVeritransSDK() != null &&
                VeritransSDK.getVeritransSDK().getMerchantName() != null ?
                VeritransSDK.getVeritransSDK().getMerchantName() :
                VeritransSDK.getVeritransSDK().getClientKey());
        properties.setPaymentType(paymentType);
        if (bankType != null && !bankType.equals("")) {
            properties.setBank(bankType);
        }
        properties.setResponseTime(responseTime);

        event.setProperties(properties);

        MixpanelAnalyticsManager.trackEvent(event);
    }

    public void trackMixpanel(String eventName, String paymentType, long responseTime) {
        MixpanelEvent event = new MixpanelEvent();
        event.setEvent(eventName);

        MixpanelProperties properties = new MixpanelProperties();
        properties.setOsVersion(Build.VERSION.RELEASE);
        properties.setVersion(BuildConfig.VERSION_NAME);
        properties.setPlatform(PLATFORM);
        properties.setDeviceId(SdkUtil.getDeviceId());
        properties.setToken(BuildConfig.MIXPANEL_TOKEN);
        properties.setMerchant(VeritransSDK.getVeritransSDK() != null &&
                VeritransSDK.getVeritransSDK().getMerchantName() != null ?
                VeritransSDK.getVeritransSDK().getMerchantName() :
                VeritransSDK.getVeritransSDK().getClientKey());
        properties.setPaymentType(paymentType);
        properties.setResponseTime(responseTime);

        event.setProperties(properties);

        MixpanelAnalyticsManager.trackEvent(event);
    }

    public void trackMixpanel(String eventName, String paymentType, long responseTime, String errorMessage) {
        MixpanelEvent event = new MixpanelEvent();
        event.setEvent(eventName);

        MixpanelProperties properties = new MixpanelProperties();
        properties.setOsVersion(Build.VERSION.RELEASE);
        properties.setVersion(BuildConfig.VERSION_NAME);
        properties.setPlatform(PLATFORM);
        properties.setDeviceId(SdkUtil.getDeviceId());
        properties.setToken(BuildConfig.MIXPANEL_TOKEN);
        properties.setPaymentType(paymentType);
        properties.setMerchant(VeritransSDK.getVeritransSDK() != null &&
                VeritransSDK.getVeritransSDK().getMerchantName() != null ?
                VeritransSDK.getVeritransSDK().getMerchantName() :
                VeritransSDK.getVeritransSDK().getClientKey());
        properties.setResponseTime(responseTime);
        properties.setMessage(errorMessage);

        event.setProperties(properties);

        MixpanelAnalyticsManager.trackEvent(event);
    }

    public void trackMixpanel(String eventName, String paymentType, String bank, long responseTime, String errorMessage) {
        MixpanelEvent event = new MixpanelEvent();
        event.setEvent(eventName);

        MixpanelProperties properties = new MixpanelProperties();
        properties.setOsVersion(Build.VERSION.RELEASE);
        properties.setVersion(BuildConfig.VERSION_NAME);
        properties.setPlatform(PLATFORM);
        properties.setDeviceId(SdkUtil.getDeviceId());
        properties.setToken(BuildConfig.MIXPANEL_TOKEN);
        properties.setPaymentType(paymentType);
        properties.setBank(bank);
        properties.setMerchant(VeritransSDK.getVeritransSDK() != null &&
                VeritransSDK.getVeritransSDK().getMerchantName() != null ?
                VeritransSDK.getVeritransSDK().getMerchantName() :
                VeritransSDK.getVeritransSDK().getClientKey());
        properties.setResponseTime(responseTime);
        properties.setMessage(errorMessage);

        event.setProperties(properties);

        MixpanelAnalyticsManager.trackEvent(event);
    }
}
