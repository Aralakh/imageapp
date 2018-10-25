package com.example.lawrenjuip.imageapp.fragments;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lawrenjuip.imageapp.R;
import com.example.lawrenjuip.imageapp.apiservices.ImageApi;
import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.example.lawrenjuip.imageapp.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;

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

        setHasOptionsMenu(true);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_single_image_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.edit:
                //swap textviews to edit texts, change behavior of submit button to save changes
                //maybe show a pop up dialog to let user know, add a cancel button to not save changes?
                return true;
            case R.id.delete:
                //delete image, change behavior of submit button to delete.
                //same as above
                ImageApi imageApi = new ImageApi();
                imageApi.deleteImage(new RestCallback<ResponseBody>() {
                    @Override
                    public void onResponse(ResponseBody response) {
                        SharedPreferences preferences = getActivity().getPreferences(getContext().MODE_PRIVATE);
                        List<SavedImage> saveImagesList =  FileUtils.loadExistingImages(preferences);
                        saveImagesList.remove(savedImage);
                        FileUtils.saveImages(saveImagesList, preferences);
                        getActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onError() {
                        //let them know it failed
                    }
                }, savedImage.getDeleteHash());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
