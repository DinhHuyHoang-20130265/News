package com.example.news;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.news.adapter.NewsAdapter;
import com.example.news.adapter.News_Adapter;
import com.example.news.dao.NewsDAO;
import com.example.news.models.Item;
import com.example.news.models.News;

import java.util.ArrayList;
import java.util.List;

public class SavedActivity extends AppCompatActivity {
    ListView lv;
    List<Item> ItemLists = new ArrayList<>();
    Dialog dialog;
    Button btn_add, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        Intent intent = getIntent();
        lv = findViewById(R.id.lv_saved);
        UpdateLV();
    }

    public void UpdateLV() {
        ArrayList<Item> list = (ArrayList<Item>) new NewsDAO(SavedActivity.this).getSaved();
        SavedAdapter adapter = new SavedAdapter(getApplicationContext(), SavedActivity.this, list);
        lv.setAdapter(adapter);
    }

    public class SavedAdapter extends ArrayAdapter<Item> {
        private final Context context;
        private final ArrayList<Item> list;
        private final SavedActivity main;

        public SavedAdapter(@NonNull Context context, SavedActivity activity, ArrayList<Item> list) {
            super(context, 0, list);
            this.context = context;
            this.list = list;
            this.main = activity;
        }

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
                TextView tv_link_news = v.findViewById(R.id.iv_news);
                TextView tv_date_news = v.findViewById(R.id.tv_date_news);
                iv_news.setImageURI(Uri.parse(item.getLinkImg()));
                tv_title_item_news.setText(item.getTitle());
                tv_link_news.setText(item.getLink());
                tv_date_news.setText(item.getDate());
            }

            return v;
        }
    }
}