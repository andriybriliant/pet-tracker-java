package com.apps.pettracker.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.apps.pettracker.R;
import com.apps.pettracker.objects.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddNewLogActivity extends AppCompatActivity {
    String petId;
    String categoryId;
    String userId;
    String categoryName;
    Button datePickButton;
    Button doneButton;
    EditText nameTextInput;
    EditText descriptionTextInput;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_log);
        Intent intent = getIntent();
        petId = intent.getStringExtra("petId");
        categoryId = intent.getStringExtra("categoryId");
        userId = intent.getStringExtra("userId");
        datePickButton = findViewById(R.id.new_log_date_button);
        nameTextInput = findViewById(R.id.new_log_name_input);
        descriptionTextInput = findViewById(R.id.add_log_description_input);
        doneButton = findViewById(R.id.new_log_done_button);
        categoryName = intent.getStringExtra("categoryName");

        if(categoryName.equals("Weight")){
            nameTextInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            nameTextInput.setHint("5.5");
        }

        datePickButton.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddNewLogActivity.this,
                    R.style.CustomDatePickerDialog,
                    (view, year1, month1, dayOfMonth) -> {
                        datePickButton.setText(year1 + "/" + dayOfMonth + "/" + (month1 + 1));
                        datePickButton.setTextColor(getColor(R.color.text_color_primary));
                    },
                    year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        doneButton.setOnClickListener(v ->{
            String nameText = nameTextInput.getText().toString();
            String descriptionText = descriptionTextInput.getText().toString();
            String dateText = datePickButton.getText().toString();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/dd/MM");
            Date date;
            long milliseconds;
            try {
                date = simpleDateFormat.parse(dateText);
                milliseconds = date.getTime();
                Log log = new Log(nameText, descriptionText, milliseconds);
                log.setPetId(petId);
                log.setCategoryId(categoryId);

                if(categoryName.equals("Weight")){
                    db.collection("users")
                            .document(userId)
                            .collection("pets")
                            .document(petId)
                            .collection("logs")
                            .whereEqualTo("name", "Weight")
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    QuerySnapshot querySnapshot = task1.getResult();
                                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                    db.collection("users")
                                            .document(userId)
                                            .collection("pets")
                                            .document(petId)
                                            .collection("logs")
                                            .document(documentSnapshot.getId())
                                            .collection("logs")
                                            .orderBy("date", Query.Direction.DESCENDING)
                                            .limit(1)
                                            .get()
                                            .addOnCompleteListener(task2 -> {
                                                if(task2.isSuccessful()){
                                                    QuerySnapshot querySnapshot1 = task2.getResult();
                                                    DocumentSnapshot documentSnapshot1 = querySnapshot1.getDocuments().get(0);
                                                    db.collection("users")
                                                            .document(userId)
                                                            .collection("pets")
                                                            .document(petId).update("weight", (Double.parseDouble(documentSnapshot1.getString("name"))));
                                                }
                                            });
                                }
                            });
                }

                db.collection("users")
                        .document(userId)
                        .collection("pets")
                        .document(petId)
                        .collection("logs")
                        .document(categoryId)
                        .collection("logs")
                        .document(log.getId())
                        .set(log)
                        .addOnSuccessListener(aVoid -> {
                            setResult(RESULT_OK);
                            finish();
                        });
            } catch (ParseException | NullPointerException e) {
                android.util.Log.d("Error", e.getMessage());
            }


        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_log_constraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
