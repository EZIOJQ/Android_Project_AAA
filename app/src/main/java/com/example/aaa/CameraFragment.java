package com.example.aaa;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.clans.fab.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraFragment extends Fragment {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int IMAGE_LOAD_REQUEST = 1999;
    public static final String IMAGE_TO_SEND = "editableImage";
    private String photo;
    private String name;
    private DataBaseHandler databaseHandler;
    private FloatingActionButton take_photo;
    private FloatingActionButton open_lib;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.camera_fragment, container, false);
        take_photo = view.findViewById(R.id.take_photo);
        open_lib = view.findViewById(R.id.open_lib);
        databaseHandler = new DataBaseHandler(getContext());
        take_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
        //TODO add open lib fragment
        open_lib.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(photoPickerIntent, IMAGE_LOAD_REQUEST);
            }
        });
        return view;
    }
    private void setDataToDataBase() {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DataBaseHandler.KEY_IMG_URL, photo);
        cv.put(DataBaseHandler.KEY_IMG_NAME, name);

        long id = db.insert(DataBaseHandler.TABLE_NAME, null, cv);
        if (id < 0) {
            Toast.makeText(getContext(), "Something went wrong. Please try again later...", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Add successful", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap theImage = (Bitmap) data.getExtras().get("data");
            photo = getEncodedString(theImage);
            name = "PLACEHOLDER";
            setDataToDataBase();
        }
        if (requestCode == IMAGE_LOAD_REQUEST && resultCode == Activity.RESULT_OK){
            Intent editImage = new Intent(this.getContext(), EditImage.class);
            Uri imageUri = data.getData();
            Log.d("sendImage", imageUri.toString());
            editImage.putExtra(IMAGE_TO_SEND,imageUri.toString());
            startActivity(editImage);
        }
    }


    private String getEncodedString(Bitmap bitmap){

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,100, os);

      /* or use below if you want 32 bit images

       bitmap.compress(Bitmap.CompressFormat.PNG, (0–100 compression), os);*/
        byte[] imageArr = os.toByteArray();
        return Base64.encodeToString(imageArr, Base64.URL_SAFE);

    }
}
