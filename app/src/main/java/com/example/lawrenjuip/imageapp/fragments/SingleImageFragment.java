package com.example.lawrenjuip.imageapp.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lawrenjuip.imageapp.R;
import com.example.lawrenjuip.imageapp.apiservices.ImageApi;
import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.example.lawrenjuip.imageapp.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

import static com.example.lawrenjuip.imageapp.utils.Constants.SAVED_IMAGES_KEY;

public class SingleImageFragment extends Fragment {
    private SavedImage savedImage;
    private Drawable placeholderImage;
    private TextView name;
    private EditText editName;
    private Button submitButton;
    private Button cancelButton;
    private boolean isEditMode = false;

    public static SingleImageFragment newInstance(SavedImage image){
        SingleImageFragment fragment = new SingleImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(SAVED_IMAGES_KEY, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            savedImage = bundle.getParcelable(SAVED_IMAGES_KEY);
        }

        View view = inflater.inflate(R.layout.fragment_single_image, container, false);
        ImageView imageView = view.findViewById(R.id.beautifulImage);
        editName = view.findViewById(R.id.editName);
        name = view.findViewById(R.id.name);
        submitButton = view.findViewById(R.id.submitButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        if(!savedImage.getTitle().isEmpty()){
            name.setText(savedImage.getTitle());
        }

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                savedImage.setTitle(editName.getText().toString());
                name.setText(savedImage.getTitle());
                isEditMode = false;
                updateImage();
                updateViewForEditing();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode = false;
                updateViewForEditing();
            }
        });

        updateViewForEditing();

        placeholderImage = createPlaceHolder();

        Picasso.get()
                .load(savedImage.getImageUrl())
                .error(R.drawable.ic_image_error)
                .placeholder(placeholderImage)
                .into(imageView);

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
                isEditMode = true;
                updateViewForEditing();
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateViewForEditing(){
        if(isEditMode){
            name.setVisibility(View.GONE);
            editName.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
        } else{
            name.setVisibility(View.VISIBLE);
            editName.setVisibility(View.GONE);
            submitButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
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

    private void deleteImage(){
        ImageApi imageApi = new ImageApi();
        imageApi.deleteImage(new RestCallback<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                SharedPreferences preferences = getActivity().getPreferences(getContext().MODE_PRIVATE);
                List<SavedImage> saveImagesList =  FileUtils.loadExistingImages(preferences);
                for(SavedImage image: saveImagesList){
                    if(savedImage.equals(image)){
                        saveImagesList.remove(image);
                    }
                }
                FileUtils.saveImages(saveImagesList, preferences);
                getActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onError() {
                //let them know it failed
            }
        }, savedImage.getDeleteHash());
    }

    private void updateImage(){
        ImageApi imageApi = new ImageApi();
        imageApi.updateImage(new RestCallback<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                SharedPreferences preferences = getActivity().getPreferences(getContext().MODE_PRIVATE);
                List<SavedImage> saveImagesList =  FileUtils.loadExistingImages(preferences);
                List<SavedImage> updatedList = new ArrayList<>();
                for(int i = 0; i < saveImagesList.size(); i++){
                    if(saveImagesList.get(i).equals(savedImage)){
                        updatedList.add(savedImage);
                    }else{
                        updatedList.add(saveImagesList.get(i));
                    }
                }
                FileUtils.saveImages(updatedList, preferences);
                Toast.makeText(getActivity(), getString(R.string.update_successful), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError() {

            }
        }, savedImage.getDeleteHash(), savedImage.getTitle());
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getContext());
        deleteBuilder.setTitle("Delete image")
                .setMessage(R.string.delete_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImage();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog deleteDialog = deleteBuilder.create();
        deleteDialog.show();
    }
}
