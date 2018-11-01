package com.example.lawrenjuip.imageapp.models;

public class Image {
    public boolean success;
    public int status;
    public UploadedImage data;

    public static class UploadedImage {
        String id;
        String title;
        String deletehash;
        String link;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDeleteHash() {
            return deletehash;
        }

        public void setDeleteHash(String deleteHash) {
            this.deletehash = deleteHash;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

    }
}
