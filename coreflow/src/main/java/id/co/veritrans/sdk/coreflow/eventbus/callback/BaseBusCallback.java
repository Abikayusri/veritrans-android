package id.co.veritrans.sdk.coreflow.eventbus.callback;

import id.co.veritrans.sdk.coreflow.eventbus.events.GeneralErrorEvent;
import id.co.veritrans.sdk.coreflow.eventbus.events.NetworkUnavailableEvent;

/**
 * @author rakawm
 */
public interface BaseBusCallback {

    void onEvent(NetworkUnavailableEvent event);

    void onEvent(GeneralErrorEvent event);
}
