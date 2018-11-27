package com.example.lawrenjuip.imageapp.presenters;

import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.SavedImage;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class SingleImagePresenter {
   private SingleImageView singleImageView;

    public SingleImagePresenter(SingleImageView singleImageView){
        this.singleImageView = singleImageView;
    }

    public void deleteImage(final SavedImage savedImage, final ImageStorage imageStorage, ImageApi imageApi){
        imageApi.deleteImage(new RestCallback<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                List<SavedImage> saveImagesList =  imageStorage.loadImages();
                List<SavedImage> updatedImagesList = new ArrayList<>();
                for(SavedImage image: saveImagesList){
                    if(!savedImage.equals(image)){
                        updatedImagesList.add(image);
                    }
                }
                imageStorage.saveImages(updatedImagesList);
                singleImageView.goBackOneScreen();
            }

            @Override
            public void onError() {
                //let them know it failed
            }
        }, savedImage.getDeleteHash());
    }

    public interface ImageStorage {
        List<SavedImage> loadImages();
        void saveImages(List<SavedImage> imagesToSave);
    }

    public interface SingleImageView {
        void goBackOneScreen();
    }

    public interface ImageApi {
        void deleteImage(RestCallback<ResponseBody> callback, String deleteHash);
    }
}
