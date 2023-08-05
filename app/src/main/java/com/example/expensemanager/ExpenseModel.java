package com.example.expensemanager;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExpenseModel implements Serializable {
    private String expenseId;
    private String note;
    private String category;
    private String type;
    private long amount;
    private long time;
    private String uid;
    public ExpenseModel() {

    }

    public ExpenseModel(String expenseId, String note, String category, String type, long amount, long time, String uid) {
        this.expenseId = expenseId;
        this.note = note;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.time = time;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public String getCategory() {
        return category;
    }

    public long getAmount() {
        return amount;
    }

    public long getTime() {
        return time;
    }

    public String getDate() {
        long x = this.getTime();
        DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm");
        Date sol = new Date(x);
        return obj.format(sol);
    }
}
