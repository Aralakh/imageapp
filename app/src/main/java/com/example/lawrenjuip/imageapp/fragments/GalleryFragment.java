package com.example.lawrenjuip.imageapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lawrenjuip.imageapp.R;
import com.example.lawrenjuip.imageapp.adapters.ImageAdapter;
import com.example.lawrenjuip.imageapp.apiservices.ImageApi;
import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.Image;
import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    private RecyclerView imageRecyclerView;
    private GridLayoutManager imageGridLayoutManager;
    private ImageAdapter imageAdapter;
    private static final String SHARED_PREFERENCES_KEY = "savedImages";

    //Update the adapter to handle SavedImages rather than Images; there should be no need to save a list here to operate on. Picasso should operate off the URL.
    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AssetManager assetManager = getContext().getAssets();
        InputStream is = null;
        try {
            is = assetManager.open("portrait_placeholder.png");
            createImageFileFromAssets(is);
            File imageFile = new File(getContext().getFilesDir(), "placeholderImage.png");

            ImageApi imageApi = new ImageApi();
            imageApi.uploadImage(new RestCallback<Image>() {
                @Override
                public void onResponse(Image response) {
                    //save response
                    SavedImage image = new SavedImage(response.data.getDeleteHash(), response.data.getLink());
                    List<SavedImage> savedImageList = new ArrayList<>();
                    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
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

                    savedImageList.add(image);
                    String json = savedImageJsonAdapter.toJson(savedImageList);
                    preferences.edit().putString(SHARED_PREFERENCES_KEY, json).apply();
                }

                @Override
                public void onError() {
                    Log.d("OnResponse error ", "something went wrong");

                }
            }, imageFile);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (is == null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View galleryView = inflater.inflate(R.layout.fragment_gallery, container, false);

        imageRecyclerView = galleryView.findViewById(R.id.image_recycler_view);
        imageGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        imageRecyclerView.setLayoutManager(imageGridLayoutManager);

        return galleryView;
    }

    public void createImageFileFromAssets(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = inputStream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, size);
            }
            inputStream.close();

            FileOutputStream fileOutputStream = getContext().openFileOutput("placeholderImage.png", Context.MODE_PRIVATE);
            buffer = byteArrayOutputStream.toByteArray();
            fileOutputStream.write(buffer);
            fileOutputStream.close();
        } catch (Exception e) {
            Log.d("write to file ", e.getMessage());
        }
    }
}
