package com.apps.pettracker.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.apps.pettracker.R;
import com.apps.pettracker.objects.Category;
import com.apps.pettracker.objects.Pet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetViewModel {
    private final MutableLiveData<List<Pet>> petList = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public LiveData<List<Pet>> getPetList(){
        return petList;
    }

    //TODO: Make fetching from database safe from exceptions
    public void fetchPets(View view) {
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setRefreshing(true);
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users")
                .document(userId)
                .collection("pets")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<Pet> pets = new ArrayList<>();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("Pets fetched", document.getId() + " => " + document.getData());
                                String petId = document.getId();
                                Log.d("Pet id", petId);
                                String petName = document.getString("name");
                                String petAge = document.getString("age");
                                String petGender = document.getString("gender");
                                String petType = document.getString("type");
                                String petBreed = document.getString("breed");
                                String petColor = document.getString("color");
                                String petImageUrl = document.getString("imageURL");
                                double petWeight = 0;
                                boolean petIndoor = true;
                                boolean petNeutered = false;
                                try {
                                    petWeight = document.getDouble("weight");
                                }catch(NullPointerException e){
                                    Log.d("Error getting pet weight from the database", e.toString());
                                }

                                try {
                                    petIndoor = document.getBoolean("indoor");
                                }catch(NullPointerException e){
                                    Log.d("Error getting pet indoor value from the database", e.toString());
                                }

                                try {
                                    petNeutered = document.getBoolean("neutered");
                                }catch(NullPointerException e){
                                    Log.d("Error getting pet neutered value from the database", e.toString());
                                }

                                Pet newPet = new Pet(petName, petGender, petAge, petType, petBreed, petColor, petWeight, petNeutered, petIndoor);
                                newPet.setImageURL(petImageUrl);
                                newPet.setPetId(petId);
                                pets.add(newPet);
                            }
                            petList.setValue(pets);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        else{
                            Log.d("Pets fetched error", "Error", task.getException());
                        }
                    }
                });
    }

    public void addPet(Pet newPet){
        String userId = mAuth.getCurrentUser().getUid();
        String petId = newPet.getPetId();
        Category vetVisits = new Category("Vet Visits");
        Category medication = new Category("Medication");
        Category vaccines = new Category("Vaccines");
        Category weight = new Category("Weight");
        Category surgeries = new Category("Surgeries");

        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .set(newPet)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireStore", "Document successfully added");
                    db.collection("users")
                            .document(userId)
                            .collection("pets")
                            .document(petId)
                            .collection("logs")
                            .document(vetVisits.getId())
                            .set(vetVisits);

                    db.collection("users")
                            .document(userId)
                            .collection("pets")
                            .document(petId)
                            .collection("logs")
                            .document(medication.getId())
                            .set(medication);

                    db.collection("users")
                            .document(userId)
                            .collection("pets")
                            .document(petId)
                            .collection("logs")
                            .document(vaccines.getId())
                            .set(vaccines);

                    db.collection("users")
                            .document(userId)
                            .collection("pets")
                            .document(petId)
                            .collection("logs")
                            .document(weight.getId())
                            .set(weight);

                    db.collection("users")
                            .document(userId)
                            .collection("pets")
                            .document(petId)
                            .collection("logs")
                            .document(surgeries.getId())
                            .set(surgeries);
                })
                .addOnFailureListener(e -> {
                    Log.w("FireStore", "Failed to add a document", e);
                });

    }

}
