package com.example.lawrenjuip.imageapp.viewmodels;

import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.example.lawrenjuip.imageapp.presenters.ImageStorage;
import com.example.lawrenjuip.imageapp.utils.BaseObserver;
import com.example.lawrenjuip.imageapp.views.ImageEditApi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SingleImageViewModel {
    private ImageStorage imageStorage;
    private ImageEditApi imageApi;
    public Observable<List<SavedImage>> savedImageList;

    public SingleImageViewModel(ImageStorage imageStorage, ImageEditApi imageApi){
        this.imageApi = imageApi;
        this.imageStorage = imageStorage;
        savedImageList = imageStorage.images();
    }

    public void deleteImage(final SavedImage savedImage){
        imageApi.deleteImage(savedImage.getDeleteHash())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            List<SavedImage> updatedImagesList = new ArrayList<>();
                            List<SavedImage> currentImagesList = new ArrayList<>();
                            if (!savedImageList.toList().blockingGet().isEmpty()) {
                                currentImagesList = savedImageList.toList().blockingGet().get(0);
                            }

                            for (int i = 0; i < currentImagesList.size(); i++) {
                                if (!currentImagesList.get(i).equals(savedImage)) {
                                    updatedImagesList.add(savedImage);
                                }
                            }

                            imageStorage.saveImages(updatedImagesList);
                        }
                    }
                });
    }

    public void updateImage(final SavedImage savedImage){
        imageApi.updateImage(savedImage.getDeleteHash(), savedImage.getTitle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Response<ResponseBody>>(){
                   @Override
                   public void onNext(Response<ResponseBody> response){
                       if(response.isSuccessful()){
                           List<SavedImage> updatedImagesList = new ArrayList<>();
                           List<SavedImage> currentImagesList = new ArrayList<>();
                           if(!savedImageList.toList().blockingGet().isEmpty()){
                               currentImagesList = savedImageList.toList().blockingGet().get(0);
                           }

                           for(int i = 0; i < currentImagesList.size(); i++){
                               if(currentImagesList.get(i).equals(savedImage)){
                                   updatedImagesList.add(savedImage);
                               }else{
                                   updatedImagesList.add(currentImagesList.get(i));
                               }
                           }

                           imageStorage.saveImages(updatedImagesList);
                       }
                   }
                });
    }
}
