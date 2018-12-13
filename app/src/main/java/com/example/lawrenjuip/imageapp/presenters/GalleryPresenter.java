package com.example.lawrenjuip.imageapp.presenters;

import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.Image;
import com.example.lawrenjuip.imageapp.models.SavedImage;

import java.io.File;
import java.util.List;

public class GalleryPresenter {
    private ImageStorage imageStorage;
    private ImageApi imageApi;
    private GalleryImageView galleryImageView;

    public GalleryPresenter(GalleryImageView galleryImageView, ImageStorage imageStorage, ImageApi imageApi){
        this.galleryImageView = galleryImageView;
        this.imageStorage = imageStorage;
        this.imageApi = imageApi;
    }

    public void uploadImage(File imageFile){
        imageApi.uploadImage(new RestCallback<Image>() {
            @Override
            public void onResponse(Image response) {
                SavedImage image = new SavedImage(response.data.getDeleteHash(), response.data.getLink());
                List<SavedImage> savedImageList = imageStorage.loadImages();

                savedImageList.add(image);
                imageStorage.saveImages(savedImageList);
                galleryImageView.updateAdapter(savedImageList);
            }

            @Override
            public void onError() {

            }
        }, imageFile);
    }

    public interface ImageApi {
        void uploadImage(RestCallback<Image> callback, File imageFile);
    }

    public interface GalleryImageView {
        void updateAdapter(List<SavedImage> savedImageList);
    }
}
