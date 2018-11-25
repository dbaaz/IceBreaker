package com.arbiter.droid.icebreakerprot1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.arbiter.droid.icebreakerprot1.Common.getEncoded64ImageStringFromBitmap;
import static com.arbiter.droid.icebreakerprot1.Common.imageViewtoByteArray;
import static com.arbiter.droid.icebreakerprot1.Common.uploadImage;

public class CreateProfileActivity extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    Intent i;
    TextInputEditText til;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        sharedPref = this.getSharedPreferences("Icebreak", 0);
        til = findViewById(R.id.dobinput);
        final GoogleSignInAccount acc = getIntent().getBundleExtra("accdetailbundle").getParcelable("accdet");
        TextInputLayout nameedit = findViewById(R.id.textInputLayout);
        final ImageView imgview = findViewById(R.id.imageView);
        Spinner genderspin = findViewById(R.id.spinner2);
        //Toast.makeText(this, acc.getPhotoUrl().toString(), Toast.LENGTH_SHORT).show();
        try {
            Picasso.get().load(acc.getPhotoUrl().toString()).into(imgview);

        } catch (Exception e) {
        }
        nameedit.getEditText().setText(acc.getDisplayName().toString());
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
        i = new Intent(this, ExtendedCreateProfileActivity.class);
        til.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Button btn = findViewById(R.id.button);
        FirebaseApp.initializeApp(this);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nametxt = findViewById(R.id.name_textedit);
                Spinner gender = findViewById(R.id.spinner2);
                EditText dobinput = findViewById(R.id.dobinput);
                Spinner interested = findViewById(R.id.spinner3);
                //users user = new users(gender.getSelectedItem().toString(),dobinput.getText().toString(),interested.getSelectedItem().toString(),"");
                mDatabase.child("users").child(nametxt.getText().toString()).child("gender").setValue(gender.getSelectedItem().toString());
                mDatabase.child("users").child(nametxt.getText().toString()).child("name").setValue(nametxt.getText().toString());
                mDatabase.child("users").child(nametxt.getText().toString()).child("dob").setValue(dobinput.getText().toString());
                mDatabase.child("users").child(nametxt.getText().toString()).child("interested").setValue(interested.getSelectedItem().toString());
                /*try {
                    mDatabase.child("users").child(nametxt.getText().toString()).child("prof_img").setValue(getEncoded64ImageStringFromBitmap(imgview));
                }catch(Exception e){}*/
                try {
                    uploadImage("/prof_img/" + nametxt.getText().toString(), imageViewtoByteArray(imgview));
                }catch(Exception e){}
                newUser(nametxt.getText().toString(), gender.getSelectedItem().toString(), dobinput.getText().toString(), interested.getSelectedItem().toString());
                startActivity(i);
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        til.setText(sdf.format(myCalendar.getTime()));
    }

    void newUser(String name, String gender, String dob, String interest) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("saved_name", name);
        editor.putString("saved_dob", dob);
        editor.putString("saved_gender", gender);
        editor.putString("saved_interest", interest);
        editor.commit();
    }


}
