package com.viansmul.uplodimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGEREQUEST = 1;

    private Button chooceImage;
    private Button mUplod;
    private EditText mfilename;
    private ImageView mimageView;
    private TextView mtoProfile;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    FirebaseDatabase database;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtoProfile = findViewById(R.id.toProfile);
        chooceImage = findViewById(R.id.btnbrowse);
        mUplod = findViewById(R.id.btnuplod);
        mfilename = findViewById(R.id.filename);
        mimageView = findViewById(R.id.imageview);
        database    = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = database.getReference("uploadss");

        chooceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openFileChooser();
            }
        });

        mUplod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadFile();

            }
        });

        mtoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagesActivity();
            }
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGEREQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGEREQUEST && resultCode ==RESULT_OK && data != null && data.getData() !=null){
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(mimageView);
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton().getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if (mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                +"."+ getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                }
                            }, 500);

                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            UploadEuy upload = new UploadEuy(mfilename.getText().toString().trim(),
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            DatabaseReference uploads = mDatabaseRef.push();
                            String namaFile= mfilename.getText().toString();
                            String image=  mimageView.getImageMatrix().toString();

                            uploads.child(uploadId).child("mfilename").setValue(namaFile);
                            uploads.child(uploadId).child("mimageView").setValue(image);
                            uploads.child(uploadId).child("URL_Image").setValue(upload);

                            //mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });



        }else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
            Toast.makeText(getApplicationContext(),"Foto Berhasil di Upload", Toast.LENGTH_SHORT).show();
    }

    private void openImagesActivity() {
        Intent intent = new Intent(this, RecyclceImage.class);
        startActivity(intent);
    }
}
