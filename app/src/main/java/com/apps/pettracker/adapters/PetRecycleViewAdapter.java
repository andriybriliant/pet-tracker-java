package com.apps.pettracker.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.pettracker.activities.PetDetailsActivity;
import com.apps.pettracker.R;
import com.apps.pettracker.objects.Pet;
import com.apps.pettracker.utils.Animations;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PetRecycleViewAdapter extends RecyclerView.Adapter<PetRecycleViewAdapter.PetViewHolder> {
    private List<Pet> petList;

    public PetRecycleViewAdapter(List<Pet> petList){
        this.petList = petList;
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView petImage, typePicture, genderIcon, loadingIcon;
        TextView petName, petWeight;
        ImageView deletePet, editPet;
        ConstraintLayout petItemConstraint;

        public PetViewHolder(@NonNull View itemView){
            super(itemView);
            petImage = itemView.findViewById(R.id.pet_item_image);
            typePicture = itemView.findViewById(R.id.pet_item_icon);
            genderIcon = itemView.findViewById(R.id.pet_item_gender_icon);
            petName = itemView.findViewById(R.id.pet_item_name);
            petWeight = itemView.findViewById(R.id.pet_item_weight);
            deletePet = itemView.findViewById(R.id.pet_item_delete);
            editPet = itemView.findViewById(R.id.pet_item_edit);
            petItemConstraint = itemView.findViewById(R.id.pet_item_constraint);
            loadingIcon = itemView.findViewById(R.id.pet_item_loading);
        }
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_item, parent, false);
        return new PetViewHolder(view);
    }

    //TODO: Make fetching from database safe from exceptions
    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID;
        if(currentUser != null){
            userID = currentUser.getUid();
        }else{
            userID = "0";
        }
        Pet pet = petList.get(position);
        String petImageUrl = pet.getImageURL();
        StorageReference petImageReference = storageReference.child(petImageUrl);
        String documentId = pet.getPetId();
        Animation fadeAnimation = new AlphaAnimation(1F, 0.6F);
        fadeAnimation.setDuration(100);

        Animation rotation = new RotateAnimation(0F, 360F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        rotation.setRepeatCount(Animation.INFINITE);
        rotation.setDuration(800);

        holder.petName.setText(pet.getName());
        holder.petWeight.setText(String.valueOf(pet.getWeight()));

        if(pet.getType().equals("Cat")){
            holder.typePicture.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cat_solid));
        }
        else{
            holder.typePicture.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.dog_solid));
        }

        if(pet.getGender().equals("Male")){
            holder.genderIcon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.mars_solid));
        }
        else{
            holder.genderIcon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.venus_solid));
        }

        holder.petWeight.setText(String.valueOf(pet.getWeight()));

        holder.petImage.setVisibility(View.GONE);
        holder.loadingIcon.startAnimation(rotation);
        holder.loadingIcon.setVisibility(View.VISIBLE);

        //Fetching pet image from database and changing UI accordingly
        storageReference.child(pet.getImageURL()).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.petImage.setImageBitmap(bmp);
            holder.loadingIcon.setVisibility(View.GONE);
            holder.loadingIcon.clearAnimation();
            holder.petImage.setVisibility(View.VISIBLE);
        });

        holder.petItemConstraint.setOnClickListener(v -> {
            Context context = v.getContext();
            holder.petItemConstraint.startAnimation(fadeAnimation);
            Intent intent = new Intent(context, PetDetailsActivity.class);
            intent.putExtra("pet_id", documentId);
            context.startActivity(intent);
        });

        holder.deletePet.setOnClickListener(v -> {
            Context context = v.getContext();
            AlertDialog.Builder askDeleteBuilder = new AlertDialog
                    .Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));

            askDeleteBuilder.setMessage(R.string.this_action_cannot_be_undone)
                    .setTitle(R.string.delete_pet_profile_prompt);

            askDeleteBuilder.setPositiveButton(R.string.yes, (dialog, which) ->
                    db.collection("users")
                    .document(userID)
                    .collection("pets")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        petList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        petImageReference.delete();
                        Log.d("FireStore", "Document successfully deleted");
                    }).addOnFailureListener(e -> Log.w("FireStore", "Error deleting document", e))
            );

            askDeleteBuilder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());

            AlertDialog askDeleteDialog = askDeleteBuilder.create();
            askDeleteDialog.show();
        });
    }

    @Override
    public int getItemCount(){
        return petList.size();
    }

    public void setPetList(List<Pet> newPetList){
        petList = newPetList;
        notifyDataSetChanged();
    }
}
