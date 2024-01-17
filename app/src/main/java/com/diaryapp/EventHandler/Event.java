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

    // сеттеры
    public void setId(int eventId) {
        this.eventId = eventId;
    }

    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public void setGroup(String groupName) {
        this.groupName = groupName;
    }

    public void setTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setType(int eventType) {
        this.eventType = eventType;
    }

    public void setStartTime(int eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public void setEndTime(int eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public void setColor(int eventColor) {
        this.eventColor = eventColor;
    }

    // геттеры
    public int getId() {
        return eventId;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String getGroup() {
        return groupName;
    }

    public String getTitle() {
        return eventTitle;
    }

    public String getDate() {
        return eventDate;
    }

    public int getType() {
        return eventType;
    }

    public int getStartTime() {
        return eventStartTime;
    }

    public int getEndTime() {
        return eventEndTime;
    }

    public int getColor() {
        return eventColor;
    }


    @SuppressLint("Range")
    public Event() {
        this.eventId = 0;
        this.isClosed = false;
        this.groupName = "None";
        this.eventTitle = "Untitled";
        this.eventDate = "2024-01-01";
        this.eventType = 1;
        this.eventStartTime = 1000;
        this.eventEndTime = 2400;
        this.eventColor = 1;
    }


    // конструктор из данных напрямую
    public Event(boolean isClosed, String groupName, String eventTitle, String eventDate,
                 int eventType, int eventStartTime, int eventEndTime, int eventColor) {
        this.isClosed = isClosed;
        this.groupName = groupName;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventColor = eventColor;
    }

    // конструктор из курсора базы данных
    @SuppressLint("Range")
    public Event(Cursor cursor) {
        this.eventId = cursor.getInt(cursor.getColumnIndex("event_id"));
        this.isClosed = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("is_closed")));
        this.groupName = cursor.getString(cursor.getColumnIndex("group_name"));
        this.eventTitle = cursor.getString(cursor.getColumnIndex("event_title"));
        this.eventDate = cursor.getString(cursor.getColumnIndex("event_date"));
        this.eventType = cursor.getInt(cursor.getColumnIndex("event_type"));
        this.eventStartTime = cursor.getInt(cursor.getColumnIndex("event_start_time"));
        this.eventEndTime = cursor.getInt(cursor.getColumnIndex("event_end_time"));
        this.eventColor = cursor.getInt(cursor.getColumnIndex("event_color"));
    }

    public void save(DbHandler db) {
        db.addEvent(this);
    }

    public void update(DbHandler db) {
        db.updateEvent(this);
    }

    public void delete(DbHandler db) {
        db.deleteEvent(this);
    }

    public static Event getById(DbHandler db, int Id) {
        return db.getEventById(Id);
    }
}
