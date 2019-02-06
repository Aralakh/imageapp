package com.example.lawrenjuip.imageapp.apiservices;

import com.example.lawrenjuip.imageapp.models.Image;
import com.example.lawrenjuip.imageapp.views.ImageUploadApi;

import java.io.File;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;


import static com.example.lawrenjuip.imageapp.utils.Constants.CLIENT_ID;

public class ImgurGalleryImageApi implements ImageUploadApi {
    ImageRestClient imageRestClient = ImgurService.getRetrofit(ImageRestClient.class);

    public Observable<Image> uploadImage(File imageFile){
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            return imageRestClient.uploadImage("Client-ID " + CLIENT_ID, requestFile);
        }
}
