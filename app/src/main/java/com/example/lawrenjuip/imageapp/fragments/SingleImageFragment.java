package com.example.lawrenjuip.imageapp.fragments;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lawrenjuip.imageapp.R;
import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

public class SingleImageFragment extends Fragment {
    private SavedImage savedImage;
    private Drawable placeholderImage;

    public static SingleImageFragment newInstance(){
        SingleImageFragment fragment = new SingleImageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            savedImage = savedInstanceState.getParcelable("saved_image");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_single_image, container, false);
        ImageView imageView = view.findViewById(R.id.beautifulImage);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            savedImage = bundle.getParcelable("saved_image");

            placeholderImage = createPlaceHolder();

            Picasso.get()
                    .load(savedImage.getImageUrl())
                    .error(R.drawable.ic_image_error)
                    .placeholder(placeholderImage)
                    .into(imageView);
        }

        return view;
    }

    private Drawable createPlaceHolder(){
        AssetManager assetManager = getContext().getAssets();
        InputStream inputStream = null;
        Drawable drawable = null;

        try{
            inputStream = assetManager.open("portrait_placeholder.png");
            drawable = Drawable.createFromStream(inputStream, null);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }finally {
            if(inputStream == null){
                try{
                    inputStream.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }

        return drawable;
    }

}
