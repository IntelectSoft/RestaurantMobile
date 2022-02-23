package com.example.igor.restaurantmobile.BrockerServiceUtils;

import com.example.igor.restaurantmobile.BrockerServiceUtils.Body.InformationData;
import com.example.igor.restaurantmobile.BrockerServiceUtils.Body.SendGetURI;
import com.example.igor.restaurantmobile.BrockerServiceUtils.Body.SendRegisterApplication;
import com.example.igor.restaurantmobile.BrockerServiceUtils.Results.ErrorMessage;
import com.example.igor.restaurantmobile.BrockerServiceUtils.Results.GetNews;
import com.example.igor.restaurantmobile.BrockerServiceUtils.Results.RegisterApplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BrockerServiceAPI {
    @GET("/ISLicenseService/json/Ping")
    Call<Boolean> ping ();

    @POST("/ISLicenseService/json/RegisterApplication")
    Call<RegisterApplication> registerApplicationCall(@Body SendRegisterApplication bodyRegisterApp);

    @POST("/ISLicenseService/json/GetURI")
    Call<RegisterApplication> getURICall(@Body SendGetURI sendGetURI);

    @POST("/ISLicenseService/json/UpdateDiagnosticInformation")
    Call<ErrorMessage> updateDiagnosticInfo (@Body InformationData informationData);

    @GET("/ISLicenseService/json/GetNews")
    Call<GetNews> getNews (@Query("ID") int id, @Query("ProductType") int productType);
}
