package com.example.igor.restaurantmobile;

import android.app.Application;

import com.example.igor.restaurantmobile.AssortimentList.Assortiment;
import com.example.igor.restaurantmobile.AssortimentList.AssortmentService;
import com.example.igor.restaurantmobile.AssortimentList.ClosureType;
import com.example.igor.restaurantmobile.AssortimentList.Comments;
import com.example.igor.restaurantmobile.AssortimentList.KitMember;
import com.example.igor.restaurantmobile.AssortimentList.Table;
import com.example.igor.restaurantmobile.CreateNewBill.NewBill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
    public static final String mMapAssortmentIsFolder = "AssortmentIsFolder";
    public static final String mMapAssortmentParenGuid = "AssortmentParenGuid";
    public static final String mMapAssortmentGuid = "AssortmentGuid";
    public static final String mMapAssortmentKitMembers = "AssortmentKitMembers";
    public static final String mMapCommentPriceLineGuid = "AssortmentComments";
    public static final String mMapCommentName = "CommentName";
    public static final String mMapCommentGuid = "CommentGuid";
    public static final String mMapAssortmentPriceLineGuid = "AssortmentLineGuid";
    public static final String mNewBillGuid = "NewBill";
    public static final String mGuidZero = "00000000-0000-0000-0000-000000000000";
    public static final String mNewBillTableGuid = "TableGuidNewBill";
    public static final String mIPConnect = "IP";
    public static final String mPortConnect = "Port";
    public static final String mDeviceID = "ID_Device";
    public static final String mSaveOrderIntent = "SavedOrder";


    List<Assortiment> assortmentList ;
    List<Table> tableList;
    List<ClosureType> closureTypeLists;
    List<Comments> commentsLists;
    NewBill mNewBill;

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

    public String getCommentName (String commentId){
        String commentName = null;
        for (Comments comment:commentsLists) {
            String assortmentGuid = comment.getUid();
            if(assortmentGuid.equals(commentId)){
                commentName = comment.getComment();
                break;
            }
        }
        return commentName;
    }

    public ArrayList<HashMap<String, Object>> getAssortmentFromParent(String parentID){
        ArrayList<HashMap<String, Object>> assortment_list = new ArrayList<>();
        for (Assortiment assortment:assortmentList) {
            String parentGUID = assortment.getParentUid();
            if (parentGUID.equals(parentID)){
                HashMap<String, Object> assortmentMap = new HashMap<>();
                boolean isFolder = assortment.getIsFolder();
                if(isFolder){
                    assortmentMap.put(mMapAssortmentIsFolder,isFolder);
                    assortmentMap.put(mMapAssortmentName, assortment.getName());
                    assortmentMap.put(mMapAssortmentGuid,assortment.getUid());
                    assortmentMap.put(mMapAssortmentParenGuid,assortment.getParentUid());
                    assortmentMap.put(mMapAssortmentIcon,R.mipmap.folder);
                    assortment_list.add(assortmentMap);
                }
                else{
                    assortmentMap.put(mMapAssortmentIsFolder,isFolder);
                    assortmentMap.put(mMapAssortmentName, assortment.getName());
                    assortmentMap.put(mMapAssortmentGuid,assortment.getUid());
                    assortmentMap.put(mMapAssortmentParenGuid,assortment.getParentUid());
                    assortmentMap.put(mMapAssortmentPrice,assortment.getPrice());
                    assortmentMap.put(mMapAssortmentPriceLineGuid,assortment.getPricelineUid());
                    assortmentMap.put(mMapAssortmentIcon,R.drawable.asl901);
                    assortment_list.add(assortmentMap);
                }
            }
        }
        SortAssortmentList(assortment_list);
        return assortment_list;
    }

    public ArrayList<HashMap<String, Object>> getAssortmentFromName(String name){
        ArrayList<HashMap<String, Object>> assortment_list = new ArrayList<>();
        for (Assortiment assortment:assortmentList) {
            String nameAssortment = assortment.getName().toLowerCase();
            if (nameAssortment.equals(name.toLowerCase())){
                HashMap<String, Object> assortmentMap = new HashMap<>();
                boolean isFolder = assortment.getIsFolder();
                if(isFolder){
                    assortmentMap.put(mMapAssortmentIsFolder,isFolder);
                    assortmentMap.put(mMapAssortmentName, assortment.getName());
                    assortmentMap.put(mMapAssortmentGuid,assortment.getUid());
                    assortmentMap.put(mMapAssortmentParenGuid,assortment.getParentUid());
                    assortmentMap.put(mMapAssortmentIcon,R.mipmap.folder);
                    assortment_list.add(assortmentMap);
                }
                else{
                    assortmentMap.put(mMapAssortmentIsFolder,isFolder);
                    assortmentMap.put(mMapAssortmentName, assortment.getName());
                    assortmentMap.put(mMapAssortmentGuid,assortment.getUid());
                    assortmentMap.put(mMapAssortmentParenGuid,assortment.getParentUid());
                    assortmentMap.put(mMapAssortmentPrice,assortment.getPrice()+" lei");
                    assortmentMap.put(mMapAssortmentPriceLineGuid,assortment.getPricelineUid());
                    assortmentMap.put(mMapAssortmentIcon,R.drawable.asl901);
                    assortment_list.add(assortmentMap);
                }
            }
        }
        SortAssortmentList(assortment_list);
        return assortment_list;
    }

    public Assortiment getAssortmentFromID(String id){
       Assortiment assortiment = null;
        for (Assortiment assortment:assortmentList) {
            String idAssortment = assortment.getUid();
            if (idAssortment.equals(id)){
               assortiment = assortment;
               break;

            }
        }
        return assortiment;
    }

    public void setNewBill (NewBill bill){
        this.mNewBill = bill;
    }
    public NewBill getNewBill (){
        return mNewBill;
    }


    private static void SortAssortmentList(ArrayList<HashMap<String, Object>> asl_list) {
        Collections.sort(asl_list, new Comparator<HashMap<String, Object>>() {

            public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

                String xy1 = String.valueOf(o1.get(mMapAssortmentIsFolder));
                String xy2 = String.valueOf(o2.get(mMapAssortmentIsFolder));
                int sComp = xy2.compareTo(xy1);

                if (sComp != 0) {
                    return sComp;
                } else {
                    String x1 = o1.get(mMapAssortmentName).toString();
                    String x2 = o2.get(mMapAssortmentName).toString();
                    return x1.compareTo (x2);
                }
            }});
    }
}
