package com.example.lawrenjuip.imageapp.views;

import com.example.lawrenjuip.imageapp.models.Image;

import java.io.File;

import io.reactivex.Observable;

public interface ImageUploadApi {
        Observable<Image> uploadImage(File imageFile);
}
