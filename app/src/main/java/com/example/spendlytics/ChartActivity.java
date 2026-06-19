package com.example.spendlytics;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChartActivity extends AppCompatActivity {

    PieChart pieChart;
    BarChart barChart;
    DatabaseHelper db;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        db    = new DatabaseHelper(this);
        phone = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getString("phone", "");

        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        loadPieChart();
        loadBarChart();
    }

    // ── PIE CHART — Category-wise spending ────────────────
    void loadPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        Cursor c = db.getCategoryTotals(phone);

        if (c.moveToFirst()) {
            do {
                String category = c.getString(0);
                float  amount   = c.getFloat(1);
                entries.add(new PieEntry(amount, category));
            } while (c.moveToNext());
        }
        c.close();

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setText("Spending by Category");
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.animateY(1200);
        pieChart.invalidate();
    }

    // ── BAR CHART — Last Week vs This Week ────────────────
    void loadBarChart() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal;

        // This Week
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String tw1 = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String tw2 = sdf.format(cal.getTime());

        // Last Week
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, -7);
        String lw1 = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String lw2 = sdf.format(cal.getTime());

        float thisWeek = (float) db.getExpenseByDateRange(phone, tw1, tw2);
        float lastWeek = (float) db.getExpenseByDateRange(phone, lw1, lw2);

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, lastWeek));
        entries.add(new BarEntry(1f, thisWeek));

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Comparison");
        dataSet.setColors(Color.parseColor("#FF6384"),
                Color.parseColor("#36A2EB"));
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.getDescription().setText("Last Week vs This Week");
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"Last Week", "This Week"}));
        barChart.animateY(1200);
        barChart.invalidate();
    }
}
