package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.adapter.News_Adapter;
import com.example.news.dao.NewsDAO;
import com.example.news.data.FirebaseData;
import com.example.news.models.Item;
import com.example.news.models.User;
import com.example.news.xmlpullparser.XmlPullParserHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    ListView lv;
    public List<Item> ItemLists = new ArrayList<>();
    String link;
    Dialog dialog;
    Button btn_save, btn_cancel;
    NewsDAO dao;
    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        lv = findViewById(R.id.lv_news);
        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        Toast.makeText(getApplicationContext(), "" + link, Toast.LENGTH_SHORT).show();
        if (checkInternet()) {
            downloadNew();
        }
        ImageView goback = findViewById(R.id.goback);
        TextView name = findViewById(R.id.cateView);

        goback.setOnClickListener(v -> onBackPressed());
        name.setText(intent.getStringExtra("nameCate"));
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            addToHistory(i);
            openLink(i);
        });
        editTextSearch = findViewById(R.id.editTextSearch);
        Button buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(v -> {
            String keyword = editTextSearch.getText().toString();
            performSearch(keyword);
        });
        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            saveDialog(i);
            return true;
        });
        dao = new NewsDAO(NewsActivity.this);
    }


    private void performSearch(String keyword) {
        ArrayList<Item> searchResult = new ArrayList<>();
        for (Item item : ItemLists)
            if (item.getTitle().contains(keyword))
                searchResult.add(item);
        News_Adapter adapter = new News_Adapter((Context) NewsActivity.this, searchResult);
        lv.setAdapter(adapter);
    }

    public void addToHistory(int i) {
        SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Type type = new TypeToken<List<Item>>() {
        }.getType();
        List<Item> items = new Gson().fromJson(sharedPref.getString("history", null), type);
        if (!items.contains(ItemLists.get(i))) {
            items.add(ItemLists.get(i));
            editor.putString("history", new Gson().toJson(items));
            editor.apply();
        }
    }

    public boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifi.isConnected() || mobile.isConnected();
    }

    public void openLink(int i) {
        Toast.makeText(getApplicationContext(), "Đang truy cập tin tức", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NewsActivity.this, WebViewActivity.class);
        intent.putExtra("linknews", ItemLists.get(i).getLink());
        startActivity(intent);
    }

    public void saveDialog(int i) {
        dialog = new Dialog(NewsActivity.this);
        dialog.setContentView(R.layout.dialog_save);
        btn_save = dialog.findViewById(R.id.btn_save);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(view -> dialog.dismiss());
        btn_save.setOnClickListener(view -> {
            try {
                ArrayList<Item> list = (ArrayList<Item>) new NewsDAO(NewsActivity.this).getSaved();
                if (!list.contains(ItemLists.get(i))) {
                    new FirebaseData().insertSaved(ItemLists.get(i), getUser());
                    Toast.makeText(getApplicationContext(), "Lưu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bạn đã lưu tin tức này rồi !", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    public User getUser() {
        SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);
        Type type = new TypeToken<User>() {
        }.getType();
        return new Gson().fromJson(sharedPref.getString("user", null), type);
    }

    public void downloadNew() {
        new downloadXML(NewsActivity.this, lv).execute(link);
    }

    @SuppressLint("StaticFieldLeak")
    public class downloadXML extends AsyncTask<String, Void, List<Item>> {

        News_Adapter adapter;
        private ListView listView;
        private Context context;

        public downloadXML(Context context, ListView lv) {
            this.context = context;
            this.listView = lv;
        }

        @Override
        protected List<Item> doInBackground(String... strings) {
            try {
                ItemLists = loadURLfromNetWork(strings[0]);
                return ItemLists;
            } catch (Exception ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Item> list) {
            super.onPostExecute(list);
            adapter = new News_Adapter(context, (ArrayList<Item>) list);
            if (list != null)
                listView.setAdapter(adapter);
        }

        private InputStream downloadURL(String url) throws IOException {
            java.net.URL url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream();
        }

        public List<Item> loadURLfromNetWork(String strUrl) throws Exception {
            XmlPullParserHandler handler = new XmlPullParserHandler();
            ItemLists = null;
            try (InputStream stream = downloadURL(strUrl)) {
                Log.i("00000", stream.toString());
                ItemLists = handler.Pasers(stream);
            }
            Log.i("00000", String.valueOf(ItemLists.size()));
            return ItemLists;
        }
    }
}