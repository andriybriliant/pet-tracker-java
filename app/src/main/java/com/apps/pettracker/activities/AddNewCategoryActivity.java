package com.apps.pettracker.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.apps.pettracker.R;
import com.apps.pettracker.objects.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddNewCategoryActivity extends AppCompatActivity {
    Button doneButton;
    EditText nameText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userId;
    String petId;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_category);
        doneButton = findViewById(R.id.add_category_done_button);
        nameText = findViewById(R.id.add_category_name_input);
        userId = mAuth.getCurrentUser().getUid();
        petId = getIntent().getStringExtra("petId");

        doneButton.setOnClickListener(view -> {
            String categoryName = nameText.getText().toString();
            addCategoryToDatabase(categoryName);
            setResult(RESULT_OK);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_log_constraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addCategoryToDatabase(String categoryName){
        Category category = new Category(categoryName, petId);
        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("logs")
                .document(category.getId())
                .set(category);
    }
}
