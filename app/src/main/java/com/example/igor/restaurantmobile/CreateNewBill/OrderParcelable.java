
package com.example.igor.restaurantmobile.CreateNewBill;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderParcelable implements Parcelable {

    @SerializedName("AssortimentUid")
    @Expose
    private String assortimentUid;
    @SerializedName("PriceLineUid")
    @Expose
    private String priceLineUid;
    @SerializedName("Count")
    @Expose
    private String count;
    @SerializedName("Comments")
    @Expose
    private List<String> comments = null;

    private String uid;

    private String internUid;

    public OrderParcelable(Parcel in) {
        assortimentUid = in.readString();
        priceLineUid = in.readString();
        count = in.readString();
        comments = in.createStringArrayList();
        uid = in.readString();
        internUid = in.readString();
    }

    public OrderParcelable(Order order) {
        assortimentUid = order.getAssortimentUid();
        priceLineUid = order.getPriceLineUid();
        count = String.valueOf(order.getCount());
        comments = order.getComments();
        uid = order.getUid();
        internUid = order.getInternUid();
    }

    public static final Creator<OrderParcelable> CREATOR = new Creator<OrderParcelable>() {
        @Override
        public OrderParcelable createFromParcel(Parcel in) {
            return new OrderParcelable(in);
        }

        @Override
        public OrderParcelable[] newArray(int size) {
            return new OrderParcelable[size];
        }
    };

    public String getAssortimentUid() {
        return assortimentUid;
    }

    public void setAssortimentUid(String assortimentUid) {
        this.assortimentUid = assortimentUid;
    }

    public String getPriceLineUid() {
        return priceLineUid;
    }

    public void setPriceLineUid(String priceLineUid) {
        this.priceLineUid = priceLineUid;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getInternUid() {
        return internUid;
    }

    public void setInternUid(String internUid) {
        this.internUid = internUid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(assortimentUid);
        parcel.writeString(priceLineUid);
        parcel.writeString(count);
        parcel.writeStringList(comments);
        parcel.writeString(uid);
        parcel.writeString(internUid);
    }
}
