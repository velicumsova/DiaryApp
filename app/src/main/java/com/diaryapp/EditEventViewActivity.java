package com.diaryapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;

public class EditEventViewActivity extends AppCompatActivity {
    private Spinner groupList;
    private DbHandler dbHandler;
    private Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editeventview);
        groupList = findViewById(R.id.eventGroupSpinner);

        int eventId = getIntent().getIntExtra("eventId", -1); // -1 по умолчанию

        dbHandler = new DbHandler(this);
        event = dbHandler.getEventById(eventId);

        fillWidgets();
    }

    private void fillWidgets() {
        String[] groupNames = new String[]{"Group1", "Group2", "Group3"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupNames);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        groupList.setAdapter(adapter);
    }
}


