package com.example.spendlytics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "expenses.db";
    private static final int    DB_VERSION = 1;

    // Table and column names
    public static final String TABLE   = "expenses";
    public static final String COL_ID       = "id";
    public static final String COL_AMOUNT   = "amount";
    public static final String COL_CATEGORY = "category";
    public static final String COL_NOTE     = "note";
    public static final String COL_DATE     = "date";
    public static final String COL_PHONE    = "phone";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
                + COL_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_AMOUNT   + " REAL NOT NULL, "
                + COL_CATEGORY + " TEXT NOT NULL, "
                + COL_NOTE     + " TEXT, "
                + COL_DATE     + " TEXT NOT NULL, "
                + COL_PHONE    + " TEXT NOT NULL)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // ── CREATE ─────────────────────────────────────────────
    public long addExpense(Expense e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_AMOUNT,   e.getAmount());
        cv.put(COL_CATEGORY, e.getCategory());
        cv.put(COL_NOTE,     e.getNote());
        cv.put(COL_DATE,     e.getDate());
        cv.put(COL_PHONE,    e.getPhone());
        long id = db.insert(TABLE, null, cv);
        db.close();
        return id;
    }


    public List<Expense> getAllExpenses(String phone) {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE + " WHERE " + COL_PHONE + "=? ORDER BY id DESC",
                new String[]{phone});
        if (c.moveToFirst()) {
            do {
                list.add(new Expense(
                        c.getInt(0),
                        c.getDouble(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }


    public void deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public double getTotalExpense(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE + " WHERE " + COL_PHONE + "=?",
                new String[]{phone});
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close(); db.close();
        return total;
    }

    public Cursor getCategoryTotals(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + COL_CATEGORY + ", SUM(" + COL_AMOUNT + ") AS total"
                        + " FROM " + TABLE + " WHERE " + COL_PHONE + "=?"
                        + " GROUP BY " + COL_CATEGORY,
                new String[]{phone});
    }

    public double getExpenseByDateRange(String phone, String from, String to) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE
                        + " WHERE " + COL_PHONE + "=? AND " + COL_DATE + " BETWEEN ? AND ?",
                new String[]{phone, from, to});
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        db.close();
        return total;
    }
}
