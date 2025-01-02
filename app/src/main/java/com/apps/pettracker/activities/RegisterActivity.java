package com.apps.pettracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.apps.pettracker.R;
import com.apps.pettracker.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        Button register_button = findViewById(R.id.register_button);
        EditText name_input = findViewById(R.id.register_name_input);
        EditText email_input = findViewById(R.id.register_email_input);
        EditText password_input = findViewById(R.id.register_password_input);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_input.getText().toString();
                String name = name_input.getText().toString();
                String password = password_input.getText().toString();

                if (validateEmailAndPassword(name, email, password)){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String userID = user.getUid();
                                        User createdUser = new User(name, email, userID);
                                        addUserToDatabase(createdUser);
                                        updateUI(userID);
                                    }else{
                                        Log.d("Register", "Failed to register the user");
                                    }
                                }
                            });
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_constraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean validateEmailAndPassword(String name, String email, String password){
        if(name.isEmpty() || (!email.contains(".") && !email.contains("@")) || password.length() < 8){
            return false;
        }
        return true;
    }

    public void addUserToDatabase(User user){
        db.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(aVoid -> {
                    Log.d("User database", "Successfully added user to database");
                })
                .addOnFailureListener(aVoid -> {
                    Log.d("User database", "Failed to add user to database");
                });
    }

    public void updateUI(String userID){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.putExtra("user_id", userID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
