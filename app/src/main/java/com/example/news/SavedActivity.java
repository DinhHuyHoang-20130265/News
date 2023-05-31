package com.example.news;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.news.adapter.NewsAdapter;
import com.example.news.dao.NewsDAO;
import com.example.news.data.FirebaseData;
import com.example.news.models.Item;
import com.example.news.models.News;
import com.example.news.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SavedActivity extends AppCompatActivity {
    ListView lv;
    List<Item> ItemLists = new ArrayList<>();
    Dialog dialog;
    Button btn_remove, btn_cancel;
    NewsDAO dao;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        lv = findViewById(R.id.lv_saved);
        UpdateLV();
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            addToHistory(i);
            openLink(i);
        });
        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            removeItemDialog(i);
            return true;
        });
        dao = new NewsDAO(SavedActivity.this);
    }

    public void openLink(int i) {
        Toast.makeText(getApplicationContext(), "đang truy cập vào tin tức", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SavedActivity.this, WebViewActivity.class);
        intent.putExtra("linknews", ItemLists.get(i).getLink());
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void removeItemDialog(int i) {
        dialog = new Dialog(SavedActivity.this);
        dialog.setContentView(R.layout.dialog_del);
        btn_remove = dialog.findViewById(R.id.btn_del);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(view -> dialog.dismiss());
        btn_remove.setOnClickListener(view -> {
            try {
                Item item = ItemLists.get(i);
                new FirebaseData().deleteSaved(item, getUser());
            } catch (Exception e) {
                Log.d("Super error", e.toString());
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "xóa thành công", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            UpdateLV();
        });
        dialog.show();
    }

    public User getUser() {
        SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);
        Type type = new TypeToken<User>() {
        }.getType();
        return new Gson().fromJson(sharedPref.getString("user", null), type);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void UpdateLV() {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.getSaved(getUser()).thenAccept(newsList -> {
            ItemLists = newsList;
            TextView empty = findViewById(R.id.empty);
            empty.setVisibility(View.INVISIBLE);
            SavedAdapter adapter = new SavedAdapter(getApplicationContext(), SavedActivity.this, (ArrayList<Item>) newsList);
            lv.setAdapter(adapter);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
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