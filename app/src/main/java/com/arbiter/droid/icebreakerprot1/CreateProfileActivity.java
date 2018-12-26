package com.arbiter.droid.icebreakerprot1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.arbiter.droid.icebreakerprot1.Common.compressImage;
import static com.arbiter.droid.icebreakerprot1.Common.getPreference;
import static com.arbiter.droid.icebreakerprot1.Common.setCurrentUser;
import static com.arbiter.droid.icebreakerprot1.Common.setPreference;
import static com.arbiter.droid.icebreakerprot1.Common.uploadAvatarImage;

public class CreateProfileActivity extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    Intent i;
    TextInputEditText til;
    String prof_img_uri;
    ImageView imgview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        final EditText nametxt = findViewById(R.id.name_textedit);
        final Spinner gender = findViewById(R.id.spinner2);
        final EditText dobinput = findViewById(R.id.dobinput);
        final Spinner interested = findViewById(R.id.spinner3);
        imgview = findViewById(R.id.imageView);
        FirebaseApp.initializeApp(this);
        til = findViewById(R.id.dobinput);
        final TextInputLayout nameedit = findViewById(R.id.textInputLayout);
        final Button btn = findViewById(R.id.button);
        i = new Intent(this, ExtendedCreateProfileActivity.class);
        if(getIntent().hasExtra("editmode"))
        {
            setTitle("Edit Profile");
            btn.setText("Save");
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            firebaseDatabase.child("users").child(getPreference("saved_name")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Shimmer shimmer = new Shimmer.ColorHighlightBuilder().build();
                    ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
                    shimmerDrawable.setShimmer(shimmer);
                    GlideApp.with(getBaseContext()).load(dataSnapshot.child("prof_img_url").getValue().toString()).apply(RequestOptions.circleCropTransform()).into(imgview);
                    nametxt.setText(dataSnapshot.getKey());
                    String genderResult = dataSnapshot.child("gender").getValue().toString();
                    dobinput.setText(dataSnapshot.child("dob").getValue().toString());
                    String interestedResult = dataSnapshot.child("interested").getValue().toString();
                    switch(genderResult)
                    {
                        case "Male":
                            gender.setSelection(1);
                            break;
                        case "Female":
                            gender.setSelection(2);
                            break;
                        case "Other":
                            gender.setSelection(3);
                            break;
                    }
                    switch(interestedResult)
                    {
                        case "Male":
                            interested.setSelection(1);
                            break;
                        case "Female":
                            interested.setSelection(2);
                            break;
                        case "Other":
                            interested.setSelection(3);
                            break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            //final FirebaseUser acc = getIntent().getBundleExtra("accdetailbundle").getParcelable("accdet");
            FirebaseUser acc = FirebaseAuth.getInstance().getCurrentUser();
            try {
                Shimmer shimmer = new Shimmer.ColorHighlightBuilder().build();
                ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
                shimmerDrawable.setShimmer(shimmer);
                String profileUri;
                String uid = null;
                String providerId=null;
                for (UserInfo userInfo : acc.getProviderData())
                {
                    uid = userInfo.getUid();
                    providerId = userInfo.getProviderId();
                }
                if (FacebookAuthProvider.PROVIDER_ID.equals(providerId))
                {
                    profileUri = "https://graph.facebook.com/" + uid + "/picture?height=500";
                    GraphRequest request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        String birthday = object.getString("birthday");
                                        String genderval = object.getString("gender");
                                        String[] birth_array = birthday.split("/");
                                        birthday=birth_array[1]+"/"+birth_array[0]+"/"+birth_array[2];
                                        dobinput.setText(birthday);
                                        switch (genderval)
                                        {
                                            case "male":
                                                gender.setSelection(1);
                                                break;
                                            case "female":
                                                gender.setSelection(2);
                                                break;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();

                }
                else
                    profileUri = acc.getPhotoUrl().toString();
                GlideApp.with(getBaseContext()).load(profileUri).placeholder(shimmerDrawable).into(imgview);

            } catch (Exception e) {
                e.printStackTrace();
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
            if(!nametxt.getText().toString().trim().equals("")&&!dobinput.getText().toString().trim().equals("")&&gender.getSelectedItemPosition()!=0&&interested.getSelectedItemPosition()!=0)
            {
                    mDatabase.child("users").child(nametxt.getText().toString()).child("gender").setValue(gender.getSelectedItem().toString());
                    mDatabase.child("users").child(nametxt.getText().toString()).child("name").setValue(nametxt.getText().toString());
                    mDatabase.child("users").child(nametxt.getText().toString()).child("dob").setValue(dobinput.getText().toString());
                    mDatabase.child("users").child(nametxt.getText().toString()).child("interested").setValue(interested.getSelectedItem().toString());
                    try {
                        Bitmap bitmap = ((BitmapDrawable) imgview.getDrawable()).getBitmap();
                        File tmp = new File(getCacheDir() + "temp.jpeg");
                        FileOutputStream ostream = new FileOutputStream(tmp);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.close();
                        if(!nametxt.getText().toString().equals(""))
                            setCurrentUser(nametxt.getText().toString());
                        uploadAvatarImage("/prof_img/" + nametxt.getText().toString(), compressImage(tmp, getApplicationContext(), true));
                    } catch (Exception e) {
                    }
                    setUser(nametxt.getText().toString(), gender.getSelectedItem().toString(), dobinput.getText().toString(), interested.getSelectedItem().toString());
                    startActivity(i);
                    finish();
            }
            else
                Toast.makeText(CreateProfileActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
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

        setPreference("saved_name",name);
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
