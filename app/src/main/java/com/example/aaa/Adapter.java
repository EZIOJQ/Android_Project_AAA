package com.example.aaa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Adapter extends ArrayAdapter {
    Context context;
    SQLiteDatabase db;
    DataBaseHandler dataBaseHandler;
    private ArrayList<Image> images;
    Adapter(Context context, int resource_id, ArrayList<Image> objects){
        super(context, resource_id, objects);
        this.images = objects;
    }

    @Override
    public int getCount(){
        return images.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        View v;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.grid_items, null, false);
        TextView image_title = v.findViewById(R.id.image_title);
        ImageView imageView = v.findViewById(R.id.image);
        Image cur_image = images.get(position);
        image_title.setText(cur_image.getImage_title());
        imageView.setImageBitmap(getBitmapFromEncodedString(cur_image.getImage()));
        return v;
    }

    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        return BitmapFactory.decodeByteArray(arr, 0, arr.length);

    }
}
