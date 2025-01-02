package com.apps.pettracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.pettracker.R;
import com.apps.pettracker.objects.Log;

import java.util.List;

//TODO: Make logs creator to simultaneously fetch to calendar using db.get().where...
public class LogsRecyclerViewAdapter extends RecyclerView.Adapter<LogsRecyclerViewAdapter.LogsViewHolder> {
    private List<Log> logList;

    public static class LogsViewHolder extends RecyclerView.ViewHolder {
        TextView logName;
        ImageView logIcon;
        public LogsViewHolder(@NonNull View itemView) {
            super(itemView);
            logName = itemView.findViewById(R.id.log_name_text);
            logIcon = itemView.findViewById(R.id.log_item_image);
        }
    }

    public LogsRecyclerViewAdapter(List<Log> logList){
        this.logList = logList;
    }

    @NonNull
    @Override
    public LogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_item, parent, false);
        return new LogsRecyclerViewAdapter.LogsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogsViewHolder holder, int position) {
        Log log = logList.get(position);
        holder.logName.setText(log.getName());
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public void setLogList(List<Log> logs){
        this.logList = logs;
        notifyDataSetChanged();
    }
}
