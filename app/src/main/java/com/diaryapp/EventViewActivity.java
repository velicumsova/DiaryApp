package com.diaryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        CheckBox closeBox = findViewById(R.id.closeBox);
        TextView eventDate = findViewById(R.id.eventDate);
        TextView eventTime = findViewById(R.id.eventTime);
        ImageButton editButton = findViewById(R.id.editButton);
        ImageButton deleteButton = findViewById(R.id.deleteButton);
        TextView eventGroup = findViewById(R.id.eventGroup);
        EditText eventCardText = findViewById(R.id.eventCardText);

        // bind elements
        returnButton.setOnClickListener(view -> onReturnClick());
        closeBox.setOnClickListener(view -> onCloseClick());
        editButton.setOnClickListener(view -> onEditClick());
        deleteButton.setOnClickListener(view -> onDeleteClick());
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
    private void onCloseClick() {
        System.out.println("closing event...");
    }
    private void onDeleteClick() {
        System.out.println("deleting event...");
        this.onReturnClick();
    }
}

