package com.midtrans.sdk.uikit.views.cimb_click;

import com.midtrans.sdk.corekit.callback.TransactionCallback;
import com.midtrans.sdk.corekit.models.TransactionResponse;
import com.midtrans.sdk.uikit.abstracts.BasePaymentPresenter;
import com.midtrans.sdk.uikit.abstracts.BasePaymentView;
import com.midtrans.sdk.uikit.constants.AnalyticsEventName;

/**
 * Created by Fajar on 25/10/17.
 */

public class CimbClickPaymentPresenter extends BasePaymentPresenter<BasePaymentView> {
    public CimbClickPaymentPresenter(BasePaymentView view) {
        super();
        this.view = view;
    }

    public void startPayment() {
        getMidtransSDK().paymentUsingCIMBClick(getMidtransSDK().readAuthenticationToken(),
            new TransactionCallback() {
                @Override
                public void onSuccess(TransactionResponse response) {
                    transactionResponse = response;
                    view.onPaymentSuccess(response);
                    trackEvent(AnalyticsEventName.PAGE_STATUS_SUCCESS);
                }

                @Override
                public void onFailure(TransactionResponse response, String reason) {
                    transactionResponse = response;
                    view.onPaymentFailure(response);
                    trackEvent(AnalyticsEventName.PAGE_STATUS_FAILED);
                }

                @Override
                public void onError(Throwable error) {
                    view.onPaymentError(error);
                }
            });
    }
}
