package com.apps.pettracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.pettracker.R;
import com.apps.pettracker.adapters.LogsCategoriesRecyclerViewAdapter;
import com.apps.pettracker.objects.Category;
import com.apps.pettracker.viewmodels.LogsFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class LogsFragment extends Fragment {
    RecyclerView logsCategoriesRecyclerView;
    LogsCategoriesRecyclerViewAdapter logsCategoriesRecyclerViewAdapter;
    LogsFragmentViewModel logsFragmentViewModel;
    List<Category> categoryList;

    public LogsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_logs, container, false);
        String petId = getArguments().getString("petId");
        logsCategoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        categoryList = new ArrayList<>();
        logsCategoriesRecyclerViewAdapter = new LogsCategoriesRecyclerViewAdapter(categoryList);
        logsFragmentViewModel = new LogsFragmentViewModel();

        logsCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        logsCategoriesRecyclerView.setAdapter(logsCategoriesRecyclerViewAdapter);

        logsFragmentViewModel.getCategoryList().observe(getViewLifecycleOwner(), categories -> {
            if(categories == null || categories.isEmpty()){
                logsCategoriesRecyclerView.setVisibility(View.GONE);
            }else{
                logsCategoriesRecyclerView.setVisibility(View.VISIBLE);
                logsCategoriesRecyclerViewAdapter.setCategoryList(categories);
            }
        });

        logsFragmentViewModel.fetchCategories(petId);

        return view;
    }
}
