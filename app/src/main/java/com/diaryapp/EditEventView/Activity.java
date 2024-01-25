package com.diaryapp.EditEventView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;
import com.diaryapp.EventHandler.EventCalendar;
import com.diaryapp.EventHandler.EventGroup;
import com.diaryapp.R;

import java.util.Objects;

public class Activity extends AppCompatActivity {
    private LinearLayout eventCardHeader;
    private ImageButton returnButton;
    private ImageButton saveButton;
    private Spinner groupList;
    private DbHandler dbHandler;
    private Event event;
    private EditText eventName;
    private ImageButton deleteGroup;
    private ImageButton addGroup;
    private EditText eventText;

    private RadioGroup eventTypeRadioGroup;
    private RadioButton simpleTypeButton;
    private RadioButton longTermTypeButton;

    private TextView eventDate;
    private TextView eventStartTime;
    private TextView eventEndTime;

    private RadioGroup eventColorRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editeventview);

        // init widgets
        eventCardHeader = findViewById(R.id.eventCardHeader);
        saveButton = findViewById(R.id.saveButton);
        returnButton = findViewById(R.id.returnButton);
        groupList = findViewById(R.id.eventGroupSpinner);
        eventName = findViewById(R.id.eventName);
        deleteGroup = findViewById(R.id.deleteGroupButton);
        addGroup = findViewById(R.id.addGroupButton);
        eventText = findViewById(R.id.eventText);

        eventTypeRadioGroup = findViewById(R.id.eventTypeGroup);
        simpleTypeButton = findViewById(R.id.typeSimpleButton);
        longTermTypeButton = findViewById(R.id.typeLongTermButton);

        eventDate = findViewById(R.id.eventDate);
        eventStartTime = findViewById(R.id.eventStartTime);
        eventEndTime = findViewById(R.id.eventEndTime);

        eventColorRadioGroup = findViewById(R.id.colorRadioGroup);

        int eventId = getIntent().getIntExtra("eventId", -1); // -1 по умолчанию
        dbHandler = new DbHandler(this);
        event = dbHandler.getEventById(eventId);

        // bind widgets
        returnButton.setOnClickListener(view -> onReturnClick());
        saveButton.setOnClickListener(view -> onSaveClick());
        deleteGroup.setOnClickListener(view -> onDeleteClick());
        addGroup.setOnClickListener(view -> onAddClick());
        eventTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> onTypeChanged(checkedId));
        eventDate.setOnClickListener(view -> selectDate());
        eventStartTime.setOnClickListener(view -> selectTime(true));
        eventEndTime.setOnClickListener(view -> selectTime(false));
        eventColorRadioGroup.setOnCheckedChangeListener((group, checkedId) -> onColorChanged(checkedId));

        fillWidgets();
    }

    private void onDeleteClick() {
        String selectedGroup = (String) groupList.getSelectedItem();

        if (!Objects.equals(selectedGroup, "Без группы")) {
            new AlertDialog.Builder(this)
                .setTitle("Подтверждение удаления")
                .setMessage("Удалить группу \"" + selectedGroup + "\"? Это также затронет все связанные с этой группой события.")

                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    EventGroup group = new EventGroup(selectedGroup);
                    group.delete(dbHandler);
                    groupList.setAdapter(getGroupsAdapter());
                    groupList.setSelection(-1);
                })

                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }
    }

    private void onTypeChanged(int checkedId) {
        if (checkedId == R.id.typeSimpleButton) {
            simpleTypeButton.setBackgroundResource(R.drawable.radio_button_border);
            longTermTypeButton.setBackgroundResource(R.drawable.event_card_button);
            eventEndTime.setVisibility(View.GONE);
            event.setType(0);
        } else if (checkedId == R.id.typeLongTermButton) {
            simpleTypeButton.setBackgroundResource(R.drawable.event_card_button);
            longTermTypeButton.setBackgroundResource(R.drawable.radio_button_border);
            eventEndTime.setVisibility(View.VISIBLE);
            event.setType(1);
        }
    }

    private void onColorChanged(int checkedId) {
        eventCardHeader.setBackground(newEventHeader(getColorById(checkedId)));
        event.setColor(getColorById(checkedId));
    }


    private int getColorById(int id) {
        int color;

        if (id == R.id.redColorButton) {
            color = Color.argb(255, 252, 122, 122);
        }
        else if (id == R.id.orangeColorButton) {
            color = Color.argb(255, 252, 169, 122);
        }
        else if (id == R.id.yellowColorButton) {
            color = Color.argb(255, 252, 223, 122);
        }
        else if (id == R.id.greenColorButton) {
            color = Color.argb(255, 122, 252, 143);
        }
        else if (id == R.id.blueColorButton) {
            color = Color.argb(255, 122, 221, 252);
        }
        else if (id == R.id.indigoColorButton) {
            color = Color.argb(255, 122, 151, 252);
        }
        else {
            color = Color.argb(255, 205, 122, 244);
        }

        return color;
    }

    private void onSaveClick() {
        event.setTitle(String.valueOf(eventName.getText()));
        event.setGroup((String) groupList.getSelectedItem());
        event.setText(String.valueOf(eventText.getText()));
        event.save(dbHandler);

        onReturnClick();
    }

    private void onAddClick() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.group_manager_layout, null);

        final EditText input = customView.findViewById(R.id.editTextGroupName);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите имя группы")
                .setView(customView)
                .setPositiveButton("Добавить", (dialog, which) -> {

                    String groupName = input.getText().toString();
                    EventGroup.add(dbHandler, groupName);
                    groupList.setAdapter(getGroupsAdapter());
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    dialog.cancel();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onReturnClick() {
        Intent intent = new Intent(this, com.diaryapp.EventView.Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void fillWidgets() {
        eventCardHeader.setBackground(newEventHeader(event.getColor()));
        eventName.setText(event.getTitle());
        eventText.setText(event.getText());
        groupList.setAdapter(getGroupsAdapter());
        simpleTypeButton.setChecked(event.getType() == 0);
        longTermTypeButton.setChecked(event.getType() == 1);
        eventDate.setText(com.diaryapp.EventView.Activity.convertDate(event.getDate()));
        eventStartTime.setText(com.diaryapp.EventView.Activity.convertTime(event.getStartTime()));
        eventEndTime.setText(com.diaryapp.EventView.Activity.convertTime(event.getEndTime()));
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

    private ArrayAdapter<String> getGroupsAdapter() {
        String[] groupNames = EventCalendar.getAllGroupNames(dbHandler).toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupNames);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        return adapter;
    }


    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    @SuppressLint("DefaultLocale")
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                    event.setDate(selectedDate);
                    eventDate.setText(com.diaryapp.EventView.Activity.convertDate(selectedDate));
                },
                year,
                month,
                dayOfMonth
        );

        datePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void selectTime(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog;
        if (isStartTime) {
            timePickerDialog = new TimePickerDialog(this,
                    (view, selectedHour, selectedMinute) -> {
                        if (selectedHour * 100 + selectedMinute == 0) {
                            event.setStartTime(1);
                        } else {
                            event.setStartTime(selectedHour * 100 + selectedMinute);
                        }
                        eventStartTime.setText(com.diaryapp.EventView.Activity.convertTime(event.getStartTime()));
                    },
                    hour,
                    minute,
                    true
            );

        } else {
            timePickerDialog = new TimePickerDialog(this,
                    (view, selectedHour, selectedMinute) -> {
                        if (selectedHour * 100 + selectedMinute == 0) {
                            event.setEndTime(1);
                            eventEndTime.setText(com.diaryapp.EventView.Activity.convertTime(event.getEndTime()));
                        } else if (selectedHour * 100 + selectedMinute > event.getStartTime()) {
                            event.setEndTime(selectedHour * 100 + selectedMinute);
                            eventEndTime.setText(com.diaryapp.EventView.Activity.convertTime(event.getEndTime()));
                        } else if (selectedHour * 100 + selectedMinute == event.getStartTime()) {
                            simpleTypeButton.setBackgroundResource(R.drawable.radio_button_border);
                            longTermTypeButton.setBackgroundResource(R.drawable.event_card_button);
                            eventEndTime.setVisibility(View.GONE);
                            event.setType(0);
                        }
                    },
                    hour,
                    minute,
                    true
            );

        }
        timePickerDialog.show();
    }
}
