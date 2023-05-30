package com.example.news;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.news.dao.NewsDAO;
import com.example.news.models.Item;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    ListView lv;
    public List<Item> ItemLists = new ArrayList<>();
    Dialog dialog;
    Button btn_add, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        lv = findViewById(R.id.lv_history);
        Intent intent = getIntent();

        lv.setOnItemClickListener((adapterView, view, i, l) -> {

        });
        lv.setOnItemLongClickListener((AdapterView.OnItemLongClickListener) (adapterView, view, i, l) -> {
            openDialog(ItemLists.get(i));
            return false;
        });
    }

    public void openDialog(Item item) {
        dialog = new Dialog(HistoryActivity.this);
        dialog.setContentView(R.layout.dialog_add);
        btn_add = dialog.findViewById(R.id.btn_add);
        NewsDAO dao = new NewsDAO(HistoryActivity.this);
        btn_add.setOnClickListener(view -> {
            if (dao.insertHistory(item)) {
                Toast.makeText(getApplicationContext(), "thêm thành công", Toast.LENGTH_SHORT).show();
                // UpdateLV();
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}
