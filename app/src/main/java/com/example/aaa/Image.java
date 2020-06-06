package com.example.aaa;

import android.content.Context;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;


@Entity(tableName = "image")
class Image {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "image_id")
    public long uid;

    @ColumnInfo(name = "image_name")
    public String imageName;
    @ColumnInfo(name = "image_url")
    public String imageUrl;
    @ColumnInfo(name = "share_link")
    private String shareLink;

    @Ignore
    private List<MarkerEntity> markerList;

    Image(String imageName, String imageUrl, String shareLink){
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.shareLink = shareLink;
    }

    public List<MarkerEntity> getMarkerList()  {return this.markerList;}
    public void setMarkerList(List<MarkerEntity> markers) {this.markerList = markers;}

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<Marker> getMarkerListForView(Context contest){
        if (this.markerList != null){
            Log.d("databaseDebug", "getMarkerListForView: " + imageName);
            ArrayList<Marker> markers = new ArrayList<>();
            for (MarkerEntity markerEntity : markerList){
                Marker marker = new Marker(contest);
                marker.setPath(markerEntity.getAudioUrl());
                marker.setPosition(markerEntity.posX, markerEntity.posY);
                markers.add(marker);
            }
            return markers;
        }
        return null;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
