package com.example.aaa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    GridView gridView;
    ArrayList<Image> images = new ArrayList<>();
    private SQLiteDatabase db;
    int uid;
    String image;
    String name;
    Cursor cursor;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new CameraFragment(), false);
        gridView = findViewById(R.id.gridView);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        DataBaseHandler dataBaseHandler = new DataBaseHandler(this);
        db = dataBaseHandler.getWritableDatabase();
        setData();
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }
                }
        );
    }

    private void refresh(){
        setData();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void loadFragment(Fragment fragment, Boolean bool) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        if (bool)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setData(){
        if (!images.isEmpty()){
            images.clear();
        }
        Log.d("databaseDebug", "initials database");
        String[] columns = {DataBaseHandler.KEY_ID, DataBaseHandler.KEY_IMG_URL, DataBaseHandler.KEY_IMG_NAME};
        cursor = db.rawQuery("SELECT * FROM data", null);
        while(cursor.moveToNext()){
            int index_id = cursor.getColumnIndex(DataBaseHandler.KEY_ID);
            int index_image = cursor.getColumnIndex(DataBaseHandler.KEY_IMG_URL);
            int index_name = cursor.getColumnIndex(DataBaseHandler.KEY_IMG_NAME);
            uid = cursor.getInt(index_id);
            image = cursor.getString(index_image);
            name = cursor.getString(index_name);
            images.add(new Image(name, image, uid));
        }
        if (images.size() == 0){
            gridView.setVisibility(View.GONE);
        } else{
            Adapter ad = new Adapter(this, R.layout.grid_items, images);
            gridView.setAdapter(ad);
        }
    }





}


