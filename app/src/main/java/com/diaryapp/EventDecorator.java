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
    private final DbHandler db;
    private final Drawable drawable;

    public EventDecorator(Context context, DbHandler dbHandler) {
        db = dbHandler;
        drawable = createCircleDrawable(context);
    }

    public boolean shouldDecorate(CalendarDay day) {
        List<Event> events = EventCalendar.getEventsForDay(db, day.getYear(), day.getMonth() + 1, day.getDay());
        return events.size() > 0;
    }

    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }

    private Drawable createCircleDrawable(Context context) {
        int color;
        CalendarDay day = CalendarDay.today();

        int eventCount = EventCalendar.getEventsForDay(db, day.getYear(), day.getMonth(), day.getDay()).size();
        System.out.println("Events: " + eventCount);

        if (eventCount <= 5 && eventCount > 0) {
            color = ContextCompat.getColor(context, R.color.light_pink);
        } else if (eventCount > 5 && eventCount <= 15) {
            color = ContextCompat.getColor(context, R.color.medium_red);
        } else {
            color = ContextCompat.getColor(context, R.color.light_pink);
        }

        Log.d("EventDecorator", "Event Count: " + eventCount);
        Log.d("EventDecorator", "Color: " + color);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(color);

        return gradientDrawable;
    }
}
