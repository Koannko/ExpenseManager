package com.example.expensemanager;

public class ExpenseModel {
    private String expenseId;
    private String note;
    private String category;
    private String type;
    private long amount;
    private long time;
    public ExpenseModel() {

    }

    public ExpenseModel(String expenseId, String note, String category, String type, long amount, long time) {
        this.expenseId = expenseId;
        this.note = note;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
