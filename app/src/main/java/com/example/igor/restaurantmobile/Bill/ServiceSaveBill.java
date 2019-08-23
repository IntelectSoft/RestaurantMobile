package com.example.igor.restaurantmobile.Bill;

import com.example.igor.restaurantmobile.BillList.BillListResponseService;
import com.example.igor.restaurantmobile.CreateNewBill.NewBill;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceSaveBill {
    @POST("/MobileCash/json/AddOrders")
      Call<BillListResponseService> saveBill(@Body NewBill bill);
}
