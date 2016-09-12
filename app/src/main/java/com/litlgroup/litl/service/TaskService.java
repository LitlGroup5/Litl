package com.litlgroup.litl.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.litlgroup.litl.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Hari on 9/11/2016.
 */
public class TaskService extends IntentService {
    // Must create a default constructor
    public TaskService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
    }

    StorageReference storageReference;
    ArrayList<String> mediaUrls;

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference =
                storage.
                        getReferenceFromUrl(getString(R.string.storage_reference_url))
                        .child(getString(R.string.storage_reference_tasks_child));
        mediaUrls = new ArrayList<>();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
        Uri fileUri = (Uri)intent.getExtras().get("uri");
        startFileUpload(fileUri, true);
    }

    private void startFileUpload(final Uri fileUri, boolean isImage) {
        try {

            String filename = getFileName(fileUri);
            if (filename == null || filename.isEmpty()) {
                return;
            }
            StorageReference imagesStorageReference = storageReference.child("images");
            StorageReference videosStorageReference = storageReference.child("videos");
            StorageReference fileStorageReference;
            if (isImage) {
                fileStorageReference = imagesStorageReference.child(filename);
            } else {
                fileStorageReference = videosStorageReference.child(filename);
            }

            InputStream stream = new FileInputStream(new File(fileUri.getPath()));

            UploadTask uploadTask = fileStorageReference.putStream(stream);

//            btnPostTask.setEnabled(false);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
//                    TastyToast.makeText(CreateTaskActivity.this, "File upload failed", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null) {
                        mediaUrls.add(downloadUrl.toString());
                    }

//                    btnPostTask.setEnabled(true);

                }
            });

        } catch (Exception ex) {
            Timber.e("Error uploading file", ex);
            ex.printStackTrace();
        }
    }

    private String getFileName(Uri fileUri) {
        try {

            return (new File("" + fileUri)).getName();
        } catch (Exception ex) {
            Timber.e("Error getting file name from uri", ex);
        }
        return "";
    }



}