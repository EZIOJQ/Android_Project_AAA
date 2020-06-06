package com.example.aaa;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


public class Marker extends androidx.appcompat.widget.AppCompatImageButton implements View.OnClickListener{


    private float pos_x;
    private float pos_y;
    private String recordFile;

    public Marker(Context context) {
        super(context);
    }


    public float getPos_x() {
        return pos_x;
    }

    public float getPos_y() {
        return pos_y;
    }

    public void setPosition(float x, float y) {
        this.pos_x = x;
        this.pos_y = y;
    }


    public void setPath(String s) {
        this.recordFile = s;
    }

    public float[] getPos() {
        return new float[]{pos_x, pos_y};
    }

    public String getPath() { return this.recordFile; }

    @Override
    public void onClick(View v) {
        //setChecked(!isChecked());
        Log.d("CircleButton", "setBackground()");
    }


}
