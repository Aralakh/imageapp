package com.example.lawrenjuip.imageapp.views;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;


public interface ImageEditApi {
    Observable<Response<ResponseBody>> deleteImage(String deleteImageHash);
    Observable<Response<ResponseBody>> updateImage(String deleteImageHash, String title);
}
