package com.diaryapp;
import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.style.RelativeSizeSpan;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

public class EventDecorator implements DayViewDecorator {
    private final Context context;
    private final DbHandler dbHandler;
    private final Drawable drawable;



    public EventDecorator(Context context, DbHandler dbHandler) {
        this.context = context;
        this.dbHandler = dbHandler;
        // Получаем количество событий для текущей даты
        CalendarDay today = CalendarDay.today();
        int eventCount = dbHandler.getEventsForDay(today.getDate().toString()).size();

        // Создаем круглый drawable с учетом количества событий
        this.drawable = createCircleDrawable(context, eventCount);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Проверяем, есть ли события в этот день
        String date = String.format("%04d-%02d-%02d", day.getYear(), day.getMonth() + 1, day.getDay());
        List<Event> events = dbHandler.getEventsForDay(date);
        return events.size() > 0;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }

    private Drawable createCircleDrawable(Context context, int eventCount) {
        int color;

        // Определение цвета в зависимости от количества событий
        if (eventCount <= 5 && eventCount > 0) {
            color = ContextCompat.getColor(context, R.color.light_pink);
        } else if (eventCount > 5 && eventCount <= 15) {
            color = ContextCompat.getColor(context, R.color.medium_red);
        } else {
            color = ContextCompat.getColor(context, R.color.dark_red);
        }

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setSize(100, 100); // Размер вашего круга
        gradientDrawable.setColor(color);

        return gradientDrawable;
    }
}
