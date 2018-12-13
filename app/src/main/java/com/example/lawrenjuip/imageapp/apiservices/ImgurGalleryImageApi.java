package com.example.lawrenjuip.imageapp.apiservices;

import android.util.Log;

import com.example.lawrenjuip.imageapp.models.Image;
import com.example.lawrenjuip.imageapp.presenters.GalleryPresenter;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.lawrenjuip.imageapp.utils.Constants.CLIENT_ID;

public class ImgurGalleryImageApi implements GalleryPresenter.ImageApi {
    ImageRestClient imageRestClient = ImgurService.getRetrofit(ImageRestClient.class);

    public void uploadImage(final RestCallback<Image> callback, File imageFile){
        try{
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imageRestClient.uploadImage("Client-ID " + CLIENT_ID, requestFile).enqueue(new Callback<Image>() {
                @Override
                public void onResponse(Call<Image> call, Response<Image> response) {
                    callback.onResponse(response.body());
                }

                @Override
                public void onFailure(Call<Image> call, Throwable t) {
                    callback.onError();
                    t.printStackTrace();
                }
            });
        }catch(Exception e){
            Log.d("uploadImage ", e.getMessage());
        }
    }
}
