package com.arbiter.droid.icebreakerprot1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.arbiter.droid.icebreakerprot1.Common.setStorageImageToImageView;

public class ViewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        String name;
        final SharedPreferences sharedPrefs = this.getSharedPreferences("Icebreak",0);
        DatabaseReference fd = FirebaseDatabase.getInstance().getReference().child("users").child(sharedPrefs.getString("saved_name",""));
        this.setTitle(name=getIntent().getStringExtra("name"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView bioTextView = findViewById(R.id.textView4);
        final TextView genTextView = findViewById(R.id.textView9);
        setSupportActionBar(toolbar);
        fd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bioTextView.setText(dataSnapshot.child("bio").getValue().toString());
                genTextView.setText(dataSnapshot.child("gender").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),ChatActivity.class);
                i.putExtra("venname",getTitle());
                i.putExtra("groupChat","no");
                i.putExtra("sender",sharedPrefs.getString("saved_name",""));
                startActivity(i);
            }
        });
        final ImageView imgView = findViewById(R.id.picture);
        final ImageView imageView2 = findViewById(R.id.imageView2);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("prof_img").child(name);
        setStorageImageToImageView(storageReference,imageView2);

    }
}
