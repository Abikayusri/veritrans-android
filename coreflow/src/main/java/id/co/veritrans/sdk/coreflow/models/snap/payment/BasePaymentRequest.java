package id.co.veritrans.sdk.coreflow.models.snap.payment;

import com.google.gson.annotations.SerializedName;

/**
 * @author rakawm
 */
public abstract class BasePaymentRequest {
    @SerializedName("transaction_id")
    private String transactionId;
    private String authenticityToken;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public void setAuthenticityToken(String authenticityToken) {
        this.authenticityToken = authenticityToken;
    }
}
