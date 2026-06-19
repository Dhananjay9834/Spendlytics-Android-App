package com.example.spendlytics;

public class Expense {
    private int    id;
    private double amount;
    private String category, note, date, phone;

    public Expense(int id, double amount, String category, String note, String date, String phone) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.date = date;
        this.phone = phone;
    }
    // Constructor for new expense (no id yet)
    public Expense(double amount, String category, String note, String date, String phone) {
        this(-1, amount, category, note, date, phone);
    }
    public int    getId()       { return id; }
    public double getAmount()   { return amount; }
    public String getCategory() { return category; }
    public String getNote()     { return note; }
    public String getDate()     { return date; }
    public String getPhone()    { return phone; }
}
