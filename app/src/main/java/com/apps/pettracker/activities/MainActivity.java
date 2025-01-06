package com.apps.pettracker.activities;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.apps.pettracker.R;
import com.apps.pettracker.adapters.PetRecycleViewAdapter;
import com.apps.pettracker.objects.Pet;
import com.apps.pettracker.utils.Animations;
import com.apps.pettracker.viewmodels.PetViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PetViewModel petViewModel;
    private FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SwipeRefreshLayout swipeRefreshLayout;
    ActivityResultLauncher<Intent> addPetLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIfLoggedIn();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        dataBase.setFirestoreSettings(settings);

        addPetLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d("Result", String.valueOf(result.getResultCode()));
            if (result.getResultCode() == Activity.RESULT_OK) {
                petViewModel.fetchPets(findViewById(R.id.main));
            }
        });

        List<Pet> petList = new ArrayList<>();
        ImageButton add_pet_button = findViewById(R.id.add_new_pet_button);
        ImageView settings_button = findViewById(R.id.settings_button);
        RecyclerView petListRecyclerView = findViewById(R.id.pet_cards_view);
        PetRecycleViewAdapter petViewAdapter = new PetRecycleViewAdapter(petList);
        TextView no_pet_added = findViewById(R.id.no_pet_added_text);

        petViewModel = new PetViewModel();
        petListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        petListRecyclerView.setAdapter(petViewAdapter);

        petViewModel.getPetList().observe(this, pets -> {
            if(pets == null || pets.isEmpty()){
                petListRecyclerView.setVisibility(View.GONE);
                no_pet_added.setVisibility(View.VISIBLE);
            }else{
                petListRecyclerView.setVisibility(View.VISIBLE);
                no_pet_added.setVisibility(View.GONE);
                petViewAdapter.setPetList(pets);
            }
        });

        petViewModel.fetchPets(findViewById(R.id.main));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            petViewModel.fetchPets(findViewById(R.id.main));
        });
        add_pet_button.setOnClickListener(v -> {
            Animation anim = new AlphaAnimation(1F, 0.7F);
            anim.setDuration(100);
            v.startAnimation(anim);
            Intent intent = new Intent(MainActivity.this, AddNewPetActivity.class);
            addPetLauncher.launch(intent);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        });

        settings_button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        setCurrentDateToText();
    }

    private void setCurrentDateToText(){
        handleIfLoggedIn();
        TextView dateText = findViewById(R.id.date_text);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d");

        String formattedDate = currentDate.format(formatter);

        String currentMonthString = formattedDate.split(" ")[0];
        String currentDayString = formattedDate.split(" ")[1];

        switch (currentMonthString){
            case "January":
                currentMonthString = getString(R.string.january);
                break;
            case "February":
                currentMonthString = getString(R.string.february);
                break;
            case "March":
                currentMonthString = getString(R.string.march);
                break;
            case "April":
                currentMonthString = getString(R.string.april);
                break;
            case "May":
                currentMonthString = getString(R.string.may);
                break;
            case "June":
                currentMonthString = getString(R.string.june);
                break;
            case "July":
                currentMonthString = getString(R.string.july);
                break;
            case "August":
                currentMonthString = getString(R.string.august);
                break;
            case "September":
                currentMonthString = getString(R.string.september);
                break;
            case "October":
                currentMonthString = getString(R.string.october);
                break;
            case "November":
                currentMonthString = getString(R.string.november);
                break;
            case "December":
                currentMonthString = getString(R.string.december);
                break;
        }

        String currentDateString = currentMonthString + " " + currentDayString;
        dateText.setText(currentDateString);
    }

    @Override
    public void onStart(){
        super.onStart();
        handleIfLoggedIn();
    }

    private void handleIfLoggedIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("User id", currentUser.getUid());
        }else {
            Log.d("F", "Failed");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            finish();
        }
    }
}