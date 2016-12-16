package org.thoughtcrime.securesms.payment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentList {
    @SerializedName("data")
    private List<Payment> payments;

    public PaymentList() {}

    public List<Payment> getPayments() {
        return payments;
    }
}
