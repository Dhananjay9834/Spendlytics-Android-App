package com.example.spendlytics;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;

public class AnalysisActivity extends AppCompatActivity {

    TextView tvThisWeek, tvLastWeek, tvThisMonth;
    DatabaseHelper db;
    String phone;
    TextView tvThisWeekDates, tvLastWeekDates, tvMonthName, tvComparison;
    ProgressBar progressThisWeek, progressLastWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        db    = new DatabaseHelper(this);
        phone = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getString("phone", "");


        tvThisWeekDates  = findViewById(R.id.tvThisWeekDates);
        tvLastWeekDates  = findViewById(R.id.tvLastWeekDates);
        tvMonthName      = findViewById(R.id.tvMonthName);
        tvComparison     = findViewById(R.id.tvComparison);
        progressThisWeek = findViewById(R.id.progressThisWeek);
        progressLastWeek = findViewById(R.id.progressLastWeek);

        findViewById(R.id.btnViewCharts).setOnClickListener(v ->
                startActivity(new Intent(this, ChartActivity.class)));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tvThisWeek  = findViewById(R.id.tvThisWeek);
        tvLastWeek  = findViewById(R.id.tvLastWeek);
        tvThisMonth = findViewById(R.id.tvThisMonth);

        calculateAnalysis();
    }

    void calculateAnalysis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        // ── This Week ──────────────────────────────────────
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String thisWeekStart = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String thisWeekEnd = sdf.format(cal.getTime());

        // ── Last Week ──────────────────────────────────────
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, -7);
        String lastWeekStart = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String lastWeekEnd = sdf.format(cal.getTime());

        // ── This Month ─────────────────────────────────────
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String monthStart = sdf.format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String monthEnd = sdf.format(cal.getTime());

        double tw = db.getExpenseByDateRange(phone, thisWeekStart, thisWeekEnd);
        double lw = db.getExpenseByDateRange(phone, lastWeekStart, lastWeekEnd);
        double tm = db.getExpenseByDateRange(phone, monthStart, monthEnd);

        tvThisWeek.setText("This Week: ₹" + String.format("%.2f", tw));
        tvLastWeek.setText("Last Week: ₹" + String.format("%.2f", lw));
        tvThisMonth.setText("This Month: ₹" + String.format("%.2f", tm));

        // Show date ranges under each card
        tvThisWeekDates.setText(thisWeekStart + "  →  " + thisWeekEnd);
        tvLastWeekDates.setText(lastWeekStart + "  →  " + lastWeekEnd);

// Show current month name
        String monthName = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
        tvMonthName.setText(monthName);

// Progress bars — scale relative to whichever week is higher
        double maxWeek = Math.max(tw, lw);
        if (maxWeek > 0) {
            progressThisWeek.setProgress((int) ((tw / maxWeek) * 100));
            progressLastWeek.setProgress((int) ((lw / maxWeek) * 100));
        }

// Comparison message
        if (tw < lw) {
            tvComparison.setText("🎉 You spent less this week! Keep it up.");
            tvComparison.setTextColor(android.graphics.Color.parseColor("#1E6B3C"));
        } else if (tw > lw) {
            tvComparison.setText("⚠️ You spent more this week than last week.");
            tvComparison.setTextColor(android.graphics.Color.parseColor("#C62828"));
        } else {
            tvComparison.setText("➡️ Spending is same as last week.");
            tvComparison.setTextColor(android.graphics.Color.parseColor("#FF6F00"));
        }
    }
}
