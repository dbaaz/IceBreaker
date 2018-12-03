package com.arbiter.droid.icebreakerprot1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.arbiter.droid.icebreakerprot1.Common.imageViewtoByteArray;
import static com.arbiter.droid.icebreakerprot1.Common.setStorageImageToImageView;
import static com.arbiter.droid.icebreakerprot1.Common.uploadImage;

public class CreateProfileActivity extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    Intent i;
    TextInputEditText til;
    SharedPreferences sharedPref;
    String prof_img_uri;
    ImageView imgview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        sharedPref = this.getSharedPreferences("Icebreak", 0);
        final EditText nametxt = findViewById(R.id.name_textedit);
        final Spinner gender = findViewById(R.id.spinner2);
        final EditText dobinput = findViewById(R.id.dobinput);
        final Spinner interested = findViewById(R.id.spinner3);
        imgview = findViewById(R.id.imageView);
        FirebaseApp.initializeApp(this);
        til = findViewById(R.id.dobinput);
        TextInputLayout nameedit = findViewById(R.id.textInputLayout);
        Button btn = findViewById(R.id.button);
        i = new Intent(this, ExtendedCreateProfileActivity.class);
        if(getIntent().hasExtra("editmode"))
        {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("/prof_img/" + sharedPref.getString("saved_name", ""));
            //Picasso.get().load(prof_img).into(imgview);
            setStorageImageToImageView(storageReference,imgview);
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            firebaseDatabase.child("users").child(sharedPref.getString("saved_user","")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    nametxt.setText(dataSnapshot.getKey().toString());
                    String genderResult = dataSnapshot.child("gender").getValue().toString();
                    dobinput.setText(dataSnapshot.child("dob").getValue().toString());
                    String interestedResult = dataSnapshot.child("interested").getValue().toString();
                    switch(genderResult)
                    {
                        case "Male":
                            gender.setSelection(0);
                            break;
                        case "Female":
                            gender.setSelection(1);
                            break;
                        case "Other":
                            gender.setSelection(2);
                            break;
                    }
                    switch(interestedResult)
                    {
                        case "Male":
                            gender.setSelection(0);
                            break;
                        case "Female":
                            gender.setSelection(1);
                            break;
                        case "Other":
                            gender.setSelection(2);
                            break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            final GoogleSignInAccount acc = getIntent().getBundleExtra("accdetailbundle").getParcelable("accdet");

            try {
                Picasso.get().load(acc.getPhotoUrl().toString()).into(imgview);

            } catch (Exception e) {
            }
            nameedit.getEditText().setText(acc.getDisplayName().toString());
        }
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        til.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child("users").child(nametxt.getText().toString()).child("gender").setValue(gender.getSelectedItem().toString());
                mDatabase.child("users").child(nametxt.getText().toString()).child("name").setValue(nametxt.getText().toString());
                mDatabase.child("users").child(nametxt.getText().toString()).child("dob").setValue(dobinput.getText().toString());
                mDatabase.child("users").child(nametxt.getText().toString()).child("interested").setValue(interested.getSelectedItem().toString());
                try {
                    uploadImage("/prof_img/" + nametxt.getText().toString(), imageViewtoByteArray(imgview));
                }catch(Exception e){}
                setUser(nametxt.getText().toString(), gender.getSelectedItem().toString(), dobinput.getText().toString(), interested.getSelectedItem().toString());
                startActivity(i);
                finish();

            }
        });
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });
    }
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        if(prof_img_uri!=null) {
            savedInstanceState.putString("prof_img", prof_img_uri);
        }
        super.onSaveInstanceState(savedInstanceState);
    }
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        String temp = savedInstanceState.getString("prof_img");
        if(temp!=null) {
            imgview.setImageURI(Uri.parse(temp));
            prof_img_uri=temp;
        }
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        til.setText(sdf.format(myCalendar.getTime()));
    }

    void setUser(String name, String gender, String dob, String interest) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("saved_name", name);
        editor.commit();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    File file = new File(getCacheDir(),"tmpCrop.jpg");
                    if(!file.exists())
                        file.createNewFile();
                    UCrop.of(selectedImage,Uri.fromFile(file))
                            .withAspectRatio(10, 10)
                            .withMaxResultSize(1024, 1024)
                            .start(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            prof_img_uri=resultUri.toString();
            imgview.setImageURI(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

}
