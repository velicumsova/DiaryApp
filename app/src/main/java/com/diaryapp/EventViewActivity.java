package com.diaryapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EventViewActivity extends AppCompatActivity {
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private DbHandler dbHandler;
    private Event event;
    private LinearLayout eventCardHeader;
    private TextView eventName;
    private CheckBox closeBox;
    private ImageButton returnButton;
    private TextView eventDate;
    private TextView eventTime;
    private ImageButton editButton;
    private ImageButton deleteButton;
    private TextView eventGroup;
    private TextView eventText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventview);

        int eventId = getIntent().getIntExtra("eventId", -1); // -1 по умолчанию

        dbHandler = new DbHandler(this);
        event = dbHandler.getEventById(eventId);

        // init elements
        returnButton = findViewById(R.id.returnButton);
        eventCardHeader = findViewById(R.id.eventCardHeader);
        eventName = findViewById(R.id.eventName);
        closeBox = findViewById(R.id.closeBox);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        eventGroup = findViewById(R.id.eventGroup);
        eventText = findViewById(R.id.eventText);

        if (eventId == -1) {
            onReturnClick();
        } else {
            fillWidgets();
        }

        // bind elements
        closeBox.setOnClickListener(view -> onCloseClick());
        returnButton.setOnClickListener(view -> onReturnClick());
        editButton.setOnClickListener(view -> onEditClick());
        deleteButton.setOnClickListener(view -> onDeleteClick());
    }

    @SuppressLint("SetTextI18n")
    private void fillWidgets() {
        eventName.setText(event.getTitle());
        eventDate.setText(convertDate(event.getDate()) + " |");
        eventGroup.setText(event.getGroup());
        System.out.println(event.getText());
        eventText.setText(event.getText());
        eventCardHeader.setBackground(newEventHeader(event.getColor()));

        if (event.getType() == 0) {
            eventTime.setText(convertTime(event.getStartTime()));
        } else {
            eventTime.setText(convertTime(event.getStartTime()) + " - " + convertTime(event.getEndTime()));
        }

    }

    private Drawable newEventHeader(int color) {
        Drawable drawable = Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.event_card_header)).mutate();

        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(android.R.id.background);

            if (gradientDrawable != null) {
                gradientDrawable.setColor(color);
            }
        }
        return drawable;
    }

    @SuppressLint("DefaultLocale")
    private static String convertTime(int timeValue) {
        if (timeValue >= 0 && timeValue <= 2400) {
            int hours = timeValue / 100;
            int minutes = timeValue % 100;
            return String.format("%02d:%02d", hours, minutes);
        } else {
            return "Без времени";
        }
    }

    private static String convertDate(String inputDate) {
        try {
            Date date = inputDateFormat.parse(inputDate);
            assert date != null;
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Без даты";
        }
    }

    private void onReturnClick() {
        System.out.println("returning to calendar...");
        Intent intent = new Intent(this, CalendarViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void onEditClick() {
        System.out.println("editing event...");
    }

    private void onDeleteClick() {
        System.out.println("deleting event...");
        event.delete(dbHandler);
        onReturnClick();
    }

    @SuppressLint("ResourceType")
    private void onCloseClick() {
        crossText(eventName, closeBox.isChecked());
        crossText(eventGroup, closeBox.isChecked());
        crossText(eventDate, closeBox.isChecked());
        crossText(eventTime, closeBox.isChecked());
        if (closeBox.isChecked()) {
            System.out.println("closing event...");
            eventCardHeader.setBackground(newEventHeader(0x20000000));

        } else {
            System.out.println("opening event...");
            eventCardHeader.setBackground(newEventHeader(event.getColor()));
        }
    }
    private void crossText(TextView textView, boolean isCrossing) {
        if (isCrossing) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}

