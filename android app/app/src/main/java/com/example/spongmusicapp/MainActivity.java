package com.example.spongmusicapp;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spongmusicapp.Model.UploadSong;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView textViewImage;
    ProgressBar progressBar;
    Uri audioUri ;
    StorageReference nStorageref;
    StorageTask nUploadsTask;
    DatabaseReference referenceSongs;
    String songCategory;
    MediaMetadataRetriever metadataRetriever;
    byte [] art;
    String title1, artist1, album_art1 = "", duration1;
    TextView title,artist,album,duration,genre;
    ImageView album_art;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewImage = findViewById(R.id.textViewSongsFilesSelected);
        progressBar = findViewById(R.id.progressbar);
        title = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        duration = findViewById(R.id.duration);
        album = findViewById(R.id.album);
        genre = findViewById(R.id.genre);
        album_art = findViewById(R.id.imageview);

        metadataRetriever = new MediaMetadataRetriever();
        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        nStorageref = FirebaseStorage.getInstance().getReference().child("songs");

        Spinner spinner = findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);

        List <String> categories = new ArrayList<>();

        categories.add("TRENDING");
        categories.add("BOLLYWOOD");
        categories.add("INTERNATIONAL");
        categories.add("REGIONAL");
        categories.add("REMIX");

        ArrayAdapter <String> dataAdapter = new ArrayAdapter<>( this , android.R.layout.simple_spinner_item, categories );

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view,int i, long l )  {

        songCategory = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "Selected:" +songCategory,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView){

    }

    public void openAudioFiles (View v){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

     if(requestCode == 101 && resultCode == RESULT_OK  && data.getData() !=null){

         audioUri = data.getData();
         String fileNames = getFileName(audioUri);
         textViewImage.setText(fileNames);
         metadataRetriever.setDataSource( this, audioUri);

         art = metadataRetriever.getEmbeddedPicture();
         Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0,art.length);
         album_art.setImageBitmap(bitmap);
         album.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
         artist.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
         genre.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
         duration.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
         title.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

         artist1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
         title1  = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
         duration1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
     }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {

                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }

        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf( '/');
            if (cut != -1) {
                result = result.substring(cut +1);
            }
        }
        return  result;
    }

    public void uploadFileTofirebase (View v){
        if(textViewImage.equals("No file Selected")){
            Toast.makeText( this, "Please select an image", Toast.LENGTH_SHORT).show();

        }
        else{
            if(nUploadsTask !=null && nUploadsTask.isInProgress()){
                Toast.makeText(this, "Uploading in progress!", Toast.LENGTH_SHORT).show();
            }else{
                uploadFiles();
            }
        }
    }

    private void uploadFiles() {

        if(audioUri != null){
            Toast.makeText(this, "Uploading please wait!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference = nStorageref.child(System.currentTimeMillis()+"."+getfileextension(audioUri));
            nUploadsTask = storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            UploadSong uploadSong = new UploadSong(songCategory,title1,artist1,album_art1,duration1,uri.toString());
                            String uploadId = referenceSongs.push().getKey();
                            referenceSongs.child(uploadId).setValue(uploadSong);


                        }
                    });
                }
            }).addOnProgressListener (new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void  onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        }else{
            Toast.makeText( this, "No file selected for upload",Toast.LENGTH_SHORT).show();
        }
    }

    private String getfileextension(Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }

    public void  openAlbumUploadsActivity(View v){
        Intent in = new Intent( MainActivity.this, UploadAlbumActitvity.class);
        startActivity(in);
    }
}
