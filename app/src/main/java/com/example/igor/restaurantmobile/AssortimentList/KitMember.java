
package com.example.igor.restaurantmobile.AssortimentList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KitMember {

    @SerializedName("AssortimentList")
    @Expose
    private List<String> assortimentList = null;
    @SerializedName("Mandatory")
    @Expose
    private Boolean mandatory;
    @SerializedName("StepNumber")
    @Expose
    private Integer stepNumber;

    public List<String> getAssortimentList() {
        return assortimentList;
    }

    public void setAssortimentList(List<String> assortimentList) {
        this.assortimentList = assortimentList;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

}
