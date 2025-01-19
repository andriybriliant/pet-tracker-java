package com.apps.pettracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.apps.pettracker.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PetDetailsFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public TextView petBreedText, petColorText, petNeuteredText, petDateOfBirthText, petWeightText, petIndoor, lastVetVisitDate;
    public ImageView shieldIcon;

    public PetDetailsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_pet_details, container, false);
        petBreedText = view.findViewById(R.id.breed_text);
        petColorText = view.findViewById(R.id.color_text);
        petNeuteredText = view.findViewById(R.id.neutered_text);
        petDateOfBirthText = view.findViewById(R.id.birth_date);
        petWeightText = view.findViewById(R.id.weight_text);
        shieldIcon = view.findViewById(R.id.neutered_icon);
        petIndoor = view.findViewById(R.id.outdoor_holder);
        lastVetVisitDate = view.findViewById(R.id.last_vet_visit_holder);
        Bundle bundle = getArguments();
        String userId = bundle.getString("userId");
        String petId = bundle.getString("petId");

        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("logs")
                .whereEqualTo("name", "Vet Visits")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        db.collection("users")
                                .document(userId)
                                .collection("pets")
                                .document(petId)
                                .collection("logs")
                                .document(documentSnapshot.getId())
                                .collection("logs")
                                .orderBy("date", Query.Direction.DESCENDING)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        QuerySnapshot querySnapshot1 = task1.getResult();
                                        if(!querySnapshot1.getDocuments().isEmpty()){
                                            DocumentSnapshot documentSnapshot1 = querySnapshot1.getDocuments().get(0);
                                            long milliseconds = documentSnapshot1.getLong("date");
                                            DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTimeInMillis(milliseconds);
                                            lastVetVisitDate.setText(dateFormatter.format(calendar.getTime()));
                                        }
                                    }
                                });
                    }
                });
        return view;
    }
}
