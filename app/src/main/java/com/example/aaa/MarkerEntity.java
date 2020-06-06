package com.example.aaa;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "marker")
public class MarkerEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "marker_id")
    public long uid;

    @ColumnInfo(name = "pos_x")
    public float posX;

    @ColumnInfo(name = "pos_y")
    public float posY;

    @ColumnInfo(name = "image_id")
    public long imageId;

    @ColumnInfo(name = "audio")
    public String audioUrl;

    MarkerEntity(float posX, float posY, String audioUrl){
        this.posX = posX;
        this.posY = posY;
        this.audioUrl = audioUrl;
    }

    public void setImageId(long imageId){
        this.imageId = imageId;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public long getImageId() {
        return imageId;
    }


}
