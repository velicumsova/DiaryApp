package com.diaryapp.CalendarView;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

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

    public EventDecorator(DbHandler dbHandler) {
        db = dbHandler;
        drawable = createCircleDrawable();
    }

    public boolean shouldDecorate(CalendarDay day) {
        List<Event> events = EventCalendar.getEventsForDay(db, day.getYear(), day.getMonth() + 1, day.getDay());
        return events.size() > 0;
    }

    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }

    private Drawable createCircleDrawable() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(Color.argb(128, 255, 95, 95));

        return gradientDrawable;
    }
}
