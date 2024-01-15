package com.diaryapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.diaryapp.EventHandler.Event;

import java.util.ArrayList;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 1;

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE events (" +
                "event_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "is_closed BOOL," +
                "group_name TEXT," +
                "event_title TEXT," +
                "event_date DATE," +
                "event_type INTEGER," +
                "event_start_time INTEGER," +
                "event_end_time INTEGER," +
                "event_color INTEGER)";
        db.execSQL(createTableQuery);

        String createGroupTableQuery = "CREATE TABLE event_groups (" +
                "group_name TEXT PRIMARY KEY)";
        db.execSQL(createGroupTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS event_groups");
        onCreate(db);
    }

    // управление группами -----------------------------------------------

    // добавить группу
    public void addGroup(String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("group_name", groupName);

        db.insert("event_groups", null, values);
        db.close();
    }

    // удалить группу
    public void deleteGroup(String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("event_groups", "group_name=?", new String[]{groupName});
        db.delete("events", "group_name=?", new String[]{groupName});
        db.close();
    }

    // изменить группу (старое имя -> новое имя)
    public void updateGroup(String oldGroupName, String newGroupName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("group_name", newGroupName);

        db.update("event_groups", values, "group_name=?", new String[]{oldGroupName});

        // обновление связанных событий
        ContentValues eventValues = new ContentValues();
        eventValues.put("group_name", newGroupName);

        db.update("events", eventValues, "group_name=?", new String[]{oldGroupName});
        db.close();
    }

    // получить все группы
    public List<String> getAllGroups() {
        List<String> groupNames = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM event_groups", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                String groupName = cursor.getString(cursor.getColumnIndex("group_name"));

                groupNames.add(groupName);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return groupNames;
    }

    // управление событиями -----------------------------------------------

    // добавить новое событие
    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (!doesGroupExist(event.getGroupName(), db)) {
            addGroup(event.getGroupName());
        }

        ContentValues values = new ContentValues();
        values.put("is_closed", event.isClosed());
        values.put("group_name", event.getGroupName());
        values.put("event_title", event.getEventTitle());
        values.put("event_date", event.getEventDate());
        values.put("event_type", event.getEventType());
        values.put("event_start_time", event.getEventStartTime());
        values.put("event_end_time", event.getEventEndTime());
        values.put("event_color", event.getEventColor());

        db.insert("events", null, values);
        db.close();
    }
    private boolean doesGroupExist(String groupName, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM event_groups WHERE group_name=?", new String[]{groupName});
        boolean groupExists = cursor.getCount() > 0;
        cursor.close();
        return groupExists;
    }

    // удалить событие
    public void deleteEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("events", "event_id=?", new String[]{String.valueOf(event.getEventId())});
        db.close();
    }

    // изменить событие
    public void updateEvent(Event updatedEvent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("is_closed", updatedEvent.isClosed());
        values.put("group_name", updatedEvent.getGroupName());
        values.put("event_title", updatedEvent.getEventTitle());
        values.put("event_date", updatedEvent.getEventDate());
        values.put("event_type", updatedEvent.getEventType());
        values.put("event_start_time", updatedEvent.getEventStartTime());
        values.put("event_end_time", updatedEvent.getEventEndTime());
        values.put("event_color", updatedEvent.getEventColor());

        db.update("events", values, "event_id=?", new String[]{String.valueOf(updatedEvent.getEventId())});
        db.close();
    }

    // получить все события
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM events", null);

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event(cursor);
                events.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return events;
    }

    // получить все события определенной группы
    public List<Event> getEventsByGroupName(String groupName) {
        List<Event> events = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM events WHERE group_name = ?", new String[]{groupName});

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event(cursor);
                events.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return events;
    }

    // получить все события определенного дня (отсортированы по времени)
    public List<Event> getEventsByDay(String selectedDate) {
        List<Event> events = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM events WHERE event_date = ? ORDER BY event_start_time", new String[]{selectedDate});

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event(cursor);
                events.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return events;
    }
}
