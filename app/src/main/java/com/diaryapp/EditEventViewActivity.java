package com.diaryapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;
import com.diaryapp.EventHandler.EventCalendar;
import com.diaryapp.EventHandler.EventGroup;

public class EditEventViewActivity extends AppCompatActivity {
    private ImageButton returnButton;
    private ImageButton saveButton;
    private Spinner groupList;
    private DbHandler dbHandler;
    private Event event;
    private EditText eventName;
    private ImageButton deleteGroup;
    private ImageButton addGroup;
    private EditText eventText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editeventview);

        // init widgets
        saveButton = findViewById(R.id.saveButton);
        returnButton = findViewById(R.id.returnButton);
        groupList = findViewById(R.id.eventGroupSpinner);
        eventName = findViewById(R.id.eventName);
        deleteGroup = findViewById(R.id.deleteGroupButton);
        addGroup = findViewById(R.id.addGroupButton);
        eventText = findViewById(R.id.eventText);

        int eventId = getIntent().getIntExtra("eventId", -1); // -1 по умолчанию
        dbHandler = new DbHandler(this);
        event = dbHandler.getEventById(eventId);

        // bind widgets
        returnButton.setOnClickListener(view -> onReturnClick());
        saveButton.setOnClickListener(view -> onSaveClick());
        addGroup.setOnClickListener(view -> onAddClick());
        
        fillWidgets();
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
        eventName.setText(event.getTitle());
        eventText.setText(event.getText());
        groupList.setAdapter(getGroupsAdapter());
    }

    private ArrayAdapter<String> getGroupsAdapter() {
        String[] groupNames = EventCalendar.getAllGroupNames(dbHandler).toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupNames);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        int defaultPosition = adapter.getPosition(event.getGroup());

        groupList.setSelection(1);
        return adapter;
    }
}


