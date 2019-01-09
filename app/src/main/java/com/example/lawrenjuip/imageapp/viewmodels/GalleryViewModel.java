package com.example.lawrenjuip.imageapp.viewmodels;

import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.example.lawrenjuip.imageapp.presenters.ImageStorage;

import java.util.List;

import io.reactivex.Observable;

public class GalleryViewModel {
    public Observable<String> screenTitle = Observable.just("NewTitle");
    public List<SavedImage> savedImageList;
    private ImageStorage imageStorage;

    public GalleryViewModel(ImageStorage imageStorage){
        this.imageStorage = imageStorage;
        savedImageList = imageStorage.loadImages();
    }

}
