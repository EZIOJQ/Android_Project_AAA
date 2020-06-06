package com.example.aaa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    GridView gridView;
    ArrayList<Image> images = new ArrayList<>();
    private SQLiteDatabase db;
    int uid;
    String image;
    String name;
    Cursor cursor;
    SwipeRefreshLayout swipeRefreshLayout;
    private ClipboardManager clipboardManager;
    Dialog sharePopDialog;
    String shareLink;
    private final Context curContext = this;
    private FileDownService fileDownService;
    private DataBaseHandler dataBaseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new CameraFragment(), false);
        gridView = findViewById(R.id.gridView);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        dataBaseHandler = new DataBaseHandler(this);
        setData();
        swipeRefreshLayout.setOnRefreshListener(
                () -> refresh()
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            readClipboard();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    private void readClipboard(){
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null || !clipboardManager.hasPrimaryClip()) {
            Log.d("clipboard", "readClipboard: " + "no clip data");
            return;
        }
        ClipData pData = clipboardManager.getPrimaryClip();
        ClipData.Item item = pData.getItemAt(0);
        String txtpaste = item.getText().toString();
        if (txtpaste.equals("d5dad5c7-0ea0-4fd8-ac83-bacc295a299e")) {
            clipboardManager.clearPrimaryClip();
            shareLink = txtpaste;
            sharePopDialog = new Dialog(this);
            sharePopDialog.setContentView(R.layout.clipboard_popup);
            TextView textClose = sharePopDialog.findViewById(R.id.close_open_dialog);
            textClose.setOnClickListener(v -> sharePopDialog.dismiss());
            TextView shareLinkText = sharePopDialog.findViewById(R.id.shareLinkText);
            String finalOpenString = "Do you want to open share link " + shareLink + "?";
            shareLinkText.setText(finalOpenString);
            sharePopDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            sharePopDialog.setCancelable(false);
            sharePopDialog.show();
            Button openButton = sharePopDialog.findViewById(R.id.open_share_button);
            openButton.setOnClickListener(openShareLinkOnClickListener);
        }
    }

    private void refresh(){
        setData();
        swipeRefreshLayout.setRefreshing(false);
    }



    View.OnClickListener openShareLinkOnClickListener = v -> {
        if (shareLink == null) {
            Toast.makeText(this, "Share link not valid!", Toast.LENGTH_SHORT).show();
        }else {
            Intent viewImage = new Intent(curContext, ViewImage.class);
            viewImage.putExtra("shareLink", shareLink);
            sharePopDialog.dismiss();
            startActivity(viewImage);
        }
    };

    public void loadFragment(Fragment fragment, Boolean bool) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        if (bool)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setData(){
        if (!images.isEmpty()){
            images.clear();
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                images = (ArrayList<Image>) dataBaseHandler.getAllImages();
                if (images.size() == 0){
                    gridView.setVisibility(View.GONE);
                } else{
                    Adapter ad = new Adapter(curContext, R.layout.grid_items, images);
                    gridView.setAdapter(ad);
                }
            }
        });
    }
}


