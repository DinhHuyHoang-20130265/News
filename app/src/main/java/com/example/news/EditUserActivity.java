package com.example.news;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.news.data.FirebaseData;
import com.example.news.models.User;
import com.google.gson.Gson;

public class EditUserActivity extends AppCompatActivity {
    Button buttonSua;
    EditText editId, editTextType, editTextMK, editTextTK;
    ImageView img_back_right2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        buttonSua = findViewById(R.id.buttonSua);
        img_back_right2 = findViewById(R.id.img_back_right2);
        editId = findViewById(R.id.editId);
        editTextType = findViewById(R.id.editTextType);
        editTextMK = findViewById(R.id.editTextMK);
        editTextTK = findViewById(R.id.editTextTK);

        editId.setEnabled(false);

        Gson gson = new Gson();
        User user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        img_back_right2.setOnClickListener(view -> onBackPressed());
        editId.setText(user.getId() + "");
        editTextType.setText(user.getType() + "");
        editTextMK.setText(user.getPassword() + "");
        editTextTK.setText(user.getUsername() + "");
        buttonSua.setOnClickListener(view -> {
            String id = editId.getText().toString();
            String name = editTextTK.getText().toString();
            String pass = editTextMK.getText().toString();
            int type = Integer.parseInt(editTextType.getText().toString());
            if (name.equals("") || pass.equals("") || (type != 1 && type != 0)) {
                Toast.makeText(getApplicationContext(), "Yêu cầu nhập các thông tin đầy đủ và hợp lệ", Toast.LENGTH_SHORT).show();
            } else {
                new FirebaseData().updateUser(id, name, pass, type);
                Toast.makeText(getApplicationContext(), "Cập nhật tài khoản thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AnalyzeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
