package com.example.lawrenjuip.imageapp.viewmodels;

import com.example.lawrenjuip.imageapp.models.Image;
import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.example.lawrenjuip.imageapp.presenters.ImageStorage;
import com.example.lawrenjuip.imageapp.utils.BaseObserver;
import com.example.lawrenjuip.imageapp.views.ImageUploadApi;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        imageApi.uploadImage(imageFile).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Image>(){
                     @Override
                     public void onNext(Image image) {
                         SavedImage savedImage = new SavedImage(image.data.getDeleteHash(), image.data.getLink());
                        List<SavedImage> savedImageList = imageStorage.loadImages();

                        savedImageList.add(savedImage);
                        imageStorage.saveImages(savedImageList);
                     }
                 }
        );
    }
}
