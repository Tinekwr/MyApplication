package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAdapter extends ArrayAdapter<HashMap<String, String>> {

    private int resource;
    private ArrayList<HashMap<String, String>> items;

    public MyAdapter(@NonNull Context context, int resource,
                     ArrayList<HashMap<String, String>> items) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(resource, parent, false);
        }

        HashMap<String, String> item = items.get(position);

        TextView title = convertView.findViewById(R.id.itemTitle);
        TextView detail = convertView.findViewById(R.id.itemDetail);

        if (item != null) {
            title.setText(item.get("ItemTitle"));
            detail.setText(item.get("ItemDetail"));
        }

        return convertView;
    }
}