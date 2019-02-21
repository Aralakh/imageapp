package com.example.lawrenjuip.imageapp.apiservices;

import com.example.lawrenjuip.imageapp.models.Image;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

interface ImageRestClient {
    @POST("image/{image}/")
    Observable<Image> uploadImage(@Header("Authorization") String auth, @Body RequestBody file);

    @DELETE("image/{imageDeleteHash}")
    Observable<Response<ResponseBody>> deleteImage(@Header("Authorization") String auth, @Path("imageDeleteHash") String imageDeleteHash);

    @GET("image/{imageHash}")
    Observable<Image> getImage(@Path("imageHash") String imageHash);

    @Multipart
    @POST("image/{imageDeleteHash}")
    Observable<Response<ResponseBody>> updateImage(@Header("Authorization") String auth, @Path("imageDeleteHash") String imageDeleteHash, @Part("title") RequestBody title);
}
