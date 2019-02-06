package com.example.lawrenjuip.imageapp.presenters;

import com.example.lawrenjuip.imageapp.models.SavedImage;

import java.util.List;

import io.reactivex.Observable;

public interface ImageStorage {
    List<SavedImage> loadImages();
    void saveImages(List<SavedImage> imagesToSave);
    Observable<List<SavedImage>> images();
}
