package com.midtrans.sdk.corekit.core.api.snap.model.pay.response.bcaklikpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BcaKlikPayDataResponse {
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("params")
    @Expose
    private BcaKlikPayDataParamsResponse params;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public BcaKlikPayDataParamsResponse getParams() {
        return params;
    }

    public void setParams(BcaKlikPayDataParamsResponse params) {
        this.params = params;
    }
}