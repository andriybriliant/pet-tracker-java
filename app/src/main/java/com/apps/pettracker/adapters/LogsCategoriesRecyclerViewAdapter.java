package com.apps.pettracker.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.pettracker.R;
import com.apps.pettracker.activities.CategoryLogsActivity;
import com.apps.pettracker.objects.Category;
import com.apps.pettracker.utils.Animations;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LogsCategoriesRecyclerViewAdapter extends RecyclerView.Adapter<LogsCategoriesRecyclerViewAdapter.LogsCategoriesViewHolder> {

    private List<Category> categoryList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        String userId = mAuth.getCurrentUser().getUid();
        Animation fadeAnimation = new AlphaAnimation(1F, 0.6F);
        holder.logsCategoryItemConstraint.setLongClickable(true);
        String categoryName = category.getName();

        switch (categoryName){
            case "Vaccines":
                holder.categoryName.setText(R.string.vaccines);
                break;
            case "Weight":
                holder.categoryName.setText(R.string.weight);
                break;
            case "Medication":
                holder.categoryName.setText(R.string.medication);
                break;
            case "Surgeries":
                holder.categoryName.setText(R.string.surgeries);
                break;
            case "Vet Visits":
                holder.categoryName.setText(R.string.vet_visits);
                break;
            default:
                holder.categoryName.setText(category.getName());
                break;
        }


        fadeAnimation.setDuration(100);

        holder.logsCategoryItemConstraint.setOnClickListener(view -> {
            Context context = view.getContext();
            holder.logsCategoryItemConstraint.startAnimation(fadeAnimation);
            view.startAnimation(Animations.LogsItemHoldAnimation(0.95F));
            Intent intent = new Intent(context, CategoryLogsActivity.class);
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("petId", petId);
            context.startActivity(intent);
        });

        holder.logsCategoryItemConstraint.setOnLongClickListener(v -> {
            v.startAnimation(Animations.LogsItemHoldAnimation(0.95F));

            AlertDialog.Builder askDeleteBuilder = new AlertDialog
                    .Builder(new ContextThemeWrapper(v.getContext(), R.style.AlertDialogCustom));

            askDeleteBuilder.setMessage(R.string.this_action_cannot_be_undone)
                    .setTitle(R.string.delete_category_prompt);

            askDeleteBuilder.setPositiveButton(R.string.yes, (dialog, which) -> {
                if(categoryName.equals("Vaccines") || categoryName.equals("Weight") || categoryName.equals("Medication") || categoryName.equals("Surgeries") || categoryName.equals("Vet Visits")){
                    Toast.makeText(v.getContext(), "Cannot delete this category", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.collection("users")
                        .document(userId)
                        .collection("pets")
                        .document(petId)
                        .collection("logs")
                        .document(categoryId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            categoryList.remove(holder.getAdapterPosition());
                            Toast.makeText(v.getContext(), "Successfully deleted category", Toast.LENGTH_SHORT).show();
                            notifyItemRemoved(holder.getAdapterPosition());
                        });
            });

            askDeleteBuilder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());

            AlertDialog askDeleteDialog = askDeleteBuilder.create();
            askDeleteDialog.show();
            return true;
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
