package com.team_inertia.gonly_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team_inertia.gonly_android.R;
import com.team_inertia.gonly_android.model.ReviewResponse;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewResponse> reviews = new ArrayList<>();

    public void setReviews(List<ReviewResponse> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewResponse review = reviews.get(position);
        holder.userName.setText(review.getUserName());
        holder.comment.setText(review.getComment());

        // Show stars based on rating
        if (review.getRating() != null) {
            String stars = "";
            for (int i = 0; i < review.getRating(); i++) {
                stars = stars + "⭐";
            }
            holder.rating.setText(stars);
        } else {
            holder.rating.setText("No rating");
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView userName, rating, comment;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.reviewUserName);
            rating = itemView.findViewById(R.id.reviewRating);
            comment = itemView.findViewById(R.id.reviewComment);
        }
    }
}