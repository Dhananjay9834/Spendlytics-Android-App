package com.example.spendlytics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ExpenseAdapter adapter;
    DatabaseHelper db;
    TextView tvTotal, tvEmpty, tvWelcome;
    String phone, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load saved session
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        phone = prefs.getString("phone", "");
        name  = prefs.getString("name", "User");

        db          = new DatabaseHelper(this);
        tvTotal     = findViewById(R.id.tvTotal);
        tvEmpty     = findViewById(R.id.tvEmpty);
        tvWelcome   = findViewById(R.id.tvWelcome);
        recyclerView = findViewById(R.id.recyclerView);

        tvWelcome.setText("Hello, " + name + "!");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // FAB — Add expense
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddExpenseActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();  // Refresh list every time we come back
    }

    void loadExpenses() {
        List<Expense> list = db.getAllExpenses(phone);
        double total = db.getTotalExpense(phone);

        tvTotal.setText("Total: ₹" + String.format("%.2f", total));
        tvEmpty.setVisibility(list.isEmpty() ?
                android.view.View.VISIBLE : android.view.View.GONE);

        adapter = new ExpenseAdapter(list, id -> {
            // Delete callback
            db.deleteExpense(id);
            loadExpenses();
        });
        recyclerView.setAdapter(adapter);
    }

    // ── Options Menu ─────────────────────────────────────
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_analysis) {
            startActivity(new Intent(this, AnalysisActivity.class));
        } else if (id == R.id.menu_charts) {
            startActivity(new Intent(this, ChartActivity.class));
        } else if (id == R.id.menu_logout) {
            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }
}
