package com.example.RateMyLunchBox_New;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.ListFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.util.Base64.DEFAULT;

/**
 * more detailed info about the post like comments and persons who liked the post.
 * functions: like/ unlike post, comment
 */

public class PostDetail extends ListFragment {
    private ArrayList<CommentObject> comments;
    private ArrayList<PostObject> thePost;
    private ArrayList<String> likes;
    private ArrayAdapter adapter;
    private RequestQueue requestQueue;
    private String url = "https://ratemylunchbox.herokuapp.com/";
    private String stringImage;
    private EditText CommentContent;
    private String theCommentContent = "";
    private Context context;
    private String mParam1;
    private ImageView imageView;

    public PostDetail() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.post_detail, container, false);
        requestQueue = (RequestQueue) Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        mParam1 = (String) getArguments().get("info");

        Button showComment = view.findViewById(R.id.ShowComments);
        Button showLike = view.findViewById(R.id.ShowLikes);
        Button postComment = view.findViewById(R.id.Comment);
        Button likeorunlikepost = view.findViewById(R.id.LikeOrUnlikePost);
        CommentContent = view.findViewById(R.id.CommentContent);
        imageView = view.findViewById(R.id.PostImage);

        getPostById();
        getPostImage();


        showComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getComments();
            }
        });

        showLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getLikes();
            }
        });

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                postComment();
            }
        });

        likeorunlikepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                postlikeOrUnlike();

            }
        });

        context = inflater.getContext();
        return view;
    }

    private void postlikeOrUnlike() {
        StringRequest request = new StringRequest(Request.Method.POST,
                url + "post/like/or/unlike",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<String> theMessage = new ArrayList<>();
                        theMessage.add(response);
                        adapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_list_item_1, theMessage);
                        setListAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                int postID = 0;
                for(PostObject p: thePost){
                    postID += p.getId();
                }
                params.put("postID", String.valueOf(postID));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization",  "Bearer " + Login.getACCESS_TOKEN());
                return params;
            }
        };
        requestQueue.add(request);
    }


    private void getPostById() {
        StringRequest request;
        request = new StringRequest(Request.Method.GET, url + "getpost/" + mParam1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gs = new Gson();
                        System.out.println(response);
                        thePost = gs.fromJson(response, PostList.class).getPosts();
                        adapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_list_item_1, thePost);
                        System.out.println(adapter);
                        setListAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        requestQueue.add(request);
    }

    private void getPostImage() {
        StringRequest request;
        request = new StringRequest(Request.Method.GET, url + "getpicpost/" + mParam1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stringImage = response;
                        Bitmap imageBitmap = StrImageToBitmap(stringImage);
                        imageView.setImageBitmap(imageBitmap);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        requestQueue.add(request);
    }

    private void getLikesHelper(){
        StringRequest request;
        request = new StringRequest(Request.Method.GET, url + "get_number_of_Likes/" + mParam1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        likes = new ArrayList<>();
                        likes.add("number of likes: " + response);
                        for(PostObject e: thePost){
                            ArrayList<String> users = e.getLikedBy();
                            likes.addAll(users);
                        }
                        adapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_list_item_1, likes);
                        System.out.println(adapter);
                        setListAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        requestQueue.add(request);
    }

    private void getLikes() {
        StringRequest requestPost;
        requestPost = new StringRequest(Request.Method.GET, url + "getpost/" + mParam1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gs = new Gson();
                        System.out.println(response);
                        thePost = gs.fromJson(response, PostList.class).getPosts();

                        getLikesHelper();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        requestQueue.add(requestPost);

    }

    private void getComments() {
        StringRequest request = new StringRequest(Request.Method.GET,
                url + "get_comment_for_post/" + mParam1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gs = new Gson();
                        comments = gs.fromJson(response, CommentList.class).getComments();
                        adapter = new ArrayAdapter<>(
                                context, android.R.layout.simple_list_item_1,
                                comments);
                        setListAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        requestQueue.add(request);
    }

    private void postComment() {
        StringRequest request = new StringRequest(Request.Method.POST, url + "post/comment",
                new Response.Listener<String>() {
                    /*refresh data for showcomments*/
                    @Override
                    public void onResponse(String response) {
                        getComments();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                theCommentContent += CommentContent.getText();
                int postID = 0;
                for(PostObject p: thePost){
                    postID += p.getId();
                }
                params.put("postID", String.valueOf(postID));
                params.put("content", theCommentContent);
                CommentContent.setText(null);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization",  "Bearer " + Login.getACCESS_TOKEN());
                return params;
            }
        };
        requestQueue.add(request);
    }

    private Bitmap StrImageToBitmap(String string){
        byte [] encodeByte= Base64.decode(string, DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
