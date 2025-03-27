package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText heightInput;
    private EditText weightInput;
    private Button calculateBmiButton;
    private TextView bmiResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        calculateBmiButton = findViewById(R.id.calculateBmiButton);
        bmiResultText = findViewById(R.id.bmiResultText);

        calculateBmiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });
    }

    private void calculateBMI() {
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
            //double height = Double.parseDouble(heightStr);
            double height = Double.parseDouble(heightStr) / 100.0;
            double weight = Double.parseDouble(weightStr);
            double bmi = weight / (height * height);
            bmi = Math.round(bmi * 100) / 100.0; // 保留两位小数

            String bmiResult = "BMI结果：" + bmi + "\n";
            bmiResult += getHealthAdvice(bmi);

            bmiResultText.setText(bmiResult);
        } else {
            bmiResultText.setText("请输入有效的身高和体重值");
        }
    }

    private String getHealthAdvice(double bmi) {
        if (bmi < 18.5) {
            return "体重过轻";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            return "体重正常";
        } else if (bmi >= 25 && bmi < 29.9) {
            return "体重过重";
        } else {
            return "肥胖";
        }
    }
}