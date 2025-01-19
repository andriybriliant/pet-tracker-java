package com.apps.pettracker.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.pettracker.objects.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        String categoryName = documentSnapshot.getString("name");
                        if(categoryName.equals("Weight")){
                            dataBase.collection("users")
                                    .document(userId)
                                    .collection("pets")
                                    .document(petId)
                                    .collection("logs")
                                    .whereEqualTo("name", "Weight")
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            QuerySnapshot querySnapshot = task1.getResult();
                                            DocumentSnapshot documentSnapshot1 = querySnapshot.getDocuments().get(0);
                                            dataBase.collection("users")
                                                    .document(userId)
                                                    .collection("pets")
                                                    .document(petId)
                                                    .collection("logs")
                                                    .document(documentSnapshot1.getId())
                                                    .collection("logs")
                                                    .orderBy("date", Query.Direction.DESCENDING)
                                                    .limit(1)
                                                    .get()
                                                    .addOnCompleteListener(task2 -> {
                                                        if(task2.isSuccessful()){
                                                            QuerySnapshot querySnapshot1 = task2.getResult();
                                                            if(!querySnapshot1.isEmpty()){
                                                                DocumentSnapshot documentSnapshot2 = querySnapshot1.getDocuments().get(0);
                                                                dataBase.collection("users")
                                                                        .document(userId)
                                                                        .collection("pets")
                                                                        .document(petId).update("weight", (Double.parseDouble(documentSnapshot2.getString("name"))));
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
                });
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
