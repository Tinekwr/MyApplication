package com.example.myapplication;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity2 extends ListActivity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 5) {

                    List<String> rateList = (List<String>) msg.obj;
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RateListActivity2.this,
                            android.R.layout.simple_list_item_1, rateList);
                    setListAdapter(adapter);
                }
            }
        };

        Thread t = new Thread(() -> {
            List<String> rateList = run();
            Message msg = handler.obtainMessage(5);
            msg.obj = rateList;
            handler.sendMessage(msg);
        });
        t.start();
    }

    private List<String> run() {
        List<String> rateList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            Element table = doc.select("table").get(1);
            Elements tds = table.select("td");

            for (int i = 0; i < tds.size(); i += 8) {
                String currencyName = tds.get(i).text();
                String rate = tds.get(i + 5).text();
                rateList.add(currencyName + " ==> 汇率：" + rate);
            }
        } catch (IOException e) {
            rateList.add("抓取失败：" + e.getMessage());
        }

        return rateList;
    }
}
