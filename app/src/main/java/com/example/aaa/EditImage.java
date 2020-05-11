package com.example.aaa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
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
    private View touchOutside;


    private Button leftBtn;
    private Button rightBtn;

    private RelativeLayout rootView;
    private Marker currentEditMarker;

    private ImageButton recordBtn;
    private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    private MediaRecorder mediaRecorder;
    private String recordFile;




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

        touchOutside = findViewById(R.id.outside_touch);

        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);

        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = EditBottomSheetBehavior.from(bottomSheetLayout);

        image_for_edit.setOnTouchListener(imageOnTouchListener);


        image_for_edit.setOnClickListener(imageClickListener);
        leftBtn.setOnClickListener(leftBtnOnClickListener);
        rightBtn.setOnClickListener(rightBtnOnClickListener);

        touchOutside.setOnClickListener(outsideOnClickListener);

        recordBtn = findViewById(R.id.record_btn);
        recordBtn.setBackgroundColor(Color.TRANSPARENT);

        recordBtn.setOnClickListener(recordOnClickListener);

    }

    //get touch position every time users touch
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

    View.OnClickListener imageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                float x = lastTouchDownXY[0];
                float y = lastTouchDownXY[1];
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


//                leftBtn.setText("Close");
//                rightBtn.setText("Next");
                Log.i("editImage", "x: "+x+ " y: "+y);

                View parent = (View) touchOutside.getParent();
                RelativeLayout.LayoutParams outsideparams = (RelativeLayout.LayoutParams) touchOutside.getLayoutParams();
                outsideparams.height = (int)(parent.getHeight() - getResources().getDimension(R.dimen.bottom_sheet_height));
                outsideparams.width = parent.getWidth();
                Log.i("outside", "onCreate: "+parent.getHeight()+" "+parent.getWidth());
                touchOutside.setLayoutParams(outsideparams);
                touchOutside.setVisibility(View.VISIBLE);

                currentEditMarker = new Marker(EditImage.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
                currentEditMarker.setLayoutParams(params);
                currentEditMarker.setX(x-50);
                currentEditMarker.setY(y-100);
                currentEditMarker.setId(R.id.newMarker);
                currentEditMarker.setBackgroundResource(R.drawable.pin);
                rootView.addView(currentEditMarker);
                currentEditMarker.setPosition(x,y);
                markers.add(currentEditMarker);
            }
        }

    };

    View.OnClickListener leftBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            }
            else {
            }
        }
    };

    View.OnClickListener rightBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                currentEditMarker.setId(markers.size()-1);
                currentEditMarker.setOnTouchListener(markerListener);
            }
            else {
            }
        }
    };

    View.OnClickListener outsideOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                rootView.removeView(findViewById(R.id.newMarker));
                markers.remove(markers.size()-1);
                View parent = (View) touchOutside.getParent();
                RelativeLayout.LayoutParams outsideparams = (RelativeLayout.LayoutParams) touchOutside.getLayoutParams();
                outsideparams.height = 0;
                outsideparams.width = 0;
                touchOutside.setLayoutParams(outsideparams);
            }
            else {

                Log.i("editImage", "onClick: back");
            }
        }
    };

    //drag and drop
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

    View.OnClickListener recordOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isRecording) {
                //Stop Recording
                stopRecording();
                recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_botton_stop));
                isRecording = false;
            }
            else {
                //Start Recording
                if(checkPermissions()) {
                    Log.i("filePath", "startRecording");
                    startRecording();
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_botton_recording));
                    isRecording = true;
                }
                else {
                    Toast.makeText(EditImage.this, "Please grant the permission of audio recording", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecording() {

        String recordPath = EditImage.this.getExternalFilesDir( "/").getAbsolutePath();
        recordFile = "filename.3gp";
        Log.i("filePath", "startRecording " + recordPath);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean checkPermissions() {
        if(ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }

    }


}
