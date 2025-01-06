package com.apps.pettracker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_logs);
        List<Log> logList = new ArrayList<>();
        userId = mAuth.getCurrentUser().getUid();
        petId = getIntent().getStringExtra("petId");
        categoryId = getIntent().getStringExtra("categoryId");
        logsViewModel = new LogsViewModel();
        logsRecyclerViewAdapter = new LogsRecyclerViewAdapter(logList);
        categoryNameText = findViewById(R.id.category_logs_name);
        RecyclerView logsRecyclerView = findViewById(R.id.logs_recycler_view);

        logsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        logsRecyclerView.setAdapter(logsRecyclerViewAdapter);

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
