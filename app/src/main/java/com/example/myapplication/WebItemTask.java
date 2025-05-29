package com.example.myapplication;

import android.os.Handler;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WebItemTask implements Runnable {

    private static final String TAG = "CustomActivity";
    private Handler handler;
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    @Override
    public void run() {
        ArrayList<RateItem> resultList = new ArrayList<>();
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
                        resultList.add(new RateItem(currencyName, Float.parseFloat(exchangeRate)));
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "获取汇率数据失败: " + e.getMessage());
        }

        handler.sendMessage(handler.obtainMessage(2, resultList));
    }
}
