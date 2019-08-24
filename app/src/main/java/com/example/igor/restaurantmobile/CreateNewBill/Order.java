
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
    private String count;
    @SerializedName("Comments")
    @Expose
    private List<String> comments = null;

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

}
