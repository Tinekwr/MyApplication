package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CountActivity3 extends AppCompatActivity implements View.OnClickListener {

    private TextView scoreATextView;
    private TextView scoreBTextView;
    private int scoreA = 0;
    private int scoreB = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_count3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mylistview2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化视图
        initializeViews();
    }

    private void initializeViews() {

        scoreATextView = findViewById(R.id.scoreATextView);
        Button add3AButton = findViewById(R.id.add3AButton);
        Button add2AButton = findViewById(R.id.add2AButton);
        Button add1AButton = findViewById(R.id.add1AButton);

        scoreBTextView = findViewById(R.id.scoreBTextView);
        Button add3BButton = findViewById(R.id.add3BButton);
        Button add2BButton = findViewById(R.id.add2BButton);
        Button add1BButton = findViewById(R.id.add1BButton);
        Button resetButton = findViewById(R.id.resetButton);


        add3AButton.setOnClickListener(this);
        add2AButton.setOnClickListener(this);
        add1AButton.setOnClickListener(this);
        add3BButton.setOnClickListener(this);
        add2BButton.setOnClickListener(this);
        add1BButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);


        updateScores();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.add3AButton) {
            scoreA += 3;
        } else if (id == R.id.add2AButton) {
            scoreA += 2;
        } else if (id == R.id.add1AButton) {
            scoreA += 1;
        } else if (id == R.id.add3BButton) {
            scoreB += 3;
        } else if (id == R.id.add2BButton) {
            scoreB += 2;
        } else if (id == R.id.add1BButton) {
            scoreB += 1;
        } else if (id == R.id.resetButton) {
            scoreA = 0;
            scoreB = 0;
        }

        updateScores();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("teama_score", scoreA);
        outState.putInt("teamb_score", scoreB);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scoreA = savedInstanceState.getInt("teama_score");
        scoreB = savedInstanceState.getInt("teamb_score");

        scoreATextView.setText(String.valueOf(scoreA));
        scoreBTextView.setText(String.valueOf(scoreB));

    }

    private void updateScores() {
        scoreATextView.setText(String.valueOf(scoreA));
        scoreBTextView.setText(String.valueOf(scoreB));
    }




}