package com.example.spendlytics;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etAmount, etNote;
    Spinner spinnerCategory;
    Button btnSave;
    DatabaseHelper db;
    String phone;

    String[] categories = {
            "Food", "Transport", "Shopping", "Entertainment",
            "Health", "Education", "Utilities", "Other"
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_expense);

        db    = new DatabaseHelper(this);
        phone = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getString("phone", "");

        etAmount         = findViewById(R.id.etAmount);
        etNote           = findViewById(R.id.etNote);
        spinnerCategory  = findViewById(R.id.spinnerCategory);
        btnSave          = findViewById(R.id.btnSave);

        TextView tvDate = findViewById(R.id.tvDate);
        String today = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText("📅  " + today);

// Also add cancel button handler:
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> finish());
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        btnSave.setOnClickListener(v -> saveExpense());
    }

    void saveExpense() {
        String amountStr = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Enter amount"); return;
        }
        double amount   = Double.parseDouble(amountStr);
        String category = spinnerCategory.getSelectedItem().toString();
        String note     = etNote.getText().toString().trim();
        // Auto-fill current date
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        Expense expense = new Expense(amount, category, note, date, phone);
        db.addExpense(expense);

        Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
        finish();  // Go back to dashboard
    }
}
