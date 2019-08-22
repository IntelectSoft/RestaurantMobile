package com.example.igor.restaurantmobile.Bill;

import com.example.igor.restaurantmobile.BillList.BillListResponseService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceCloseBill {
    @GET("/MobileCash/json/CloseBill")
      Call<BillListResponseService> getCloseBill (@Query("deviceId") String param1, @Query("billUid") String param2,@Query("closeTypeUid") String param3);
}
