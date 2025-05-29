package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ExrateActivity3 extends AppCompatActivity implements Runnable {
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mylistview2), (v, insets) -> {
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
            @Override public void afterTextChanged(Editable s) {
                if (!selectedCurrency.isEmpty()) {
                    calculateAndDisplayResult();
                }
            }
        });

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 8) {
                    Bundle bdl = msg.getData();

                    float webDollar = bdl.getFloat("web_dollar", -1);
                    float webEuro = bdl.getFloat("web_euro", -1);
                    float webWon = bdl.getFloat("web_won", -1);
                    Log.i(TAG, "handleMessage: 抓到的美元汇率=" + webDollar + ", 欧元=" + webEuro + ", 韩元=" + webWon);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (webDollar > 0) editor.putFloat("DOLLAR_RATE", webDollar);
                    if (webEuro > 0) editor.putFloat("EURO_RATE", webEuro);
                    if (webWon > 0) editor.putFloat("WON_RATE", webWon);
                    editor.apply();

                    Toast.makeText(ExrateActivity3.this, "网络汇率已更新", Toast.LENGTH_SHORT).show();

                    if (!selectedCurrency.isEmpty() && !etRmb.getText().toString().isEmpty()) {
                        calculateAndDisplayResult();
                    }
                }
                super.handleMessage(msg);
            }
        };

        Thread t = new Thread(this);
        t.start();
        // 新的内容
        //WebItemTask task = new WebItemTask();
        //task.setHandler(handler);
        //Thread t2 = new Thread(task);
        //t2.start();
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
            String rateSource = isUsingDefaultRate() ? "(默认汇率)" : "(自定义/网络汇率)";

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

    public void run() {
        Log.i(TAG, "run: 开始抓取网页汇率...");
        Bundle retbundle = new Bundle();

        try {
            Thread.sleep(3000); // 模拟延迟
            Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            Elements tables = doc.getElementsByTag("table");
            Element table2 = tables.get(1);
            Elements trs = table2.getElementsByTag("tr");
            trs.remove(0); // 去掉表头

            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");
                if (tds.size() >= 6) {
                    String currencyName = tds.get(0).text().trim();
                    String exchangeRateStr = tds.get(5).text().trim();
                    try {
                        float rate = 100 / Float.parseFloat(exchangeRateStr);
                        if ("美元".equals(currencyName)) {
                            retbundle.putFloat("web_dollar", rate);
                        } else if ("欧元".equals(currencyName)) {
                            retbundle.putFloat("web_euro", rate);
                        } else if ("韩国元".equals(currencyName)) {
                            retbundle.putFloat("web_won", rate);
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "汇率转换失败: " + exchangeRateStr);
                    }
                }
            }

            Message msg = handler.obtainMessage(8);
            msg.setData(retbundle);
            handler.sendMessage(msg);

        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "网页汇率抓取失败", e);
        }
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "UTF-8");
        int rsz;
        while ((rsz = in.read(buffer, 0, buffer.length)) >= 0) {
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}
