package com.example.news;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.news.data.FirebaseData;

public class RegisterActivity extends AppCompatActivity {
    TextView ed_user, ed_pass, ed_comfirm;
    Button reg;

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ed_user = findViewById(R.id.editTextTK);
        ed_pass = findViewById(R.id.editTextMK);
        ed_comfirm = findViewById(R.id.editTextXNMK);
        reg = findViewById(R.id.buttonDangKy);

        reg.setOnClickListener(view -> new FirebaseData().checkReg(ed_user.getText().toString(), ed_pass.getText().toString()).thenAccept(check -> {
            if (check) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        }));
    }
}
