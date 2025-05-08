package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RatelistActivity3 extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> rateList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratelist3);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mylistview2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.mylistview);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rateList);
        listView.setAdapter(adapter);


        run();
    }

    private void run() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();

                Element table = doc.select("table").get(1);
                Elements tds = table.select("td");

                List<String> result = new ArrayList<>();
                for (int i = 0; i < tds.size(); i += 8) {
                    String currency = tds.get(i).text();
                    String rate = tds.get(i + 5).text();
                    result.add(currency + " ==> " + rate);
                }


                handler.post(() -> {
                    rateList.clear();
                    rateList.addAll(result);
                    adapter.notifyDataSetChanged();
                });

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> {
                    rateList.clear();
                    rateList.add("获取失败");
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }
}
