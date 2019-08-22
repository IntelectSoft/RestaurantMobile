package com.example.igor.restaurantmobile.AssortimentList;

import com.example.igor.restaurantmobile.BillList.BillListResponseService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceGetAssortmentList {
    @GET("/MobileCash/json/GetAssortimentList")
      Call<AssortmentService> getAssortmentList(@Query("deviceId") String param1);
}
