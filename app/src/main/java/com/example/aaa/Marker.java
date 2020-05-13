package com.example.aaa;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class Marker extends androidx.appcompat.widget.AppCompatImageButton implements View.OnClickListener{
    private float[] position = new float[2];
    private String icon;
    private String recordFile;


    public Marker(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public Marker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Marker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPosition(float x, float y) {
        this.position = new float[] {x,y};
    }

    public void setIcon(String s) {
        this.icon = s;
    }

    public void setPath(String s) {
        this.recordFile = s;
    }

    public float[] getPos() {
        return this.position;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getPath() { return this.recordFile; }


    @Override
    public void onClick(View v) {
        //setChecked(!isChecked());
        Log.d("CircleButton", "setBackground()");
    }



}
