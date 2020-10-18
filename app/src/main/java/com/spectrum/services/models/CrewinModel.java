package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abins Shaji on 05/03/18.
 */

public class CrewinModel {
    @SerializedName("status")
    private String status;
    @SerializedName("crew_detail")
    private List<String> strings;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List getStrings() {
        return strings;
    }

    public void setStrings(List strings) {
        this.strings = strings;
    }
}


