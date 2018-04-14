package com.learn.yhviews.notepad.entity;

/**
 * Created by yhviews on 2018/4/10.
 */

public class Notify {
    private String time;
    private boolean isOpen;
    private String repeatCycle;
    private int id;

    public String getRepeatCycle() {
        return repeatCycle;
    }

    public void setRepeatCycle(String repeatCycle) {
        this.repeatCycle = repeatCycle;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Notify(String time, boolean isOpen, String repeatCycle, int id) {

        this.time = time;
        this.isOpen = isOpen;
        this.repeatCycle = repeatCycle;
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public Notify(String time, boolean isOpen, String repeatCycle) {
        this.time = time;
        this.isOpen = isOpen;
        this.repeatCycle = repeatCycle;
    }

    public Notify() {

    }
}
