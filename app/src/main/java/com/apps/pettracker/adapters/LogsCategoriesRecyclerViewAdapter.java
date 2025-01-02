package com.apps.pettracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.pettracker.R;
import com.apps.pettracker.activities.CategoryLogsActivity;
import com.apps.pettracker.objects.Category;

import java.util.List;

public class LogsCategoriesRecyclerViewAdapter extends RecyclerView.Adapter<LogsCategoriesRecyclerViewAdapter.LogsCategoriesViewHolder> {

    private List<Category> categoryList;

    public LogsCategoriesRecyclerViewAdapter(List<Category> categoryList){
        this.categoryList = categoryList;
    }

    public static class LogsCategoriesViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ConstraintLayout logsCategoryItemConstraint;

        public LogsCategoriesViewHolder(@NonNull View itemView){
            super(itemView);
            this.categoryName = itemView.findViewById(R.id.category_name_text);
            this.logsCategoryItemConstraint = itemView.findViewById(R.id.logs_category_constraint);
        }
    }

    @NonNull
    @Override
    public LogsCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.logs_category_item, parent, false);
        return new LogsCategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogsCategoriesViewHolder holder, int position) {
        Category category = categoryList.get(position);
        String categoryId = category.getId();
        String petId = category.getPetId();
        Animation fadeAnimation = new AlphaAnimation(1F, 0.6F);

        holder.categoryName.setText(category.getName());
        fadeAnimation.setDuration(100);

        holder.logsCategoryItemConstraint.setOnClickListener(view -> {
            Context context = view.getContext();
            holder.logsCategoryItemConstraint.startAnimation(fadeAnimation);
            Intent intent = new Intent(context, CategoryLogsActivity.class);
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("petId", petId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategoryList(List<Category> categoryList){
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }
}
