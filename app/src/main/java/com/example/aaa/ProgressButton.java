package com.example.aaa;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.nfc.cardemulation.CardEmulation;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;


public class ProgressButton {

    private CardView cardView;
    private ProgressBar progressBar;
    private ConstraintLayout layout;
    private TextView textView;

    Animation fade_in;

    public ProgressButton(Context ct, View view) {
        cardView = view.findViewById(R.id.card_view);
        layout = view.findViewById(R.id.cardview_constraint_layout);
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.upload_textView);

    }

    public void buttonActivated() {
        progressBar.setVisibility(View.VISIBLE);
        textView.setText("Please wait...");
    }

    public void buttonFinished() {
        progressBar.setVisibility(View.GONE);
        textView.setText("Done");
    }

}
