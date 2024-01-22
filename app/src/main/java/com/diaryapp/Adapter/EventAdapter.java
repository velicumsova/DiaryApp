package com.diaryapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.diaryapp.EventHandler.Event;
import com.diaryapp.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final Context context;
    private List<Event> events;
    private OnEventClickListener onEventClickListener;


    public EventAdapter(Context context) {
        this.context = context;
    }

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.onEventClickListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);

        // Set an OnCheckedChangeListener to handle checkbox state changes
        holder.todoCheckBox.setOnCheckedChangeListener(null); // Remove previous listener

        // Update the isClosed property when the checkbox state changes
        holder.todoCheckBox.setChecked(event.isClosed());

        // Set the background color and text style dynamically based on the closed state
        if (event.isClosed()) {
            holder.cardView.setCardBackgroundColor(0x20000000);
            holder.todoCheckBox.setPaintFlags(holder.todoCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.cardView.setCardBackgroundColor(event.getColor());
            holder.todoCheckBox.setPaintFlags(holder.todoCheckBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }


        // Add a new listener to handle checkbox state changes
        holder.todoCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the isClosed property when the checkbox state changes
            event.setClosed(isChecked);
            // Optionally, you can save the updated event in the database here
        });

        holder.itemView.setOnClickListener(v -> {
            if (onEventClickListener != null) {
                onEventClickListener.onEventClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private CheckBox todoCheckBox;
        private TextView textViewTime;
        private CardView cardView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            todoCheckBox = itemView.findViewById(R.id.todoCheckBox);
            textViewTime = itemView.findViewById(R.id.eventTimeString);
            cardView = itemView.findViewById(R.id.eventCard);
        }

        public void bind(Event event) {
            todoCheckBox.setText(event.getTitle());
            String formattedTime = formatTime(event.getStartTime(), event.getEndTime(), event.getType());
            textViewTime.setText(formattedTime);

            // Set the CheckBox state based on the isClosed property
            todoCheckBox.setChecked(event.isClosed());
        }

        private String formatTime(int startTime, int endTime, int eventType) {
            String startTimeStr = String.valueOf(startTime);

            if (eventType == 0) {
                // For type 0 (simple event), return only the startTime
                if (startTimeStr.length() == 3) {
                    startTimeStr = "0" + startTimeStr;
                }
                return startTimeStr.substring(0, 2) + ":" + startTimeStr.substring(2);
            } else if (eventType == 1) {
                // For type 1 (event with start and end time), format as "start - end"
                String endTimeStr = String.valueOf(endTime);

                if (startTimeStr.length() == 3) {
                    startTimeStr = "0" + startTimeStr;
                }
                if (endTimeStr.length() == 3) {
                    endTimeStr = "0" + endTimeStr;
                }

                return startTimeStr.substring(0, 2) + ":" + startTimeStr.substring(2) +
                        " - " +
                        endTimeStr.substring(0, 2) + ":" + endTimeStr.substring(2);
            }
            return "";
        }
    }
}