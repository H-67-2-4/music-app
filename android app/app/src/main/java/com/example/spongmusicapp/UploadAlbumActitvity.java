package com.example.spongmusicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.spongmusicapp.Model.Constants;
import com.example.spongmusicapp.Model.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadAlbumActitvity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonChoose;
    private Button buttonUpload;
    private EditText edittextName;
    private ImageView imageview;
    String songsCategory;
    private static final int PICk_IMAGE_REQUESt = 234;

    private Uri fileFath;
    StorageReference storageReference;
    DatabaseReference nDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_album_actitvity);

        buttonChoose = findViewById(R.id.buttonChoose);
        buttonUpload = findViewById(R.id.buttonUpload);
        edittextName = findViewById(R.id.edit_text);
        imageview = findViewById(R.id.imageview);

        storageReference = FirebaseStorage.getInstance().getReference();
        nDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        Spinner spinner = findViewById(R.id.spinner);



        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);



        List<String> categories = new ArrayList<>();

        categories.add("TRENDING");
        categories.add("BOLLYWOOD");
        categories.add("INTERNATIONAL");
        categories.add("REGIONAL");
        categories.add("REMIX");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>( this , android.R.layout.simple_spinner_item, categories );

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {

                songsCategory = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(UploadAlbumActitvity.this, "Selected:" + songsCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public void onClick(View view) {

        if(view == buttonChoose) {
            showFileChoose();

        }
        else if(view == buttonUpload){
            uploadFile();
        }
    }

    private void uploadFile() {
        if(fileFath != null){
            final ProgressDialog progressDialog = new ProgressDialog( this);
            progressDialog.setTitle("uploading..");
            progressDialog.show();
            final StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS +System.currentTimeMillis() + "." + getFileExtension(fileFath));

        sRef.putFile(fileFath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                       String url = uri.toString();
                        Upload upload = new Upload(edittextName.getText().toString().trim(),
                                url, songsCategory);
                        String uploadId = nDatabase.push().getKey();
                        nDatabase.child(uploadId).setValue(upload);
                        progressDialog.dismiss();
                        Toast.makeText( UploadAlbumActitvity.this, "File Uploaded" , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();

                Toast.makeText( UploadAlbumActitvity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("uploaded " + ((int)progress)+"%......");
            }
        });

        }

    }

    private void showFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICk_IMAGE_REQUESt);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICk_IMAGE_REQUESt && resultCode == RESULT_OK && data != null && data.getData() != null){
            fileFath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fileFath);
                imageview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getFileExtension (Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getMimeTypeFromExtension(cr.getType(uri));
    }

}


