package com.example.news;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.news.data.FirebaseData;

public class RegisterActivity extends AppCompatActivity {
    TextView ed_user, ed_pass, ed_comfirm;
    Button reg;
    ImageView img_back_right4;

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ed_user = findViewById(R.id.editTextTenChuTK);
        ed_pass = findViewById(R.id.editMk);
        ed_comfirm = findViewById(R.id.editTextMK);
        reg = findViewById(R.id.buttonDangKy);

        img_back_right4 = findViewById(R.id.img_back_right4);
        img_back_right4.setOnClickListener(view -> onBackPressed());
        reg.setOnClickListener(view -> new FirebaseData().checkReg(ed_user.getText().toString(), ed_pass.getText().toString()).thenAccept(check -> {
            if (ed_pass.getText().toString().equals(ed_comfirm.getText().toString())) {
                if (check) {
                    Toast.makeText(getApplicationContext(), "Đăng ký thành công, mời bạn đăng nhập", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Tên tài khoản đã có người sử dụng", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "2 mật khẩu không trùng nhau", Toast.LENGTH_SHORT).show();
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        }));
    }
}
