package com.example.lawrenjuip.imageapp.presenters;

import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.SavedImage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SingleImagePresenterTest {
     SingleImagePresenter.SingleImageView imageView;
     SingleImagePresenter.ImageStorage imageStorage;
     SingleImagePresenter.ImageApi imageApi;
     SingleImagePresenter imagePresenter;

    @Before
    public void setUp(){
        imageView = mock(SingleImagePresenter.SingleImageView.class);
        imageStorage = mock(SingleImagePresenter.ImageStorage.class);
        imageApi = mock(SingleImagePresenter.ImageApi.class);
        imagePresenter = new SingleImagePresenter(imageView, imageApi, imageStorage);
    }

    @Test
    public void assertImageSavedWhenRequestSucceeds(){
        SavedImage imageToSave = new SavedImage("saveHash", "imageUrl");
        imageToSave.setTitle("new title");

        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        imagePresenter.updateImage(imageToSave);
        verify(imageApi).updateImage(callbackArgumentCaptor.capture(), eq("saveHash"), eq( "new title"));
        callbackArgumentCaptor.getValue().onResponse(null);
        verify(imageStorage, atMost(1)).saveImages(ArgumentMatchers.<SavedImage>anyList());
    }

    @Test
    public void assertImageNotSavedWhenRequestFails(){
        SavedImage imageToSave = new SavedImage("saveHash", "imageUrl");
        imageToSave.setTitle("new title");

        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        imagePresenter.updateImage(imageToSave);
        verify(imageApi).updateImage(callbackArgumentCaptor.capture(), eq("saveHash"), eq("new title"));
        callbackArgumentCaptor.getValue().onError();
        verify(imageStorage, never()).saveImages(ArgumentMatchers.<SavedImage>anyList());
    }

    @Test
    public void assertCorrectImageIsDeletedWhenRequestSucceeds(){
        SavedImage imageToDelete = new SavedImage("deleteHash", "imageUrl");
        SavedImage imageToKeep1 = new SavedImage("someHash", "someUrl");
        SavedImage imageToKeep2 = new SavedImage("someOtherHash", "someOtherUrl");
        List<SavedImage> savedImages = new ArrayList<>();

        savedImages.add(imageToDelete);
        savedImages.add(imageToKeep1);
        savedImages.add(imageToKeep2);

        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        imagePresenter.deleteImage(imageToDelete);
        verify(imageApi).deleteImage(callbackArgumentCaptor.capture(), eq("deleteHash"));
        callbackArgumentCaptor.getValue().onResponse(null);
        verify(imageStorage, atMost(1)).saveImages(savedImages);

    }

    @Test
    public void assertWentBackOneScreenWhenRequestSucceeds(){
        SavedImage imageToDelete = new SavedImage("deleteHash", "imageUrl");
        List<SavedImage> savedImages = new ArrayList<>();

        savedImages.add(imageToDelete);

        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        imagePresenter.deleteImage(imageToDelete);
        verify(imageApi).deleteImage(callbackArgumentCaptor.capture(), eq("deleteHash"));
        callbackArgumentCaptor.getValue().onResponse(null);
        verify(imageView).goBackOneScreen();
    }

    @Test
    public void assertImageNotDeletedWhenRequestFails(){
        SavedImage imageToNotDelete = new SavedImage("deleteHash", "imageUrl");
        SavedImage imageToKeep1 = new SavedImage("someHash", "someUrl");
        SavedImage imageToKeep2 = new SavedImage("someOtherHash", "someOtherUrl");
        List<SavedImage> savedImages = new ArrayList<>();

        savedImages.add(imageToNotDelete);
        savedImages.add(imageToKeep1);
        savedImages.add(imageToKeep2);

        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        imagePresenter.deleteImage(imageToNotDelete);
        verify(imageApi).deleteImage(callbackArgumentCaptor.capture(), eq("deleteHash"));
        callbackArgumentCaptor.getValue().onError();
        verify(imageStorage, never()).saveImages(ArgumentMatchers.<SavedImage>anyList());
    }

    @Test
    public void assertDidNotGoBackOneScreenWhenRequestFails(){
        SavedImage imageToDelete = new SavedImage("deleteHash", "imageUrl");
        List<SavedImage> savedImages = new ArrayList<>();

        savedImages.add(imageToDelete);
        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        imagePresenter.deleteImage(imageToDelete);
        verify(imageApi).deleteImage(callbackArgumentCaptor.capture(), eq("deleteHash"));
        callbackArgumentCaptor.getValue().onError();
        verify(imageView, never()).goBackOneScreen();
    }
}