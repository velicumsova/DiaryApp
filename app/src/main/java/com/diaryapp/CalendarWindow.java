package com.diaryapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
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

    private List<Event> originalEventList;
    private MaterialCalendarView calendarView;

    private boolean isSortingAlphabeticallyEnabled = false;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        // Инициализация базы данных
        dbHandler = new DbHandler(this);

        // Инициализация виджетов
        this.calendarView = findViewById(R.id.сalendarView);
        tasksDateText = findViewById(R.id.tasksDateText);
        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);

        // Инициализация RecyclerView и адаптера
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(eventAdapter);

        // Set the OnEventClickListener
        eventAdapter.setOnEventClickListener(event -> {
            // Placeholder method for now, replace with your logic
            onEventClick(event);
        });

        ImageButton sortButton = findViewById(R.id.imageButton);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переключение состояния сортировки
                isSortingAlphabeticallyEnabled = !isSortingAlphabeticallyEnabled;

                // Вызывайте метод для сортировки событий по алфавиту (если сортировка включена)
                sortEventsAlphabetically();
            }
        });


        // Обработчик выбора даты в CalendarView
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    // Обновление списка событий при выборе даты
                    updateEventList(String.format("%04d-%02d-%02d", date.getYear(), date.getMonth() + 1, date.getDay()));
                }
            }
        });

        // Начальная загрузка событий для текущей даты
        updateEventList(String.format("%04d-%02d-%02d", getCurrentYear(), getCurrentMonth() + 1, getCurrentDay()));

        // В вашем методе инициализации активности или фрагмента
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.FAG);
        addButton.setOnClickListener(v -> addEvent());

        // Добавление декоратора событий
        calendarView.addDecorator(new EventDecorator(this, dbHandler));

        // Обновление декоратора
        calendarView.invalidateDecorators();
    }

    private void onEventClick(Event event) {
        // заглушка
        System.out.println("Вы нажали на событие: " + event.getTitle());
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
        // Создайте копию списка для оригинального порядка
        originalEventList = new ArrayList<>(eventList);

        // Обновление RecyclerView через адаптер
        eventAdapter.setEvents(eventList);

        // Обновление текста с датой
        tasksDateText.setText(selectedDate);

        // Обновление декоратора
        calendarView.invalidateDecorators();

        // После обновления RecyclerView и текста с датой, вызовите notifyDataSetChanged
        // Это гарантирует, что RecyclerView обновится, включая состояние CheckBox
        eventAdapter.notifyDataSetChanged();
    }

    private void sortEventsAlphabetically() {
        if (isSortingAlphabeticallyEnabled) {
            // Используйте компаратор для сортировки событий по алфавиту
            Collections.sort(eventList, new Comparator<Event>() {
                @Override
                public int compare(Event event1, Event event2) {
                    return event1.getTitle().compareToIgnoreCase(event2.getTitle());
                }
            });
        } else {
            // Восстановите оригинальный порядок событий
            eventList.clear();
            eventList.addAll(originalEventList);
        }

        // Обновите RecyclerView через адаптер после сортировки или восстановления порядка
        eventAdapter.setEvents(eventList);
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

            newEvent.setTitle("Начальница");
            newEvent.setStartTime(1200);
            newEvent.setEndTime(1300);
            newEvent.setType(1);
            newEvent.setColor(Color.parseColor("#7AFC8F"));

            //все цвета из макета фигмы
            // FC7A7A - красный
            // FCA97A - оранжевый
            // FCDF7A - желтый
            // 7AFC8F - зеленый
            // 7ADDFC - голубой
            // 7A97FC - синий
            // CD7AF4 - фиолетовый
            newEvent.save(dbHandler);

            // Обновление списка событий
            updateEventList(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
