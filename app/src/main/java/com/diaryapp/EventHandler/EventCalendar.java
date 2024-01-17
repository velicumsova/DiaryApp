package com.diaryapp.EventHandler;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

public class EventCalendar {
    public static List<Event> getEventsForDay (DbHandler db, int year, int month, int day) {

        @SuppressLint("DefaultLocale") String date = String.format("%04d-%02d-%02d", year, month, day);
        return db.getEventsForDay(date);
    }

    public static List<Event> getEventsForMonth (DbHandler db, int year, int month) {
        @SuppressLint("DefaultLocale") String start = String.format("%04d-%02d-01", year, month);
        @SuppressLint("DefaultLocale") String end = String.format("%04d-%02d-31", year, month);

        return db.getEventsForPeriod(start, end);
    }

    public static List<Event> getAllEvents (DbHandler db) {
        return db.getAllEvents();
    }

    public static List<EventGroup> getAllGroups (DbHandler db) {
        List<String> groupNames = db.getAllGroups();

        List<EventGroup> groups = new ArrayList<>();

        for (String groupName: groupNames) {
            groups.add(new EventGroup(groupName));
        }
        return groups;
    }
}
