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
 * registration fragment, adds new user to database
 */


public class Register extends Fragment {
    private TextView name;
    private TextView password;
    private TextView registerResult;
    private RequestQueue requestQueue;
    private String url = "https://ratemylunchbox.herokuapp.com/user/register";


    public Register() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        requestQueue = Volley.newRequestQueue(
                Objects.requireNonNull(getActivity()).getApplicationContext());
        name = view.findViewById(R.id.Name);
        password = view.findViewById(R.id.Password);

        Button register = view.findViewById(R.id.Register);
        Button login = view.findViewById(R.id.Login);
        registerResult = view.findViewById(R.id.RegistrationResult);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login login = new Login();
                assert getFragmentManager() != null;
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.content, login).commit();
            }
        });

        return view;
    }

    private void registration() {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gs = new Gson();
                        Map<String,String> res = gs.fromJson(response,
                                new TypeToken<HashMap<String,String>>(){}.getType());
                        registerResult.setText(res.get("message"));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        requestQueue.add(request);
    }
}
