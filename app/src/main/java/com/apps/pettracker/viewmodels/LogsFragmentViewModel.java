package com.apps.pettracker.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.pettracker.objects.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LogsFragmentViewModel {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    MutableLiveData<List<Category>> categoryList = new MutableLiveData<>();

    public LiveData<List<Category>> getCategoryList() {
        return categoryList;
    }

    public void fetchCategories(String petId){
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("logs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        List<Category> categories = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            String categoryId = documentSnapshot.getId();
                            String categoryName = documentSnapshot.getString("name");
                            Category category = new Category(categoryName, petId);
                            Log.d("Category", category.getName());
                            category.setId(categoryId);
                            categories.add(category);
                        }
                        categoryList.setValue(categories);
                    }
                });
    }

}
