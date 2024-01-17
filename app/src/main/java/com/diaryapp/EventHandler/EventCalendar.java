package com.diaryapp.EventHandler;

import android.annotation.SuppressLint;

import com.diaryapp.EventHandler.DB.DbHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс для управления событиями и группами вместе.
 */
public class EventCalendar {

    /**
     * Метод для получения всех событий определенного дня.
     * @param db объект DbHandler.
     * @param year год.
     * @param month месяц.
     * @param day день.
     * @return список объектов класса Event.
     */
    public static List<Event> getEventsForDay (DbHandler db, int year, int month, int day) {

        @SuppressLint("DefaultLocale") String date = String.format("%04d-%02d-%02d", year, month, day);
        return db.getEventsForDay(date);
    }

    /**
     * Метод для получения всех событий определенного месяца.
     * @param db объект DbHandler.
     * @param year год.
     * @param month месяц.
     * @return список объектов класса Event.
     */
    public static List<Event> getEventsForMonth (DbHandler db, int year, int month) {
        @SuppressLint("DefaultLocale") String start = String.format("%04d-%02d-01", year, month);
        @SuppressLint("DefaultLocale") String end = String.format("%04d-%02d-31", year, month);

        return db.getEventsForPeriod(start, end);
    }

    /**
     * Метод для получения всех событий.
     * @param db объект DbHandler.
     * @return список объектов класса Event.
     */
    public static List<Event> getAllEvents (DbHandler db) {
        return db.getAllEvents();
    }

    /**
     * Метод для получения всех групп.
     * @param db объект DbHandler.
     * @return список объектов класса EventGroup.
     */
    public static List<EventGroup> getAllGroups (DbHandler db) {
        List<String> groupNames = db.getAllGroups();

        List<EventGroup> groups = new ArrayList<>();

        for (String groupName: groupNames) {
            groups.add(new EventGroup(groupName));
        }
        return groups;
    }
}
