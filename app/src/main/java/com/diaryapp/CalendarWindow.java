package com.diaryapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diaryapp.Adapter.EventAdapter;
import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarWindow extends AppCompatActivity {
    private TextView tasksDateText;
    private EventAdapter eventAdapter;
    private DbHandler dbHandler;
    private List<Event> eventList;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        // Инициализация базы данных
        dbHandler = new DbHandler(this);

        // Инициализация виджетов
        CalendarView calendarView = findViewById(R.id.calendarView);
        tasksDateText = findViewById(R.id.tasksDateText);
        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);

        // Инициализация RecyclerView и адаптера
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(eventAdapter);

        // Обработчик выбора даты в CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            updateEventList(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth));
        });

        // Начальная загрузка событий для текущей даты
        updateEventList(String.format("%04d-%02d-%02d", getCurrentYear(), getCurrentMonth() + 1, getCurrentDay()));

        // В вашем методе инициализации активности или фрагмента
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.FAG);
        addButton.setOnClickListener(v -> addEvent());
    }

    @Override
    protected void onDestroy() {
        // Закрываем базу данных при уничтожении активности
        if (dbHandler != null) {
            dbHandler.close();
        }
        super.onDestroy();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void updateEventList(String selectedDate) {
        // Очистка списка перед загрузкой новых событий
        eventList.clear();

        // Загрузка событий на выбранную дату
        List<Event> eventsForDay = dbHandler.getEventsForDay(selectedDate);
        eventList.addAll(eventsForDay);

        // Обновление RecyclerView через адаптер
        eventAdapter.setEvents(eventList);

        // Обновление текста с датой
        tasksDateText.setText(selectedDate);

        // После обновления RecyclerView и текста с датой, вызовите notifyDataSetChanged
        // Это гарантирует, что RecyclerView обновится, включая состояние CheckBox
        eventAdapter.notifyDataSetChanged();
    }

    private int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    private int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    private int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void addEvent() {
        String currentDate = tasksDateText.getText().toString();

        try {
            Event newEvent = new Event();
            newEvent.setDate(currentDate);
            newEvent.save(dbHandler);

            // Обновление RecyclerView через адаптер
            eventAdapter.setEvents(eventList);

            // Update the list for the selected date
            updateEventList(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
