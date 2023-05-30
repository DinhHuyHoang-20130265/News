package com.example.news.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.news.data.DbHelper;
import com.example.news.models.Item;
import com.example.news.models.News;

import java.util.ArrayList;
import java.util.List;

public class NewsDAO {
    private static final String TABLE_NAME = "News";
    private SQLiteDatabase db;
    public static List<News> listNews = new ArrayList<>();

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
            if (db.insert("Saved", null, values) != -1) ;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean insert(News news) {
        ContentValues values = new ContentValues();
        values.put("name", news.getName());
        values.put("link", news.getLink());
        try {
            if (db.insert(TABLE_NAME, null, values) != -1) ;
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
            if (db.update(TABLE_NAME, values, "id = ?", new String[]{strID}) != -1) ;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(int id) {
        String strID = String.valueOf(id);
        if (db.delete(TABLE_NAME, "id = ?", new String[]{strID}) >= 0) {
            return true;
        }
        return false;
    }

    public List<News> getALL() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return getData(sql);
    }

    public News getNews(int id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id=?";
        List<News> list = getData(sql, String.valueOf(id));
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    @SuppressLint("Range")
    private List<News> getData(String sql, String... selectionArgs) {
        List<News> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
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
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            Item obj = new Item();
            obj.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            obj.setLink(cursor.getString(cursor.getColumnIndex("link")));
            obj.setDate(cursor.getString(cursor.getColumnIndex("dPost")));
            obj.setLinkImg(cursor.getString(cursor.getColumnIndex("linkImg")));
            list.add(obj);
        }
        return list;
    }

    public List<Item> getSaved() {
        String sql = "SELECT * FROM " + "Saved";
        return getDataSaved(sql);
    }
}
