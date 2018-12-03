package com.arbiter.droid.icebreakerprot1;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Random;

public class Common {
    static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    static String getEncoded64ImageStringFromBitmap(ImageView iv1) {

        return "";
    }
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
    static void uploadImage(String dbPath, byte[] stream)
    {
        UploadTask uploadTask = storageReference.child(dbPath).putBytes(stream);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("myapp",e.getMessage());
            }
        });
    }
    static void setStorageImageToImageView(StorageReference storageReference, final ImageView imageView)
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
    }
    static String getImageURLFromStroageReference(StorageReference storageReference)
    {
        final String[] url={""};
        //final CountDownLatch latch = new CountDownLatch(1);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url[0]=uri.toString();
                //latch.countDown();
            }
        });
        try {
            //latch.await();
        }catch (Exception e){}
        return url[0];
    }
    static Date getDate(long timeStamp){

            //SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            timeStamp = Long.parseLong(timeStamp+"") * 1000L;
            Date netDate = (new Date(timeStamp));
            return netDate;
    }


}
