package com.spectrum.services.models.payfort;

import com.google.gson.annotations.SerializedName;

public class PayResponceToServer {
    @SerializedName("transaction_id")
    String transaction_id;
    @SerializedName("booking_id")
    String booking_id;
    @SerializedName("amount")
    String amount;
    @SerializedName("customer_id")
    String customer_id;
    @SerializedName("payment_status")
    String payment_status;

    public PayResponceToServer(String transaction_id, String booking_id, String amount, String customer_id, String payment_status) {
        this.transaction_id = transaction_id;
        this.booking_id = booking_id;
        this.amount = amount;
        this.customer_id = customer_id;
        this.payment_status = payment_status;
    }

}
