package com.example.RateMyLunchBox_New;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Login fragment creating ACCESS_TOKEN
 */


public class Login extends Fragment {
    private static String ACCESS_TOKEN = null;
    private String loginError = "Wrong password or username";
    private PostsMain p = new PostsMain();
    private TextView loginErrorTextView;
    private TextView name;
    private TextView password;
    private RequestQueue mQueue;
    private FragmentTransaction fragmentTransaction;
    private String url = "https://ratemylunchbox.herokuapp.com/user/login";


    public Login(){
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        fragmentTransaction = getFragmentManager().beginTransaction();
        mQueue = (RequestQueue) Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        name = view.findViewById(R.id.Name);
        password = view.findViewById(R.id.Password);
        loginErrorTextView = view.findViewById(R.id.LoginError);
        Button login = view.findViewById(R.id.Register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser(v);
            }
        });
        return view;
    }

    private void LoginUser(final View v) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gs = new Gson();
                        Map<String,String> res = gs.fromJson(response,
                                new TypeToken<HashMap<String,String>>(){}.getType());
                        ACCESS_TOKEN = res.get("access_token");
                        if(ACCESS_TOKEN != null) {
                            assert getFragmentManager() != null;
                            fragmentTransaction.replace(R.id.content, p).commit();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginErrorTextView.setText(loginError);
                error.printStackTrace();
            }

        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params=new HashMap<>();
                String theName = String.valueOf(name.getText());
                String thePassword = String.valueOf(password.getText());
                params.put("username", theName);
                params.put("password", thePassword);
                return params;
            }
        };
        mQueue.add(request);
        p.setIsLoggedIn(true);
    }

    static String getACCESS_TOKEN() {
        return ACCESS_TOKEN;
    }

}
