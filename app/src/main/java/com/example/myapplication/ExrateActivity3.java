package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import android.util.MalformedJsonException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.io.Reader;
import java.io.InputStreamReader;



import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;



public class ExrateActivity3 extends AppCompatActivity implements Runnable{
    private EditText etRmb;
    private Button btnDollar, btnEuro, btnWon, btnConfig;
    private TextView tvResult;

    private String selectedCurrency = "";
    private ActivityResultLauncher<Intent> configLauncher;
    private SharedPreferences sharedPreferences;

    public static final float DEFAULT_DOLLAR_RATE = 0.14f;
    public static final float DEFAULT_EURO_RATE = 0.13f;
    public static final float DEFAULT_WON_RATE = 190.0f;

    private static final String TAG = "Rate";

    private Handler handler;

    @SuppressLint("HandlerLeak")
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

        Thread t = new Thread(this);
        t.start();//this.run

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.i(TAG, "handleMessage:接受消息");
                if(msg.what == 8){
                    String str = (String)msg.obj;
                    Log.i(TAG,"handleMessage: str=" + str);
                    tvResult.setText(str);

                }
                super.handleMessage(msg);
            }
        };
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

    URL url;

    public void run() {
        Log.i(TAG, "run.........");
        //send message

        try {
            Thread.sleep(3000);
            Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            Log.i(TAG, "run:title=" + doc.title());

            Elements tables = doc.getElementsByTag("table");
            Element table2 = tables.get(1);
            Elements trs = table2.getElementsByTag("tr");
            Log.i(TAG, "run: trs=" + trs);
            trs.remove(0);
            for(Element tr : trs) {
                Elements tds = tr.children();
                Element td1 = tds.first();
                Element td2 = tds.get(5);

                String str1 = td1.text();
                String str2 = td2.text();
                Log.i(TAG, "run:" + str1 + "==>" + str2);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Message msg = handler.obtainMessage(8, "swufe.edu.cn");
        handler.sendMessage(msg);
        Log.i(TAG, "i forget");
    }

    private String inputStream2String(InputStream inputStream)
        throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "UTF-8");
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}