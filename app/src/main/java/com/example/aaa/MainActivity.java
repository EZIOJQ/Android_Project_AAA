package com.example.aaa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    GridView gridView;
    ArrayList<Image> images = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new CameraFragment(), false);
        gridView = findViewById(R.id.gridView);
        images.add(new Image("name_1", R.drawable.donut_circle));
        images.add(new Image("name_2", R.drawable.icecream_circle));
        images.add(new Image("name_3", R.drawable.froyo_circle));
        Adapter ad = new Adapter(this, R.layout.grid_items, images);
        gridView.setAdapter(ad);
    }
    public void loadFragment(Fragment fragment, Boolean bool) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        if (bool)
            transaction.addToBackStack(null);
        transaction.commit();
    }


}


