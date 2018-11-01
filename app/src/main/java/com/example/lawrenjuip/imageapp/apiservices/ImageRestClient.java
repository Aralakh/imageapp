package com.example.lawrenjuip.imageapp.apiservices;

import com.example.lawrenjuip.imageapp.models.Image;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface ImageRestClient {
    @POST("image/{image}/")
    Call<Image> uploadImage(@Header("Authorization") String auth, @Body RequestBody file);

    @DELETE("image/{imageDeleteHash}")
    Call<ResponseBody> deleteImage(@Header("Authorization") String auth, @Path("imageDeleteHash") String imageDeleteHash);

    @GET("image/{imageHash}")
    Call<Image> getImage(@Path("imageHash") String imageHash);

    @POST("image/{imageDeleteHash}")
    Call<ResponseBody> updateImage(@Header("Authorization") String auth, @Path("imageDeleteHash") String imageDeleteHash, @Body RequestBody title);
}
