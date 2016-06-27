package id.co.veritrans.sdk.coreflow;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import id.co.veritrans.sdk.coreflow.core.SdkCoreFlowBuilder;
import id.co.veritrans.sdk.coreflow.core.VeritransSDK;
import id.co.veritrans.sdk.coreflow.utilities.Utils;

/**
 * Created by ziahaqi on 26/06/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, NetworkInfo.class})
public class SDKCoreFlowBuilderTest {
    @Mock
    Context context;
    @Mock
    Resources resources;
    @Mock
    ConnectivityManager connectivityManager;
    @Mock
    NetworkInfo networkInfo;

    private SdkCoreFlowBuilder sdkCoreFlowBuilder;

    @Before
    public void setup(){
        PowerMockito.mockStatic(Log.class);
        Mockito.when(context.getApplicationContext()).thenReturn(context);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
//        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
//        PowerMockito.when(Utils.isNetworkAvailable(context)).thenReturn(true);


        sdkCoreFlowBuilder = new SdkCoreFlowBuilder(context, SDKConfig.CLIENT_KEY, SDKConfig.MERCHANT_BASE_URL);
    }

    @Test
    public void isValidDataFailedTest(){
        Assert.assertTrue(sdkCoreFlowBuilder.isValidData());
        sdkCoreFlowBuilder = new SdkCoreFlowBuilder(context, null, SDKConfig.MERCHANT_BASE_URL);
        Assert.assertFalse(sdkCoreFlowBuilder.isValidData());
    }

    @Test
    public void buidSDKTest_whenInvalidData(){
        sdkCoreFlowBuilder = new SdkCoreFlowBuilder(context, null, SDKConfig.MERCHANT_BASE_URL);
        Assert.assertNull(sdkCoreFlowBuilder.buildSDK());

    }

    @Test
    public void buidSDKTest(){
        Assert.assertNotNull(sdkCoreFlowBuilder.buildSDK());
    }



}
