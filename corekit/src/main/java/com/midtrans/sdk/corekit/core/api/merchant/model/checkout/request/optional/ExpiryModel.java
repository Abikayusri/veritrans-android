package com.midtrans.sdk.corekit.core.api.merchant.model.checkout.request.optional;

import com.google.gson.annotations.SerializedName;

import com.midtrans.sdk.corekit.base.enums.ExpiryTimeUnit;

import java.io.Serializable;

public class ExpiryModel implements Serializable {
    private final String UNIT_HOUR = "hours";
    private final String UNIT_MINUTE = "minutes";
    private final String UNIT_DAY = "days";

    @SerializedName("start_time")
    private String startTime;
    @SerializedName("unit")
    private String unit;
    @SerializedName("duration")
    private int duration;

    public ExpiryModel(String startTime,
                       ExpiryTimeUnit unit,
                       int duration) {
        this.startTime = startTime;
        this.duration = duration;
        setUnit(unit);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public ExpiryTimeUnit getUnit() {
        switch (this.unit) {
            case UNIT_DAY:
                return ExpiryTimeUnit.DAY;
            case UNIT_HOUR:
                return ExpiryTimeUnit.HOUR;
            case UNIT_MINUTE:
                return ExpiryTimeUnit.MINUTE;
        }
        return null;
    }

    public void setUnit(ExpiryTimeUnit unit) {
        switch (unit) {
            case HOUR:
                this.unit = UNIT_HOUR;
                break;
            case MINUTE:
                this.unit = UNIT_MINUTE;
                break;
            case DAY:
                this.unit = UNIT_DAY;
                break;
        }

    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}