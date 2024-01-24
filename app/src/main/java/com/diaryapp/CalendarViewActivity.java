package com.diaryapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diaryapp.Adapter.EventAdapter;
import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarViewActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1; // нужно для удаления события и возвращения в календарь
    private TextView tasksDateText;
    private EventAdapter eventAdapter;
    private DbHandler dbHandler;
    private List<Event> eventList;
    private List<Event> originalEventList;
    private MaterialCalendarView calendarView;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        dbHandler = new DbHandler(this);

        this.calendarView = findViewById(R.id.сalendarView);
        tasksDateText = findViewById(R.id.tasksDateText);
        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(eventAdapter);

        eventAdapter.setOnEventClickListener(this::onEventClick);

        ImageButton sortButton = findViewById(R.id.imageButton);
        sortButton.setOnClickListener(v -> showSortOptionsDialog());
        ImageButton filterButton = findViewById(R.id.imageButton2);
        filterButton.setOnClickListener(v -> showFilterOptionsDialog());


        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (selected) {
                System.out.println(date.getMonth());
                updateEventList(String.format("%04d-%02d-%02d", date.getYear(), date.getMonth() + 1, date.getDay()));
            }
        });

        Calendar currentDateCalendar = Calendar.getInstance();
        updateEventList(String.format("%04d-%02d-%02d", currentDateCalendar.get(Calendar.YEAR),
                currentDateCalendar.get(Calendar.MONTH) + 1, currentDateCalendar.get(Calendar.DAY_OF_MONTH)));

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.FAG);
        addButton.setOnClickListener(v -> addEvent());

        calendarView.addDecorator(new EventDecorator(this, dbHandler));
        calendarView.invalidateDecorators();
    }

    //ФИЛЬТРАЦИЯ
    private void showFilterOptionsDialog() {
        String selectedDate = tasksDateText.getText().toString();
        List<Event> eventsForDay = dbHandler.getEventsForDay(selectedDate);

        Set<String> availableGroups = new HashSet<>();
        for (Event event : eventsForDay) {
            availableGroups.add(event.getGroup());
        }

        List<String> availableGroupList = new ArrayList<>(availableGroups);

        CharSequence[] groupNames = new CharSequence[availableGroupList.size()];
        boolean[] selectedGroups = new boolean[availableGroupList.size()];

        for (int i = 0; i < availableGroupList.size(); i++) {
            groupNames[i] = availableGroupList.get(i);
            selectedGroups[i] = true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите группы для фильтрации");
        builder.setMultiChoiceItems(groupNames, selectedGroups, (dialog, which, isChecked) -> selectedGroups[which] = isChecked);
        builder.setPositiveButton("Применить", (dialog, which) -> {
            filterBySelectedGroups(selectedGroups, availableGroupList);
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterBySelectedGroups(boolean[] selectedGroups, List<String> availableGroups) {
        String selectedDate = tasksDateText.getText().toString();
        List<Event> filteredEvents = getEventsForDayAndSelectedGroups(selectedDate, selectedGroups, availableGroups);

        eventList.clear();
        eventList.addAll(filteredEvents);
        eventAdapter.setEvents(eventList);
        eventAdapter.notifyDataSetChanged();
    }

    private List<Event> getEventsForDayAndSelectedGroups(String selectedDate, boolean[] selectedGroups, List<String> availableGroups) {
        List<Event> eventsForDay = dbHandler.getEventsForDay(selectedDate);
        List<Event> filteredEvents = new ArrayList<>();

        for (Event event : eventsForDay) {
            for (int i = 0; i < selectedGroups.length; i++) {
                if (selectedGroups[i] && event.getGroup().equals(availableGroups.get(i))) {
                    filteredEvents.add(event);
                    break;
                }
            }
        }

        return filteredEvents;
    }


    //СОРТИРОВКА
    private void showSortOptionsDialog() {
        final CharSequence[] options = {"По алфавиту", "По продолжительности", "Сбросить сортировку"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите тип сортировки");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    sortEventsAlphabetically();
                    break;
                case 1:
                    sortEventsByDuration();
                    break;
                case 2:
                    resetSort();
                    break;
            }
        });

        builder.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortEventsAlphabetically() {
        eventList.sort((event1, event2) -> event1.getTitle().compareToIgnoreCase(event2.getTitle()));

        eventAdapter.setEvents(eventList);
        eventAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortEventsByDuration() {
        eventList.sort((event1, event2) -> {
            int duration1 = event1.getEndTime() - event1.getStartTime();
            int duration2 = event2.getEndTime() - event2.getStartTime();
            return Integer.compare(duration2, duration1);
        });

        eventAdapter.setEvents(eventList);
        eventAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void resetSort() {
        eventList.clear();
        eventList.addAll(originalEventList);

        eventAdapter.setEvents(eventList);
        eventAdapter.notifyDataSetChanged();
    }

    private void onEventClick(Event event) {
        Intent intent = new Intent(this, EventViewActivity.class);
        intent.putExtra("eventId", event.getId());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == 0) {

            updateEventList(tasksDateText.getText().toString());
        }
    }


    @Override
    protected void onDestroy() {
        if (dbHandler != null) {
            dbHandler.close();
        }
        super.onDestroy();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void updateEventList(String selectedDate) {
        eventList.clear();

        List<Event> eventsForDay = dbHandler.getEventsForDay(selectedDate);
        eventList.addAll(eventsForDay);
        originalEventList = new ArrayList<>(eventList);

        eventAdapter.setEvents(eventList);
        tasksDateText.setText(selectedDate);
        calendarView.invalidateDecorators();
        eventAdapter.notifyDataSetChanged();
    }

    public void addEvent() {
        String currentDate = tasksDateText.getText().toString();

        try {
            Event newEvent = new Event();
            newEvent.setDate(currentDate);
            newEvent.add(dbHandler);
            updateEventList(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
