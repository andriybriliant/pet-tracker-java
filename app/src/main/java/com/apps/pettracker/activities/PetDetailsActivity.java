package com.apps.pettracker.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.apps.pettracker.R;
import com.apps.pettracker.fragments.CalendarFragment;
import com.apps.pettracker.fragments.LogsFragment;
import com.apps.pettracker.fragments.PetDetailsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//TODO: Make fetching from database safe from exceptions
public class PetDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    PetDetailsFragment petDetailsFragment;
    ConstraintLayout pet_details_container;
    TextView nameText;
    ImageView pet_profile_picture_gender_icon, loading_icon, pet_profile_image;
    String petId;
    String userId;
    BottomNavigationView bottomNav;
    FragmentContainerView fragmentContainer;
    Bundle bundle = new Bundle();
    MenuItem mainMenuItem;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);
        ImageView go_back_button = findViewById(R.id.go_back_pet_details);
        pet_profile_picture_gender_icon = findViewById(R.id.pet_gender_icon_details);
        pet_profile_image = findViewById(R.id.pet_details_profile_image);
        bottomNav = findViewById(R.id.pet_details_navigation);
        petDetailsFragment = new PetDetailsFragment();
        fragmentContainer = findViewById(R.id.pet_details_fragment_view);
        bottomNav.setOnItemSelectedListener(selectedListener);
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.pet_details_fragment_view, petDetailsFragment).commit();

        nameText = findViewById(R.id.pet_details_name);
        petId = getIntent().getStringExtra("pet_id");

        go_back_button.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pet_details_constraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        pet_details_container = findViewById(R.id.pet_details_container);
        loading_icon = findViewById(R.id.pet_details_loading);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            userId = currentUser.getUid();
        }else{
            userId = "0";
        }
        fetchPets(R.id.pet_details_container);
    }

    private final NavigationBarView.OnItemSelectedListener selectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;
            if(itemId == R.id.main_nav){
                selectedFragment = petDetailsFragment;
            } else if (itemId == R.id.calendar_nav) {
                selectedFragment = new CalendarFragment();
            } else if (itemId == R.id.logs_nav) {
                selectedFragment = new LogsFragment();
            }
            if (selectedFragment != null) {
                bundle.putString("petId", petId);
                selectedFragment.setArguments(bundle);
                fetchPets(R.id.pet_details_fragment_view);
                getSupportFragmentManager().beginTransaction().replace(R.id.pet_details_fragment_view, selectedFragment).commit();
            }
            return true;
        }
    };

    private void fetchPets(int itemToRefreshId) {
        Animation rotation = new RotateAnimation(0F, 360F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        rotation.setRepeatCount(Animation.INFINITE);
        rotation.setDuration(800);
        findViewById(itemToRefreshId).setVisibility(View.GONE);
        mainMenuItem = bottomNav.getMenu().findItem(R.id.main_nav);
        loading_icon.startAnimation(rotation);
        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        String petName = document.getString("name");
                        String petGender = document.getString("gender");
                        String petBreed = document.getString("breed");
                        String petColor = document.getString("color");
                        String petDateOfBirth = document.getString("dateOfBirth");
                        String petType = document.getString("type");
                        String petImageUrl = document.getString("imageURL");
                        double petWeight = document.getDouble("weight");
                        boolean petNeutered = Boolean.TRUE.equals(document.getBoolean("neutered"));
                        boolean petIndoor = Boolean.TRUE.equals(document.getBoolean("indoor"));
                        nameText.setText(petName);
                        petDetailsFragment.petBreedText.setText(petBreed);
                        petDetailsFragment.petColorText.setText(petColor);
                        petDetailsFragment.petDateOfBirthText.setText(petDateOfBirth);
                        petDetailsFragment.petWeightText.setText(String.valueOf(petWeight));
                        Log.d("pet", petDetailsFragment.petBreedText.getText().toString());

                        if (petType != null && petType.equals("Cat")){
                            mainMenuItem.setIcon(R.drawable.cat_solid);
                            petDetailsFragment.shieldIcon.setImageDrawable(AppCompatResources.getDrawable(PetDetailsActivity.this, R.drawable.shield_cat_solid));
                        }
                        else{
                            mainMenuItem.setIcon(R.drawable.dog_solid);
                            petDetailsFragment.shieldIcon.setImageDrawable(AppCompatResources.getDrawable(PetDetailsActivity.this, R.drawable.shield_dog_solid));
                        }

                        if(petGender != null && petGender.equals("Male")){
                            pet_profile_picture_gender_icon.setImageDrawable(AppCompatResources.getDrawable(PetDetailsActivity.this, R.drawable.mars_solid));
                            if(petNeutered){
                                petDetailsFragment.petNeuteredText.setText(R.string.neutered_male);
                            }else{
                                petDetailsFragment.petNeuteredText.setText(R.string.not_neutered_male);
                            }
                        } else if (petGender != null && petGender.equals("Female")) {
                            pet_profile_picture_gender_icon.setImageDrawable(AppCompatResources.getDrawable(PetDetailsActivity.this, R.drawable.venus_solid));
                            if(petNeutered){
                                petDetailsFragment.petNeuteredText.setText(R.string.neutered_female);
                            }else{
                                petDetailsFragment.petNeuteredText.setText(R.string.not_neutered_female);
                            }
                        }

                        if(petIndoor){
                            petDetailsFragment.petIndoor.setText(getString(R.string.no));
                        }else{
                            petDetailsFragment.petIndoor.setText(getString(R.string.yes));
                        }

                        storageReference.child(petImageUrl).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            pet_profile_image.setPadding(0, 0, 0, 0);
                            pet_profile_image.setImageBitmap(bmp);
                        });
                    }

                    findViewById(itemToRefreshId).setVisibility(View.VISIBLE);
                    loading_icon.setVisibility(View.GONE);
                    loading_icon.clearAnimation();
                });
    }
}
