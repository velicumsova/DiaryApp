package com.diaryapp.EventView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.diaryapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Activity extends AppCompatActivity {
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final int REQUEST_CODE = 1; // нужно для возвращения в просмотр события
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

        // init widgets
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

        // bind widgets
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
        eventText.setText(event.getText());
        eventCardHeader.setBackground(newEventHeader(event.getColor()));

        if (event.getType() == 0) {
            eventTime.setText(convertTime(event.getStartTime()));
        } else {
            eventTime.setText(convertTime(event.getStartTime()) + " - " + convertTime(event.getEndTime()));
        }

        closeBox.setChecked(event.isClosed());
        onCloseClick();
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
    public static String convertTime(int timeValue) {
        if (timeValue >= 0 && timeValue <= 2400) {
            int hours = timeValue / 100;
            int minutes = timeValue % 100;
            return String.format("%02d:%02d", hours, minutes);
        } else {
            return "Без времени";
        }
    }

    public static String convertDate(String inputDate) {
        try {
            Date date = inputDateFormat.parse(inputDate);
            assert date != null;
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "01.01.2024";
        }
    }

    private void onReturnClick() {
        Intent intent = new Intent(this, com.diaryapp.CalendarView.Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void onEditClick() {
        Intent intent = new Intent(this, com.diaryapp.EditEventView.Activity.class);
        intent.putExtra("eventId", event.getId());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == 0) {
            event = Event.getById(dbHandler, event.getId());
            fillWidgets();
        }
    }

    private void onDeleteClick() {
        new AlertDialog.Builder(this)
                .setTitle("Подтверждение удаления")
                .setMessage("Удалить событие \"" + event.getTitle() + "\"? Это действие невозможно отменить")

                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    event.delete(dbHandler);
                    onReturnClick();
                })

                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @SuppressLint("ResourceType")
    private void onCloseClick() {
        crossText(eventName, closeBox.isChecked());
        crossText(eventGroup, closeBox.isChecked());
        crossText(eventDate, closeBox.isChecked());
        crossText(eventTime, closeBox.isChecked());

        event.setClosed(closeBox.isChecked());
        event.save(dbHandler);

        Event.getById(dbHandler, event.getId());

        if (closeBox.isChecked()) {
            eventCardHeader.setBackground(newEventHeader(0x20000000));
        } else {
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

