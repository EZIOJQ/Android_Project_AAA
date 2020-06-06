package com.example.aaa;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler {
    private ImageDAO imageDAO;
    private AppDatabase db;
    private int oldSize;



    DataBaseHandler(Context context) {
        db = AppDatabase.getInstance(context);
        imageDAO = db.getImageDao();
    }

    public void insertImageWithMarkers(Image image, List<Marker> markers){

        String shareLink = image.getShareLink();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                oldSize = imageDAO.getImageWithShareLink(shareLink).size();
                Log.d("databaseDebug", "run: " + oldSize);
            }
        });
        if (oldSize > 0) {
            return;
        }
        List<MarkerEntity> markerEntities = new ArrayList<>();
        for (Marker marker : markers){
            MarkerEntity markerEntity = new MarkerEntity(marker.getPos_x(), marker.getPos_y(), marker.getPath());
            markerEntities.add(markerEntity);
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean result =  imageDAO.insertImageWithMarkerList(image, markerEntities);
                Log.d("databaseDebug", "insert: " + result);
            }
        });
    }

    public Image getImageWithShareLink(String shareLink) {
        return imageDAO.getImageMarkerWithShareLink(shareLink).get(0);
    }

    public List<Image> getAllImages(){
        return imageDAO.getAllImagesWithMarker();
    }
}
