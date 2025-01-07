package com.apps.pettracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.pettracker.R;
import com.apps.pettracker.objects.Log;

import java.util.List;

public class CalendarLogsRecyclerViewAdapter extends RecyclerView.Adapter<CalendarLogsRecyclerViewAdapter.CalendarLogsViewHolder> {

    List<Log> logList;

    public CalendarLogsRecyclerViewAdapter(List<Log> logs){
        this.logList = logs;
    }

    public static class CalendarLogsViewHolder extends RecyclerView.ViewHolder{
        TextView logName;
        public CalendarLogsViewHolder(@NonNull View itemView){
            super(itemView);
            this.logName = itemView.findViewById(R.id.log_name_text);
        }
    }

    @NonNull
    @Override
    public CalendarLogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_calendar_item, parent, false);
        return new CalendarLogsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarLogsViewHolder holder, int position) {
        Log log = logList.get(position);
        holder.logName.setText(log.getName());
        android.util.Log.d("name", log.getName());
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
