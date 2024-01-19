package com.diaryapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.diaryapp.Adapter.EventAdapter;
import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.List;

public class CalendarWindow extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView tasksText;
    private TextView tasksDateText;
    private RecyclerView tasksRecyclerView;
    private EventAdapter eventAdapter;
    private DbHandler dbHandler;
    private List<Event> eventList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        // Инициализация базы данных
        dbHandler = new DbHandler(this);




        // Инициализация виджетов
        calendarView = findViewById(R.id.calendarView);
        tasksText = findViewById(R.id.tasksText);
        tasksDateText = findViewById(R.id.tasksDateText);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);

        // Инициализация RecyclerView и адаптера
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, dbHandler);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(eventAdapter);

        // Обработчик выбора даты в CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Обновление списка событий для выбранной даты
                updateEventList(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth));
            }
        });


        // Начальная загрузка событий для текущей даты
        updateEventList(String.format("%04d-%02d-%02d", getCurrentYear(), getCurrentMonth() + 1, getCurrentDay()));

        // В вашем методе инициализации активности или фрагмента
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.FAG);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Закрываем базу данных при уничтожении активности
        if (dbHandler != null) {
            dbHandler.close();
        }
        super.onDestroy();
    }


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
        // Получаем текущий год
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    private int getCurrentMonth() {
        // Получаем текущий месяц (от 0 до 11, поэтому прибавляем 1)
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    private int getCurrentDay() {
        // Получаем текущий день месяца
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }

    public void addEvent() {
        // Получаем текущую выбранную дату из CalendarView
        long selectedDateInMillis = calendarView.getDate();

        // Преобразуем миллисекунды в строку с форматом "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = tasksDateText.getText().toString();

        // Остальной код остается без изменений
        int startTime = 1100; // Время начала в формате HHmm (например, 12:00 - 1200)
        int endTime = 1100;   // Время конца в формате HHmm (например, 13:00 - 1300)

        try {
            // Create a new instance of Event for each event
            Event newEvent = new Event();

            // Set properties for the newEvent
            newEvent.setClosed(false);
            newEvent.setGroup("спорт");
            newEvent.setTitle("ананас адидас");
            newEvent.setDate(currentDate);
            newEvent.setType(0);
            newEvent.setStartTime(startTime);
            newEvent.setEndTime(endTime);

            // Проверяем тип события и устанавливаем цвет карточки в соответствии с требованиями
            if (newEvent.getType() == 0) {
                newEvent.setColor(0xFFFF7676); // Красный цвет
            } else {
                newEvent.setColor(0xFFD376FF); // Фиолетовый цвет
            }

            // Add the new event to the database
            newEvent.save(dbHandler);

            // Обновление RecyclerView через адаптер
            eventAdapter.setEvents(eventList);

            // Update the list for the selected date
            updateEventList(currentDate);

            Log.d("EventAdapter", "isClosed: " + newEvent.isClosed());
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



