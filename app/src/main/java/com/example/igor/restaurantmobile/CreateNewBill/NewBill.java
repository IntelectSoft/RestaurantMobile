
package com.example.igor.restaurantmobile.CreateNewBill;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewBill {

    @SerializedName("deviceId")
    @Expose
    private String deviceId;
    @SerializedName("billUid")
    @Expose
    private String billUid;
    @SerializedName("tableUid")
    @Expose
    private String tableUid;
    @SerializedName("orders")
    @Expose
    private List<Order> orders = null;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBillUid() {
        return billUid;
    }

    public void setBillUid(String billUid) {
        this.billUid = billUid;
    }

    public String getTableUid() {
        return tableUid;
    }

    public void setTableUid(String tableUid) {
        this.tableUid = tableUid;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

}
