package com.diaryapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;
import com.diaryapp.EventHandler.EventCalendar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

public class EventDecorator implements DayViewDecorator {
    private final DbHandler dbHandler;
    private final Drawable drawable;



    public EventDecorator(Context context, DbHandler dbHandler) {
        this.dbHandler = dbHandler;
        // Получаем количество событий для текущей даты
        CalendarDay today = CalendarDay.today();
        int eventCount = dbHandler.getEventsForDay(today.getDate().toString()).size();



        // Создаем круглый drawable с учетом количества событий
        this.drawable = createCircleDrawable(context, eventCount);
    }

    public boolean shouldDecorate(CalendarDay day) {
        List<Event> events = EventCalendar.getEventsForDay(dbHandler, day.getYear(), day.getMonth() + 1, day.getDay());
        return events.size() > 0;
    }

    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }

    private Drawable createCircleDrawable(Context context, int eventCount) {
        int color;

        // Определение цвета в зависимости от количества событий (не работает)
        if (eventCount <= 5 && eventCount > 0) {
            color = ContextCompat.getColor(context, R.color.light_pink);
        } else if (eventCount > 5 && eventCount <= 15) {
            color = ContextCompat.getColor(context, R.color.medium_red);
        } else {
            color = ContextCompat.getColor(context, R.color.light_pink); //пока пусть все будет светлое, я не знаю как исправить
        }

        Log.d("EventDecorator", "Event Count: " + eventCount);
        Log.d("EventDecorator", "Color: " + color);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(color);

        return gradientDrawable;
    }
}
