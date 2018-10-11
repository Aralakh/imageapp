package com.example.lawrenjuip.imageapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private File cameraFile;
    private static final String SHARED_PREFERENCES_KEY = "savedImages";
    private static final int REQUEST_TAKE_PICTURE = 1;

    //Update the adapter to handle SavedImages rather than Images; there should be no need to save a list here to operate on. Picasso should operate off the URL.
    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View galleryView = inflater.inflate(R.layout.fragment_gallery, container, false);

        imageRecyclerView = galleryView.findViewById(R.id.image_recycler_view);
        imageGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        imageRecyclerView.setLayoutManager(imageGridLayoutManager);

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        loadExistingImages(preferences);
        return galleryView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_camera, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.camera:
                //launch camera, take picture!
                takePicture();
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_TAKE_PICTURE){
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.lawrenjuip.imageapp.fileprovider", cameraFile);
            uploadImage(cameraFile);
            //should we delete the file after it's uploaded, to clear space? add to trello backlog
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }

    private List<SavedImage> loadExistingImages(SharedPreferences preferences){
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

        updateAdapter(savedImageList);

        return savedImageList;
    }

    private void updateAdapter(List<SavedImage> imageList){
        if(imageAdapter == null){
            imageAdapter = new ImageAdapter(imageList, getContext());
            imageRecyclerView.setAdapter(imageAdapter);
        }else{
            imageAdapter = (ImageAdapter) imageRecyclerView.getAdapter();
            imageAdapter.addAll(imageList);
        }

        imageRecyclerView.getAdapter().notifyDataSetChanged();
    }

    //update this function to take an argument/work with the camera operation
    private void uploadImage(File file){
        AssetManager assetManager = getContext().getAssets();
        InputStream is = null;
        try {
            File imageFile = file;
            if(file == null || !file.exists()) {
                is = assetManager.open("cat_placeholder.jpg");
                createImageFileFromAssets(is);
                imageFile = new File(getContext().getFilesDir(), "catPlaceholderImage.png");
            }
            ImageApi imageApi = new ImageApi();
            imageApi.uploadImage(new RestCallback<Image>() {
                @Override
                public void onResponse(Image response) {
                    //save response
                    SavedImage image = new SavedImage(response.data.getDeleteHash(), response.data.getLink());
                    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    List<SavedImage> savedImageList = loadExistingImages(preferences);

                    savedImageList.add(image);

                    updateAdapter(savedImageList);

                    JsonAdapter<List<SavedImage>> savedImageJsonAdapter = getJsonAdapter();
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
        }
    }

    private void takePicture(){
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String filename = "camera_image_" + System.currentTimeMillis();
        cameraFile = new File(getContext().getFilesDir(), filename);
        Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.lawrenjuip.imageapp.fileprovider", cameraFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo activity : cameraActivities){
            getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        startActivityForResult(cameraIntent, REQUEST_TAKE_PICTURE);

    }

    private JsonAdapter<List<SavedImage>> getJsonAdapter(){
        Moshi moshi = new Moshi.Builder().build();
        Type listOfSavedImages = Types.newParameterizedType(List.class, SavedImage.class);
        return moshi.adapter(listOfSavedImages);
    }

    private void createImageFileFromAssets(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = inputStream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, size);
            }
            inputStream.close();

            FileOutputStream fileOutputStream = getContext().openFileOutput("catPlaceholderImage.png", Context.MODE_PRIVATE);
            buffer = byteArrayOutputStream.toByteArray();
            fileOutputStream.write(buffer);
            fileOutputStream.close();
        } catch (Exception e) {
            Log.d("write to file ", e.getMessage());
        }
    }
}
