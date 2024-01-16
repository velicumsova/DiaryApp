package com.diaryapp.EventHandler;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

public class EventCalendar {
    public static List<Event> getEventsForDay (DbHandler db, int year, int month, int day) {

        @SuppressLint("DefaultLocale") String date = String.format("%04d-%02d-%02d", year, month, day);
        return db.getEventsForDay(date);
    }

    public static List<Event> getEventAmountsForMonth (DbHandler db, int year, int month) {
        @SuppressLint("DefaultLocale") String start = String.format("%04d-%02d-01", year, month);
        @SuppressLint("DefaultLocale") String end = String.format("%04d-%02d-31", year, month);

        List<Event> events = db.getEventsForPeriod(start, end);
        List<Integer> amounts = new ArrayList<>();

        return events;
    }
}
