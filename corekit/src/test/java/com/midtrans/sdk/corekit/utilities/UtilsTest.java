package com.midtrans.sdk.corekit.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, Logger.class})
public class UtilsTest {

    @Mock
    Context contextMock;
    @Mock
    private ConnectivityManager conManagerMock;
    @Mock
    private android.net.NetworkInfo networkInfoMock;
    @Mock
    private View viewMock;
    @Mock
    private InputMethodManager inputMethodMock;
    @Mock
    private IBinder binderMock;
    private String ccNumberSample = "4811111111111114";
    private String ccFormatedCCNumberSample = "4811 1111 1111 1114 ";

    @Before
    public void setup() {
        Mockito.when(contextMock.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(conManagerMock);
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(Logger.class);
    }

    @Test
    public void test_isNetworkAvailable_positive() {
        Mockito.when(conManagerMock.getActiveNetworkInfo()).thenReturn(networkInfoMock);
        Mockito.when(networkInfoMock.isAvailable()).thenReturn(true);
        Mockito.when(networkInfoMock.isConnected()).thenReturn(true);
        Assert.assertTrue(NetworkHelper.isNetworkAvailable(contextMock));
    }

    @Test
    public void test_isNetworkAvailable_negative_exceptionError() {
        Mockito.when(contextMock.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null);
        Mockito.when(conManagerMock.getActiveNetworkInfo()).thenReturn(null);
        NetworkHelper.isNetworkAvailable(null);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.error(Matchers.anyString());
    }

    @Test
    public void test_isNetworkAvailable_negative_exception() {
        Mockito.when(conManagerMock.getActiveNetworkInfo()).thenReturn(null);
        Mockito.when(networkInfoMock.isAvailable()).thenReturn(true);
        Mockito.when(networkInfoMock.isConnected()).thenReturn(true);

        Assert.assertFalse(NetworkHelper.isNetworkAvailable(contextMock));
    }

    @Test
    public void test_isNetworkAvailable_negative_notConnected() {
        Mockito.when(conManagerMock.getActiveNetworkInfo()).thenReturn(null);
        Mockito.when(networkInfoMock.isAvailable()).thenReturn(false);
        Mockito.when(networkInfoMock.isConnected()).thenReturn(true);

        Assert.assertFalse(NetworkHelper.isNetworkAvailable(contextMock));
    }

    @Test
    public void test_isNetworkAvailable_negative_notAvailable() {
        Mockito.when(conManagerMock.getActiveNetworkInfo()).thenReturn(null);
        Mockito.when(networkInfoMock.isAvailable()).thenReturn(true);
        Mockito.when(networkInfoMock.isConnected()).thenReturn(false);

        Assert.assertFalse(NetworkHelper.isNetworkAvailable(contextMock));
    }

    @Test
    public void test_hideKeyboard_positive() {
        Mockito.when(contextMock.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(inputMethodMock);
        Mockito.when(viewMock.getWindowToken()).thenReturn(binderMock);
        Mockito.when(inputMethodMock.hideSoftInputFromWindow(binderMock, 0)).thenReturn(true);
        Utils.hideKeyboard(contextMock, viewMock);

        Mockito.verify(inputMethodMock).hideSoftInputFromWindow(binderMock, 0);
    }

    @Test
    public void test_hideKeyboard_negative_exception() {
        Mockito.when(contextMock.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(null);
        Mockito.when(viewMock.getWindowToken()).thenReturn(null);
        Mockito.when(inputMethodMock.hideSoftInputFromWindow(binderMock, 0)).thenReturn(true);
        Utils.hideKeyboard(contextMock, viewMock);
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.error(Matchers.anyString());

    }

    @Test
    public void test_getMonth_positive() {
        Assert.assertEquals("January", DateTimeHelper.getMonth(1));
        Assert.assertEquals("February", DateTimeHelper.getMonth(2));
        Assert.assertEquals("March", DateTimeHelper.getMonth(3));
        Assert.assertEquals("April", DateTimeHelper.getMonth(4));
        Assert.assertEquals("May", DateTimeHelper.getMonth(5));
        Assert.assertEquals("June", DateTimeHelper.getMonth(6));
        Assert.assertEquals("July", DateTimeHelper.getMonth(7));
        Assert.assertEquals("August", DateTimeHelper.getMonth(8));
        Assert.assertEquals("September", DateTimeHelper.getMonth(9));
        Assert.assertEquals("October", DateTimeHelper.getMonth(10));
        Assert.assertEquals("November", DateTimeHelper.getMonth(11));
        Assert.assertEquals("December", DateTimeHelper.getMonth(12));
    }

    @Test
    public void test_getMonth_negative_invalid() {
        Assert.assertEquals("Invalid Month", DateTimeHelper.getMonth(123));
    }

    @Test
    public void test_formatCC_positive() {
        String formattedCC = Utils.getFormattedCreditCardNumber(ccNumberSample);
        Assert.assertEquals(ccFormatedCCNumberSample, formattedCC);
    }

    @Test
    public void test_formatCC_negative() {
        Assert.assertNotEquals(ccFormatedCCNumberSample, null);
    }

    @Test
    public void test_getCardType_positive_whenCardShort() {
        Assert.assertEquals("", Utils.getCardType("1"));
    }

    @Test
    public void test_getCardType_positive_whenCardNull() {
        Assert.assertEquals("", Utils.getCardType(""));
    }

    @Test
    public void test_getCardType_positive_whenVisa() {
        Assert.assertEquals(Utils.CARD_TYPE_VISA, Utils.getCardType("4811"));
    }

    @Test
    public void test_getCardType_positive_whenMasterCard() {
        Assert.assertEquals(Utils.CARD_TYPE_MASTERCARD, Utils.getCardType("5111"));
        Assert.assertEquals(Utils.CARD_TYPE_MASTERCARD, Utils.getCardType("5211"));
        Assert.assertEquals(Utils.CARD_TYPE_MASTERCARD, Utils.getCardType("5311"));
        Assert.assertEquals(Utils.CARD_TYPE_MASTERCARD, Utils.getCardType("5411"));
        Assert.assertEquals(Utils.CARD_TYPE_MASTERCARD, Utils.getCardType("5511"));
    }

    @Test
    public void test_getCard_positive_whenAmex() {
        Assert.assertEquals(Utils.CARD_TYPE_AMEX, Utils.getCardType("3488"));
        Assert.assertEquals(Utils.CARD_TYPE_AMEX, Utils.getCardType("3788"));
    }

    @Test
    public void test_getCard_whenInvalid() {
        Assert.assertEquals("", Utils.getCardType("9999"));
    }


    @Test
    public void test_getValidityTime_whenNull() {
        Assert.assertEquals(null, DateTimeHelper.getValidityTime(null));
    }

    @Test
    public void test_getValidityTime_whenNotNull() {
        Assert.assertNotNull(DateTimeHelper.getValidityTime("2015-10-30 20:32:51"));
    }

    @Test
    public void test_getValidtyTime_whenParseExcetion() {
        String sampleTime = "string error 23423 234";
        Assert.assertEquals(sampleTime, DateTimeHelper.getValidityTime(sampleTime));
        PowerMockito.verifyStatic(Mockito.times(1));
        Logger.error(Matchers.anyString());
    }

    @Test
    public void test_getFormattedAmount_positive() {
        Assert.assertEquals("1,000", StringHelper.getFormattedAmount(1000.0));
    }
}