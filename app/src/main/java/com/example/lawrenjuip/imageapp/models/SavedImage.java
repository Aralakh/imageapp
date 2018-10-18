package com.example.lawrenjuip.imageapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SavedImage implements Parcelable {
    String deleteHash;
    String imageUrl;

    public SavedImage(String deleteHash, String imageUrl) {
        this.deleteHash = deleteHash;
        this.imageUrl = imageUrl;
    }

    public String getDeleteHash() {
        return deleteHash;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setDeleteHash(String deleteHash) {
        this.deleteHash = deleteHash;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return imageUrl;
    }

    //Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    public SavedImage(Parcel source){
        deleteHash = source.readString();
        imageUrl = source.readString();
    }

    public static final Parcelable.Creator<SavedImage> CREATOR = new Parcelable.Creator<SavedImage>(){
        @Override public SavedImage createFromParcel(Parcel in){ return new SavedImage(in); }

        @Override
        public SavedImage[] newArray(int size) {
            return new SavedImage[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deleteHash);
        dest.writeString(this.imageUrl);
    }

}
