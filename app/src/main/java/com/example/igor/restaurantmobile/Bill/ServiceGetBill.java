package com.example.igor.restaurantmobile.Bill;

import com.example.igor.restaurantmobile.BillList.BillListResponseService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceGetBill {
    @GET("/MobileCash/json/GetBill")
      Call<BillListResponseService> getBill(@Query("deviceId") String param1, @Query("billUid") String param2);
}
