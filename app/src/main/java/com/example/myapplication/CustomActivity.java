package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomActivity extends AppCompatActivity {

    private ListView mylist;
    private static final String TAG = "CustomActivity";
    private ArrayList<HashMap<String, String>> currencyList;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 2) {
                currencyList = (ArrayList<HashMap<String, String>>) msg.obj;

                // 使用自定义MyAdapter
                MyAdapter adapter = new MyAdapter(
                        CustomActivity.this,
                        R.layout.list_item,
                        currencyList
                );
                mylist.setAdapter(adapter);

                // 设置列表项点击事件
                mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> selectedItem = currencyList.get(position);
                        String currencyName = selectedItem.get("ItemTitle");
                        String exchangeRate = selectedItem.get("ItemDetail");

                        // 跳转到计算页面
                        Intent intent = new Intent(CustomActivity.this, CalculateActivity.class);
                        intent.putExtra("currencyName", currencyName);
                        intent.putExtra("exchangeRate", exchangeRate);
                        startActivity(intent);
                    }
                });

                Log.i(TAG, "汇率数据加载完成");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        // 初始化ListView
        mylist = findViewById(R.id.mylistview2);

        // 启动网络请求线程
        fetchExchangeRates();
    }

    /**
     * 从中国银行网站获取汇率数据
     */
    private void fetchExchangeRates() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HashMap<String, String>> resultList = new ArrayList<>();

                try {
                    // 使用Jsoup获取网页数据
                    Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/")
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                            .timeout(10000)
                            .get();

                    Log.d(TAG, "成功连接到中国银行网站");

                    // 解析表格数据
                    Elements tables = doc.getElementsByTag("table");
                    if (tables.size() < 2) {
                        Log.e(TAG, "未找到汇率表格");
                        return;
                    }

                    Element rateTable = tables.get(1); // 第二个表格是汇率表
                    Elements rows = rateTable.getElementsByTag("tr");

                    // 跳过表头
                    for (int i = 1; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements cols = row.getElementsByTag("td");

                        if (cols.size() >= 6) {
                            String currencyName = cols.get(0).text();    // 货币名称
                            String exchangeRate = cols.get(5).text();   // 现汇卖出价

                            // 添加到结果列表
                            HashMap<String, String> item = new HashMap<>();
                            item.put("ItemTitle", currencyName);
                            item.put("ItemDetail", exchangeRate);
                            resultList.add(item);

                            Log.d(TAG, currencyName + ": " + exchangeRate);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "获取汇率数据失败: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e(TAG, "解析数据时出错: " + e.getMessage());
                    e.printStackTrace();
                }

                // 发送消息更新UI
                handler.sendMessage(handler.obtainMessage(2, resultList));
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 避免内存泄漏
        handler.removeCallbacksAndMessages(null);
    }
}