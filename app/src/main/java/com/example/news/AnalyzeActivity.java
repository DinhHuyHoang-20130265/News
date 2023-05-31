package com.example.news;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.news.data.FirebaseData;
import com.example.news.models.User;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeActivity extends AppCompatActivity {
    List<User> userList = new ArrayList<>();
    ListView lv;
    Dialog dialog;
    Button btn_del, btn_cancel;
    ImageView goback;

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quanlytk);
        lv = findViewById(R.id.lv_acc);
        UpdateLV();
        lv.setOnItemClickListener((adapterView, view, i, l) -> UpdateLV());

        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            openDialog(i);
            return false;
        });

        goback = findViewById(R.id.goback);
        goback.setOnClickListener(v -> onBackPressed());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void UpdateLV() {
        new FirebaseData().getALLUser().thenAccept(List -> {
            userList = List;
            UserAdapter adapter = new UserAdapter(getApplicationContext(), AnalyzeActivity.this, (ArrayList<User>) userList);
            lv.setAdapter(adapter);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openDialog(int i) {
        dialog = new Dialog(AnalyzeActivity.this);
        dialog.setContentView(R.layout.dialog_del);
        btn_del = dialog.findViewById(R.id.btn_del);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
        btn_del.setOnClickListener(view -> {
            try {
                User user = userList.get(i);
                new FirebaseData().deleteUser(user);
                Toast.makeText(getApplicationContext(), "xóa thành công", Toast.LENGTH_SHORT).show();
                UpdateLV();
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialog.show();
    }

    public class UserAdapter extends ArrayAdapter<User> {
        private final Context context;
        private final ArrayList<User> list;
        private final AnalyzeActivity main;

        public UserAdapter(@NonNull Context context, AnalyzeActivity activity, ArrayList<User> list) {
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
                v = inflater.inflate(R.layout.item_user, null);
            }
            final User user = list.get(position);
            if (v != null) {
                TextView user_id = v.findViewById(R.id.user_id);
                TextView user_name = v.findViewById(R.id.user_name);
                TextView user_type = v.findViewById(R.id.user_type);
                user_id.setText("ID: " + user.getId());
                user_name.setText("Tên đăng nhập: " + user.getUsername());
                user_type.setText("Loại tài khoản: " + (user.getType() == 0 ? "Người dùng" : "Quản lý"));
            }

            assert v != null;
            return v;
        }
    }
}
