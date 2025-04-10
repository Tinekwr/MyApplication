package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class ExrateActivity2 extends AppCompatActivity {
    private EditText rmbInput;
    private TextView resultText;

    // 汇率
    private static final double DOLLAR_RATE = 0.14;
    private static final double EURO_RATE = 0.13;
    private static final double WON_RATE = 170.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exrate2);

        rmbInput = findViewById(R.id.rmbInput);
        resultText = findViewById(R.id.resultText);

        Button btnDollar = findViewById(R.id.btnDollar);
        Button btnEuro = findViewById(R.id.btnEuro);
        Button btnWon = findViewById(R.id.btnWon);

        btnDollar.setOnClickListener(v -> convertCurrency(
                getString(R.string.convert_to_dollar).replace("Convert to ", ""),
                DOLLAR_RATE
        ));

        btnEuro.setOnClickListener(v -> convertCurrency(
                getString(R.string.convert_to_euro).replace("Convert to ", ""),
                EURO_RATE
        ));

        btnWon.setOnClickListener(v -> convertCurrency(
                getString(R.string.convert_to_won).replace("Convert to ", ""),
                WON_RATE
        ));

    }
    private void convertCurrency(String currencyType, double rate) {
        String inputStr = rmbInput.getText().toString();

        if (inputStr.isEmpty()) {
            Toast.makeText(this, R.string.enter_amount, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double rmb = Double.parseDouble(inputStr);
            if (rmb <= 0) {
                Toast.makeText(this, R.string.amount_positive, Toast.LENGTH_SHORT).show();
                return;
            }

            double result = rmb * rate;
            String resultMsg = getString(
                    R.string.result_format,
                    rmb,
                    result,
                    currencyType
            );
            resultText.setText(resultMsg);
            Toast.makeText(this, R.string.conversion_complete, Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.valid_number, Toast.LENGTH_SHORT).show();
        }
    }
}