package com.example.news.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.news.data.DbHelper;
import com.example.news.models.Item;
import com.example.news.models.News;

import java.util.ArrayList;
import java.util.List;

public class NewsDAO {
    private static final String TABLE_NAME = "News";
    private SQLiteDatabase db;

    public NewsDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public boolean insertSaved(Item item) {
        ContentValues values = new ContentValues();
        values.put("title", item.getTitle());
        values.put("link", item.getLink());
        values.put("date", item.getDate());
        values.put("linkImg", item.getLinkImg());
        try {
            db.insert("Saved", null, values);
            Log.d("success_Insert", "Success");
            Log.d("Table_after", getSaved().toString());
            return true;
        } catch (Exception e) {
            Log.d("fail_Insert", e.toString());
            return false;
        }
    }

    public boolean insert(News news) {
        ContentValues values = new ContentValues();
        values.put("name", news.getName());
        values.put("link", news.getLink());
        try {
            db.insert(TABLE_NAME, null, values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean update(News news) {
        ContentValues values = new ContentValues();
        values.put("id", news.getId());
        values.put("name", news.getName());
        values.put("link", news.getLink());
        String strID = String.valueOf(news.getId());
        try {
            db.update(TABLE_NAME, values, "id = ?", new String[]{strID});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(int id) {
        String strID = String.valueOf(id);
        return db.delete(TABLE_NAME, "id = ?", new String[]{strID}) >= 0;
    }

    public List<News> getALL() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return getData(sql);
    }

    public News getNews(int id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id=?";
        List<News> list = getData(sql, String.valueOf(id));
        return list.get(0);
    }

    @SuppressLint("Range")
    private List<News> getData(String sql, String... selectionArgs) {
        List<News> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            News obj = new News();
            obj.setId(cursor.getInt(cursor.getColumnIndex("id")));
            obj.setName(cursor.getString(cursor.getColumnIndex("name")));
            obj.setLink(cursor.getString(cursor.getColumnIndex("link")));
            list.add(obj);
        }
        return list;
    }

    @SuppressLint("Range")
    private List<Item> getDataSaved(String sql, String... selectionArgs) {
        List<Item> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            Item obj = new Item();
            obj.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id"))));
            obj.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            obj.setLink(cursor.getString(cursor.getColumnIndex("link")));
            obj.setDate(cursor.getString(cursor.getColumnIndex("date")));
            obj.setLinkImg(cursor.getString(cursor.getColumnIndex("linkImg")));
            list.add(obj);
        }
        return list;
    }

    public List<Item> getSaved() {
        String sql = "SELECT * FROM " + "Saved";
        return getDataSaved(sql);
    }

    public boolean deleteSaved(Item item) {
        String strID = String.valueOf(item.getId());
        return db.delete("Saved", "id = ?", new String[]{strID}) >= 0;
    }
}
