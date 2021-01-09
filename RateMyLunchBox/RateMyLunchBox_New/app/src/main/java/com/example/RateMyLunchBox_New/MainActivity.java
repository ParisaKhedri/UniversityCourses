package com.example.RateMyLunchBox_New;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

/**
 *
 */


public class MainActivity extends AppCompatActivity implements PostsMain.OnFragmentInteractionListener{


    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        PostsMain postsFragment = new PostsMain();
        /* land */
        if(findViewById(R.id.leftframe) != null) {
            fragmentTransaction.add(R.id.leftframe, postsFragment).commit();
        }
        /* port */
        else {
            fragmentTransaction.add(R.id.content, postsFragment).commit();
        }
    }

    @Override
    public void onFragmentInteraction(PostObject post) {
        PostDetail postDetail = new PostDetail();
        FragmentTransaction m = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("info", String.valueOf(post.getId()));
        postDetail.setArguments(args);
        if(findViewById(R.id.leftframe) != null) {
            m.replace(R.id.rightframe, postDetail).commit();
        } else{
            m.replace(R.id.content, postDetail).commit();
        }
        m.addToBackStack(null);
    }
}
