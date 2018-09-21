package com.example.lawrenjuip.imageapp.fragments;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    private RecyclerView imageRecyclerView;
    private GridLayoutManager imageGridLayoutManager;
    private List<Image> imageList = new ArrayList<>();
    private ImageAdapter imageAdapter;

    public static GalleryFragment newInstance(){
        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ImageApi imageApi =  new ImageApi();
        imageApi.uploadImage(new RestCallback<Image>() {
            @Override
            public void onResponse(Image response) {
                Log.d("Image url ", response.getLink());
            }

            @Override
            public void onError() {
                Log.d("Error ", "something went wrong");

            }
        }, new File("/Users/lawrenjuip/AndroidStudioProjects/ImageApp/app/src/main/assets/portrait_placeholder.png"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View galleryView = inflater.inflate(R.layout.fragment_gallery, container, false);

        imageRecyclerView = galleryView.findViewById(R.id.image_recycler_view);
        imageGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        imageRecyclerView.setLayoutManager(imageGridLayoutManager);

        return galleryView;
    }

}
