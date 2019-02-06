package com.example.lawrenjuip.imageapp.views;

import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.Image;

import java.io.File;

public interface ImageUploadApi {
        void uploadImage(RestCallback<Image> callback, File imageFile);
}
