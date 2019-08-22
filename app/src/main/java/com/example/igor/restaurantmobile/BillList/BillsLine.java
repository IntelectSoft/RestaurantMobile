
package com.example.igor.restaurantmobile.BillList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BillsLine {

    @SerializedName("AssortimentUid")
    @Expose
    private String assortimentUid;
    @SerializedName("Comments")
    @Expose
    private List<String> comments = null;
    @SerializedName("Count")
    @Expose
    private Double count;
    @SerializedName("KitUid")
    @Expose
    private String kitUid;
    @SerializedName("PriceLineUid")
    @Expose
    private String priceLineUid;
    @SerializedName("Sum")
    @Expose
    private Double sum;
    @SerializedName("SumAfterDiscount")
    @Expose
    private Double sumAfterDiscount;
    @SerializedName("Uid")
    @Expose
    private String uid;

    public String getAssortimentUid() {
        return assortimentUid;
    }

    public void setAssortimentUid(String assortimentUid) {
        this.assortimentUid = assortimentUid;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public String getKitUid() {
        return kitUid;
    }

    public void setKitUid(String kitUid) {
        this.kitUid = kitUid;
    }

    public String getPriceLineUid() {
        return priceLineUid;
    }

    public void setPriceLineUid(String priceLineUid) {
        this.priceLineUid = priceLineUid;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Double getSumAfterDiscount() {
        return sumAfterDiscount;
    }

    public void setSumAfterDiscount(Double sumAfterDiscount) {
        this.sumAfterDiscount = sumAfterDiscount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
