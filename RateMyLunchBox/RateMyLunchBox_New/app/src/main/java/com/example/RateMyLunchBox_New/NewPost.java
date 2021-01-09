package com.example.RateMyLunchBox_New;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.content.pm.PackageManager;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * creating new post with sensor functions and access to gallery
 */

public class NewPost extends Fragment {
    private ImageView imageView;
    private String theImage;
    private String urlPostLunchBox = "https://ratemylunchbox.herokuapp.com/post";
    private TextView postContent;
    private String thePostContent;
    private String theLocation = " ";
    private RequestQueue requestQueue;
    private String filePath;
    private Context context;
    private static final int REQUEST_CODE_GALLERY = 100;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FragmentTransaction fragmentTransaction;
    private TextView locationTextView;


    public NewPost() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_lunch_box,
                container, false);
        Button postLunchBox = view.findViewById(R.id.PostLunchBox);
        Button backToPosts = view.findViewById(R.id.BackToPosts);
        Button takePic = view.findViewById(R.id.TakePic);
        Button choosePic = view.findViewById(R.id.ChoosePic);
        locationTextView = view.findViewById(R.id.Location);
        Button setLocation = view.findViewById(R.id.SetLocation);
        postContent = view.findViewById(R.id.PostContent);
        imageView = view.findViewById(R.id.LunchBoxImage);
        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(getActivity());
        assert getFragmentManager() != null;
        fragmentTransaction = getFragmentManager().beginTransaction();

        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }


        postLunchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewPost(v);
            }
        });

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){
                    //When permission granted
                    getLocation();
                }else {
                    //When permission denied
                    ActivityCompat.requestPermissions(getActivity()
                            ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                }

            }
        });

        backToPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostsMain postsMain = new PostsMain();
                postsMain.setIsLoggedIn(true);
                fragmentTransaction.replace(R.id.content, postsMain).commit();
            }
        });

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPictureTakerAction();
            }
        });

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        context = inflater.getContext();
        requestQueue = Volley.newRequestQueue(
                Objects.requireNonNull(getActivity()).getApplicationContext());
        return view;
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, REQUEST_CODE_GALLERY);
    }

    private void dispatchPictureTakerAction(){
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();
            if (photoFile != null){
                filePath = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.RateMyLunchBox_New.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePic, 1);
            }
        }
    }
    private File createPhotoFile(){
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir =  getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name,"jpg",storageDir);
        } catch (IOException e) {
            Log.d("mylog", "except: " + e.toString());
        }
        return image;
    }

    private void getLocation() {
        this.fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                /* Initialize location */
                Location location = task.getResult();
                if(location !=null){
                    try {
                        /* Initialize geoCoder */
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        /* Initialize address list */
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        /* Set address */
                        theLocation = addresses.get(0).getAddressLine(0);
                        locationTextView.setText(theLocation);

                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_GALLERY) {
                Uri imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }else if(requestCode == 1) {
                Bitmap b = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(b);
            }
        }
    }


    private void postNewPost(final View v) {
        theImage = imageViewToString(imageView);
        thePostContent = postContent.getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, urlPostLunchBox,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostsMain p = new PostsMain();
                        p.setIsLoggedIn(true);
                        fragmentTransaction.replace(R.id.content, p).commit();
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
                params.put("image", theImage);
                params.put("post", thePostContent);
                params.put("position", theLocation);
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

    private String imageViewToString(ImageView image){
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] array = stream.toByteArray();
        return Base64.encodeToString(array, 0);
    }

}
