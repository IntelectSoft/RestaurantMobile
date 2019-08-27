
package com.example.igor.restaurantmobile.CreateNewBill;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("AssortimentUid")
    @Expose
    private String assortimentUid;
    @SerializedName("PriceLineUid")
    @Expose
    private String priceLineUid;
    @SerializedName("Count")
    @Expose
    private Double count;
    @SerializedName("Comments")
    @Expose
    private List<String> comments = null;

    private String uid;

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

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
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
}
