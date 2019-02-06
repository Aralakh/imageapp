package com.example.lawrenjuip.imageapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.lawrenjuip.imageapp.R;
import com.example.lawrenjuip.imageapp.adapters.ImageAdapter;
import com.example.lawrenjuip.imageapp.apiservices.ImgurGalleryImageApi;
import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.example.lawrenjuip.imageapp.presenters.GalleryPresenter;
import com.example.lawrenjuip.imageapp.utils.BaseObserver;
import com.example.lawrenjuip.imageapp.utils.FileUtils;
import com.example.lawrenjuip.imageapp.utils.SharedPrefsImageStorage;
import com.example.lawrenjuip.imageapp.viewmodels.GalleryViewModel;
import com.example.lawrenjuip.imageapp.views.GalleryImageView;
import com.example.lawrenjuip.imageapp.views.ImageUploadApi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment implements GalleryImageView {
    private RecyclerView imageRecyclerView;
    private GridLayoutManager imageGridLayoutManager;
    private ImageAdapter imageAdapter;
    private File cameraFile;
    private GalleryViewModel galleryViewModel;
    private static final int REQUEST_TAKE_PICTURE = 1;

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

        galleryViewModel = new GalleryViewModel(new SharedPrefsImageStorage(getActivity()), new ImgurGalleryImageApi());
        imageAdapter = new ImageAdapter(new ArrayList<SavedImage>(), getContext(),(ImageAdapter.OnImageClickListener) getActivity());

        imageRecyclerView.setAdapter(imageAdapter);
        imageRecyclerView.getAdapter().notifyDataSetChanged();
        return galleryView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        galleryViewModel.screenTitle.subscribe(new BaseObserver<String>(){
            @Override
            public void onNext(String s){
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(s);
            }
        });

        galleryViewModel.savedImageList.subscribe(new BaseObserver<List<SavedImage>>(){
            @Override
            public void onNext(List<SavedImage> savedImages) {
                updateAdapter(savedImages);
            }
        });
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
                takePicture();
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        //is this subscription necessary?
        galleryViewModel.savedImageList.subscribe(new BaseObserver<List<SavedImage>>(){
            @Override
            public void onNext(List<SavedImage> savedImages) {
                updateAdapter(savedImages);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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

    public void updateAdapter(List<SavedImage> imageList){
        imageAdapter.addAll(imageList);
        imageAdapter.notifyDataSetChanged();
    }

    private void uploadImage(File file){
        AssetManager assetManager = getContext().getAssets();
        InputStream is;
        try {
            File imageFile = file;
            if(file == null || !file.exists()) {
                is = assetManager.open("cat_placeholder.jpg");
                FileUtils.createImageFileFromAssets(is, getContext());
                imageFile = new File(getContext().getFilesDir(), "catPlaceholderImage.png");
            }
            galleryViewModel.uploadImage(imageFile);

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
}
