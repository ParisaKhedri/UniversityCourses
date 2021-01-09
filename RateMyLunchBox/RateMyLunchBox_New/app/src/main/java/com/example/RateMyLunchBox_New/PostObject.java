package com.example.RateMyLunchBox_New;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Object class mapping Post-jsonObject
 */


public class PostObject implements Comparable<PostObject>{
    private ArrayList<CommentObject> comments;
    private int id;

    private ArrayList<String> likedBy;
    private String post;
    private String image;
    private String position;

    public String getPosition() {
        return position;
    }


    public String getImage() {
        return image;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public ArrayList<String> getLikedBy() {
        return likedBy;
    }


    public String getPost() {
        return post;
    }




    @NonNull
    @Override
    public String toString() {
        if(position != null){
            return "post: " + post + "\n"+ "\n" +  "locationn: " + position;
        }
        return  "post: " + post ;
    }

    /* compareTo method for sorting posts */
    @Override
    public int compareTo(PostObject otherPost) {
        return this.post.compareTo(otherPost.post);
    }
}
