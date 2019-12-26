package com.example.igor.restaurantmobile;
/**
 * Created by Igor on 17.12.2019
 */

public class Globals{
    private static Globals instance;

    // Restrict the constructor from being instantiated
    private Globals(){}

    // Global variable
    private int data;



    public void setData(int d){
        this.data=d;
    }
    public int getData(){
        return this.data;
    }





    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
