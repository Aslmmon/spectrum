package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abins Shaji on 22/03/18.
 */

public class VUpdateModel {
    @SerializedName("status")
    String status;
    @SerializedName("android")
    VersionAndroid versionAndroid;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VersionAndroid getVersionAndroid() {
        return versionAndroid;
    }

    public void setVersionAndroid(VersionAndroid versionAndroid) {
        this.versionAndroid = versionAndroid;
    }

    public class VersionAndroid
    {
        @SerializedName("new_version")
        int new_version;
        @SerializedName("type")
        String type;

        public int getNew_version() {
            return new_version;
        }

        public void setNew_version(int new_version) {
            this.new_version = new_version;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
