package com.apps.pettracker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.apps.pettracker.R;
import com.apps.pettracker.utils.Animations;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class LogActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    String userId;
    String petId;
    String categoryId;
    String logId;
    TextView petNameText;
    EditText logName;
    EditText logDescription;
    Button dateButton;
    Button saveButton;
    ImageView refreshIcon;
    ImageView petImage;
    ImageView petGenderIcon;
    ConstraintLayout containerConstraint;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log);
        Intent currentIntent = getIntent();
        userId = currentIntent.getStringExtra("userId");
        petId = currentIntent.getStringExtra("petId");
        categoryId = currentIntent.getStringExtra("categoryId");
        logId = currentIntent.getStringExtra("logId");
        logName = findViewById(R.id.log_activity_name);
        logDescription = findViewById(R.id.log_activity_description);
        dateButton = findViewById(R.id.log_activity_date);
        saveButton = findViewById(R.id.log_activity_save_button);
        petNameText = findViewById(R.id.log_activity_pet_name);
        refreshIcon = findViewById(R.id.log_activity_refresh_icon);
        containerConstraint = findViewById(R.id.log_activity_main);
        petImage = findViewById(R.id.log_activity_pet_image);
        petGenderIcon = findViewById(R.id.log_activity_pet_gender_icon);

        containerConstraint.setVisibility(View.GONE);
        refreshIcon.setVisibility(View.VISIBLE);
        fetchLog();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.log_constraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void fetchLog(){
        Animation refreshAnimation = Animations.loadingIconLoadAnimation();
        refreshIcon.startAnimation(refreshAnimation);

        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       DocumentSnapshot documentSnapshot = task.getResult();
                       petNameText.setText(documentSnapshot.getString("name"));
                       String petImageUrl = documentSnapshot.getString("imageURL");

                       if(petImageUrl != null){
                           storageReference.child(petImageUrl).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                               Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                               petImage.setPadding(0, 0, 0, 0);
                               petImage.setImageBitmap(bmp);
                           });
                       }

                       if(Objects.equals(documentSnapshot.getString("gender"), "Female")){
                           petGenderIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.venus_solid));
                       }
                   }
                });

        db.collection("users")
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
                           logName.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                       }
                   }
                });

        db.collection("users")
                .document(userId)
                .collection("pets")
                .document(petId)
                .collection("logs")
                .document(categoryId)
                .collection("logs")
                .document(logId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        logName.setText(documentSnapshot.getString("name"));
                        logDescription.setText(documentSnapshot.getString("description"));

                        long millis = documentSnapshot.getLong("date");
                        DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(millis);
                        dateButton.setText(dateFormatter.format(calendar.getTime()));
                        dateButton.setTextColor(getColor(R.color.text_color_primary));
                        refreshIcon.setVisibility(View.GONE);
                        containerConstraint.setVisibility(View.VISIBLE);
                        refreshIcon.clearAnimation();
                    }
                });
    }
}
