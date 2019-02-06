package com.example.lawrenjuip.imageapp.viewmodels;

import android.util.Log;

import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.Image;
import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.example.lawrenjuip.imageapp.presenters.GalleryPresenter;
import com.example.lawrenjuip.imageapp.presenters.ImageStorage;
import com.example.lawrenjuip.imageapp.views.GalleryImageView;
import com.example.lawrenjuip.imageapp.views.ImageUploadApi;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

public class GalleryViewModel {
    private ImageStorage imageStorage;
    private ImageUploadApi imageApi;
    public Observable<String> screenTitle = Observable.just("NewTitle");
    public Observable<List<SavedImage>> savedImageList;

    public GalleryViewModel(ImageStorage imageStorage, ImageUploadApi imageApi){
        this.imageStorage = imageStorage;
        this.imageApi = imageApi;
        savedImageList = imageStorage.images();
    }

    public void uploadImage(File imageFile){
        imageApi.uploadImage(new RestCallback<Image>() {
            @Override
            public void onResponse(Image response) {
                SavedImage image = new SavedImage(response.data.getDeleteHash(), response.data.getLink());
                List<SavedImage> savedImageList = imageStorage.loadImages();

                savedImageList.add(image);
                imageStorage.saveImages(savedImageList);
            }

            @Override
            public void onError() {
                Log.d("Upload image", "Upload failed");
            }
        }, imageFile);
    }
}
