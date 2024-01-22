package com.diaryapp;

import static com.diaryapp.EventHandler.EventCalendar.getAllGroups;

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
import com.diaryapp.EventHandler.EventGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
        ImageButton filterButton = findViewById(R.id.imageButton2);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterOptionsDialog();
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
        Calendar currentDateCalendar = Calendar.getInstance();
        updateEventList(String.format("%04d-%02d-%02d", currentDateCalendar.get(Calendar.YEAR),
                currentDateCalendar.get(Calendar.MONTH) + 1, currentDateCalendar.get(Calendar.DAY_OF_MONTH)));

        // В вашем методе инициализации активности или фрагмента
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.FAG);
        addButton.setOnClickListener(v -> addEvent());

        // Добавление декоратора событий
        calendarView.addDecorator(new EventDecorator(this, dbHandler));

        // Обновление декоратора
        calendarView.invalidateDecorators();
    }

    //ФИЛЬТРАЦИЯ
    private void showFilterOptionsDialog() {
        // Получение всех событий для выбранной даты
        String selectedDate = tasksDateText.getText().toString();
        List<Event> eventsForDay = dbHandler.getEventsForDay(selectedDate);

        // Создание списка доступных групп на основе событий в текущей дате
        Set<String> availableGroups = new HashSet<>();
        for (Event event : eventsForDay) {
            availableGroups.add(event.getGroup());
        }

        // Создание списка доступных групп для диалога
        List<String> availableGroupList = new ArrayList<>(availableGroups);

        // Create a list of group names for the dialog
        CharSequence[] groupNames = new CharSequence[availableGroupList.size()];
        boolean[] selectedGroups = new boolean[availableGroupList.size()];

        for (int i = 0; i < availableGroupList.size(); i++) {
            groupNames[i] = availableGroupList.get(i);
            // По умолчанию все группы выбраны
            selectedGroups[i] = true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите группы для фильтрации");
        builder.setMultiChoiceItems(groupNames, selectedGroups, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Обработка выбора группы
                selectedGroups[which] = isChecked;
            }
        });
        builder.setPositiveButton("Применить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Фильтрация по выбранным группам
                filterBySelectedGroups(selectedGroups, availableGroupList);
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void filterBySelectedGroups(boolean[] selectedGroups, List<String> availableGroups) {
        // Фильтрация событий по выбранным группам и дате
        String selectedDate = tasksDateText.getText().toString();
        List<Event> filteredEvents = getEventsForDayAndSelectedGroups(selectedDate, selectedGroups, availableGroups);

        // Обновление RecyclerView через адаптер
        eventList.clear();
        eventList.addAll(filteredEvents);
        eventAdapter.setEvents(eventList);
        eventAdapter.notifyDataSetChanged();
    }

    private List<Event> getEventsForDayAndSelectedGroups(String selectedDate, boolean[] selectedGroups, List<String> availableGroups) {
        // Получение всех событий для выбранной даты
        List<Event> eventsForDay = dbHandler.getEventsForDay(selectedDate);
        List<Event> filteredEvents = new ArrayList<>();

        // Фильтрация по выбранным группам
        for (Event event : eventsForDay) {
            for (int i = 0; i < selectedGroups.length; i++) {
                if (selectedGroups[i] && event.getGroup().equals(availableGroups.get(i))) {
                    filteredEvents.add(event);
                    break; // Прерываем цикл, если событие соответствует хотя бы одной выбранной группе
                }
            }
        }

        return filteredEvents;
    }


    //СОРТИРОВКА
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

    public void addEvent() {
        String currentDate = tasksDateText.getText().toString();

        try {
            Event newEvent = new Event();
            newEvent.setDate(currentDate);

//            newEvent.setTitle("Поиграть в Mass Effect");
//            newEvent.setGroup("Gaming");
//            newEvent.setStartTime(1300);
//            newEvent.setEndTime(1300);
//            newEvent.setType(0);
            //newEvent.setColor(Color.parseColor("#7A97FC"));

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
