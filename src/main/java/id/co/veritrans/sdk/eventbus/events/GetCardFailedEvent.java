package id.co.veritrans.sdk.eventbus.events;

import id.co.veritrans.sdk.models.CardResponse;

/**
 * @author rakawm
 */
public class GetCardFailedEvent extends BaseFailedEvent<CardResponse> {
    public GetCardFailedEvent(String message, CardResponse response) {
        super(message, response);
    }

    public GetCardFailedEvent(String message, CardResponse response, String source) {
        super(message, response, source);
    }
}
