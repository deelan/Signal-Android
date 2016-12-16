package org.thoughtcrime.securesms.payment;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Payment implements Parcelable {
    private String id;
    private String description;
    private int amount;
    private Date created;
    private Metadata metadata;

    public Payment() {}

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    public Date getCreated() {
        return created;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(description);
        parcel.writeInt(amount);
        parcel.writeLong(created.getTime());
        parcel.writeParcelable(metadata, flags);
    }

    public static final Parcelable.Creator<Payment> CREATOR = new Parcelable.Creator<Payment>() {
        public Payment createFromParcel(Parcel in) {
            return new Payment(in);
        }

        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };

    private Payment(Parcel in) {
        this.id = in.readString();
        this.description = in.readString();
        this.amount = in.readInt();
        this.created = new Date(in.readLong());
        this.metadata = in.readParcelable(Metadata.class.getClassLoader());
    }
}
