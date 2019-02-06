package com.example.lawrenjuip.imageapp.views;

import com.example.lawrenjuip.imageapp.models.SavedImage;

import java.util.List;

public interface GalleryImageView {
        void updateAdapter(List<SavedImage> savedImageList);
    }
