package com.example.lawrenjuip.imageapp.models;

public class SavedImage {
    String deleteHash;
    String imageUrl;

    public SavedImage(String deleteHash, String imageUrl) {
        this.deleteHash = deleteHash;
        this.imageUrl = imageUrl;
    }
}
