package com.litlgroup.litl.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.litlgroup.litl.Manifest;
import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.DatePickerFragment;
import com.litlgroup.litl.model.Task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTaskActivity
        extends AppCompatActivity
    implements DatePickerFragment.DatePickerDialogListener

{

    @BindView(R.id.etTitle)
    EditText etTitle;

    @BindView(R.id.etDescription)
    EditText etDescription;

    @BindView(R.id.tvDueDate)
    TextView tvDueDate;

    @BindView(R.id.spCategory)
    Spinner spCategory;

    @BindView(R.id.btnPostTask)
    Button btnPostTask;

    @BindView(R.id.etAddress)
    EditText etAddress;

    @BindView(R.id.etPrice)
    EditText etPrice;

    @BindView(R.id.ivTaskImage)
    ImageView ivTaskImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        ButterKnife.bind(this);

        Glide.with(this)
                .load(R.drawable.ikea_assembly)
                .fitCenter()
                .centerCrop()
                .into(ivTaskImage);

    }


    @OnClick(R.id.btnPostTask)
    public void postTask()
    {
        try
        {

            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            String date = tvDueDate.getText().toString();
            String address = etAddress.getText().toString();
            String price = etPrice.getText().toString();
            String category = spCategory.getSelectedItem().toString();

            writeNewTask(title, description, price, date, category);

            Toast.makeText(this, "The task has been posted!", Toast.LENGTH_SHORT).show();
            finish();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void writeNewTask(String title, String description, String price,
                              String date, String category)
    {
        try {
            DatabaseReference mDatabase =
                    FirebaseDatabase.getInstance().getReference();
            String key = mDatabase.child("Tasks").push().getKey();

            Task task = new Task(title, description, price, date, category);
            Map<String, Object> taskValues = task.toMap();

            Map<String, Object> childUpdates = new HashMap<>();

            //update the offers node
            childUpdates.put("/Tasks/" + key, taskValues);

            mDatabase.updateChildren(childUpdates);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private String dueDate;

    @OnClick(R.id.tvDueDate)
    public void launchDatePicker()
    {
        try
        {
            setDatePickerListener();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    private void setDatePickerListener()
    {

        if(dueDate == null)
            dueDate = tvDueDate.getText().toString();
        if(dueDate.equals("") | !dueDate.contains("/"))
            return;
        String[] splitDate = dueDate.split("/");
        int month = Integer.parseInt(splitDate[0]);
        int day = Integer.parseInt(splitDate[1]);
        int year = Integer.parseInt(splitDate[2]);

        DialogFragment datePickerFragment = DatePickerFragment.newInstance(day, month - 1, year);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onFinishDatePickDialog(int year, int monthOfYear, int dayOfMonth) {
        try
        {
            String date = String.format(Locale.US, "%02d/%02d/%d", monthOfYear, dayOfMonth, year);
            tvDueDate.setText(date);
            dueDate = date;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    @OnClick(R.id.ivTaskImage)
    public void startCameraCapture()
    {
        try
        {
            if(!checkPermissionForCamera())
            {
                requestPermissionForCamera();
            }
            else
            {
                if(!checkPermissionForExternalStorage())
                {
                    requestPermissionForExternalStorage();
                }

                launchCamera();

            }


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static String APP_TAG = "Litl";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private Uri fileUri;

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name


        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {

                    // by this point we have the camera photo on disk
                    Bitmap takenImage = BitmapFactory.decodeFile(fileUri.getPath());
                    // RESIZE BITMAP, see section below
                    // Load the taken image into a preview
                    Glide.with(this)
                            .load(fileUri)
                            .fitCenter()
                            .centerCrop()
                            .into(ivTaskImage);

                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the image capture
                } else { // Result was a failure
                    Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), APP_TAG);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    public boolean checkPermissionForCamera(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }

    } public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    public void requestPermissionForCamera(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
            Toast.makeText(this, "Camera permission is required, please allow in App Settings", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }
    }


    public boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public void requestPermissionForExternalStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(this, "External Storage permission is required. Please allow in App Settings", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }


}
