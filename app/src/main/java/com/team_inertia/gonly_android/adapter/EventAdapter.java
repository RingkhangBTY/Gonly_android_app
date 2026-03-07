package com.team_inertia.gonly_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.team_inertia.gonly_android.R;
import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.model.EventResponse;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventResponse> events = new ArrayList<>();

    public void setEvents(List<EventResponse> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventResponse event = events.get(position);
        Context context = holder.itemView.getContext();

        holder.titleText.setText(event.getTitle());
        holder.stateText.setText("📍 " + event.getState());

        if (event.getEventType() != null) {
            holder.typeText.setText("🎪 " + event.getEventType());
        }

        // Show dates
        String dates = "";
        if (event.getStartDate() != null) {
            dates = "📅 " + event.getStartDate();
        }
        if (event.getEndDate() != null) {
            dates = dates + " → " + event.getEndDate();
        }
        holder.datesText.setText(dates);

        // ===== LOAD ACTUAL PHOTO =====
        if (event.getImageIds() != null && event.getImageIds().size() > 0) {
            long firstImageId = event.getImageIds().get(0);

            // Events use a different image endpoint
            String imageUrl = ApiClient.getBaseUrl() + "/api/events/images/" + firstImageId;

            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_event)
                    .error(R.drawable.placeholder_event)
                    .into(holder.eventImage);
        } else {
            holder.eventImage.setImageResource(R.drawable.placeholder_event);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView titleText, stateText, typeText, datesText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventItemImage);
            titleText = itemView.findViewById(R.id.eventItemTitle);
            stateText = itemView.findViewById(R.id.eventItemState);
            typeText = itemView.findViewById(R.id.eventItemType);
            datesText = itemView.findViewById(R.id.eventItemDates);
        }
    }
}