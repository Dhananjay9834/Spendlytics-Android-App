package com.example.spendlytics;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    EditText etName, etPhone;
    Button btnSendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName   = findViewById(R.id.etName);
        etPhone  = findViewById(R.id.etPhone);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        btnSendOtp.setOnClickListener(v -> {
            String name  = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                etName.setError("Enter your name"); return;
            }
            if (phone.length() != 10) {
                etPhone.setError("Enter valid 10-digit number"); return;
            }

            // Pass name + phone to OtpActivity
            Intent intent = new Intent(this, OtpActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("phone", "+91" + phone);
            startActivity(intent);
        });
    }
}
