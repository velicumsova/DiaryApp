package com.diaryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.diaryapp.EventHandler.Event;

public class EventViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventview);

        // init elements
        ImageButton returnButton = findViewById(R.id.returnButton);
        TextView eventViewName = findViewById(R.id.eventViewName);
        LinearLayout eventCardBackground = findViewById(R.id.eventCardBackground);
        LinearLayout eventCardHeader = findViewById(R.id.eventCardHeader);
        TextView eventName = findViewById(R.id.eventName);
        CheckBox checkBox = findViewById(R.id.closeBox);
        TextView eventDate = findViewById(R.id.eventDate);
        TextView eventTime = findViewById(R.id.eventTime);
        ImageButton editButton = findViewById(R.id.editButton);
        ImageButton deleteButton = findViewById(R.id.deleteButton);
        TextView eventGroup = findViewById(R.id.eventGroup);
        EditText eventCardText = findViewById(R.id.eventCardText);

        // bind elements
        returnButton.setOnClickListener(view -> onReturnClick());
        editButton.setOnClickListener(view -> System.out.println("editing event..."));
        deleteButton.setOnClickListener(view -> System.out.println("deleting event..."));
    }

    private void onReturnClick() {
        System.out.println("returning to calendar...");
        Intent intent = new Intent(this, CalendarViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

