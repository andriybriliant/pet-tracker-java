package com.apps.pettracker.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.pettracker.objects.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LogsViewModel {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Log>> logsList = new MutableLiveData<>();

    public LiveData<List<Log>> getLogsList() {
        return this.logsList;
    }

    public void fetchLogsList(String userId, String petId, String categoryId){
        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("logs")
                .document(categoryId)
                .collection("logs")
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()){
                       List<Log> logs = new ArrayList<>();
                       for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                           String logName = documentSnapshot.getString("name");
                           String logId = documentSnapshot.getId();
                           String logDescription = documentSnapshot.getString("description");
                           String logDate = documentSnapshot.getString("date");
                           Log log = new Log(logName, logDescription, logDate);
                           android.util.Log.d("Name", logName);
                           log.setId(logId);
                           logs.add(log);
                       }
                       logsList.setValue(logs);
                   }
                });
    }
}
