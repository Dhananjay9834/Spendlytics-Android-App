package com.example.spendlytics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;
import android.os.CountDownTimer;

import java.util.concurrent.TimeUnit;


public class OtpActivity extends AppCompatActivity {

    EditText etOtp;
    Button btnVerify;
    String verificationId, phone, name;
    FirebaseAuth mAuth;
    TextView tvTimer, tvPhoneHint;
    Button btnResend, btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        FirebaseAuth.getInstance().getFirebaseAuthSettings()
                .setAppVerificationDisabledForTesting(true);

        mAuth = FirebaseAuth.getInstance();
        etOtp     = findViewById(R.id.etOtp);
        btnVerify = findViewById(R.id.btnVerify);
        tvTimer        = findViewById(R.id.tvTimer);
        tvPhoneHint    = findViewById(R.id.tvPhoneHint);
        btnResend      = findViewById(R.id.btnResend);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        phone = getIntent().getStringExtra("phone");
        name  = getIntent().getStringExtra("name");

        sendOtp(phone);
        tvPhoneHint.setText("OTP sent to " + phone);

        btnBackToLogin.setOnClickListener(v -> finish());

        btnResend.setOnClickListener(v -> {
            sendOtp(phone);
            startTimer();
            btnResend.setEnabled(false);
            Toast.makeText(this, "OTP Resent!", Toast.LENGTH_SHORT).show();
        });

// Start countdown timer when activity opens
        startTimer();

        btnVerify.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            if (otp.length() == 6) verifyOtp(otp);
            else Toast.makeText(this, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show();
        });
    }

    void sendOtp(String phoneNumber) {

        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            @NonNull PhoneAuthCredential credential) {
                        // Auto-verification (rare) — sign in directly
                        signInWithCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(
                            @NonNull FirebaseException e) {
                        // Show exact error message so you can debug
                        Toast.makeText(OtpActivity.this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();

                        e.printStackTrace();  // Check Logcat for full error
                    }

                    @Override
                    public void onCodeSent(@NonNull String id,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = id;
                        Toast.makeText(OtpActivity.this,
                                "OTP Sent Successfully!",
                                Toast.LENGTH_SHORT).show();
                    }
                };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    void verifyOtp(String otp) {
        PhoneAuthCredential cred = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithCredential(cred);
    }

    void signInWithCredential(PhoneAuthCredential cred) {
        mAuth.signInWithCredential(cred).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Save user session
                SharedPreferences.Editor editor =
                        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                editor.putString("phone", phone);
                editor.putString("name", name);
                editor.apply();

                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void startTimer() {
        new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvTimer.setText("⏱  OTP expires in " + seconds + " seconds");
                tvTimer.setTextColor(
                        seconds <= 10
                                ? android.graphics.Color.parseColor("#C62828")  // red when < 10s
                                : android.graphics.Color.parseColor("#FF6F00")  // orange normally
                );
            }

            @Override
            public void onFinish() {
                tvTimer.setText("⌛  OTP expired. Please resend.");
                tvTimer.setTextColor(android.graphics.Color.parseColor("#C62828"));
                btnResend.setEnabled(true);   // Enable resend after expiry
                btnVerify.setEnabled(false);  // Disable verify
            }

        }.start();
    }
}
