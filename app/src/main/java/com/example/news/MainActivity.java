package com.example.news;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.news.adapter.NewsAdapter;
import com.example.news.dao.NewsDAO;
import com.example.news.models.News;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ListView lv_main;
    View view_add;
    Dialog dialog;
    TextInputEditText ed_name, ed_link;
    Button btn_add, btn_del, btn_cancel;
    NewsAdapter adapter;
    NewsDAO dao;
    ArrayList<News> list;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_main = findViewById(R.id.lv_main);
        view_add = findViewById(R.id.view_add);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();
                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here
                    return true;
                });

        dao = new NewsDAO(MainActivity.this);
        UpdateLV();

        deleteCache(getApplicationContext()); //xóa cache

        view_add.setOnClickListener(view -> openDialog());
        lv_main.setOnItemClickListener((adapterView, view, i, l) -> {
            if (checkNetwork()) {
                String link = list.get(i).getLink();
                if (!link.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                    intent.putExtra("link", link);
                    startActivity(intent);
                }
            } else {
                NoInternetToast();
            }
        });

        lv_main.setOnItemLongClickListener((adapterView, view, i, l) -> {
            delete(list.get(i).getId());
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDialog() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_add);
        ed_name = dialog.findViewById(R.id.ed_name);
        ed_link = dialog.findViewById(R.id.ed_link);
        btn_add = dialog.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(view -> {
            String name = Objects.requireNonNull(ed_name.getText()).toString().trim();
            String link = Objects.requireNonNull(ed_link.getText()).toString().trim();
            if (name.isEmpty() || link.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                News news = new News();
                news.setName(name);
                news.setLink(link);
                if (dao.insert(news)) {
                    Toast.makeText(getApplicationContext(), "thêm thành công", Toast.LENGTH_SHORT).show();
                    UpdateLV();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "lỗi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public void NoInternetToast() {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.no_internet_toast, null);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(v);
        toast.show();
    }

    private boolean checkNetwork() {
        boolean wifiAvailable = false;
        boolean mobileAvailable = false;
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    wifiAvailable = true;
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    mobileAvailable = true;
        }
        return wifiAvailable || mobileAvailable;
    }

    public void delete(int id) {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_del);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_del = dialog.findViewById(R.id.btn_del);
        btn_cancel.setOnClickListener(view -> dialog.dismiss());
        btn_del.setOnClickListener(view -> {
            if (dao.delete(id)) {
                Toast.makeText(getApplicationContext(), "xóa thành công", Toast.LENGTH_SHORT).show();
                UpdateLV();
            } else {
                Toast.makeText(getApplicationContext(), "lỗi", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        dialog.show();
        UpdateLV();
    }

    public void UpdateLV() {
        list = (ArrayList<News>) dao.getALL();
        adapter = new NewsAdapter(getApplicationContext(), MainActivity.this, list);
        lv_main.setAdapter(adapter);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            Toast.makeText(context.getApplicationContext(), "clear cache success", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "clear cache failed", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}