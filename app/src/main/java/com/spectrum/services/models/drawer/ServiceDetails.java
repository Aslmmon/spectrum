package com.spectrum.services.models.drawer;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abins Shaji on 14/02/18.
 */

public class ServiceDetails {
    @SerializedName("status")
    private String status;
    @SerializedName("responce_code")
    private  String responce_code;
    @SerializedName("message")
    private String message;

    @SerializedName("service_data")
    private List<ServiceData> serviceData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponce_code() {
        return responce_code;
    }

    public void setResponce_code(String responce_code) {
        this.responce_code = responce_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ServiceData> getServiceData() {
        return serviceData;
    }

    public void setServiceData(List<ServiceData> serviceData) {
        this.serviceData = serviceData;
    }

    public class ServiceData
    {
        private String service;
        private String image;
        private String details;
        private String service_id;

        public String getService_id() {
            return service_id;
        }

        public void setService_id(String service_id) {
            this.service_id = service_id;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }


        @SerializedName("minimum_charges")
        public MinCharge minCharge;

        public MinCharge getMinCharge() {
            return minCharge;
        }

        public void setMinCharge(MinCharge minCharge) {
            this.minCharge = minCharge;
        }

        @SerializedName("instructions")
        public ArrayList<InstructionData> instructionData;

        public ArrayList<InstructionData> getInstructionData() {
            return instructionData;
        }

        public void setInstructionData(ArrayList<InstructionData> instructionData) {
            this.instructionData = instructionData;
        }
    }

    public class MinCharge{
        @SerializedName("note")
        public String note;
        @SerializedName("charges")
        public ArrayList<String> chargeList;

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public ArrayList<String> getChargeList() {
            return chargeList;
        }

        public void setChargeList(ArrayList<String> chargeList) {
            this.chargeList = chargeList;
        }
    }
    public class InstructionData{
        @SerializedName("title")
        String title;
        @SerializedName("instr_details")
        ArrayList<String> detailsList;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ArrayList<String> getDetailsList() {
            return detailsList;
        }

        public void setDetailsList(ArrayList<String> detailsList) {
            this.detailsList = detailsList;
        }
    }
}
