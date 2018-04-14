package com.learn.yhviews.notepad.entity;

/**
 * Created by yhviews on 2018/4/11.
 * 为了辅助存储每一天的消费记录的实体类
 */

public class DayCostHelper {
    private String day;//几号
    private float sumMoney;//这一天消费的总额
    private int position;//在list中的位置

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public float getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(float sumMoney) {
        this.sumMoney = sumMoney;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public DayCostHelper() {

    }

    public DayCostHelper(String day, float sumMoney, int position) {

        this.day = day;
        this.sumMoney = sumMoney;
        this.position = position;
    }
}
