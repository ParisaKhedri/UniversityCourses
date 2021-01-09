package com.example.RateMyLunchBox_New;

import androidx.annotation.NonNull;

/**
 * Object class mapping comment-jsonObject
 */

public class CommentObject {
    private String content;
    private String postID;
    private String user;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return "comment: " + content + "\n" + "\n" + "username: " + user;
    }
}
