package com.diaryapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;
import com.diaryapp.EventHandler.EventCalendar;
import com.diaryapp.EventHandler.EventGroup;

import java.util.Objects;

public class EditEventViewActivity extends AppCompatActivity {
    private static final int FC7A7A = 0xFFFC7A7A;
    private static final int FCA97A = 0xFFFCA97A;
    private static final int FCDF7A = 0xFFFCDF7A;
    private static final int _7AFC8F = 0xFF7AFC8F;
    private static final int _7ADDFC = 0xFF7ADDFC;
    private static final int _7A97FC = 0xFF7A97FC;
    private static final int CD7AF4 = 0xFFCD7AF4;

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
    private RadioButton redColorButton;
    private RadioButton orangeColorButton;
    private RadioButton yellowColorButton;
    private RadioButton greenColorButton;
    private RadioButton blueColorButton;
    private RadioButton indigoColorButton;
    private RadioButton violetColorButton;

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
        redColorButton = findViewById(R.id.redColorButton);
        orangeColorButton = findViewById(R.id.orangeColorButton);
        yellowColorButton = findViewById(R.id.yellowColorButton);
        greenColorButton = findViewById(R.id.greenColorButton);
        blueColorButton = findViewById(R.id.blueColorButton);
        indigoColorButton = findViewById(R.id.indigoColorButton);
        violetColorButton = findViewById(R.id.violetColorButton);

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
        int color = 0;

        switch (id) {
            case 2131296676:
                color = Color.argb(255, 252, 122, 122);
                break;
            case 2131296649:
                color = Color.argb(255, 252, 169, 122);
                break;
            case 2131296836:
                color = Color.argb(255, 252, 223, 122);
                break;
            case 2131296501:
                color = Color.argb(255, 122, 252, 143);
                break;
            case 2131296358:
                color = Color.argb(255, 122, 221, 252);
                break;
            case 2131296524:
                color = Color.argb(255, 122, 151, 252);
                break;
            case 2131296822:
                color = Color.argb(255, 205, 122, 244);
                break;
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
        Intent intent = new Intent(this, EventViewActivity.class);
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
        eventDate.setText(EventViewActivity.convertDate(event.getDate()));
        eventStartTime.setText(EventViewActivity.convertTime(event.getStartTime()));
        eventEndTime.setText(EventViewActivity.convertTime(event.getEndTime()));
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

        int defaultPosition = adapter.getPosition(event.getGroup());

        groupList.setSelection(1);
        return adapter;
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                    event.setDate(selectedDate);
                    eventDate.setText(EventViewActivity.convertDate(selectedDate));
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
                        event.setStartTime(selectedHour * 100 + selectedMinute);
                        eventStartTime.setText(selectedHour + ":" + selectedMinute);
                    },
                    hour,
                    minute,
                    true
            );

        } else {
            timePickerDialog = new TimePickerDialog(this,
                    (view, selectedHour, selectedMinute) -> {
                        event.setEndTime(selectedHour * 100 + selectedMinute);
                        eventEndTime.setText(selectedHour + ":" + selectedMinute);
                    },
                    hour,
                    minute,
                    true
            );

        }
        timePickerDialog.show();
    }
}


