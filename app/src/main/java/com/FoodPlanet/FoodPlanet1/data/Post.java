package com.FoodPlanet.FoodPlanet1.data;

public class Post {

    public String caption;
    public String ownerId;
    public String photoUri;
    public String postId;

    public Post() {
    }

    public Post(String caption, String ownerId, String photoUri, String postId) {
        this.caption = caption;
        this.ownerId = ownerId;
        this.photoUri = photoUri;
        this.postId = postId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPhototUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
