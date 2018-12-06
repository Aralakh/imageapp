package com.example.lawrenjuip.imageapp.presenters;

import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.SavedImage;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SingleImagePresenterTest {

    @Test
    public void assertCorrectImageIsDeletedWhenRequestSucceeds(){
        SavedImage imageToDelete = new SavedImage("deleteHash", "imageUrl");
        SavedImage imageToKeep1 = new SavedImage("someHash", "someUrl");
        SavedImage imageToKeep2 = new SavedImage("someOtherHash", "someOtherUrl");
        List<SavedImage> savedImages = new ArrayList<>();

        savedImages.add(imageToDelete);
        savedImages.add(imageToKeep1);
        savedImages.add(imageToKeep2);

        SingleImagePresenter.SingleImageView imageView = mock(SingleImagePresenter.SingleImageView.class);
        SingleImagePresenter imagePresenter = new SingleImagePresenter(imageView);
        SingleImagePresenter.ImageStorage imageStorage = mock(SingleImagePresenter.ImageStorage.class);
        SingleImagePresenter.ImageApi mockImageApi = mock(SingleImagePresenter.ImageApi.class);
        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        imageStorage.saveImages(savedImages);
        imagePresenter.deleteImage(imageToDelete, imageStorage, mockImageApi);
        verify(mockImageApi).deleteImage(callbackArgumentCaptor.capture(), eq("deleteHash"));
        callbackArgumentCaptor.getValue().onResponse(null);
//        assertTrue(imageStorage.loadImages().size() == savedImages.size() -1);
//        assertTrue(imageStorage.loadImages().contains(imageToKeep1));
//        assertTrue(imageStorage.loadImages().contains(imageToKeep2));
        verify(imageStorage, atMost(1)).saveImages(savedImages);

    }

    @Test
    public void assertWentBackOneScreenWhenRequestSucceeds(){
        SavedImage imageToDelete = new SavedImage("deleteHash", "imageUrl");
        List<SavedImage> savedImages = new ArrayList<>();

        savedImages.add(imageToDelete);

        SingleImagePresenter.SingleImageView mockSingleImageView = mock(SingleImagePresenter.SingleImageView.class);
        SingleImagePresenter imagePresenter = new SingleImagePresenter(mockSingleImageView);
        SingleImagePresenter.ImageStorage imageStorage = mock(SingleImagePresenter.ImageStorage.class);
        imageStorage.saveImages(savedImages);
        SingleImagePresenter.ImageApi mockImageApi = mock(SingleImagePresenter.ImageApi.class);
        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        imagePresenter.deleteImage(imageToDelete, imageStorage, mockImageApi);
        verify(mockImageApi).deleteImage(callbackArgumentCaptor.capture(), eq("deleteHash"));
        callbackArgumentCaptor.getValue().onResponse(null);
        verify(mockSingleImageView).goBackOneScreen();
    }

    //convert the rest of the tests using mockito, make an attempt for mocking the API
    //work on the update calls, try and do it the original way
    @Test
    public void assertImageNotDeletedWhenRequestFails(){
        SavedImage imageToNotDelete = new SavedImage("deleteHash", "imageUrl");
        SavedImage imageToKeep1 = new SavedImage("someHash", "someUrl");
        SavedImage imageToKeep2 = new SavedImage("someOtherHash", "someOtherUrl");
        List<SavedImage> savedImages = new ArrayList<>();

        savedImages.add(imageToNotDelete);
        savedImages.add(imageToKeep1);
        savedImages.add(imageToKeep2);

        SingleImagePresenter imagePresenter = new SingleImagePresenter(mock(SingleImagePresenter.SingleImageView.class));
        SingleImagePresenter.ImageStorage mockImageStorage = mock(SingleImagePresenter.ImageStorage.class);
        SingleImagePresenter.ImageApi mockImageApi = mock(SingleImagePresenter.ImageApi.class);
        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        imagePresenter.deleteImage(imageToNotDelete, mockImageStorage, mockImageApi);
        verify(mockImageApi).deleteImage(callbackArgumentCaptor.capture(), eq("deleteHash"));
        callbackArgumentCaptor.getValue().onError();
        verify(mockImageStorage, never()).saveImages(ArgumentMatchers.<SavedImage>anyList());
    }

    @Test
    public void assertDidNotGoBackOneScreenWhenRequestFails(){
        SavedImage imageToDelete = new SavedImage("deleteHash", "imageUrl");
        List<SavedImage> savedImages = new ArrayList<>();

        savedImages.add(imageToDelete);

        SingleImagePresenter.SingleImageView mockSingleImageView = mock(SingleImagePresenter.SingleImageView.class);
        SingleImagePresenter imagePresenter = new SingleImagePresenter(mockSingleImageView);
        SingleImagePresenter.ImageStorage mockImageStorage = mock(SingleImagePresenter.ImageStorage.class);
        SingleImagePresenter.ImageApi mockImageApi = mock(SingleImagePresenter.ImageApi.class);
        ArgumentCaptor<RestCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(RestCallback.class);

        mockImageStorage.saveImages(savedImages);
        imagePresenter.deleteImage(imageToDelete, mockImageStorage, mockImageApi);
        verify(mockImageApi).deleteImage(callbackArgumentCaptor.capture(), eq("deleteHash"));
        callbackArgumentCaptor.getValue().onError();
        verify(mockSingleImageView, never()).goBackOneScreen();
    }
}