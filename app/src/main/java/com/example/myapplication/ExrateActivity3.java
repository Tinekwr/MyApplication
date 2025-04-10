package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


public class ExrateActivity3 extends AppCompatActivity {
    private EditText etRmb;
    private Button btnDollar, btnEuro, btnWon, btnConfig;
    private TextView tvResult;

    private String selectedCurrency = "";
    private ActivityResultLauncher<Intent> configLauncher;
    private SharedPreferences sharedPreferences;

    public static final float DEFAULT_DOLLAR_RATE = 0.14f;
    public static final float DEFAULT_EURO_RATE = 0.13f;
    public static final float DEFAULT_WON_RATE = 190.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exrate3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etRmb = findViewById(R.id.et_rmb);
        btnDollar = findViewById(R.id.rb_dollar);
        btnEuro = findViewById(R.id.rb_euro);
        btnWon = findViewById(R.id.rb_won);
        btnConfig = findViewById(R.id.btn_config);
        tvResult = findViewById(R.id.tv_result);

        sharedPreferences = getSharedPreferences("ExchangeRates", MODE_PRIVATE);

        configLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "汇率配置已更新", Toast.LENGTH_SHORT).show();
                        if (!selectedCurrency.isEmpty()) {
                            calculateAndDisplayResult();
                        }
                    }
                });

        btnDollar.setOnClickListener(v -> {
            selectCurrency("美元", btnDollar);
            calculateAndDisplayResult();
        });

        btnEuro.setOnClickListener(v -> {
            selectCurrency("欧元", btnEuro);
            calculateAndDisplayResult();
        });

        btnWon.setOnClickListener(v -> {
            selectCurrency("韩元", btnWon);
            calculateAndDisplayResult();
        });

        btnConfig.setOnClickListener(v -> openConfigActivity());

        etRmb.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!selectedCurrency.isEmpty()) {
                    calculateAndDisplayResult();
                }
            }
        });
    }

    private void selectCurrency(String currency, Button selectedButton) {
        resetButtonColors();
        selectedButton.setBackgroundColor(Color.parseColor("#FFBB86FC"));
        selectedCurrency = currency;
    }

    private void resetButtonColors() {
        btnDollar.setBackgroundColor(Color.parseColor("#33B5E5"));
        btnEuro.setBackgroundColor(Color.parseColor("#33B5E5"));
        btnWon.setBackgroundColor(Color.parseColor("#33B5E5"));
    }

    private void openConfigActivity() {
        Intent intent = new Intent(this, ConfigActivity.class);
        configLauncher.launch(intent);
    }
    private void calculateAndDisplayResult() {
        String rmbStr = etRmb.getText().toString();
        if (rmbStr.isEmpty()) {
            tvResult.setText("请输入人民币金额");
            return;
        }

        try {
            double rmb = Double.parseDouble(rmbStr);
            double rate = getSelectedRate();

            if (rate == 0) {
                tvResult.setText("无法获取汇率");
                return;
            }

            double result = rmb * rate;
            String rateSource = isUsingDefaultRate() ? "(默认汇率)" : "(自定义汇率)";
、
            tvResult.setText(String.format("%.2f 人民币 = %.2f %s %s",
                    rmb, result, selectedCurrency, rateSource));

        } catch (NumberFormatException e) {
            tvResult.setText("请输入有效的数字");
        }
    }

    private double getSelectedRate() {
        switch (selectedCurrency) {
            case "美元": return sharedPreferences.getFloat("DOLLAR_RATE", DEFAULT_DOLLAR_RATE);
            case "欧元": return sharedPreferences.getFloat("EURO_RATE", DEFAULT_EURO_RATE);
            case "韩元": return sharedPreferences.getFloat("WON_RATE", DEFAULT_WON_RATE);
            default: return 0;
        }
    }

    private boolean isUsingDefaultRate() {
        switch (selectedCurrency) {
            case "美元": return !sharedPreferences.contains("DOLLAR_RATE");
            case "欧元": return !sharedPreferences.contains("EURO_RATE");
            case "韩元": return !sharedPreferences.contains("WON_RATE");
            default: return true;
        }
    }
}