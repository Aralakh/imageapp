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
        imagePresenter.deleteImage(imageToDelete, imageStorage,new MockImageApi());
        assertTrue(imageStorage.lastSavedImages.size() == savedImages.size() -1);
        assertTrue(imageStorage.lastSavedImages.contains(imageToKeep1));
        assertTrue(imageStorage.lastSavedImages.contains(imageToKeep2));
    }

    @Test
    public void assertCorrectListIsAccessedForDeletionWhenRequestSucceeds(){

    }

    @Test
    public void assertWentBackOneScreenWhenRequestSucceeds(){

    }

    @Test
    public void assertImageNotDeletedWhenRequestFails(){

    }

    @Test
    public void assertDidNotGoBackOneScreenWhenRequestFails(){

    }

    public static class MockSingleImageView implements SingleImagePresenter.SingleImageView {
        @Override
        public void goBackOneScreen() {

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

    public static class MockImageApi implements  SingleImagePresenter.ImageApi {

        @Override
        public void deleteImage(RestCallback<ResponseBody> callback, String deleteHash) {
            callback.onResponse(null);
        }
    }
}