package com.arbiter.droid.icebreakerprot1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.arbiter.droid.icebreakerprot1.Common.compressImage;
import static com.arbiter.droid.icebreakerprot1.Common.getCurrentUser;
import static com.arbiter.droid.icebreakerprot1.Common.image_viewer_mode;
import static com.arbiter.droid.icebreakerprot1.Common.randomString;
import static com.arbiter.droid.icebreakerprot1.Common.setCurrentUser;
import static com.arbiter.droid.icebreakerprot1.Common.uploadImageFile;

public class ImageListActivity extends AppCompatActivity {

    FragmentInterface fragmentInterface;
    @BindView(R.id.progressBar) ProgressBar uploadProgressBar;
    int uploadCounter=0;
    @Override
    protected void onStart(){
        super.onStart();
        setCurrentUser(getSharedPreferences("Icebreak",0).getString("saved_name",""));
    }
    @Subscribe
    public void showProgressBar(ShowProgressBarEvent event){
        uploadCounter++;
        uploadProgressBar.setVisibility(View.VISIBLE);
    }
    @Subscribe
    public void hideProgressBar(HideProgressBarEvent event){
        uploadCounter--;
        if(uploadCounter==0)
            uploadProgressBar.setVisibility(View.GONE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list_activity);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        SharedPreferences sharedPreferences = getSharedPreferences("Icebreak",0);
        String name = sharedPreferences.getString("saved_name","");
        Bundle temp = new Bundle();
        if(getIntent().getExtras().containsKey("album_id")) {
            temp.putString("album_id",getIntent().getStringExtra("album_id"));
            image_viewer_mode=1;
            fragmentInterface.onFragmentInteract(temp);
        }
        else {
            temp.putString("target_user", name);
            image_viewer_mode=2;
            fragmentInterface.onFragmentInteract(temp);
        }
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
                    uploadImageFile("/usr_img/"+getCurrentUser()+"/"+randomString(15),file);
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