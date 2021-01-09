package com.example.RateMyLunchBox_New;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.util.Base64.DEFAULT;

/**
 * Custom  arrayAdapter containing imageView and textView
 */

public class PostListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<PostObject> postList;

    public PostListAdapter(Context context, int layout, ArrayList<PostObject> postList) {
        this.context = context;
        this.layout = layout;
        this.postList = postList;
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public Object getItem(int position) {
        return postList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private static class ViewHolder{
        ImageView PObjIm;
        TextView PostObjCont;

    }
    private Bitmap StrImageToBitmap(String string){
        byte [] encodeByte= Base64.decode(string, DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);
            holder.PostObjCont = (TextView) row.findViewById(R.id.PostObjectContent);
            holder.PObjIm = (ImageView) row.findViewById(R.id.PostObjectImage);
            row.setTag(holder);

        }else{
            holder = (ViewHolder) row.getTag();
        }
        PostObject p = postList.get(position);
        if(p.getPosition() != null){
            holder.PostObjCont.setText(p.getPost() + "\n" + "\n" + "location: "+ p.getPosition());
        }else{
            holder.PostObjCont.setText(p.getPost());
        }


        String Image = p.getImage();
        Bitmap b = StrImageToBitmap(Image);
        holder.PObjIm.setImageBitmap(b);

        return row;
    }
}
