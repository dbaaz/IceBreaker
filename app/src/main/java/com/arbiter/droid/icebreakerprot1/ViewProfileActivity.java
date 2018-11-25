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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Iterator;

import static com.arbiter.droid.icebreakerprot1.Common.setStorageImageToImageView;

public class ViewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        final String name;
        final SharedPreferences sharedPrefs = this.getSharedPreferences("Icebreak",0);
        this.setTitle(name=getIntent().getStringExtra("name"));
        DatabaseReference fd = FirebaseDatabase.getInstance().getReference().child("users").child(name);
        final DatabaseReference fd2 = FirebaseDatabase.getInstance().getReference().child("pings");
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
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
            public void onClick(View v) {
                final int[] result={0};
                fd2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = children.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = iterator.next();
                            String from = next.child("from").getValue().toString();
                            String to = next.child("to").getValue().toString();
                            String accepted = next.child("accepted").getValue().toString();
                            if (from.equals(sharedPrefs.getString("saved_name", "")) && to.equals(name) && accepted.equals("no"))
                                result[0]=1;
                            else if(from.equals(sharedPrefs.getString("saved_name", "")) && to.equals(name) && accepted.equals("yes"))
                                result[0]=2;
                        }
                        if(result[0]==0) {
                            DatabaseReference tmp = fd2.push();
                            tmp.child("from").setValue(sharedPrefs.getString("saved_name", ""));
                            tmp.child("to").setValue(name);
                            tmp.child("accepted").setValue("no");
                        }
                        else if(result[0]==1)
                            Toast.makeText(ViewProfileActivity.this, "You've already pinged this user", Toast.LENGTH_SHORT).show();
                        else
                        {
                            Intent i = new Intent(getApplicationContext(),ChatActivity.class);
                            i.putExtra("venname",getTitle());
                            i.putExtra("groupChat","no");
                            i.putExtra("sender",sharedPrefs.getString("saved_name",""));
                            startActivity(i);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        final ImageView imgView = findViewById(R.id.picture);
        final ImageView imageView2 = findViewById(R.id.imageView2);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("prof_img").child(name);
        setStorageImageToImageView(storageReference,imageView2);

    }
}
