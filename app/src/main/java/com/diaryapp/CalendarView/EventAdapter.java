package com.diaryapp.CalendarView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.diaryapp.EventHandler.DB.DbHandler;
import com.diaryapp.EventHandler.Event;
import com.diaryapp.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final Context context;
    private List<Event> events;
    private OnEventClickListener onEventClickListener;
    private final DbHandler dbHandler;


    public EventAdapter(Context context, DbHandler dbHandler) {
        this.context = context;
        this.dbHandler = dbHandler;
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


    public void updateEventClosedState(int position, boolean isClosed) {
        Event event = events.get(position);
        event.setClosed(isClosed);

        dbHandler.updateEvent(event);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            notifyItemChanged(position);
        });
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);

        holder.todoCheckBox.setOnCheckedChangeListener(null);

        holder.todoCheckBox.setChecked(event.isClosed());

        if (event.isClosed()) {
            holder.cardView.setCardBackgroundColor(0x20000000);
            holder.todoCheckBox.setPaintFlags(holder.todoCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.cardView.setCardBackgroundColor(event.getColor());
            holder.todoCheckBox.setPaintFlags(holder.todoCheckBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.todoCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateEventClosedState(holder.getAdapterPosition(), isChecked);
        });

        holder.itemView.setOnClickListener(v -> {
            if (onEventClickListener != null) {
                onEventClickListener.onEventClick(event);
            }
        });

        holder.openEventButton.setOnClickListener(v -> {
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

        private final CheckBox todoCheckBox;
        private final ImageButton openEventButton;
        private final TextView textViewTime;
        private final CardView cardView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            todoCheckBox = itemView.findViewById(R.id.todoCheckBox);
            textViewTime = itemView.findViewById(R.id.eventTimeString);
            cardView = itemView.findViewById(R.id.eventCard);
            openEventButton = itemView.findViewById(R.id.openEventButton);
        }

        public void bind(Event event) {
            todoCheckBox.setText(event.getTitle());
            String formattedTime = formatTime(event.getStartTime(), event.getEndTime(), event.getType());
            textViewTime.setText(formattedTime);
            todoCheckBox.setChecked(event.isClosed());
        }

        private String formatTime(int startTime, int endTime, int eventType) {
            String startTimeStr = String.valueOf(startTime);

            if (eventType == 0) {
                if (startTimeStr.length() == 3) {
                    startTimeStr = "0" + startTimeStr;
                }
                return startTimeStr.substring(0, 2) + ":" + startTimeStr.substring(2);
            } else if (eventType == 1) {
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