package com.example.aaa;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Adapter extends ArrayAdapter {
    Context context;
    private ArrayList<Image> images;
    private Image cur_image;
    Adapter(Context context, int resource_id, ArrayList<Image> images){
        super(context, resource_id, images);
        this.images = images;
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
        cur_image = images.get(position);
        image_title.setText(cur_image.getImageName());
        imageView.setImageURI(Uri.fromFile(new File(cur_image.getImageUrl())));
        Log.d("databaseDebug", "getView: " + cur_image.getMarkerList());
        imageView.setOnClickListener(redirectToViewOnClickListener);
        return v;
    }

    private View.OnClickListener redirectToViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            Intent intent = new Intent(getContext(), ViewImage.class).putExtra("localShareLink", cur_image.getShareLink());
            Log.d("databaseDebug", "onClick: " + cur_image.getShareLink());
            getContext().startActivity(intent);
        }
    };


    private  Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        return BitmapFactory.decodeByteArray(arr, 0, arr.length);

    }
}
