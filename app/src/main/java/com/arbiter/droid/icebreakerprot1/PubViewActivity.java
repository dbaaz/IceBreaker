package com.arbiter.droid.icebreakerprot1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.arbiter.droid.icebreakerprot1.Common.setCurrentUser;

public class PubViewActivity extends AppCompatActivity {
    @Override
    protected void onStart(){
        super.onStart();
        setCurrentUser(getSharedPreferences("Icebreak",0).getString("saved_name",""));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String venname = getIntent().getStringExtra("venname");
        setTitle(venname);
        Button users = findViewById(R.id.button10);
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(v.getContext(),UsersViewActivity.class);
                Intent i = new Intent(v.getContext(),UsersViewRecyclerActivity.class);
                i.putExtra("mode",0);
                startActivity(i);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),ChatActivity.class);
                i.putExtra("venname",venname);
                i.putExtra("groupChat","yes");
                i.putExtra("sender",getTitle());
                startActivity(i);
            }
        });

    }
}
