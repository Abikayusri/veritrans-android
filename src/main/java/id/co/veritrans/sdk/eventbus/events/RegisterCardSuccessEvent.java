package id.co.veritrans.sdk.eventbus.events;

import id.co.veritrans.sdk.models.RegisterCardResponse;

/**
 * @author rakawm
 */
public class RegisterCardSuccessEvent extends BaseSuccessEvent<RegisterCardResponse> {
    public RegisterCardSuccessEvent(RegisterCardResponse response) {
        super(response);
    }
}
