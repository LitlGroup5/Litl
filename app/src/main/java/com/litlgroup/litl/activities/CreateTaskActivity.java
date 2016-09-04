package com.litlgroup.litl.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.AddressFragment;
import com.litlgroup.litl.fragments.DatePickerFragment;
import com.litlgroup.litl.fragments.TimePickerFragment;
import com.litlgroup.litl.models.Address;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.models.UserSummary;
import com.litlgroup.litl.utils.AdvancedMediaPagerAdapter;
import com.litlgroup.litl.utils.CameraUtils;
import com.litlgroup.litl.utils.CircleIndicator;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.DateUtils;
import com.litlgroup.litl.utils.ImageUtils;
import com.litlgroup.litl.utils.Permissions;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import timber.log.Timber;

public class CreateTaskActivity
        extends AppCompatActivity
        implements DatePickerFragment.DatePickerDialogListener,
        TimePickerFragment.TimePickerDialogListener,
        AdvancedMediaPagerAdapter.StartImageCaptureListener,
        AdvancedMediaPagerAdapter.StartVideoCaptureListener,
        AdvancedMediaPagerAdapter.StartImageSelectListener,
        AdvancedMediaPagerAdapter.StartOnItemViewClickListener,
        AddressFragment.AddressFragmentListener

{

    @BindView(R.id.etTitle)
    EditText etTitle;

    @BindView(R.id.etDescription)
    EditText etDescription;

    @BindView(R.id.tvDueDate)
    TextView tvDueDate;

    @BindView(R.id.tvDueTime)
    TextView tvDueTime;

    @BindView(R.id.spCategory)
    Spinner spCategory;

    @BindView(R.id.btnPostTask)
    Button btnPostTask;

    @BindView(R.id.tvAddress)
    TextView tvAddress;

    @BindView(R.id.etPrice)
    EditText etPrice;

    @BindView(R.id.vpMedia)
    ViewPager mVpMedia;

    @BindView(R.id.vpIndicator)
    LinearLayout mViewPagerCountDots;

    @BindView(R.id.ivProfileImage)
    CircleImageView ivProfileImage;

    @BindView(R.id.ivDataBackground)
    ImageView ivDataBackground;

    CircleIndicator circleIndicator;

    AdvancedMediaPagerAdapter mediaPagerAdapter;

    Permissions permissions;

    Address address;
    DatabaseReference mDatabase;
    StorageReference storageReference;

    ArrayList<String> mediaUrls;

    Task existingTask;
    String existingTaskId;

    Boolean isEditMode = false;

    ArrayList<String> fileLocalUris;


    public enum TaskDataValidationMode { TASK_DEFAULT_MODE}

    public TaskDataValidationMode  taskDataValidationMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        ButterKnife.bind(this);

        permissions = new Permissions(this);
        address = new Address();
        setupViewPager();

        mDatabase =
                FirebaseDatabase.getInstance().getReference();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference =
                storage.
                        getReferenceFromUrl(getString(R.string.storage_reference_url))
                        .child(getString(R.string.storage_reference_tasks_child));
        mediaUrls = new ArrayList<>();
        fileLocalUris = new ArrayList<>();
        checkForExistingTaskData();

        tvDueDate.setText(getDefaultDeadlineDate());

        taskDataValidationMode = TaskDataValidationMode.TASK_DEFAULT_MODE;

        try {
            String profileImageUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.offer_profile_image)
                    .into(ivProfileImage);
        }
        catch (Exception ex)
        {
            Timber.e("Error onCreate New Task");
        }

    }

    private void checkForExistingTaskData() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                Timber.d("Checking for task data in intent");
                if (intent.getStringExtra(Constants.TASK_ID) != null) {
                    String taskId = intent.getStringExtra(Constants.TASK_ID);
                    existingTaskId = taskId;
                    isEditMode = true;
                    btnPostTask.setText("Save");
                    fetchExistingTaskData(taskId);
                } else {
                    Timber.d("No Task object found in intent data");
                }
            }

        } catch (Exception ex) {
            Timber.e("Checking for existing task data failed");
        }
    }

    private void fetchExistingTaskData(String taskId) {
        try {
            if (taskId == null || taskId.isEmpty())
                return;
            mDatabase.child(getString(R.string.firebase_tasks_table))
                    .child(taskId)
                    .addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    existingTask = dataSnapshot.getValue(Task.class);
                                    populateExistingTaskData(existingTask);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    Toast.makeText(CreateTaskActivity.this, "There was an error when fetching data, please try again later", Toast.LENGTH_SHORT).show();
                                    Timber.e("Error fetching existing task data from firebase");
                                }
                            }
                    );
        } catch (Exception ex) {
            Timber.e("Error fetching existing task data");
        }
    }

    private void populateExistingTaskData(Task task) {
        try {
            if (task == null)
                return;

            etTitle.setText(task.getTitle());

            etDescription.setText(task.getDescription());

            String timeStampMillis = task.getDeadlineDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
            Date currentTimeZone = DateUtils.getDateTime(timeStampMillis);
            String dueDate = (sdf.format(currentTimeZone).split(" "))[0];
            String dueTime = (sdf.format(currentTimeZone).split(" "))[1];
            tvDueDate.setText(dueDate);
            tvDueTime.setText(dueTime);

            Address address = task.getAddress();
            tvAddress.setText(Address.getDisplayString(address));
            try {
                ImageUtils.setBlurredMapBackground(address, ivDataBackground);
            }
            catch (Exception ex)
            {
                Timber.e("Error setting map background");
            }

            etPrice.setText(task.getPrice());
            String category = task.getCategories().get(0);

            List<String> cat = Arrays.asList(getResources().getStringArray(R.array.categories_array_values));
            int position = cat.indexOf(category);
            spCategory.setSelection(position);

            mediaUrls = (ArrayList) existingTask.getMedia();

            for (int i =0 ; i  < mediaUrls.size(); i++) {
                mediaPagerAdapter.insertUri(Uri.parse(mediaUrls.get(i)), i);
                mediaPagerAdapter.notifyDataSetChanged();
                circleIndicator.refreshIndicator();
            }

            String profileImageUrl = task.getUser().getPhoto();
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.offer_profile_image)
                    .into(ivProfileImage);

        } catch (Exception ex) {
            Timber.e("Error populating task data");
        }
    }

    @OnClick(R.id.btnPostTask)
    public void postTask() {
        try {

            boolean isTaskDataValid = validateTaskData();

            if(!isTaskDataValid)
                return;
            Task task;

            if (isEditMode) {
                task = getEditedTask();
            } else {
                task = getTask();
            }
            if (task != null) {
                if (isEditMode) {
                    saveTask(task);
                } else {
                    writeNewTask(task);
                }
                Toast.makeText(this, "The task has been posted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "The task could not be posted, please try again", Toast.LENGTH_SHORT).show();
            }

            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
            Timber.e("", ex);
        }
    }

    private Task getTask() {
        try {
            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            String date = tvDueDate.getText().toString();
            String time = tvDueTime.getText().toString();
            String timestampMillis = Task.getTimestampMillis(date, time);
            Address address = this.address;
            String price = etPrice.getText().toString();
            String category = spCategory.getSelectedItem().toString();
            List<String> categories = new ArrayList<>();
            categories.add(category);
            List<String> mediaUrls = this.mediaUrls;

            String status = "IN_BID_PROCESS";

            Task task =
                    new Task(
                            address,
                            categories,
                            timestampMillis,
                            description,
                            mediaUrls,
                            price,
                            title,
                            status
                    );

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                UserSummary userSummary = new UserSummary();
                if (user.getEmail() != null && !user.getEmail().isEmpty())
                    userSummary.setEmail(user.getEmail());
                userSummary.setId(user.getUid());
                userSummary.setName(user.getDisplayName());
                if (user.getPhotoUrl() != null)
                    userSummary.setPhoto(user.getPhotoUrl().toString());
                task.setUser(userSummary);
            }
            return task;
        } catch (Exception ex) {
            ex.printStackTrace();
            Timber.e("Error constructing task object", ex);
        }
        return null;
    }

    private Task getEditedTask() {
        try {
            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            String date = tvDueDate.getText().toString();
            String time = tvDueTime.getText().toString();
            String timestampMillis = Task.getTimestampMillis(date, time);
            Address address = this.address;
            String price = etPrice.getText().toString();
            String category = spCategory.getSelectedItem().toString();
            List<String> categories = new ArrayList<>();
            categories.add(category);
            List<String> mediaUrls = this.mediaUrls;

            String status = getString(R.string.task_in_bid_process);
            int viewedBy = existingTask.getViewedBy();
            int bidBy = existingTask.getBidBy();

            Task task =
                    new Task(
                            address,
                            categories,
                            timestampMillis,
                            description,
                            mediaUrls,
                            price,
                            title,
                            status,
                            viewedBy,
                            bidBy
                    );

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                UserSummary userSummary = new UserSummary();
                if (user.getEmail() != null && !user.getEmail().isEmpty())
                    userSummary.setEmail(user.getEmail());
                userSummary.setId(user.getUid());
                userSummary.setName(user.getDisplayName());
                if (user.getPhotoUrl() != null)
                    userSummary.setPhoto(user.getPhotoUrl().toString());
                task.setUser(userSummary);
            }
            return task;
        } catch (Exception ex) {
            ex.printStackTrace();
            Timber.e("Error constructing task object", ex);
        }
        return null;
    }

    private void writeNewTask(Task task) {
        try {

            String key = mDatabase.child(getString(R.string.firebase_tasks_table)).push().getKey();
            mDatabase.child(getString(R.string.firebase_tasks_table)).child(key).setValue(task);

            HashMap<String, Object> usermap = new HashMap<>();
            usermap.put(task.getUser().getId(), true);
            mDatabase.child(getString(R.string.firebase_tasks_table)).child(key).child("user").updateChildren(usermap);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveTask(Task task) {
        try {
            String key = existingTaskId;

            mDatabase.child(getString(R.string.firebase_tasks_table)).child(key).setValue(task);

            HashMap<String, Object> usermap = new HashMap<>();
            usermap.put(task.getUser().getId(), true);
            mDatabase.child(getString(R.string.firebase_tasks_table)).child(key).child("user").updateChildren(usermap);

        } catch (Exception ex) {
            Timber.e("Error saving task", ex);
        }

    }

    private boolean validateTaskData()
    {
        try {


            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            String date = tvDueDate.getText().toString();
            String time = tvDueTime.getText().toString();
            String address = tvAddress.getText().toString();
            String price = etPrice.getText().toString();
            String category = spCategory.getSelectedItem().toString();
            List<String> mediaUrls = this.mediaUrls;

            boolean isValid  = true;

            if(taskDataValidationMode == TaskDataValidationMode.TASK_DEFAULT_MODE)
            {
                if(title == null || title.trim().isEmpty())
                {
                    etTitle.setError("Title is required");
                    isValid = false;
                }

                if(description == null || description.trim().isEmpty())
                {
                    etDescription.setError("Description is required");
                    isValid = false;
                }

                if(date == null || date.trim().isEmpty())
                {
                    tvDueDate.setError("Date is required");
                    isValid = false;
                }

                if(time == null || time.trim().isEmpty())
                {
                    tvDueTime.setError("Time is required");
                    isValid = false;
                }

                if(address == null || address.trim().isEmpty())
                {
                    tvAddress.setError("Address is required");
                    isValid = false;
                }

                if(price == null || price.trim().isEmpty())
                {
                    etPrice.setError("Price is required");
                    isValid = false;
                }

                if(mediaUrls == null || mediaUrls.size() == 0)
                {
                    //Toast.makeText(CreateTaskActivity.this, "Please add an image/video", Toast.LENGTH_SHORT).show();
                    TastyToast.makeText(CreateTaskActivity.this, "Please add an image/video", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                    isValid = false;
                }

                return isValid;
            }

            return true;
        }
        catch (Exception ex)
        {
            Timber.e("Error validating task data");
            return false;
        }
    }


    private String dueDate;

    @OnClick(R.id.tvDueDate)
    public void launchDatePicker() {
        try {
            setDatePickerListener();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void setDatePickerListener() {

        if (dueDate == null)
            dueDate = tvDueDate.getText().toString();
        if (dueDate.equals("") | !dueDate.contains("/"))
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
        try {
            String date = String.format(Locale.US, "%02d/%02d/%d", monthOfYear, dayOfMonth, year);
            tvDueDate.setText(date);
            dueDate = date;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @OnClick(R.id.tvDueTime)
    public void launchTimePicker() {
        try {
            setTimePickerListener();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String completionTime;

    private void setTimePickerListener() {
        try {
            if (completionTime == null)
                completionTime = tvDueTime.getText().toString();

            if (completionTime.equals("") | !completionTime.contains(":"))
                return;
            String[] splitTime = completionTime.split(":");
            int hour = Integer.parseInt(splitTime[0]);
            String[] secondSplitString = (splitTime[1].split(" "));
            int minute = Integer.parseInt(secondSplitString[0]);
            String amPm;
            if (secondSplitString.length == 1)
                amPm = "";
            else
                amPm = secondSplitString[1];
            DialogFragment timePickerFragment = TimePickerFragment.newInstance(minute, hour, amPm);
            timePickerFragment.show(getSupportFragmentManager(), "timePicker");
        }
        catch (Exception ex)
        {
            Timber.e("Error launching time picker");
            ex.printStackTrace();
        }
    }

    @Override
    public void onFinishTimePickDialog(int hourOfDay, int minute, String amPm) {
        try {
            String time = String.format(Locale.US, "%d:%02d %s", hourOfDay, minute, amPm.toUpperCase(Locale.US));
            tvDueTime.setText(time);
            completionTime = time;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @OnClick(R.id.tvAddress)
    public void startAddressFragment() {
        try {
            FragmentManager fm = getSupportFragmentManager();

            AddressFragment addressFragment =
                    AddressFragment.newInstance(address, AddressFragment.AddressValidationMode.TASK_ADDRESS_MODE);

            addressFragment.show(fm, "fragment_address");
        } catch (Exception ex) {
            Timber.e("Error in Address fragment");
        }
    }

    private void setupViewPager() {
        mediaPagerAdapter = new AdvancedMediaPagerAdapter(this);
        mVpMedia.setAdapter(mediaPagerAdapter);
        circleIndicator = new CircleIndicator(mViewPagerCountDots, mVpMedia);
        circleIndicator.setViewPagerIndicator();
    }

    public void startFullScreenMedia()
    {
        try
        {
            Intent intent = new Intent(CreateTaskActivity.this, MediaFullScreenActivity.class);
            intent.putExtra("urls", fileLocalUris);
            startActivity(intent);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting full screen media");
        }
    }


    int pageIndex = 0;

    public void startCameraImageCapture() {
        try {
            if (!permissions.checkPermissionForCamera()) {
                permissions.requestPermissionForCamera();
            } else {
                if (!permissions.checkPermissionForExternalStorage()) {
                    permissions.requestPermissionForExternalStorage();
                }
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
                    launchCameraForImage();
                else
                {
                    Toast.makeText(this, "No camera on device", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startCameraVideoCapture() {
        try {
            if (!permissions.checkPermissionForCamera()) {
                permissions.requestPermissionForCamera();
            } else {
                if (!permissions.checkPermissionForExternalStorage()) {
                    permissions.requestPermissionForExternalStorage();
                }
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
                    launchCameraForVideo();
                else
                {
                    Toast.makeText(this, "No camera on device", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 1035;
    public final static int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1036;

    private Uri fileUri;

    public void launchCameraForImage() {
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

    public void launchCameraForVideo() {
        // create Intent to capture a video and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = CameraUtils.getOutputMediaFileUri(CameraUtils.MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the video file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to capture video
            startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {

                    fileLocalUris.add(fileUri.toString());
                    startFileUpload(fileUri, true);

                    mediaPagerAdapter.insert(fileUri, pageIndex);
                    mediaPagerAdapter.notifyDataSetChanged();
                    circleIndicator.refreshIndicator();

                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the image capture
                } else { // Result was a failure
                    Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
            }
            else if(requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE)
            {
                if (resultCode == RESULT_OK) {

                    fileLocalUris.add(fileUri.toString());
                    startFileUpload(fileUri, false);


                    mediaPagerAdapter.insert(fileUri, pageIndex);
                    mediaPagerAdapter.notifyDataSetChanged();
                    circleIndicator.refreshIndicator();

                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the video capture
                } else {
                    Toast.makeText(this, "Failed to record video",  Toast.LENGTH_LONG).show();
                }
            }

            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    //Some error handling
                }

                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                    Uri fileUri = Uri.fromFile(imageFile);
                    fileLocalUris.add(fileUri.toString());

                    startFileUpload(fileUri, true);
                    //Handle the image
                    mediaPagerAdapter.insert(Uri.parse(imageFile.getAbsolutePath()), pageIndex);
                    mediaPagerAdapter.notifyDataSetChanged();
                    circleIndicator.refreshIndicator();
                }

                @Override
                public void onCanceled(EasyImage.ImageSource source, int type) {
                    //Cancel handling, you might wanna remove taken photo if it was canceled
                    if (source == EasyImage.ImageSource.CAMERA) {
                        File photoFile = EasyImage.lastlyTakenButCanceledPhoto(CreateTaskActivity.this);
                        if (photoFile != null) photoFile.delete();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void startImageSelect() {
        try {
            if (!permissions.checkPermissionForExternalStorage()) {
                permissions.requestPermissionForExternalStorage();
            }
            EasyImage.openGallery(this, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
        } catch (Exception ex) {
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

    @Override
    public void onStartImageCapture(int position) {
        try {
            pageIndex = position;
            startCameraImageCapture();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStartVideoCapture(int position) {
        try {
            pageIndex = position;
            startCameraVideoCapture();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStartImageSelect(int position) {
        try {
            pageIndex = position;
            startImageSelect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
            if(isImage) {
                fileStorageReference = imagesStorageReference.child(filename);
            }
            else
            {
                fileStorageReference = videosStorageReference.child(filename);
            }

            InputStream stream = new FileInputStream(new File(fileUri.getPath()));

            UploadTask uploadTask = fileStorageReference.putStream(stream);

            btnPostTask.setEnabled(false);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(CreateTaskActivity.this, "File upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null)
                        mediaUrls.add(downloadUrl.toString());

                    btnPostTask.setEnabled(true);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (fileLocalUris.size() > 0) {
                for (String fileName : fileLocalUris) {
                    File file = new File(fileName);
                    if (file.exists())
                        if(!file.delete())
                        {
                            Timber.e(String.format("file %s could not be deleted", fileName));
                        }
                }
            }
        }
        catch (Exception ex)
        {
            Timber.e("Error deleting captured files");
        }
    }

    @Override
    public void onFinishAddressFragment(Address address) {
        try {
            this.address = address;
            tvAddress.setText(Address.getDisplayString(address));
             ImageUtils.setBlurredMapBackground(address, ivDataBackground);

        } catch (Exception ex) {
            Timber.e("User entered address could not be parsed");
        }
    }

    @Override
    public void onStartItemViewClicked(int pageIndex) {
        startFullScreenMedia();
    }

    private String getDefaultDeadlineDate()
    {
        try {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return String.format(Locale.US, "%02d/%02d/%d", month , day, year );
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

}
