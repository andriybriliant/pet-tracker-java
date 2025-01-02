package com.apps.pettracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.apps.pettracker.R;

public class PetDetailsFragment extends Fragment {

    public TextView petBreedText, petColorText, petNeuteredText, petDateOfBirthText, petWeightText, petIndoor;
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
        return view;
    }
}
