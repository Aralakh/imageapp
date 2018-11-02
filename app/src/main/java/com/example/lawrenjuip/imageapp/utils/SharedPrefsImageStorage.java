package com.example.lawrenjuip.imageapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.example.lawrenjuip.imageapp.presenters.SingleImagePresenter;

import java.util.List;

public class SharedPrefsImageStorage implements SingleImagePresenter.ImageStorage {
    private SharedPreferences preferences;

    public SharedPrefsImageStorage(Activity activity){

        preferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public List<SavedImage> loadImages() {
        List<SavedImage> saveImagesList =  FileUtils.loadExistingImages(preferences);
        return saveImagesList;
    }

    @Override
    public void saveImages(List<SavedImage> imagesToSave) {
        FileUtils.saveImages(imagesToSave, preferences);
    }
}
