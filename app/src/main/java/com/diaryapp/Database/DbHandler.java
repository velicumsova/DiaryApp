package com.diaryapp.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        // Создание таблицы events
        String createTableQuery = "CREATE TABLE events (" +
                "event_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "group_name INTEGER," +
                "event_title TEXT," +
                "event_date TEXT," +
                "event_type INTEGER," +
                "event_start_time INTEGER," +
                "event_end_time INTEGER," +
                "event_color INTEGER)";
        db.execSQL(createTableQuery);

        // Создание таблицы event_groups
        String createGroupTableQuery = "CREATE TABLE event_groups (" +
                "group_name INTEGER PRIMARY KEY)";
        db.execSQL(createGroupTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Обновление базы данных, если версия изменилась
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS event_groups");
        onCreate(db);
    }

    public List<String> getAllGroupNames() {
        List<String> groupNames = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT group_name FROM event_groups", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String groupName = cursor.getString(cursor.getColumnIndex("group_name"));
                groupNames.add(groupName);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return groupNames;
    }
}
