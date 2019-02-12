package com.midtrans.sdk.corekit.utilities;

import com.midtrans.sdk.corekit.base.enums.Currency;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StringHelper {

    private static final String TAG = "StringHelper";

    private static final String IDR = "IDR";
    private static final String SGD = "SGD";

    public static String checkCurrency(String currency) {
        if (currency == null) {
            return Currency.IDR;
        } else if (currency.equalsIgnoreCase(IDR)) {
            return Currency.IDR;
        } else if (currency.equalsIgnoreCase(SGD)) {
            return Currency.SGD;
        } else {
            return Currency.IDR;
        }
    }

    public static String getFormattedAmount(double amount) {
        try {
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
            otherSymbols.setDecimalSeparator('.');
            otherSymbols.setGroupingSeparator(',');
            String amountString = new DecimalFormat("#,###.##", otherSymbols).format(amount);
            return amountString;
        } catch (NullPointerException | IllegalArgumentException e) {
            return "" + amount;
        }
    }

    public static boolean isValidURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return true;
        } catch (MalformedURLException malformedURLException) {
            return false;
        } catch (Exception exception) {
            return false;
        }
    }
}