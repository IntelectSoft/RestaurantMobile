package com.example.igor.restaurantmobile.BillList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceBillList {
    @GET("/MobileCash/json/GetBillsList")
      Call<BillListResponseService> getBillsList(@Query("deviceId") String param1, @Query("includeLines") boolean param2);
}
