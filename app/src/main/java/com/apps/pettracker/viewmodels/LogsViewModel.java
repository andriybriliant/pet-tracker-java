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
                           String petLogId = documentSnapshot.getString("petId");
                           String categoryLogId = documentSnapshot.getString("categoryId");
                           String logDescription = documentSnapshot.getString("description");
                           long logDate = documentSnapshot.getLong("date");
                           Log log = new Log(logName, logDescription, logDate);
                           android.util.Log.d("Name", logName);
                           log.setId(logId);
                           log.setCategoryId(categoryLogId);
                           log.setPetId(petLogId);
                           logs.add(log);
                       }
                       logsList.setValue(logs);
                   }
                });
    }

    public static void removeLog(String userId, String petId, String categoryId, String logId){
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
        dataBase.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("logs")
                .document(categoryId)
                .collection("logs")
                .document(logId)
                .delete();
    }
}
