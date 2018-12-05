package com.arbiter.droid.icebreakerprot1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import androidx.annotation.NonNull;
import id.zelory.compressor.Compressor;

public class Common {
    static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    static String current_user="";
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxcvbnm";
    public static Random RANDOM = new Random();

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }
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
                final DatabaseReference tmp = databaseReference.child("users").child(current_user).child("prof_img_url");
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
    static void uploadImageFile(final String dbPath, File file)
    {
        final UploadTask uploadTask = storageReference.child(dbPath).putFile(Uri.fromFile(file));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                final DatabaseReference tmp = databaseReference.child("users").child(current_user).child("image_url").push();
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
    static void uploadImage(final String dbPath, byte[] stream)
    {
        final UploadTask uploadTask = storageReference.child(dbPath).putBytes(stream);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                final DatabaseReference tmp = databaseReference.child("users").child(current_user).child("image_url").push();
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
        int height=720,width=1280;
        if(avatar) {
            height = 300;
            width = 300;
        }
        return new Compressor(context)
                .setMaxHeight(height)
                .setMaxWidth(width)
                .setQuality(25)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFile(file);
    }
}
