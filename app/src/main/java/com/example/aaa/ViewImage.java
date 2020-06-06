package com.example.aaa;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewImage extends AppCompatActivity {


    private ArrayList<Marker> markers = new ArrayList<>();



    //
    private Marker currentPlayMarker;
    private Button deleteMarkerBtn;

    private RelativeLayout rootView;

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private ImageButton playBtn;
    private File fileToPlay;
    private TextView durationText;
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;

    //For detail bottom sheet
    private BottomSheetBehavior detailBottomSheetBehavior;
    private ConstraintLayout detailBottomSheetLayout;

    //For share content
    private String encodedImage;
    private String encodedAudio;
    private Context curContext = this;
    String currentPhotoPath;
    Bitmap imageBitmap;

    //local database
    DataBaseHandler dataBaseHandler;
    Image image;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        rootView = findViewById(R.id.markerList);
        String localShareLink = getIntent().getStringExtra(("localShareLink"));
        String shareLink = getIntent().getStringExtra("shareLink");
        if(shareLink != null) downloadContent(shareLink);
        if(localShareLink != null) fetchContent(localShareLink);



        detailBottomSheetLayout = findViewById(R.id.detail_bottom_sheet);
        detailBottomSheetBehavior = BottomSheetBehavior.from(detailBottomSheetLayout);

        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if(isPlaying) {
                            stopAudio();
                        }
                        mediaPlayer.release();
                        mediaPlayer = null;
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };

        detailBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);

        //////////////


        playBtn = findViewById(R.id.play_button);
        playBtn.setOnClickListener(playOnClickListener);
//        durationText = findViewById(R.id.media_duration);
        deleteMarkerBtn = findViewById(R.id.detail_header_delete);
        deleteMarkerBtn.setVisibility(View.GONE);

        playerSeekbar = findViewById(R.id.player_seekbar);

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(isPlaying) {
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });


    }

    private void fetchContent(String localShareLink){
        dataBaseHandler = new DataBaseHandler(this);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Insert Data
                image = dataBaseHandler.getImageWithShareLink(localShareLink);
                ImageView imageView = findViewById(R.id.view_image);
                imageView.setImageURI(Uri.fromFile(new File(image.getImageUrl())));
                markers = image.getMarkerListForView(curContext);
                Log.d("databaseDebug", "run: " + image.getMarkerList().size());
                addMarkertoList(markers);
            }
        });
    }


    private void downloadContent(String shareLink) {
        FileDownService fileDownService = ServiceGenerator.createService(FileDownService.class);
        Call<ResponseBody> call = fileDownService.getContent(shareLink);
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String res = response.body().string();
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // add image
                        encodedImage = jsonObject.getString("image");
                        byte[] decodedString = Base64.getDecoder().decode(encodedImage);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        saveImage(decodedByte);
                        ImageView imageView = findViewById(R.id.view_image);
                        imageView.setImageBitmap(decodedByte);

                        // add marker list
                        JSONArray markerList = jsonObject.getJSONArray("markers");
                        for (int j = 0; j < markerList.length(); j++) {
                            JSONObject markerJson = markerList.getJSONObject(j);
                            encodedAudio = markerJson.getJSONObject("audio").getString("audio");
                            byte[] decodedAudioByte = Base64.getDecoder().decode(encodedAudio);
                            String audioPath = saveAudio(decodedAudioByte);
                            float pos_x = (float)markerJson.getDouble("pos_x");
                            float pos_y = (float)markerJson.getDouble("pos_y");
                            Marker marker = new Marker(curContext);
                            marker.setPosition(pos_x, pos_y);
                            marker.setPath(audioPath);
                            markers.add(marker);
                        }
                        addMarkertoList(markers);

                        //store to local database
                        image = new Image("testImage", currentPhotoPath, shareLink);
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                // Insert Data
                                dataBaseHandler = new DataBaseHandler(curContext);
                                dataBaseHandler.insertImageWithMarkers(image, markers);
                            }
                        });



                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(curContext, "Download Failed! Try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarkertoList(ArrayList<Marker> markers) {
        for(Marker m : markers) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
            m.setLayoutParams(params);
            m.setX(m.getPos()[0]-50);
            m.setY(m.getPos()[1]-100);
            m.setId(View.generateViewId());
            m.setBackgroundResource(R.drawable.pin);
            rootView.addView(m);
            m.setOnClickListener(markerOnClickListener);
        }
    }

    private String saveImage(Bitmap inputImage) throws IOException {
        String timeStamp =  new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = curContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            inputImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        currentPhotoPath = photoFile.getAbsolutePath();
        return currentPhotoPath;
    }


    private String saveAudio(byte[] inputAudio) throws IOException {
        String timeStamp =  new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "3gp_" + timeStamp + "_";
        File storageDir = curContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File audioFile = File.createTempFile(
                audioFileName,
                ".3gp",
                storageDir
        );
        try {
            FileOutputStream out = new FileOutputStream(audioFile);
            out.write(inputAudio);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioFile.getAbsolutePath();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
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



    View.OnClickListener markerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("markerclick", "CLick Marker");
            if(detailBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {

                detailBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                currentPlayMarker = findViewById(v.getId());
                mediaPlayer = new MediaPlayer();
                Log.i("Play_log", "playpath " + currentPlayMarker.getPath());
                try {
                    //get file
                    Log.d("filepath", currentPlayMarker.getPath());
                    mediaPlayer.setDataSource(currentPlayMarker.getPath());

                    mediaPlayer.prepare();
                    int duration = mediaPlayer.getDuration();
                    @SuppressLint("DefaultLocale") String time = String.format("%02d min, %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                    );
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopAudio();
                        }
                    });


                    //seekbar
                    playerSeekbar.setMax(duration);
                    seekbarHandler = new Handler();
                    updateRunnable();
                    seekbarHandler.postDelayed(updateSeekbar, 0);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
            }
        }
    };

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null) {
                    playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                    seekbarHandler.postDelayed(this,500);
                }
            }
        };
    }


    View.OnClickListener playOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("Play_log", "onClick: " + v.getResources().getResourceName(v.getId()));
            if(isPlaying) {
                pauseAudio();
            }
            else {
                resumeAudio();
            }
        }
    };





    private void stopAudio() {
        mediaPlayer.seekTo(0);
        mediaPlayer.pause();
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
        Toast.makeText(getApplicationContext(), "Playing Stopped", Toast.LENGTH_LONG).show();
        playBtn.setBackground(getResources().getDrawable(R.drawable.media_play_button));
    }


    private void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
        Toast.makeText(getApplicationContext(), "Playing Paused", Toast.LENGTH_LONG).show();
        playBtn.setBackground(getResources().getDrawable(R.drawable.media_play_button));
    }

    private void resumeAudio() {
        mediaPlayer.start();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar,0);
        Toast.makeText(getApplicationContext(), "Playing Continued", Toast.LENGTH_LONG).show();
        isPlaying = true;
        playBtn.setBackground(getResources().getDrawable(R.drawable.media_stop_button));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isPlaying) {
            stopAudio();
        }
    }
}
