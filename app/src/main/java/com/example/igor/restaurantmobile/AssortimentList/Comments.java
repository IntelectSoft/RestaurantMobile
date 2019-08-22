
package com.example.igor.restaurantmobile.AssortimentList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comments {

    @SerializedName("AdditionalAssortimentUid")
    @Expose
    private String additionalAssortimentUid;
    @SerializedName("Comment")
    @Expose
    private String comment;
    @SerializedName("Price")
    @Expose
    private Double price;
    @SerializedName("PricelineUid")
    @Expose
    private String pricelineUid;
    @SerializedName("Uid")
    @Expose
    private String uid;

    public String getAdditionalAssortimentUid() {
        return additionalAssortimentUid;
    }

    public void setAdditionalAssortimentUid(String additionalAssortimentUid) {
        this.additionalAssortimentUid = additionalAssortimentUid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPricelineUid() {
        return pricelineUid;
    }

    public void setPricelineUid(String pricelineUid) {
        this.pricelineUid = pricelineUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
