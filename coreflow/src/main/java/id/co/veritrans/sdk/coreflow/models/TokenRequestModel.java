package id.co.veritrans.sdk.coreflow.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import id.co.veritrans.sdk.coreflow.models.snap.CreditCard;

/**
 * Created by ziahaqi on 7/19/16.
 */
public class TokenRequestModel {

    @SerializedName("customer_details")
    private final CustomerDetails costumerDetails;

    @SerializedName("item_details")
    private  ArrayList<ItemDetails> itemDetails;

    @SerializedName("transaction_details")
    private SnapTransactionDetails transactionDetails;

    @SerializedName("credit_card")
    private CreditCard creditCard;

    public TokenRequestModel(SnapTransactionDetails transactionDetails, ArrayList<ItemDetails> itemDetails,
                             CustomerDetails customerDetails) {
        this.transactionDetails = transactionDetails;
        this.itemDetails = itemDetails;
        this.costumerDetails = customerDetails;
    }

    public TokenRequestModel(SnapTransactionDetails transactionDetails, ArrayList<ItemDetails> itemDetails, CustomerDetails customerDetails, CreditCard creditCard) {
        this.transactionDetails = transactionDetails;
        this.itemDetails = itemDetails;
        this.costumerDetails = customerDetails;
        this.creditCard = creditCard;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public CustomerDetails getCostumerDetails() {
        return costumerDetails;
    }

    public ArrayList<ItemDetails> getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ArrayList<ItemDetails> itemDetails) {
        this.itemDetails = itemDetails;
    }

    public SnapTransactionDetails getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(SnapTransactionDetails transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public String getString(){
        String json = "";
        try {
            Gson gson = new Gson();
            json = gson.toJson(this);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return json;
    }

}
