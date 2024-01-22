package com.diaryapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
                showSortOptionsDialog();
            }
        });


        // Обработчик выбора даты в CalendarView
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (selected) {
                System.out.println(date.getMonth());
                // Обновление списка событий при выборе даты
                updateEventList(String.format("%04d-%02d-%02d", date.getYear(), date.getMonth() + 1, date.getDay()));
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

    private void showSortOptionsDialog() {
        // Список вариантов сортировки
        final CharSequence[] options = {"По алфавиту", "По продолжительности", "Сбросить сортировку"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите тип сортировки");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Обработка выбора пользователя
                switch (which) {
                    case 0:
                        // По алфавиту
                        sortEventsAlphabetically();
                        break;
                    case 1:
                        // По продолжительности
                        sortEventsByDuration();
                        break;
                    case 2:
                        // Сбросить сортировку
                        resetSort();
                        break;
                }
            }
        });
        builder.show();
    }

    private void resetSort() {
        // Восстановление оригинального порядка
        eventList.clear();
        eventList.addAll(originalEventList);

        // Обновите RecyclerView через адаптер после сброса сортировки
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

        eventAdapter.notifyDataSetChanged();
    }

    private void sortEventsAlphabetically() {
        // сортировка по алфавиту
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                return event1.getTitle().compareToIgnoreCase(event2.getTitle());
            }
        });

        // Обновите RecyclerView через адаптер после сортировки или восстановления порядка
        eventAdapter.setEvents(eventList);
        eventAdapter.notifyDataSetChanged();
    }

    private void sortEventsByDuration() {
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                // Сравнение по продолжительности (разнице между временами начала и конца) в обратном порядке
                int duration1 = event1.getEndTime() - event1.getStartTime();
                int duration2 = event2.getEndTime() - event2.getStartTime();
                return Integer.compare(duration2, duration1);
            }
        });

        // Обновите RecyclerView через адаптер после сортировки
        eventAdapter.setEvents(eventList);
        eventAdapter.notifyDataSetChanged();
    }

    private int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    private int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
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

//            newEvent.setTitle("День дня");
//            newEvent.setStartTime(1200);
//            newEvent.setEndTime(2300);
//            newEvent.setType(1);
//            newEvent.setColor(Color.parseColor("#7A97FC"));

            //все цвета из макета фигмы
            // FC7A7A - красный
            // FCA97A - оранжевый
            // FCDF7A - желтый
            // 7AFC8F - зеленый
            // 7ADDFC - голубой
            // 7A97FC - синий
            // CD7AF4 - фиолетовый
            newEvent.add(dbHandler);

            // Обновление списка событий
            updateEventList(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
