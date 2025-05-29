package com.example.myapplication;

import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalculateActivity extends AppCompatActivity {
    private TextView currencyNameText;
    private TextView exchangeRateText;
    private EditText amountInput;
    private Button calculateButton;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currencyNameText = findViewById(R.id.currencyName);
        exchangeRateText = findViewById(R.id.exchangeRate);
        amountInput = findViewById(R.id.amountInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultText = findViewById(R.id.resultText);

        // 获取数据
        String currencyName = getIntent().getStringExtra("currencyName");
        String exchangeRate = getIntent().getStringExtra("exchangeRate");
        
        currencyNameText.setText("货币: " + currencyName);
        exchangeRateText.setText("汇率: " + exchangeRate);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateExchange(currencyName, exchangeRate);
            }
        });
    }

    private void calculateExchange(String currencyName, String exchangeRateStr) {
        try {
            // 获取输入人民币金额
            String amountStr = amountInput.getText().toString();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "请输入人民币金额", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            double exchangeRate = Double.parseDouble(exchangeRateStr);

            // 计算
            double result = amount * 100 / exchangeRate;

            DecimalFormat df = new DecimalFormat("#.##");
            String formattedResult = df.format(result);

            // 显示结果
            resultText.setText(String.format("%.2f 人民币 可兑换 %s %s",
                    amount, formattedResult, currencyName));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
        }
    }
}
