package com.example.RateMyLunchBox_New;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.FragmentTransaction;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * the start-page/main-page of the app,
 * this fragment has two layouts:
 * 1- viewLoggedIn: buttons: newPost and Logout
 * 2- viewLoggedOut : buttons:
 */

public class PostsMain extends ListFragment {
    private OnFragmentInteractionListener mListener;
    private ArrayList<PostObject> allPosts;
    private Boolean isLoggedIn = false;
    private RequestQueue requestQueue;
    private PostListAdapter adapter;
    private String url = "https://ratemylunchbox.herokuapp.com/";
    private Context context;
    private FragmentTransaction fragmentTransaction;


    public PostsMain() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getFragmentManager() != null;
        fragmentTransaction = getFragmentManager().beginTransaction();
        View viewLoggedIn = null;
        viewLoggedIn = inflater.inflate(R.layout.loggedinview_main, container, false);
        Button newPost = viewLoggedIn.findViewById(R.id.NewPost);
        Button Logout = viewLoggedIn.findViewById(R.id.LogOut);

        View viewLoggedOut = null;
        viewLoggedOut = inflater.inflate(R.layout.loggedoutview_main, container, false);

        requestQueue = Volley.newRequestQueue(
                Objects.requireNonNull(getActivity()).getApplicationContext());
        getAllPostsData();
        context = inflater.getContext();

        if(isLoggedIn) {
            newPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewPost p = new NewPost();
                    fragmentTransaction.replace(R.id.content, p).commit();
                }
            });


            Logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isLoggedIn = false;
                    logout();
                    PostsMain pp = new PostsMain();
                    fragmentTransaction.replace(R.id.content, pp).commit();
                }
            });
            return viewLoggedIn;
        }else {
            loggedOutView(viewLoggedOut);
            return viewLoggedOut;
        }
    }

    private void loggedOutView(View view){
        Button login = view.findViewById(R.id.Login);
        Button register = view.findViewById(R.id.Register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login login = new Login();
                isLoggedIn = true;
                fragmentTransaction.replace(R.id.content, login).commit();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register register = new Register();
                fragmentTransaction.replace(R.id.content, register).commit();

            }
        });
    }

    private void getAllPostsData() {
        StringRequest request;
        request = new StringRequest(Request.Method.GET, url + "getallposts",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gs = new Gson();
                        allPosts = gs.fromJson(response, PostList.class).getPosts();

                        Collections.sort(allPosts, new Comparator<PostObject>() {
                            @Override
                            public int compare(PostObject p1, PostObject p2) {
                                return p2.getLikedBy().size() - p1.getLikedBy().size();
                            }
                        });

                        adapter = new PostListAdapter(context, R.layout.postobject, allPosts);
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

    private void logout() {
        StringRequest request = new StringRequest(Request.Method.POST, url + "logout",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization",  "Bearer " + Login.getACCESS_TOKEN());
                return params;
            }
        };
        requestQueue.add(request);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.onFragmentInteraction(allPosts.get((int)id));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(PostObject post);
    }

    void setIsLoggedIn(Boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
}