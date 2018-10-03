package com.example.lawrenjuip.imageapp.apiservices;

import android.util.Log;

import com.example.lawrenjuip.imageapp.models.Image;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.lawrenjuip.imageapp.utils.Constants.CLIENT_ID;

public class ImageApi {
    ImageRestClient imageRestClient = ImgurService.getRetrofit(ImageRestClient.class);

    public void deleteImage(RestCallback<String> callback, String deleteImageHash){
        try{
            Response response = imageRestClient.deleteImage(deleteImageHash);
            callback.onResponse(response.toString());
        }catch(Exception e){
            callback.onError();
        }
    }

    public void uploadImage(final RestCallback<Image> callback, File imageFile){
        try{
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
//            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "portrait_placeholder", requestFile);
            imageRestClient.uploadImage("Client-ID " + CLIENT_ID, requestFile).enqueue(new Callback<Image>() {
                @Override
                public void onResponse(Call<Image> call, Response<Image> response) {
                    Log.d("Vegetable ", "We hit on response!");
                    callback.onResponse(response.body());
                }

                @Override
                public void onFailure(Call<Image> call, Throwable t) {
                    callback.onError();
                    Log.d("Tomato ", "We hit on failure");
                    t.printStackTrace();
                }
            });
        }catch(Exception e){
            Log.d("Potato ", e.getMessage());
        }
    }

    public void getImage(RestCallback<Image> callback, String imageHash){
        try{
//            Image response = imageRestClient.getImage(imageHash);
//            callback.onResponse(response);
        }catch(Exception e){
            callback.onError();
        }
    }
}
