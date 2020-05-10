package com.example.aaa;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class EditImage extends AppCompatActivity {
    RelativeLayout relativeLayout;
    String cur_image;

    ArrayList<Marker> markers = new ArrayList<>();
    private float[] lastTouchDownXY = new float[2];

    private float oldXvalue;
    private float oldYvalue;

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomSheetLayout;
    private Button leftBtn;
    private Button rightBtn;
    private RelativeLayout rootView;
    private Marker currentEditMarker;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = (RelativeLayout) findViewById(R.id.imageCanvas);
        setContentView(R.layout.activity_edit_image);
        ImageView image_for_edit = findViewById(R.id.edit_image);
        cur_image = getIntent().getStringExtra(CameraFragment.IMAGE_TO_SEND);
        image_for_edit.setImageURI(Uri.parse(cur_image));

        rootView = findViewById(R.id.markerList);

        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);

        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = EditBottomSheetBehavior.from(bottomSheetLayout);

        image_for_edit.setOnTouchListener(imageOnTouchListener);


        image_for_edit.setOnLongClickListener(imageLongClickListener);
        leftBtn.setOnClickListener(leftBtnOnClickListener);
        rightBtn.setOnClickListener(rightBtnOnClickListener);

    }

    View.OnTouchListener imageOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // save the X,Y coordinates
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lastTouchDownXY[0] = event.getX();
                lastTouchDownXY[1] = event.getY();
            }

            // let the touch event pass on to whoever needs it
            return false;
        }
    };

    View.OnLongClickListener imageLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                float x = lastTouchDownXY[0];
                float y = lastTouchDownXY[1];
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


                leftBtn.setText("Close");
                rightBtn.setText("Next");
                Log.i("editImage", "x: "+x+ " y: "+y);

                currentEditMarker = new Marker(EditImage.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
                currentEditMarker.setLayoutParams(params);
                currentEditMarker.setX(x-50);
                currentEditMarker.setY(y-100);
                currentEditMarker.setId(R.id.newMarker);
                currentEditMarker.setBackgroundResource(R.drawable.pin);
                rootView.addView(currentEditMarker);
                markers.add(currentEditMarker);
            }
            return true;
        }

    };

    View.OnClickListener leftBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                leftBtn.setText("Back");
                rightBtn.setText("Next");
                rootView.removeView(findViewById(R.id.newMarker));
                markers.remove(markers.size()-1);
            }
            else {

                Log.i("editImage", "onClick: back");
            }
        }
    };

    View.OnClickListener rightBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                currentEditMarker.setId(markers.size()-1);
                currentEditMarker.setOnTouchListener(markerListener);
                leftBtn.setText("Back");
                rightBtn.setText("Next");
            }
            else {

                Log.i("editImage", "onClick: forward");
            }
        }
    };

    View.OnTouchListener markerListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent me){
            if (me.getAction() == MotionEvent.ACTION_DOWN){
                oldXvalue = me.getX();
                oldYvalue = me.getY();
                Log.i("editImage", "Action Down " + oldXvalue + "," + oldYvalue);
            }else if (me.getAction() == MotionEvent.ACTION_MOVE  ){
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(v.getWidth(), v.getHeight());
                v.setX(me.getRawX() - (v.getWidth() / 2));
                v.setY(me.getRawY() - (v.getHeight()));
                v.setLayoutParams(params);
            }
            return true;
        }
    };

}
