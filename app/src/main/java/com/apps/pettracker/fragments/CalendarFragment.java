package com.apps.pettracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.pettracker.R;
import com.apps.pettracker.adapters.CalendarLogsRecyclerViewAdapter;
import com.apps.pettracker.objects.Log;
import com.apps.pettracker.viewmodels.CalendarFragmentViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CalendarView calendarView;
    RecyclerView logsRecyclerView;
    CalendarLogsRecyclerViewAdapter calendarLogsRecyclerViewAdapter;
    CalendarFragmentViewModel calendarFragmentViewModel;
    public CalendarFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView = view.findViewById(R.id.calendar_view);
        Bundle bundle = getArguments();
        String petId = bundle.getString("petId");
        String userId = bundle.getString("userId");
        List<Log> logList = new ArrayList<>();
        logsRecyclerView = view.findViewById(R.id.calendar_logs_recycler);
        calendarLogsRecyclerViewAdapter = new CalendarLogsRecyclerViewAdapter(logList);
        calendarFragmentViewModel = new CalendarFragmentViewModel();

        logsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        logsRecyclerView.setAdapter(calendarLogsRecyclerViewAdapter);

        calendarFragmentViewModel.getLogsList().observe(getViewLifecycleOwner(), logs -> {
            if(logs == null || logs.isEmpty()){
                logsRecyclerView.setVisibility(View.GONE);
            }else{
                logsRecyclerView.setVisibility(View.VISIBLE);
                android.util.Log.d("Logs", "fetched");
                android.util.Log.d("Logname", String.valueOf(logs.size()));
                calendarLogsRecyclerViewAdapter.setLogList(logs);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String dateString = year + "/" + dayOfMonth + "/" + (month + 1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = null;
                long milliseconds;
                try {
                    date = simpleDateFormat.parse(dateString);
                    milliseconds = date.getTime();

                    calendarFragmentViewModel.fetchLogs(userId, petId, milliseconds);
                    android.util.Log.d("Logs", String.valueOf(milliseconds));
                    android.util.Log.d("Logs", dateString);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        calendarFragmentViewModel.fetchLogs(userId, petId, System.currentTimeMillis());

        return view;
    }
}
