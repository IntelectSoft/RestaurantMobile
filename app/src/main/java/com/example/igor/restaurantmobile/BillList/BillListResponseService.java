
package com.example.igor.restaurantmobile.BillList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BillListResponseService {

    @SerializedName("BillsList")
    @Expose
    private List<Bill> billsList = null;
    @SerializedName("Result")
    @Expose
    private Integer result;
    @SerializedName("ResultMessage")
    @Expose
    private String resultMessage;

    public List<Bill> getBillsList() {
        return billsList;
    }

    public void setBillsList(List<Bill> billsList) {
        this.billsList = billsList;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

}
