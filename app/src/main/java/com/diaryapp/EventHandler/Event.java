package com.diaryapp.EventHandler;

import android.annotation.SuppressLint;
import android.database.Cursor;

public class Event {
    private int eventId;
    private boolean isClosed;
    private String groupName;
    private String eventTitle;
    private String eventDate;
    private int eventType;
    private int eventStartTime;
    private int eventEndTime;
    private int eventColor;

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(int eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public int getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(int eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public int getEventColor() {
        return eventColor;
    }

    public void setEventColor(int eventColor) {
        this.eventColor = eventColor;
    }

    // дефолт
    public Event(String groupName, String eventTitle, String eventDate,
                 int eventType, int eventStartTime, int eventEndTime, int eventColor) {
        this.groupName = groupName;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventColor = eventColor;
    }

    // из курсора базы данных
    @SuppressLint("Range")
    public Event(Cursor cursor) {
        this.eventId = cursor.getInt(cursor.getColumnIndex("event_id"));
        this.groupName = cursor.getString(cursor.getColumnIndex("group_name"));
        this.eventTitle = cursor.getString(cursor.getColumnIndex("event_title"));
        this.eventDate = cursor.getString(cursor.getColumnIndex("event_date"));
        this.eventType = cursor.getInt(cursor.getColumnIndex("event_type"));
        this.eventStartTime = cursor.getInt(cursor.getColumnIndex("event_start_time"));
        this.eventEndTime = cursor.getInt(cursor.getColumnIndex("event_end_time"));
        this.eventColor = cursor.getInt(cursor.getColumnIndex("event_color"));
    }

    // пустой
    public Event() {
    }
}
