package com.example.aaa;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Adapter extends ArrayAdapter {
    private ArrayList<Image> images;
    Adapter(Context context, int resource_id, ArrayList<Image> objects){
        super(context, resource_id, objects);
        this.images = objects;
    }

    @Override
    public int getCount(){
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.grid_items, null);
        TextView image_title = v.findViewById(R.id.image_title);
        ImageView imageView = v.findViewById(R.id.image);
        image_title.setText(images.get(position).getImage_title());
        imageView.setImageResource(images.get(position).getImage());
        return v;
    }
}
