package com.apps.pettracker.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.pettracker.objects.Category;
import com.apps.pettracker.objects.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CalendarFragmentViewModel {
    MutableLiveData<List<Log>> logsList = new MutableLiveData<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Log>> getLogsList(){
        return this.logsList;
    }

    public void fetchLogs(String userId, String petId, long milliseconds){
        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("logs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        logsList.setValue(new ArrayList<>());
                        List<Log> logs = new ArrayList<>();
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            String categoryId = documentSnapshot.getId();
                            db.collection("users")
                                    .document(userId)
                                    .collection("pets")
                                    .document(petId)
                                    .collection("logs")
                                    .document(categoryId)
                                    .collection("logs")
                                    .whereEqualTo("date", milliseconds)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                       if (task1.isSuccessful()){
                                           for (QueryDocumentSnapshot documentSnapshot1 : task1.getResult()){
                                               String logId = documentSnapshot1.getId();
                                               String logName = documentSnapshot1.getString("name");
                                               String logDescription = documentSnapshot1.getString("description");
                                               long logDate = documentSnapshot1.getLong("date");
                                               Log log = new Log(logName, logDescription, logDate);
                                               log.setId(logId);
                                               logs.add(log);
                                               logsList.setValue(logs);
                                               android.util.Log.d("LogName", logName);
                                           }
                                       }
                                    });
                        }
                    }
                });
    }
}
