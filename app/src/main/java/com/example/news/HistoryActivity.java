package com.example.news;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.news.dao.NewsDAO;
import com.example.news.models.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class HistoryActivity extends AppCompatActivity {
    ListView lv;
    public List<Item> ItemLists = new ArrayList<>();
    Dialog dialog;
    Button btn_del, btn_cancel;
    ImageView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        lv = findViewById(R.id.lv_history);

        UpdateLV();
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            addToHistory(i);
            openLink(i);
        });
        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            openDialog(i);
            return true;
        });
        goback = findViewById(R.id.goback);
        goback.setOnClickListener(v -> onBackPressed());
    }

    public void openLink(int i) {
        Toast.makeText(getApplicationContext(), "đang truy cập vào tin tức", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HistoryActivity.this, WebViewActivity.class);
        intent.putExtra("linknews", ItemLists.get(i).getLink());
        startActivity(intent);
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

    public void openDialog(int i) {
        dialog = new Dialog(HistoryActivity.this);
        dialog.setContentView(R.layout.dialog_del);
        btn_del = dialog.findViewById(R.id.btn_del);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
        btn_del.setOnClickListener(view -> {
            SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            Type type = new TypeToken<List<Item>>() {
            }.getType();
            List<Item> items = new Gson().fromJson(sharedPref.getString("history", null), type);
            items.remove(i);
            editor.putString("history", new Gson().toJson(items));
            editor.apply();
            UpdateLV();
            lv.invalidate();
            Toast.makeText(getApplicationContext(), "xóa thành công", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.show();
    }

    public void UpdateLV() {
        SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);
        Type type = new TypeToken<List<Item>>() {
        }.getType();
        List<Item> items = new Gson().fromJson(sharedPref.getString("history", null), type);
        if (!items.isEmpty()) {
            TextView empty = findViewById(R.id.empty);
            empty.setVisibility(View.INVISIBLE);
            HistoryAdapter adapter = new HistoryAdapter(getApplicationContext(), HistoryActivity.this, (ArrayList<Item>) items);
            lv.setAdapter(adapter);
        }
    }

    public class HistoryAdapter extends ArrayAdapter<Item> {
        private final Context context;
        private final ArrayList<Item> list;
        private final HistoryActivity main;

        public HistoryAdapter(@NonNull Context context, HistoryActivity activity, ArrayList<Item> list) {
            super(context, 0, list);
            this.context = context;
            this.list = list;
            this.main = activity;
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.item_newss, null);
            }
            final Item item = list.get(position);
            if (v != null) {
                ImageView iv_news = v.findViewById(R.id.iv_news);
                TextView tv_title_item_news = v.findViewById(R.id.tv_title_item_news);
                TextView tv_link_news = v.findViewById(R.id.tv_link_news);
                TextView tv_date_news = v.findViewById(R.id.tv_date_news);
                Picasso.with(getContext()).load(item.getLinkImg()).into(iv_news);
                tv_title_item_news.setText(item.getTitle());
                tv_link_news.setText(item.getLink());
                tv_date_news.setText(item.getDate());
            }

            assert v != null;
            return v;
        }
    }
}
