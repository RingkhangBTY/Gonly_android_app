package com.team_inertia.gonly_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.team_inertia.gonly_android.GemDetailActivity;
import com.team_inertia.gonly_android.R;
import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.model.GemResponse;

import java.util.ArrayList;
import java.util.List;

public class GemAdapter extends RecyclerView.Adapter<GemAdapter.GemViewHolder> {

    private List<GemResponse> gems = new ArrayList<>();

    public void setGems(List<GemResponse> gems) {
        this.gems = gems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gem, parent, false);
        return new GemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GemViewHolder holder, int position) {
        GemResponse gem = gems.get(position);
        Context context = holder.itemView.getContext();

        // Set text fields
        holder.nameText.setText(gem.getName());
        holder.stateText.setText("📍 " + gem.getState());

        if (gem.getCategory() != null) {
            holder.categoryText.setText("🏷️ " + gem.getCategory());
        } else {
            holder.categoryText.setText("");
        }

        if (gem.getAvgRating() != null) {
            holder.ratingText.setText("⭐ " + gem.getAvgRating()
                    + " (" + gem.getReviewCount() + ")");
        } else {
            holder.ratingText.setText("⭐ No ratings");
        }

        // Show photo count
        int photoCount = 0;
        if (gem.getImageIds() != null) {
            photoCount = gem.getImageIds().size();
        }
        holder.photosText.setText("📸 " + photoCount + " photo" + (photoCount == 1 ? "" : "s"));

        // ===== LOAD ACTUAL PHOTO =====
        // IMPORTANT: Always clear Glide first when view is recycled
        // Otherwise old images flash before new one loads
        Glide.with(context).clear(holder.gemImage);

        if (gem.getImageIds() != null && gem.getImageIds().size() > 0) {
            long firstImageId = gem.getImageIds().get(0);
            String imageUrl = ApiClient.getBaseUrl() + "/api/gems/images/" + firstImageId;

            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_gem)
                    .error(R.drawable.placeholder_gem)
                    .into(holder.gemImage);
        } else {
            holder.gemImage.setImageResource(R.drawable.placeholder_gem);
        }

        // Click to open gem detail
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GemDetailActivity.class);
                intent.putExtra("gemId", gem.getId().longValue());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gems.size();
    }

    static class GemViewHolder extends RecyclerView.ViewHolder {
        ImageView gemImage;
        TextView nameText, stateText, categoryText, ratingText, photosText;

        public GemViewHolder(@NonNull View itemView) {
            super(itemView);
            gemImage = itemView.findViewById(R.id.gemItemImage);
            nameText = itemView.findViewById(R.id.gemItemName);
            stateText = itemView.findViewById(R.id.gemItemState);
            categoryText = itemView.findViewById(R.id.gemItemCategory);
            ratingText = itemView.findViewById(R.id.gemItemRating);
            photosText = itemView.findViewById(R.id.gemItemPhotos);
        }
    }
}