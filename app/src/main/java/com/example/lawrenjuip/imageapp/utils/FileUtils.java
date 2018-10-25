package com.example.lawrenjuip.imageapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lawrenjuip.imageapp.adapters.ImageAdapter;
import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.lawrenjuip.imageapp.utils.Constants.SHARED_PREFERENCES_KEY;

public final class FileUtils {

    public static List<SavedImage> loadExistingImages(SharedPreferences preferences){
        List<SavedImage> savedImageList = new ArrayList<>();
        Moshi moshi = new Moshi.Builder().build();
        Type listOfSavedImages = Types.newParameterizedType(List.class, SavedImage.class);
        JsonAdapter<List<SavedImage>> savedImageJsonAdapter = moshi.adapter(listOfSavedImages);

        //get any existing saved images
        if (preferences.contains(SHARED_PREFERENCES_KEY)){
            String json = preferences.getString(SHARED_PREFERENCES_KEY, null);
            try {
                savedImageList = savedImageJsonAdapter.fromJson(json);
            } catch (IOException ioe) {
                Log.d("Loading images ", ioe.getMessage());
            }
        }

        return savedImageList;
    }

    public static void createImageFileFromAssets(InputStream inputStream, Context context) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = inputStream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, size);
            }
            inputStream.close();

            FileOutputStream fileOutputStream = context.openFileOutput("catPlaceholderImage.png", Context.MODE_PRIVATE);
            buffer = byteArrayOutputStream.toByteArray();
            fileOutputStream.write(buffer);
            fileOutputStream.close();
        } catch (Exception e) {
            Log.d("write to file ", e.getMessage());
        }
    }

    public static List<SavedImage> saveImages(List<SavedImage> imageList, SharedPreferences preferences){
        JsonAdapter<List<SavedImage>> savedImageJsonAdapter = getJsonAdapter();
        String json = savedImageJsonAdapter.toJson(imageList);
        preferences.edit().putString(SHARED_PREFERENCES_KEY, json).apply();
        return imageList;
    }

    public static JsonAdapter<List<SavedImage>> getJsonAdapter(){
        Moshi moshi = new Moshi.Builder().build();
        Type listOfSavedImages = Types.newParameterizedType(List.class, SavedImage.class);
        return moshi.adapter(listOfSavedImages);
    }
}
