package com.example.news;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SavedActivity extends AppCompatActivity {
    ListView lv;
    List<Item> ItemLists = new ArrayList<>();
    Dialog dialog;
    Button btn_remove, btn_cancel;
    NewsDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        lv = findViewById(R.id.lv_saved);
        UpdateLV();
        ItemLists = (ArrayList<Item>) new NewsDAO(SavedActivity.this).getSaved();
        lv.setOnItemClickListener((adapterView, view, i, l) -> openLink(i));
        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            removeItemDialog(i);
            return true;
        });
        dao = new NewsDAO(SavedActivity.this);
    }

    public void openLink(int i) {
        Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SavedActivity.this, WebViewActivity.class);
        intent.putExtra("linknews", ItemLists.get(i).getLink());
        startActivity(intent);
    }

    private void removeItemDialog(int i) {
        dialog = new Dialog(SavedActivity.this);
        dialog.setContentView(R.layout.dialog_del);
        btn_remove = dialog.findViewById(R.id.btn_del);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(view -> dialog.dismiss());
        btn_remove.setOnClickListener(view -> {
            try {
                Item item = ItemLists.get(i);
                dao.deleteSaved(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "xóa thành công", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            UpdateLV();
        });
        dialog.show();
    }

    public void UpdateLV() {
        ArrayList<Item> list = (ArrayList<Item>) new NewsDAO(SavedActivity.this).getSaved();
        if (!list.isEmpty()) {
            TextView empty = findViewById(R.id.empty);
            empty.setVisibility(View.INVISIBLE);
            SavedAdapter adapter = new SavedAdapter(getApplicationContext(), SavedActivity.this, list);
            lv.setAdapter(adapter);
        }
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