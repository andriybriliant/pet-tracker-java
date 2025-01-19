package com.apps.pettracker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.pettracker.R;
import com.apps.pettracker.adapters.LogsRecyclerViewAdapter;
import com.apps.pettracker.objects.Log;
import com.apps.pettracker.viewmodels.LogsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryLogsActivity extends AppCompatActivity {
    String petId;
    String categoryId;
    String userId;
    LogsViewModel logsViewModel;
    LogsRecyclerViewAdapter logsRecyclerViewAdapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView categoryNameText;
    ImageButton addNewLogButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_logs);
        List<Log> logList = new ArrayList<>();
        ActivityResultLauncher<Intent> addLogLauncher;
        userId = mAuth.getCurrentUser().getUid();
        petId = getIntent().getStringExtra("petId");
        categoryId = getIntent().getStringExtra("categoryId");
        logsViewModel = new LogsViewModel();
        logsRecyclerViewAdapter = new LogsRecyclerViewAdapter(logList);
        categoryNameText = findViewById(R.id.category_logs_name);
        addNewLogButton = findViewById(R.id.add_logs_button);
        RecyclerView logsRecyclerView = findViewById(R.id.logs_recycler_view);

        logsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        logsRecyclerView.setAdapter(logsRecyclerViewAdapter);

        addLogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            android.util.Log.d("Result", String.valueOf(result.getResultCode()));
            if (result.getResultCode() == Activity.RESULT_OK) {
                logsViewModel.fetchLogsList(userId, petId, categoryId);
            }
        });

        logsViewModel.getLogsList().observe(this, logs -> {
            if (logs.isEmpty()){
                logsRecyclerView.setVisibility(View.GONE);
            }else{
                logsRecyclerView.setVisibility(View.VISIBLE);
                logsRecyclerViewAdapter.setLogList(logs);
            }
        });

        logsViewModel.fetchLogsList(userId, petId, categoryId);
        setCategoryName();

        addNewLogButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddNewLogActivity.class);
            intent.putExtra("petId", petId);
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("userId", userId);
            intent.putExtra("categoryName", categoryNameText.getText().toString());
            addLogLauncher.launch(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.category_logs_constraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void setCategoryName(){
        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("logs")
                .document(categoryId)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        String categoryName = documentSnapshot.getString("name");
                        categoryNameText.setText(categoryName);
                    }
                });
    }
}
