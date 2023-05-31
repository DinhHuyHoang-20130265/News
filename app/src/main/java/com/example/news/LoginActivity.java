package com.example.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.news.data.FirebaseData;
import com.example.news.models.User;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {
    TextView ed_user, ed_pass;
    Button login;

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_user = findViewById(R.id.ed_user);
        ed_pass = findViewById(R.id.ed_pass);

        login = findViewById(R.id.btn_login);
        TextView reg = findViewById(R.id.create);
        reg.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        login.setOnClickListener(view -> new FirebaseData().checkLogin(ed_user.getText().toString(), ed_pass.getText().toString()).thenAccept(user -> {
            if (user != null) {
                Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                setUser(user);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        }));
    }

    public void setUser(User user) {
        SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user", new Gson().toJson(user));
        editor.apply();
    }
}
