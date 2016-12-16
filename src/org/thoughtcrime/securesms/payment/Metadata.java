package org.thoughtcrime.securesms.payment;

import android.os.Parcel;
import android.os.Parcelable;

public class Metadata implements Parcelable {
    private String contact;
    private String productId;
    private String skuId;

    public Metadata() {}

    public String getContact() {
        return contact;
    }

    public String getProductId() {
        return productId;
    }

    public String getSkuId() {
        return skuId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStringArray(new String[] {
                this.contact,
                this.productId,
                this.skuId});
    }

    public static final Parcelable.Creator<Metadata> CREATOR = new Parcelable.Creator<Metadata>() {
        public Metadata createFromParcel(Parcel in) {
            return new Metadata(in);
        }

        public Metadata[] newArray(int size) {
            return new Metadata[size];
        }
    };

    private Metadata(Parcel in) {
        String[] data = new String[3];

        in.readStringArray(data);
        this.contact = data[0];
        this.productId = data[1];
        this.skuId = data[2];
    }
}
