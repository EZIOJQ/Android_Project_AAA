package com.example.aaa;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;

public class EditImage extends AppCompatActivity {

    String cur_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        ImageView image_for_edit = findViewById(R.id.edit_image);
        cur_image = getIntent().getStringExtra(CameraFragment.IMAGE_TO_SEND);
        image_for_edit.setImageURI(Uri.parse(cur_image));
    }
}
