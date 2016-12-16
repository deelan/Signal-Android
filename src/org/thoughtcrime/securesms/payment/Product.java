package org.thoughtcrime.securesms.payment;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String name;
    private String productId;
    private String skuId;
    private String description;
    private int amount;

    public Product() {}

    public Product(String name, String productId, String skuId, String description, int amount) {
        this.name = name;
        this.productId = productId;
        this.skuId = skuId;
        this.description = description;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }
    public String getProductId() {
        return productId;
    }

    public String getSkuId() {
        return skuId;
    }

    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(productId);
        parcel.writeString(skuId);
        parcel.writeString(description);
        parcel.writeInt(amount);
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private Product(Parcel in) {
        this.name = in.readString();
        this.productId = in.readString();
        this.skuId = in.readString();
        this.description = in.readString();
        this.amount = in.readInt();
    }
}
