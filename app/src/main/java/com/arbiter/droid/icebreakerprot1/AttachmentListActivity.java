package com.arbiter.droid.icebreakerprot1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import static com.arbiter.droid.icebreakerprot1.Common.getPreference;
import static com.arbiter.droid.icebreakerprot1.Common.setCurrentUser;

public class AttachmentListActivity extends AppCompatActivity {
    @Override
    protected void onStart(){
        super.onStart();
        setCurrentUser(getPreference("saved_name"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_list);
    }
}
