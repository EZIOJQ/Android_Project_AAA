package com.example.aaa;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class Marker extends androidx.appcompat.widget.AppCompatImageButton {
    private float[] position = new float[2];
    private String icon;

    public Marker(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public Marker(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public Marker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public void setPosition(float x, float y) {
        position = new float[] {x,y};
    }

    public void setIcon(String s) {
        icon = s;
    }

    public float[] getPos() {
        return this.position;
    }

    public String getIcon() {
        return this.icon;
    }

}
