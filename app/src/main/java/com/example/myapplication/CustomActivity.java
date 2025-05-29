package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CustomActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "CustomActivity";

    private ListView listView;
    private ProgressBar progressBar;

    private Handler handler;
    private ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        listView = findViewById(R.id.mylistview2);
        progressBar = findViewById(R.id.progressBar);

        listView.setOnItemClickListener(this);

        handler = new Handler(msg -> {
            if (msg.what == 2) {
                dataList = (ArrayList<HashMap<String, String>>) msg.obj;

                SimpleAdapter adapter = new SimpleAdapter(CustomActivity.this, dataList,
                        R.layout.list_item,
                        new String[]{"ItemTitle", "ItemDetail", "ItemExtra"},
                        new int[]{R.id.itemTitle, R.id.itemDetail, R.id.itemExtra});
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }
            return true;
        });

        SharedPreferences prefs = getSharedPreferences("rate_prefs", MODE_PRIVATE);
        String lastUpdateDate = prefs.getString("last_update_date", "");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());

        if (today.equals(lastUpdateDate)) {
            Log.i(TAG, "今天已更新，读取数据库");
            loadFromDatabase();
        } else {
            Log.i(TAG, "今天未更新，联网获取");
            fetchExchangeRatesAndUpdateDatabase(today);
        }
    }

    private void loadFromDatabase() {
        RateManager manager = new RateManager(this);
        List<RateItem> dbList = manager.getAllRates();

        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
        for (RateItem item : dbList) {
            HashMap<String, String> map = new HashMap<>();
            map.put("ItemTitle", item.getCname());
            map.put("ItemDetail", String.valueOf(item.getCval()));
            map.put("ItemExtra", "来自数据库");
            resultList.add(map);
        }

        handler.sendMessage(handler.obtainMessage(2, resultList));


    }

    private void fetchExchangeRatesAndUpdateDatabase(String today) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
            RateManager manager = new RateManager(CustomActivity.this);
            manager.clearAll();

            try {
                Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/")
                        .header("User-Agent", "Mozilla/5.0")
                        .timeout(10000)
                        .get();

                Elements tables = doc.getElementsByTag("table");
                if (tables.size() >= 2) {
                    Element rateTable = tables.get(1);
                    Elements rows = rateTable.getElementsByTag("tr");

                    for (int i = 1; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements cols = row.getElementsByTag("td");

                        if (cols.size() >= 6) {
                            String currencyName = cols.get(0).text();
                            String exchangeRate = cols.get(5).text();

                            HashMap<String, String> item = new HashMap<>();
                            item.put("ItemTitle", currencyName);
                            item.put("ItemDetail", exchangeRate);
                            item.put("ItemExtra", "来自网络");
                            resultList.add(item);

                            manager.saveRate(new RateItem(currencyName, Float.parseFloat(exchangeRate)));
                        }
                    }
                }

                SharedPreferences.Editor editor = getSharedPreferences("rate_prefs", MODE_PRIVATE).edit();
                editor.putString("last_update_date", today);
                editor.apply();

            } catch (IOException e) {
                Log.e(TAG, "获取汇率失败：" + e.getMessage());
            }

            handler.sendMessage(handler.obtainMessage(2, resultList));
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> map = dataList.get(position);
        String title = map.get("ItemTitle");
        String detail = map.get("ItemDetail");

        Intent intent = new Intent(this, CalculateActivity.class);
        intent.putExtra("currencyName", title);
        intent.putExtra("exchangeRate", detail);
        startActivity(intent);
    }
}
