package com.apps.pettracker.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.apps.pettracker.R;
import com.apps.pettracker.objects.Pet;
import com.apps.pettracker.viewmodels.PetViewModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class AddNewPetActivity extends AppCompatActivity {
    ImageView picked_image_icon;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result ->{
        if(result.getResultCode() == RESULT_OK){
            Uri imageURI = result.getData().getData();
            if (imageURI != null){
                picked_image_icon.setPadding(0, 0, 0, 0);
                picked_image_icon.setClipToOutline(true);
                picked_image_icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(this)
                        .load(imageURI)
                        .override(500, 500)
                        .into(picked_image_icon);
            }
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_pet);
        ImageView close_activity_button = findViewById(R.id.go_back_button);
        Button done_button = findViewById(R.id.add_pet_done_button);
        Button date_pick_button = findViewById(R.id.date_pick_button);
        EditText name_input = findViewById(R.id.name_input);
        EditText breed_input = findViewById(R.id.breed_input);
        EditText color_input = findViewById(R.id.color_input);
        RadioGroup neutered_radio_group = findViewById(R.id.neutered_group);
        RadioGroup gender_radio_group = findViewById(R.id.gender_radio_group);
        RadioGroup indoor_radio_group = findViewById(R.id.type_radio_group);
        RadioGroup type_radio_group = findViewById(R.id.pet_type_group);
        PetViewModel petViewModel = new PetViewModel();
        picked_image_icon = findViewById(R.id.pet_profile_picture);
        picked_image_icon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.paw_solid));

        picked_image_icon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddNewPetActivity.this, Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker();
                } else {
                    ActivityCompat.requestPermissions(AddNewPetActivity.this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
                }
            }
        });

        close_activity_button.setOnClickListener(v -> {
            finish();
        });

        done_button.setOnClickListener(v -> {
            String petName = name_input.getText().toString();
            String petBreed = breed_input.getText().toString();
            String petColor = color_input.getText().toString();
            String petGender;
            String petType;
            String petDateOfBirth = date_pick_button.getText().toString();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            boolean petNeutered, petIndoor;
            int selectedGender = gender_radio_group.getCheckedRadioButtonId();
            int selectedNeutered = neutered_radio_group.getCheckedRadioButtonId();
            int selectedIndoor = indoor_radio_group.getCheckedRadioButtonId();
            int selectedType = type_radio_group.getCheckedRadioButtonId();
            petGender = handleGenderSelection(selectedGender);
            petType = handleTypeSelection(selectedType);
            byte[] imageBytes = convertImageToByteArray(picked_image_icon);

            if(petName.isEmpty()){
                Toast.makeText(AddNewPetActivity.this, "No name specified", Toast.LENGTH_SHORT).show();
                return;
            }
            if(petBreed.isEmpty()){
                Toast.makeText(AddNewPetActivity.this, "No breed specified", Toast.LENGTH_SHORT).show();
                return;
            }
            if(petColor.isEmpty()){
                Toast.makeText(AddNewPetActivity.this, "No color specified", Toast.LENGTH_SHORT).show();
                return;
            }
            if(petGender.isEmpty()){
                Toast.makeText(AddNewPetActivity.this, "No gender specified", Toast.LENGTH_SHORT).show();
                return;
            }
            if(selectedIndoor == -1){
                Toast.makeText(AddNewPetActivity.this, "Not specified if the animal is indoor", Toast.LENGTH_SHORT).show();
                return;
            }
            if(selectedNeutered == -1){
                Toast.makeText(AddNewPetActivity.this, "Not specified if the animal is neutered", Toast.LENGTH_SHORT).show();
                return;
            }
            if(petType.isEmpty()){
                Toast.makeText(AddNewPetActivity.this, "No type specified", Toast.LENGTH_SHORT).show();
                return;
            }
            if(petDateOfBirth.equals("Date Of Birth")){
                Toast.makeText(AddNewPetActivity.this, "No date of birth specified", Toast.LENGTH_SHORT).show();
                return;
            }
            petNeutered = handleNeuteredSelection(selectedNeutered);
            petIndoor = handleIndoorSelected(selectedIndoor);


            Pet newPet = new Pet(petName, petGender, petDateOfBirth, petType, petBreed, petColor, 0, petNeutered, petIndoor);
            String petProfileUrl = userId + "/" + newPet.getPetId() + "/profilePicture.jpeg";
            newPet.setImageURL(petProfileUrl);
            petViewModel.addPet(newPet);
            StorageReference storageReference = storage.getReference();
            StorageReference petProfileStorageReference = storageReference.child(petProfileUrl);
            UploadTask uploadTask = petProfileStorageReference.putBytes(imageBytes);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                setResult(RESULT_OK);
                finish();
            });

        });
        date_pick_button.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddNewPetActivity.this,
                    R.style.CustomDatePickerDialog,
                    (view, year1, month1, dayOfMonth) -> {
                        date_pick_button.setText(dayOfMonth + "." + (month1 + 1) + "." + year1);
                        date_pick_button.setTextColor(getColor(R.color.text_color_primary));
                    },
            year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_pet_constraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String handleGenderSelection(int selectedGender){
        if(selectedGender == -1){
            return "";
        }
        else if(selectedGender == R.id.male_radio_button){
            return "Male";
        }else{
            return "Female";
        }
    }

    private byte[] convertImageToByteArray(ImageView imageView){
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap;
        Drawable imageDrawable = imageView.getDrawable();
        if(imageDrawable instanceof VectorDrawable){
            bitmap = ((BitmapDrawable) AppCompatResources.getDrawable(this, R.drawable.pet_item_image_placeholder)).getBitmap();
        }else {
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private boolean handleNeuteredSelection(int selectedNeutered){
        if(selectedNeutered == R.id.neutered_radio_button){
            return true;
        }else if(selectedNeutered == R.id.not_neutered_radio_button){
            return false;
        }else{
            return false;
        }
    }

    private boolean handleIndoorSelected(int selectedIndoor){
        if(selectedIndoor == R.id.indoor_radio_button){
            return true;
        }
        else if(selectedIndoor == R.id.outdoor_radio_button){
            return false;
        }else{
            return false;
        }
    }

    private String handleTypeSelection(int selectedType){
        if(selectedType == R.id.cat_radio_button){
            return "Cat";
        }
        else if(selectedType == R.id.dog_radio_button){
            return "Dog";
        }
        return "";
    }

    //TODO: Make image picker to choose only images
    public void openImagePicker(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
