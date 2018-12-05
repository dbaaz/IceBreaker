package com.arbiter.droid.icebreakerprot1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

import static com.arbiter.droid.icebreakerprot1.Common.compressImage;
import static com.arbiter.droid.icebreakerprot1.Common.current_user;
import static com.arbiter.droid.icebreakerprot1.Common.randomString;
import static com.arbiter.droid.icebreakerprot1.Common.uploadImageFile;

public class ImageListActivity extends AppCompatActivity {

    FragmentInterface fragmentInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list_activity);
        SharedPreferences sharedPreferences = getSharedPreferences("Icebreak",0);
        String name = sharedPreferences.getString("saved_name","");
        Bundle temp = new Bundle();
        temp.putString("target_user",name);
        fragmentInterface.onFragmentInteract(temp);
        FloatingActionButton uploadFab = findViewById(R.id.floatingActionButton2);
        uploadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });
    }
    public interface FragmentInterface
    {
        void onFragmentInteract(Bundle bundle);
    }
    public void setOnFragmentInteract(FragmentInterface fragmentInterface)
    {
        this.fragmentInterface = fragmentInterface;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String realPath = new RealPathUtil().getRealPath(this, data.getData());
                try {
                    File file = compressImage(new File(realPath),getApplicationContext(),false);
                    uploadImageFile("/usr_img/"+current_user+"/"+randomString(15),file);
                    Bundle tmp = new Bundle();
                    tmp.putString("update_path",realPath);
                    fragmentInterface.onFragmentInteract(tmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}