package com.arbiter.droid.icebreakerprot1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import androidx.annotation.NonNull;
import id.zelory.compressor.Compressor;

public class Common {
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static String current_user="";
    private static SharedPreferences sharedPreferences;
    static int image_viewer_mode=0;
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxcvbnm";
    public static Random RANDOM = new Random();
    public static void setDefaultPreferences(SharedPreferences sharedPreferences) {
        Common.sharedPreferences = sharedPreferences;
    }
    public static void setPreference(String preference,String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(preference,value);
        edit.commit();
        if(!sharedPreferences.getString(preference,"").equals(value)||sharedPreferences.getString(preference,"").equals(""))
            setPreference(preference,value);
    }
    public static String getPreference(String preference){
        return sharedPreferences.getString(preference,"");
    }
    public static String getCurrentUser()
    {
        return current_user;
    }
    public static void setCurrentUser(String current_user)
    {
        Common.current_user=current_user;
    }
    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }
    public static DatabaseReference getDatabaseReference()
    {
        return databaseReference;
    }
    public static StorageReference getStorageReference() { return storageReference; }
    static byte[] imageViewtoByteArray(ImageView iv1)
    {
        BitmapDrawable drawable = (BitmapDrawable) iv1.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        return data;
    }
    static void uploadAvatarImage(final String dbPath, File file)
    {
        final UploadTask uploadTask = storageReference.child(dbPath).putFile(Uri.fromFile(file));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                final DatabaseReference tmp = databaseReference.child("users").child(getCurrentUser()).child("prof_img_url");
                storageReference.child(dbPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        tmp.setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("myapp",e.getMessage());
            }
        });
    }
    static void uploadImageFile(final String dbPath, final File file)
    {
        final UploadTask uploadTask = storageReference.child(dbPath).putFile(Uri.fromFile(file));
        EventBus.getDefault().post(new ShowProgressBarEvent());
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            final DatabaseReference tmp = databaseReference.child("users").child(getCurrentUser()).child("image_url").push();
            storageReference.child(dbPath).getDownloadUrl().addOnSuccessListener(uri -> {
                EventBus.getDefault().post(new HideProgressBarEvent());
                tmp.child("url").setValue(uri.toString());
                tmp.child("filename").setValue(dbPath.substring(dbPath.lastIndexOf('/')+1,dbPath.length()));
            });
        }).addOnFailureListener(e -> Log.v("myapp",e.getMessage()));
    }
    static void uploadImageUrl(final String url, final Context context)
    {
        final boolean[] flag = {true};
        databaseReference.child("users").child(getCurrentUser()).child("image_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();
                while(iterator.hasNext())
                {
                    DataSnapshot next = iterator.next();
                    if(url.equals(next.child("url").getValue().toString()))
                    {
                        flag[0] =false;
                        break;
                    }
                }
                if(flag[0])
                {
                    final DatabaseReference tmp = databaseReference.child("users").child(getCurrentUser()).child("image_url").push();
                    tmp.child("url").setValue(url);
                    tmp.child("filename").setValue("facebook");
                    Toast.makeText(context, "Import Successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    static void uploadImage(final String dbPath, byte[] stream)
    {
        final UploadTask uploadTask = storageReference.child(dbPath).putBytes(stream);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                final DatabaseReference tmp = databaseReference.child("users").child(getCurrentUser()).child("image_url").push();
                storageReference.child(dbPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        tmp.child("url").setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("myapp",e.getMessage());
            }
        });
    }
    /*static void setStorageImageToImageView(StorageReference storageReference, final ImageView imageView)
    {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("myapp",e.getMessage());
            }
        });
    }*/
    static Date getDate(long timeStamp){

            //SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            timeStamp = Long.parseLong(timeStamp+"") * 1000L;
            Date netDate = (new Date(timeStamp));
            return netDate;
    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    public static File compressImage(File file, Context context, boolean avatar) throws IOException
    {
        int height=1080,width=1920,quality=50;
        if(avatar) {
            height = 300;
            width = 300;
            quality = 100;
        }
        return new Compressor(context)
                .setMaxHeight(height)
                .setMaxWidth(width)
                .setQuality(quality)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFile(file);

    }
}
