package com.example.lawrenjuip.imageapp.presenters;

import com.example.lawrenjuip.imageapp.apiservices.RestCallback;
import com.example.lawrenjuip.imageapp.models.SavedImage;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

import static org.junit.Assert.*;

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
        //how do we know something needs to be deleted from the list? How to get information from mocks/inject information to mocks.
        SingleImagePresenter imagePresenter = new SingleImagePresenter(new MockSingleImageView());
        MockImageStorage imageStorage = new MockImageStorage(savedImages);
        imagePresenter.deleteImage(imageToDelete, imageStorage, new MockSuccessfulImageApi());
        assertTrue(imageStorage.lastSavedImages.size() == savedImages.size() -1);
        assertTrue(imageStorage.lastSavedImages.contains(imageToKeep1));
        assertTrue(imageStorage.lastSavedImages.contains(imageToKeep2));
    }

    //behavior verification
    @Test
    public void assertWentBackOneScreenWhenRequestSucceeds(){
        SavedImage imageToDelete = new SavedImage("deleteHash", "imageUrl");
        List<SavedImage> savedImages = new ArrayList<>();
        savedImages.add(imageToDelete);
        MockSingleImageView mockSingleImageView = new MockSingleImageView();
        SingleImagePresenter imagePresenter = new SingleImagePresenter(mockSingleImageView);
        MockImageStorage imageStorage = new MockImageStorage(savedImages);
        imagePresenter.deleteImage(imageToDelete, imageStorage, new MockSuccessfulImageApi());
        assertTrue(mockSingleImageView.timesCalledGoBackOneScreen == 1);
    }


    //state verification
    @Test
    public void assertImageNotDeletedWhenRequestFails(){
        SavedImage imageToNotDelete = new SavedImage("deleteHash", "imageUrl");
        SavedImage imageToKeep1 = new SavedImage("someHash", "someUrl");
        SavedImage imageToKeep2 = new SavedImage("someOtherHash", "someOtherUrl");
        List<SavedImage> savedImages = new ArrayList<>();
        savedImages.add(imageToNotDelete);
        savedImages.add(imageToKeep1);
        savedImages.add(imageToKeep2);

        SingleImagePresenter imagePresenter = new SingleImagePresenter(new MockSingleImageView());
        MockImageStorage imageStorage = new MockImageStorage(savedImages);
        imagePresenter.deleteImage(imageToNotDelete, imageStorage, new MockFailedImageApi());
        assertTrue(imageStorage.savedImagesToReturn.size() == savedImages.size());
        assertTrue(imageStorage.savedImagesToReturn.contains(imageToNotDelete));
        assertTrue(imageStorage.savedImagesToReturn.contains(imageToKeep1));
        assertTrue(imageStorage.savedImagesToReturn.contains(imageToKeep2));
    }

    @Test
    public void assertDidNotGoBackOneScreenWhenRequestFails(){
        SavedImage imageToDelete = new SavedImage("deleteHash", "imageUrl");
        List<SavedImage> savedImages = new ArrayList<>();
        savedImages.add(imageToDelete);
        MockSingleImageView mockSingleImageView = new MockSingleImageView();
        SingleImagePresenter imagePresenter = new SingleImagePresenter(mockSingleImageView);
        MockImageStorage imageStorage = new MockImageStorage(savedImages);
        imagePresenter.deleteImage(imageToDelete, imageStorage, new MockFailedImageApi());
        assertTrue(mockSingleImageView.timesCalledGoBackOneScreen == 0);
    }

    public static class MockSingleImageView implements SingleImagePresenter.SingleImageView {
        public int timesCalledGoBackOneScreen = 0;

        @Override
        public void goBackOneScreen() {
            timesCalledGoBackOneScreen++;
        }
    }

    public static class MockImageStorage implements SingleImagePresenter.ImageStorage {
        private List<SavedImage> savedImagesToReturn;
        private List<SavedImage> lastSavedImages;

        MockImageStorage(List<SavedImage> imageList){
            savedImagesToReturn = imageList;
        }

        @Override
        public List<SavedImage> loadImages() {
            return savedImagesToReturn;
        }

        @Override
        public void saveImages(List<SavedImage> imagesToSave) {
            lastSavedImages = imagesToSave;
        }
    }

    public static class MockSuccessfulImageApi implements SingleImagePresenter.ImageApi {

        @Override
        public void deleteImage(RestCallback<ResponseBody> callback, String deleteHash) {
            callback.onResponse(null);
        }
    }

    public static class MockFailedImageApi implements SingleImagePresenter.ImageApi {

        @Override
        public void deleteImage(RestCallback<ResponseBody> callback, String deleteHash) {
            callback.onError();
        }
    }
}