package com.example.lawrenjuip.imageapp.apiservices;

import android.util.Log;

import com.example.lawrenjuip.imageapp.presenters.SingleImagePresenter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.lawrenjuip.imageapp.utils.Constants.CLIENT_ID;

public class ImgurSingleImageApi implements SingleImagePresenter.ImageApi {
    ImageRestClient imageRestClient = ImgurService.getRetrofit(ImageRestClient.class);

    public void deleteImage(final RestCallback<ResponseBody> callback, String deleteImageHash){
        try{
            imageRestClient.deleteImage("Client-ID " + CLIENT_ID, deleteImageHash).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        callback.onResponse(response.body());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onError();
                    t.printStackTrace();
                }
            });
        }catch(Exception e){
            Log.d("deleteImage ", e.getMessage());
        }
    }

    public void updateImage(final RestCallback<ResponseBody> callback, String deleteImageHash, String title){
        try{
            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), title);
            imageRestClient.updateImage("Client-ID " + CLIENT_ID, deleteImageHash, requestBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        callback.onResponse(response.body());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onError();
                    t.printStackTrace();
                }
            });
        }catch(Exception e){
            Log.d("updateImage ", e.getMessage());
        }
    }
}
