package com.litlgroup.litl.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.DatePickerFragment;
import com.litlgroup.litl.model.Task;
import com.litlgroup.litl.utils.CameraUtils;
import com.litlgroup.litl.utils.Permissions;

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

    Permissions permissions;

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


        permissions = new Permissions(this);
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
            if(!permissions.checkPermissionForCamera())
            {
                permissions.requestPermissionForCamera();
            }
            else
            {
                if(!permissions.checkPermissionForExternalStorage())
                {
                    permissions.requestPermissionForExternalStorage();
                }

                launchCamera();

            }


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private Uri fileUri;

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = CameraUtils.getOutputMediaFileUri(CameraUtils.MEDIA_TYPE_IMAGE);
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
     * store the file url as it could be null after returning from camera
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


}
