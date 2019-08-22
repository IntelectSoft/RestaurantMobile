
package com.example.igor.restaurantmobile.AssortimentList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Assortiment {

    @SerializedName("AllowNonIntegerSale")
    @Expose
    private Boolean allowNonIntegerSale;
    @SerializedName("AllowSaleOnlyAsKitMember")
    @Expose
    private Boolean allowSaleOnlyAsKitMember;
    @SerializedName("Comments")
    @Expose
    private List<String> comments = null;
    @SerializedName("IsFolder")
    @Expose
    private Boolean isFolder;
    @SerializedName("KitMembers")
    @Expose
    private List<KitMember> kitMembers = null;
    @SerializedName("MandatoryComment")
    @Expose
    private Boolean mandatoryComment;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("ParentUid")
    @Expose
    private String parentUid;
    @SerializedName("Price")
    @Expose
    private Double price;
    @SerializedName("PricelineUid")
    @Expose
    private String pricelineUid;
    @SerializedName("Uid")
    @Expose
    private String uid;

    public Boolean getAllowNonIntegerSale() {
        return allowNonIntegerSale;
    }

    public void setAllowNonIntegerSale(Boolean allowNonIntegerSale) {
        this.allowNonIntegerSale = allowNonIntegerSale;
    }

    public Boolean getAllowSaleOnlyAsKitMember() {
        return allowSaleOnlyAsKitMember;
    }

    public void setAllowSaleOnlyAsKitMember(Boolean allowSaleOnlyAsKitMember) {
        this.allowSaleOnlyAsKitMember = allowSaleOnlyAsKitMember;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public Boolean getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(Boolean isFolder) {
        this.isFolder = isFolder;
    }

    public List<KitMember> getKitMembers() {
        return kitMembers;
    }

    public void setKitMembers(List<KitMember> kitMembers) {
        this.kitMembers = kitMembers;
    }

    public Boolean getMandatoryComment() {
        return mandatoryComment;
    }

    public void setMandatoryComment(Boolean mandatoryComment) {
        this.mandatoryComment = mandatoryComment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
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
