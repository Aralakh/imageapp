package com.example.lawrenjuip.imageapp.presenters;

import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.Image;
import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.example.lawrenjuip.imageapp.utils.SharedPrefsImageStorage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class GalleryPresenterTest {
    GalleryPresenter.GalleryImageView imageView;
    SharedPrefsImageStorage imageStorage;
    GalleryPresenter.ImageApi imageApi;
    GalleryPresenter galleryPresenter;

    @Before
    public void setUp(){
        imageView = mock(GalleryPresenter.GalleryImageView.class);
        imageStorage = mock(SharedPrefsImageStorage.class);
        imageApi = mock(GalleryPresenter.ImageApi.class);
        galleryPresenter = new GalleryPresenter(imageView, imageStorage, imageApi);
    }

    @Test
    public void imageSavedWhenRequestSucceeds(){
        File imageFile = null;

        try{
            imageFile = File.createTempFile("someImage", ".jpg");
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);
        Image imageResponse = new Image();
        imageResponse.status = 200;
        imageResponse.success = true;
        imageResponse.data = new Image.UploadedImage();
        imageResponse.data.setDeleteHash("deleteHash");
        imageResponse.data.setId("id");
        imageResponse.data.setLink("link");
        imageResponse.data.setTitle("");

        if(imageFile != null){
          galleryPresenter.uploadImage(imageFile);
          verify(imageApi).uploadImage(callbackArgumentCaptor.capture(), Mockito.any(File.class));
          callbackArgumentCaptor.getValue().onResponse(imageResponse);
          verify(imageStorage, atMost(1)).saveImages(ArgumentMatchers.<SavedImage>anyList());
          verify(imageView, atMost(1)).updateAdapter(ArgumentMatchers.<SavedImage>anyList());
        }

        imageFile.deleteOnExit();

    }

    @Test
    public void imageNotSavedWhenRequestFails(){
        File imageFile = null;

        try{
            imageFile = File.createTempFile("someImage", ".jpg");
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        if(imageFile != null){
            galleryPresenter.uploadImage(imageFile);
            verify(imageApi).uploadImage(callbackArgumentCaptor.capture(), Mockito.any(File.class));
            callbackArgumentCaptor.getValue().onError();
            verify(imageStorage, never()).saveImages(ArgumentMatchers.<SavedImage>anyList());
        }

        imageFile.deleteOnExit();
    }

    //test that response is valid image response?
}
