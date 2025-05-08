package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ConfigActivity extends AppCompatActivity {

    private EditText etDollarRate, etEuroRate, etWonRate;
    private Button btnSave;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mylistview2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etDollarRate = findViewById(R.id.et_dollar_rate);
        etEuroRate = findViewById(R.id.et_euro_rate);
        etWonRate = findViewById(R.id.et_won_rate);
        btnSave = findViewById(R.id.btn_save);

        sharedPreferences = getSharedPreferences("ExchangeRates", MODE_PRIVATE);
        etDollarRate.setText(String.valueOf(
                sharedPreferences.getFloat("DOLLAR_RATE", ExrateActivity3.DEFAULT_DOLLAR_RATE)));
        etEuroRate.setText(String.valueOf(
                sharedPreferences.getFloat("EURO_RATE", ExrateActivity3.DEFAULT_EURO_RATE)));
        etWonRate.setText(String.valueOf(
                sharedPreferences.getFloat("WON_RATE", ExrateActivity3.DEFAULT_WON_RATE)));

        btnSave.setOnClickListener(v -> saveRates());
    }

    private void saveRates() {
        try {
            float dollarRate = Float.parseFloat(etDollarRate.getText().toString());
            float euroRate = Float.parseFloat(etEuroRate.getText().toString());
            float wonRate = Float.parseFloat(etWonRate.getText().toString());

            if (dollarRate <= 0 || euroRate <= 0 || wonRate <= 0) {
                Toast.makeText(this, "错误！", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("DOLLAR_RATE", dollarRate);
            editor.putFloat("EURO_RATE", euroRate);
            editor.putFloat("WON_RATE", wonRate);
            editor.apply();

            Toast.makeText(this, "汇率保存成功", Toast.LENGTH_SHORT).show();

            // 返回成功结果
            setResult(RESULT_OK);
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
        }
    }
}