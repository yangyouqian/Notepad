package com.learn.yhviews.notepad.entity;

/**
 * Created by yhviews on 2018/4/8.
 */

public class Cost {
    private String type;
    private String money;
    private String date;
    private String pay;//0 为收入 1 为支出
    private String note;

    public Cost() {
    }

    public Cost(String type, String money, String date, String pay, String note) {
        this.type = type;
        this.money = money;
        this.date = date;
        this.pay = pay;
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
