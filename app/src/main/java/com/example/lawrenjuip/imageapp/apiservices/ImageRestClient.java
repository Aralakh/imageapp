package com.example.lawrenjuip.imageapp.apiservices;

import com.example.lawrenjuip.imageapp.models.Image;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface ImageRestClient {
    @POST("image/{image}/")
    Call<Image> uploadImage(@Header("Authorization") String auth, @Body RequestBody file);

    @DELETE("image/{imageDeleteHash")
    Response deleteImage(@Path("imageDeleteHash") String imageDeleteHash);

    @GET("image/{imageHash}")
    Call<Image> getImage(@Path("imageHash") String imageHash);
}
