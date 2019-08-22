package com.example.igor.restaurantmobile.Bill;

import com.example.igor.restaurantmobile.BillList.BillListResponseService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServicePrintBill {
    @GET("/MobileCash/json/PrintBill")
      Call<BillListResponseService> getPrintBill(@Query("deviceId") String param1, @Query("billUid") String param2);
}
