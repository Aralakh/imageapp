package com.example.lawrenjuip.imageapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.example.lawrenjuip.imageapp.presenters.ImageStorage;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class SharedPrefsImageStorage implements ImageStorage {
    private SharedPreferences preferences;
    private BehaviorSubject<List<SavedImage>> imageSubject = BehaviorSubject.create();

    public SharedPrefsImageStorage(Activity activity){

        preferences = activity.getPreferences(Context.MODE_PRIVATE);
        imageSubject.onNext(loadImages());
    }

    @Override
    public List<SavedImage> loadImages() {
        List<SavedImage> saveImagesList =  FileUtils.loadExistingImages(preferences);
        return saveImagesList;
    }

    @Override
    public void saveImages(List<SavedImage> imagesToSave) {
        FileUtils.saveImages(imagesToSave, preferences);
        imageSubject.onNext(imagesToSave);
    }

    @Override
    public Observable<List<SavedImage>> images(){

        return imageSubject;
    }
}
