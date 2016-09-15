package com.dkondratov.opengame.model;

public class MenuItem {

    private String title;
    private int resId;

    public int getResId() {
        return resId;
    }

    public String getTitle() {
        return title;
    }

    public MenuItem(int resId, String title) {
        this.resId = resId;
        this.title = title;
    }
}
