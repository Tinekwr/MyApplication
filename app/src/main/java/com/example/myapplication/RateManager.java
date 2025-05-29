package com.example.myapplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class RateManager extends SQLiteOpenHelper {
    private static final String DB_NAME = "rate.db";
    private static final String TABLE_NAME = "rate";
    private static final int DB_VERSION = 1;

    public RateManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, cname TEXT, cval REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void saveRate(RateItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cname", item.getCname());
        values.put("cval", item.getCval());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<RateItem> getAllRates() {
        List<RateItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            RateItem item = new RateItem();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            item.setCname(cursor.getString(cursor.getColumnIndexOrThrow("cname")));
            item.setCval(cursor.getFloat(cursor.getColumnIndexOrThrow("cval")));
            list.add(item);
        }

        cursor.close();
        db.close();
        return list;
    }

    public void clearAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
