package com.apps.pettracker.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.pettracker.R;
import com.apps.pettracker.activities.LogActivity;
import com.apps.pettracker.objects.Log;
import com.apps.pettracker.utils.Animations;
import com.apps.pettracker.viewmodels.LogsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

//TODO: Make logs creator to simultaneously fetch to calendar using db.get().where...
public class LogsRecyclerViewAdapter extends RecyclerView.Adapter<LogsRecyclerViewAdapter.LogsViewHolder> {
    private List<Log> logList;

    public static class LogsViewHolder extends RecyclerView.ViewHolder {
        TextView logName;
        ImageView logIcon;
        ConstraintLayout logConstraint;
        public LogsViewHolder(@NonNull View itemView) {
            super(itemView);
            logName = itemView.findViewById(R.id.log_name_text);
            logIcon = itemView.findViewById(R.id.log_item_image);
            logConstraint = itemView.findViewById(R.id.log_item_constraint);
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Log log = logList.get(position);
        String userId = mAuth.getCurrentUser().getUid();
        String petId = log.getPetId();
        String logId = log.getId();
        String categoryId = log.getCategoryId();

        holder.logName.setText(log.getName());
        holder.logConstraint.setLongClickable(true);

        holder.logConstraint.setOnLongClickListener(v -> {
            v.startAnimation(Animations.LogsItemHoldAnimation(0.95F));

            AlertDialog.Builder askDeleteBuilder = new AlertDialog
                    .Builder(new ContextThemeWrapper(v.getContext(), R.style.AlertDialogCustom));

            askDeleteBuilder.setMessage(R.string.this_action_cannot_be_undone)
                    .setTitle(R.string.delete_log_prompt);

            askDeleteBuilder.setPositiveButton(R.string.yes, (dialog, which) -> {
                LogsViewModel.removeLog(userId, petId, categoryId, logId);
                logList.remove(position);
                notifyItemRemoved(position);
            });

            askDeleteBuilder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());

            AlertDialog askDeleteDialog = askDeleteBuilder.create();
            askDeleteDialog.show();
            return true;
        });

        holder.logConstraint.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), LogActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("petId", petId);
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("logId", logId);
            v.getContext().startActivity(intent);
        });
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
