package id.co.veritrans.sdk.coreflow.eventbus.callback;

import id.co.veritrans.sdk.coreflow.eventbus.events.GetCardFailedEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.GetCardsSuccessEvent;

/**
 * @author rakawm
 */
public interface GetCardBusCallback extends BaseBusCallback {
    void onEvent(GetCardsSuccessEvent event);

    void onEvent(GetCardFailedEvent event);
}
