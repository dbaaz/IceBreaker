package com.arbiter.droid.icebreakerprot1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import static com.arbiter.droid.icebreakerprot1.Common.setCurrentUser;

public class ChallengeListActivity extends AppCompatActivity {
    @Override
    protected void onStart(){
        super.onStart();
        setCurrentUser(getSharedPreferences("Icebreak",0).getString("saved_name",""));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_list);
        final ListView challengeList = findViewById(R.id.challengelist);
        challengeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra("RESULT_STRING",challengeList.getItemAtPosition(position).toString());
                setResult(Activity.RESULT_OK,data);
                finish();
            }
        });
    }
}
