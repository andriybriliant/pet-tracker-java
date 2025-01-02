package com.apps.pettracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.pettracker.R;
import com.apps.pettracker.objects.Category;

import java.util.List;

public class LogsCategoriesRecyclerViewAdapter extends RecyclerView.Adapter<LogsCategoriesRecyclerViewAdapter.LogsViewHolder> {

    private List<Category> categoryList;

    public LogsCategoriesRecyclerViewAdapter(List<Category> categoryList){
        this.categoryList = categoryList;
    }

    public static class LogsViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public LogsViewHolder(@NonNull View itemView){
            super(itemView);
            this.categoryName = itemView.findViewById(R.id.category_name_text);
        }
    }

    @NonNull
    @Override
    public LogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.logs_category_item, parent, false);
        return new LogsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogsViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());
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
