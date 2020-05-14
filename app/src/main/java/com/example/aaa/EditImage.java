package com.example.aaa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
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

import android.text.method.MovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditImage extends AppCompatActivity {

    RelativeLayout relativeLayout;
    String cur_image;

    ArrayList<Marker> markers = new ArrayList<>();
    private float[] lastTouchDownXY = new float[2];

    //Detecting drag and drop
    private float oldXvalue;
    private float oldYvalue;
    private boolean isMoving = false;



    //Main page button
    private Button leftBtn;
    private Button rightBtn;

    private RelativeLayout rootView;
    private Marker currentEditMarker;


    //For recording bottom sheet
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomSheetLayout;
    private View touchOutside;

    private ImageButton recordBtn;
    private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;


    private MediaRecorder mediaRecorder;
    private String recordFile;

    //For detail bottom sheet
    private BottomSheetBehavior detailBottomSheetBehavior;
    private ConstraintLayout detailBottomSheetLayout;

    //
    private Marker currentPlayMarker;

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private ImageButton playBtn;
    private File fileToPlay;
    private TextView durationText;


    //Network related
    private Uri imageUri;
    private FileUploadService fileUploadService;
    Context curr_context;




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        relativeLayout = (RelativeLayout) findViewById(R.id.imageCanvas);
        setContentView(R.layout.activity_edit_image);
        ImageView image_for_edit = findViewById(R.id.edit_image);
        cur_image = getIntent().getStringExtra(CameraFragment.IMAGE_TO_SEND);
        imageUri = Uri.parse(cur_image);
        image_for_edit.setImageURI(Uri.parse(cur_image));
        curr_context = this;


        rootView = findViewById(R.id.markerList);

//        touchOutside = findViewById(R.id.outside_touch);

        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);

        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = EditBottomSheetBehavior.from(bottomSheetLayout);

        image_for_edit.setOnTouchListener(imageOnTouchListener);


        image_for_edit.setOnClickListener(imageClickListener);
        leftBtn.setOnClickListener(leftBtnOnClickListener);
        rightBtn.setOnClickListener(rightBtnOnClickListener);

//        touchOutside.setOnClickListener(outsideOnClickListener);

        recordBtn = findViewById(R.id.record_btn);
        recordBtn.setBackgroundColor(Color.TRANSPARENT);

        recordBtn.setOnClickListener(recordOnClickListener);

        //
        detailBottomSheetLayout = findViewById(R.id.detail_bottom_sheet);
        detailBottomSheetBehavior = BottomSheetBehavior.from(detailBottomSheetLayout);
        playBtn = findViewById(R.id.play_button);
        playBtn.setOnClickListener(playOnClickListener);
        durationText = findViewById(R.id.media_duration);


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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheetLayout.getGlobalVisibleRect(outRect);

                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    rootView.removeView(findViewById(R.id.newMarker));
                    markers.remove(markers.size()-1);
                }

            }
            if (detailBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                detailBottomSheetLayout.getGlobalVisibleRect(outRect);


                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    detailBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            }
        }

        return super.dispatchTouchEvent(event);
    }

    View.OnClickListener imageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED && detailBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED) {
                float x = lastTouchDownXY[0];
                float y = lastTouchDownXY[1];
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

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
        }
    };

    View.OnClickListener rightBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ArrayList<MultipartBody.Part> audioFiles = new ArrayList<>();
            List<HashMap<String, Float>> markerMap = new ArrayList<>();
            for (int i = 0; i < markers.size(); ++i){
                Marker marker = markers.get(i);
                File audioFile = new File(marker.getPath());
                Uri audioFileUri = Uri.fromFile(audioFile);
                HashMap<String, Float> markerHash = new HashMap<>();
                markerHash.put("pos_x", marker.getPos()[0]);
                markerHash.put("pos_y]", marker.getPos()[1]);
                markerMap.add(markerHash);
                audioFiles.add(FileUploadMethods.prepareFilePart(curr_context, "audio", audioFileUri,audioFile));
            }
            List<JSONObject> jsonObj = new ArrayList<>();
            for(HashMap<String, Float> marker : markerMap) {
                JSONObject obj = new JSONObject(marker);
                jsonObj.add(obj);
            }
            JSONArray markerJsonObj = new JSONArray(jsonObj);
            String markerJsonString = markerJsonObj.toString();

            String shareLinkString = UUID.randomUUID().toString();
            RequestBody shareLink = FileUploadMethods.createPartFromString(shareLinkString);
            RequestBody markersJSON = FileUploadMethods.createPartFromString(markerJsonString);


            MultipartBody.Part imageFile = FileUploadMethods.prepareFilePart(curr_context, "image", imageUri, new File(imageUri.getPath()));


            fileUploadService = ServiceGenerator.createService(FileUploadService.class);
            Call<ResponseBody> call = fileUploadService.uploadFiles(imageFile, audioFiles, markersJSON, shareLink);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Toast.makeText(curr_context, "success!" + response, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(curr_context, "success!" + t, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

//    View.OnClickListener outsideOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                rootView.removeView(findViewById(R.id.newMarker));
//                markers.remove(markers.size()-1);
//                RelativeLayout.LayoutParams outsideparams = (RelativeLayout.LayoutParams) touchOutside.getLayoutParams();
//                outsideparams.height = 0;
//                outsideparams.width = 0;
//                touchOutside.setLayoutParams(outsideparams);
//            }
//            else if(detailBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//                RelativeLayout.LayoutParams outsideparams = (RelativeLayout.LayoutParams) touchOutside.getLayoutParams();
//                outsideparams.height = 0;
//                outsideparams.width = 0;
//                touchOutside.setLayoutParams(outsideparams);
//                detailBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                Log.i("editImage", "onClick: back");
//            }
//            else {
//
//            }
//        }
//    };

    //Marker
    //drag and drop
    View.OnTouchListener markerListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent me){
            if (me.getAction() == MotionEvent.ACTION_DOWN){
                oldXvalue = me.getRawX();
                oldYvalue = me.getRawY();
                isMoving = false;
                Log.i("editImage", "Action Down " + oldXvalue + "," + oldYvalue);
            }else if (me.getAction() == MotionEvent.ACTION_MOVE  ){
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(v.getWidth(), v.getHeight());
                v.setX(me.getRawX() - (v.getWidth() / 2));
                v.setY(me.getRawY() - (v.getHeight()));
                v.setLayoutParams(params);
                currentPlayMarker = findViewById(v.getId());
                currentPlayMarker.setPosition(me.getRawX(),me.getRawY());
                isMoving = true;
            }
            else if(me.getAction() == MotionEvent.ACTION_UP) {
                Log.i("DragMarker", "onTouch:" + "oldx " + oldXvalue + " oldy "+oldYvalue + " new x " + me.getRawX() + " new y " + me.getRawY());
                if(!isMoving || (Math.abs(me.getRawX() - oldXvalue) < 30 && Math.abs(me.getRawY() - oldYvalue) < 30)) {
                    v.performClick();
                    isMoving = false;
                }
            }
            return true;
        }
    };

    View.OnClickListener markerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("markerclick", "CLick Marker");
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED && detailBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//                View parent = (View) touchOutside.getParent();
//                RelativeLayout.LayoutParams outsideparams = (RelativeLayout.LayoutParams) touchOutside.getLayoutParams();
//                outsideparams.height = (int)(parent.getHeight() - getResources().getDimension(R.dimen.bottom_sheet_height));
//                outsideparams.width = parent.getWidth();
//                Log.i("outside", "onCreate: "+parent.getHeight()+" "+parent.getWidth());
//                touchOutside.setLayoutParams(outsideparams);
//                touchOutside.setVisibility(View.VISIBLE);
                detailBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                currentPlayMarker = findViewById(v.getId());
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopAudio();
                    }
                });
                Log.i("Play_log", "playpath " + currentPlayMarker.getPath());
                try {
                    mediaPlayer.setDataSource(currentPlayMarker.getPath());
                    mediaPlayer.prepare();
                    int duration = mediaPlayer.getDuration();
                    @SuppressLint("DefaultLocale") String time = String.format("%02d min, %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                    );
                    durationText.setText(time);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
            }
        }
    };



    //For Recording
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
        String recordPath = EditImage.this.getExternalFilesDir( "/").getAbsolutePath();

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
//        RelativeLayout.LayoutParams outsideparams = (RelativeLayout.LayoutParams) touchOutside.getLayoutParams();
//        outsideparams.height = 0;
//        outsideparams.width = 0;
//        touchOutside.setLayoutParams(outsideparams);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        currentEditMarker.setPath(recordPath + "/" + recordFile);
        currentEditMarker.setId(View.generateViewId());
        Log.i("Play_log", "Setpath: " + recordPath + "/" + recordFile);
        currentEditMarker.setOnTouchListener(markerListener);
        currentEditMarker.setOnClickListener(markerOnClickListener);

    }

    private void startRecording() {

        String recordPath = EditImage.this.getExternalFilesDir( "/").getAbsolutePath();
        recordFile = currentEditMarker.getId() + ".3gp";
        Log.i("filePath", "startRecording " + recordPath);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            Log.d("recording", "startRecording: "+e);
        }



    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    View.OnClickListener playOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("Play_log", "onClick: " + v.getResources().getResourceName(v.getId()));
            if(isPlaying) {
                stopAudio();
            }
            else {
                playAudio();
            }
        }
    };



    private void stopAudio() {
        mediaPlayer.stop();
        isPlaying = false;
        Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
        playBtn.setBackground(getResources().getDrawable(R.drawable.media_play_button));
    }

    private void playAudio() {
        mediaPlayer.start();
        Toast.makeText(getApplicationContext(), "Recording Started Playing", Toast.LENGTH_LONG).show();
        isPlaying = true;
        playBtn.setBackground(getResources().getDrawable(R.drawable.media_stop_button));

    }


}
