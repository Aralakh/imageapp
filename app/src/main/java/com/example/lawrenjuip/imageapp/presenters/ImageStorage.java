package com.example.lawrenjuip.imageapp.presenters;

import com.example.lawrenjuip.imageapp.models.SavedImage;

import java.util.List;

public interface ImageStorage {
    List<SavedImage> loadImages();
    void saveImages(List<SavedImage> imagesToSave);
}
