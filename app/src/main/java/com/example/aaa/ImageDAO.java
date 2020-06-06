package com.example.aaa;

import android.util.Log;

import androidx.room.Dao;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public abstract class ImageDAO {






    @Insert
    abstract void insertMarkerList(List<MarkerEntity> markers);

    @Query("SELECT * FROM image WHERE image_id =:id")
    abstract Image getImage(long id);

    @Query("SELECT * FROM marker WHERE image_id = :imageId")
    abstract List<MarkerEntity> getMarkerList(long imageId);

    @Query("SELECT * FROM image WHERE share_link = :shareLink")
    abstract List<Image> getImageWithShareLink(String shareLink);


    @Transaction
    @Query("SELECT * FROM image")
    abstract List<Image> getAllImage();

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insertImage(Image image);


    public boolean insertImageWithMarkerList(Image image, List<MarkerEntity> markers) {
        long imageId = insertImage(image);
        for (MarkerEntity marker : markers) {
            marker.setImageId(imageId);
        }
        insertMarkerList(markers);
        return true;
    }

    public List<Image> getAllImagesWithMarker(){
        List<Image> images = getAllImage();
        for (Image image : images) {
            List<MarkerEntity> markers = getMarkerList(image.uid);
            image.setMarkerList(markers);
        }
        return images;
    }

    public List<Image> getImageMarkerWithShareLink(String shareLink) {
        List<Image> images = getImageWithShareLink(shareLink);
        for (Image image : images) {
            List<MarkerEntity> markers = getMarkerList(image.uid);
            image.setMarkerList(markers);
        }
        return images;
    }
}
