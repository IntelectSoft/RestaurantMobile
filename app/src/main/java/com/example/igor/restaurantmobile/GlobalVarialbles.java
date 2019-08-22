package com.example.igor.restaurantmobile;

import android.app.Application;

import com.example.igor.restaurantmobile.AssortimentList.Assortiment;
import com.example.igor.restaurantmobile.AssortimentList.AssortmentService;
import com.example.igor.restaurantmobile.AssortimentList.ClosureType;
import com.example.igor.restaurantmobile.AssortimentList.Comments;
import com.example.igor.restaurantmobile.AssortimentList.Table;

import java.util.List;

public class GlobalVarialbles extends Application {
    public static final String mMapBillNumber = "BillNumber";
    public static final String mMapTableName = "TableName";
    public static final String mMapTableGuid = "TableGuid";
    public static final String mMapBillGuid = "BillGuid";
    public static final String mMapBillSumAfterDiscount = "SumAfterDiscount";
    public static final String mMapBillSum= "BillSum";
    public static final String mMapAssortmentName = "AssortmentName";
    public static final String mMapAssortmentCount = "AssortmentCount";
    public static final String mMapAssortmentIcon = "AssortmentIcon";
    public static final String mMapAssortmentPrice = "AssortmentPrice";
    public static final String mNewBillGuid = "NewBill";
    public static final String mGuidZero = "00000000-0000-0000-0000-000000000000";
    public static final String mNewBillTableGuid = "TableGuidNewBill";
    public static final String mIPConnect = "IP";
    public static final String mPortConnect = "Port";
    public static final String mDeviceID = "ID_Device";

    List<Assortiment> assortmentList ;
    List<Table> tableList;
    List<ClosureType> closureTypeLists;
    List<Comments> commentsLists;
    Table mTable;

    public void setAssortmentList (List<Assortiment> assortmentService){
        this.assortmentList = assortmentService;
    }
    public List<Assortiment> getAssortmentList(){
        return assortmentList;
    }

    public void setTableList (List<Table> tableList){
        this.tableList = tableList;
    }
    public List<Table> getTableList (){
        return tableList;
    }

    public void setClosureTypeLists (List<ClosureType> closureTypeLists ){
        this.closureTypeLists = closureTypeLists;
    }
    public List<ClosureType> getClosureTypeLists () {
        return closureTypeLists;
    }

    public void setCommentsLists(List<Comments> commentsLists){
        this.commentsLists = commentsLists;
    }
    public List<Comments> getCommentsLists(){
        return commentsLists;
    }

    public String getTableName (String id){
        String tableName = null;
        for (Table table:tableList){
            String tableID = table.getUid();
            if (tableID.equals(id)){
                tableName = table.getName();
                break;
            }
        }
        return tableName;
    }
    public int getTableListSize (){
        return tableList.size();
    }

    public boolean getAssortmentAllowNonIntegerSale (String assortmentId){
        boolean assortmentNonIntegerSales = false;
        for (Assortiment assortiment:assortmentList) {
            String assortmentGuid = assortiment.getUid();
            if(assortmentGuid.equals(assortmentId)){
                assortmentNonIntegerSales = assortiment.getAllowNonIntegerSale();
                break;
            }
        }
        return assortmentNonIntegerSales;
    }

    public String getAssortmentName (String assortmentId){
        String assortmentName = null;
        for (Assortiment assortiment:assortmentList) {
            String assortmentGuid = assortiment.getUid();
            if(assortmentGuid.equals(assortmentId)){
                assortmentName = assortiment.getName();
                break;
            }
        }
        return assortmentName;
    }

    public double getAssortmentPrice (String assortmentId){
        double assortmentPrice = 0;
        for (Assortiment assortiment:assortmentList) {
            String assortmentGuid = assortiment.getUid();
            if(assortmentGuid.equals(assortmentId)){
                assortmentPrice = assortiment.getPrice();
                break;
            }
        }
        return assortmentPrice;
    }
}
