
package com.example.igor.restaurantmobile.AssortimentList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssortmentService {

    @SerializedName("AssortimentList")
    @Expose
    private List<Assortiment> assortimentList = null;
    @SerializedName("ClosureTypeList")
    @Expose
    private List<ClosureType> closureTypeList = null;
    @SerializedName("CommentsList")
    @Expose
    private List<Comments> commentsList = null;
    @SerializedName("Result")
    @Expose
    private Integer result;
    @SerializedName("TableList")
    @Expose
    private List<Table> tableList = null;

    public List<Assortiment> getAssortimentList() {
        return assortimentList;
    }

    public void setAssortimentList(List<Assortiment> assortimentList) {
        this.assortimentList = assortimentList;
    }

    public List<ClosureType> getClosureTypeList() {
        return closureTypeList;
    }

    public void setClosureTypeList(List<ClosureType> closureTypeList) {
        this.closureTypeList = closureTypeList;
    }

    public List<Comments> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<Comments> commentsList) {
        this.commentsList = commentsList;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public List<Table> getTableList() {
        return tableList;
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }

}
