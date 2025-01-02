package com.apps.pettracker.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.apps.pettracker.R;

public class CategoryLogsActivity extends AppCompatActivity {
    String petId;
    String categoryId;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_logs);
        petId = getIntent().getStringExtra("petId");
        categoryId = getIntent().getStringExtra("categoryId");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.category_logs_constraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
