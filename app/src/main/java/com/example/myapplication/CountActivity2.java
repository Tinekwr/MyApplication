package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.net.Uri;

public class CountActivity2 extends AppCompatActivity implements View.OnClickListener {
    private TextView scoreTextView;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_count2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("score", 0);
        }

        initializeViews();
    }

    private void initializeViews() {
        scoreTextView = findViewById(R.id.scoreTextView);

        Button add3Button = findViewById(R.id.add3Button);
        Button add2Button = findViewById(R.id.add2Button);
        Button add1Button = findViewById(R.id.add1Button);
        Button resetButton = findViewById(R.id.resetButton);

        add3Button.setOnClickListener(this);
        add2Button.setOnClickListener(this);
        add1Button.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        updateScore();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.add3Button) {
            score += 3;
            //Intent intent = new
                    //Intent(Intent. ACTION_DIAL, Uri.parse("tel: 87092173"));
        } else if (id == R.id.add2Button) {
            score += 2;
        } else if (id == R.id.add1Button) {
            score += 1;
        } else if (id == R.id.resetButton) {
            score = 0;
        }

        updateScore();
    }

    private void updateScore() {
        scoreTextView.setText(String.valueOf(score));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score", score);
    }
}