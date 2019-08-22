
package com.example.igor.restaurantmobile.BillList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bill {

    @SerializedName("ClientCode")
    @Expose
    private String clientCode;
    @SerializedName("ClientName")
    @Expose
    private String clientName;
    @SerializedName("ClientUid")
    @Expose
    private String clientUid;
    @SerializedName("Lines")
    @Expose
    private List<BillsLine> lines = null;
    @SerializedName("Number")
    @Expose
    private Integer number;
    @SerializedName("Sum")
    @Expose
    private Double sum;
    @SerializedName("SumAfterDiscount")
    @Expose
    private Double sumAfterDiscount;
    @SerializedName("TableUid")
    @Expose
    private String tableUid;
    @SerializedName("Uid")
    @Expose
    private String uid;

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientUid() {
        return clientUid;
    }

    public void setClientUid(String clientUid) {
        this.clientUid = clientUid;
    }

    public List<BillsLine> getLines() {
        return lines;
    }

    public void setLines(List<BillsLine> lines) {
        this.lines = lines;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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

    public String getTableUid() {
        return tableUid;
    }

    public void setTableUid(String tableUid) {
        this.tableUid = tableUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
