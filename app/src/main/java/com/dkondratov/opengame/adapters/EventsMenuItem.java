package com.dkondratov.opengame.adapters;

import android.view.View;

import com.dkondratov.opengame.model.Event;

public class EventsMenuItem {

    public static final int CATEGORY_ITEM = 0;
    public static final int EVENT_ITEM = 1;

    private String title;
    private int type;
    private Event event;
    private View.OnClickListener onClickListener;

    public EventsMenuItem(String title, int type, Event event, View.OnClickListener onClickListener) {
        this.title = title;
        this.type = type;
        this.event = event;
        this.onClickListener = onClickListener;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public Event getEvent() {
        return event;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

}
