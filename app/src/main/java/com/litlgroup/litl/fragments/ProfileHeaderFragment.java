package com.litlgroup.litl.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.litlgroup.litl.R;
import com.litlgroup.litl.activities.ProfileActivity;
import com.litlgroup.litl.models.Address;
import com.litlgroup.litl.models.User;
import com.litlgroup.litl.utils.AdvancedMediaPagerAdapter;
import com.litlgroup.litl.utils.CameraUtils;
import com.litlgroup.litl.utils.CircleIndicator;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.Permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import timber.log.Timber;

/**
 * Created by Hari on 8/28/2016.
 */
public class ProfileHeaderFragment
        extends Fragment
    implements AddressFragment.AddressFragmentListener,
        AdvancedMediaPagerAdapter.StartImageCaptureListener,
        AdvancedMediaPagerAdapter.StartVideoCaptureListener,
        AdvancedMediaPagerAdapter.StartImageSelectListener
{

    @BindView(R.id.etProfileName)
    EditText etProfileName;

    @BindView(R.id.etProfileEmail)
    EditText etProfileEmail;

    @BindView(R.id.etAboutMe)
    EditText etAboutMe;

    @BindView(R.id.etSkills)
    EditText etSkills;

    @BindView(R.id.ibAddConnection)
    ImageButton ibAddConnection;

    @BindView(R.id.ibProfileEdit)
    ImageButton ibProfileEdit;

    @BindView(R.id.ibProfileSave)
    ImageButton ibProfileSave;

    @BindView(R.id.ibRemoveConnection)
    ImageButton ibRemoveConnection;

    @BindView(R.id.tvProfileAddress)
    TextView tvProfileAddress;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.vpMedia)
    ViewPager mVpMedia;

    @BindView(R.id.vpIndicator)
    LinearLayout mViewPagerCountDots;

    ArrayList<String> mediaUrls;

    CircleIndicator circleIndicator;

    AdvancedMediaPagerAdapter mediaPagerAdapter;

    Permissions permissions;
    StorageReference storageReference;


    DatabaseReference mDatabase;
    String onScreenUserId;
    ProfileActivity.ProfileMode profileMode;
    Address address;

    //User object corresponding to the data on screen currently being viewed
    User onScreenUser;

    String currentAuthorizedUId;

    public static ProfileHeaderFragment newInstance(String userId, ProfileActivity.ProfileMode profileMode) {
        Bundle args = new Bundle();
        ProfileHeaderFragment fragment = new ProfileHeaderFragment();
        fragment.onScreenUserId = userId;
        fragment.profileMode = profileMode;
        fragment.setArguments(args);
        return fragment;
    }

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_header, container, false);
        unbinder = ButterKnife.bind(this, view);
        try {


            setupViewPager();
            profileModeLayoutChanges();
        }
        catch (Exception ex)
        {
            Timber.e("Error creating View", ex);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference =
                storage.
                        getReferenceFromUrl(getString(R.string.storage_reference_url))
                        .child(getString(R.string.storage_reference_tasks_child));
        mediaUrls = new ArrayList<>();
        permissions = new Permissions(getActivity());

        try {
            currentAuthorizedUId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            getAuthUserData();

            if(!currentAuthorizedUId.equals(onScreenUserId)) {
                getUserData();
            }

        } catch (Exception ex) {
            Timber.e("Error creating ProfileHeaderFragment", ex);
        }
    }

    private void profileModeLayoutChanges()
    {
        try
        {
            switch (profileMode)
            {
                case ME_CREATE:
                    ibProfileEdit.setVisibility(View.GONE);
                    ibProfileSave.setVisibility(View.VISIBLE);
                    ibAddConnection.setVisibility(View.GONE);
                    ibRemoveConnection.setVisibility(View.GONE);

                    setEditModeFieldsState(true);
                    break;
                case ME_VIEW:
                    ibProfileEdit.setVisibility(View.VISIBLE);
                    ibProfileSave.setVisibility(View.GONE);
                    ibAddConnection.setVisibility(View.GONE);
                    ibRemoveConnection.setVisibility(View.GONE);
                    setEditModeFieldsState(false);
                    break;
                case ME_EDIT:
                    ibProfileEdit.setVisibility(View.GONE);
                    ibProfileSave.setVisibility(View.VISIBLE);
                    ibAddConnection.setVisibility(View.GONE);
                    ibRemoveConnection.setVisibility(View.GONE);

                    setEditModeFieldsState(true);
                    break;
                case CONNECTION:
                    ibProfileEdit.setVisibility(View.GONE);
                    ibProfileSave.setVisibility(View.GONE);
                    ibAddConnection.setVisibility(View.GONE);
                    ibRemoveConnection.setVisibility(View.VISIBLE);
                    setEditModeFieldsState(false);
                    break;
                case OTHER:
                    ibProfileEdit.setVisibility(View.GONE);
                    ibProfileSave.setVisibility(View.GONE);

                    //Set the following two to invisible now and then reset when user data has been fetched
                    ibAddConnection.setVisibility(View.GONE);
                    ibRemoveConnection.setVisibility(View.GONE);

                    setEditModeFieldsState(false);
                    break;
            }

        }
        catch (Exception ex)
        {
            Timber.e("Error when making profile-mode based layout changes",ex);
        }
    }

    private void setEditModeFieldsState(Boolean isEditMode)
    {
        try
        {
            etProfileName.setEnabled(isEditMode);
            etProfileEmail.setEnabled(isEditMode);

            etAboutMe.setEnabled(isEditMode);
            etSkills.setEnabled(isEditMode);

            if(isEditMode) {
                ibAddConnection.setVisibility(View.GONE);
            }

            tvProfileAddress.setClickable(isEditMode);
        }
        catch (Exception ex)
        {
            Timber.e("Error enabling edit mode fields", ex);
        }
    }


    private void getUserData() {
        try {
            mDatabase
                    .child(Constants.TABLE_USERS)
                    .child(onScreenUserId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                onScreenUser = dataSnapshot.getValue(User.class);
                                populateUserData(onScreenUser);
                            } catch (Exception ex) {
                                Timber.e("Error getting snapshot value", ex);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getActivity(),
                                    "There was an error when fetching user data", Toast.LENGTH_SHORT).show();

                        }
                    });

        } catch (Exception ex) {
            Timber.e("Error getting user data", ex);
        }
    }

    private void populateUserData(User user) {
        try {

            String name = user.getName();
            String email = user.getEmail();
            String bio = user.getbiography();

            ArrayList<String> skillset = null;
            if(user.getSkillSet() != null)
                skillset = (ArrayList<String>) user.getSkillSet();

            ArrayList<String> media;
            if(user.getMedia() != null) {
                media = (ArrayList<String>) user.getMedia();
                mediaUrls = media;
                for (int i =0 ; i  < mediaUrls.size(); i++) {
                    mediaPagerAdapter.insertUri(Uri.parse(mediaUrls.get(i)), i);
                    mediaPagerAdapter.notifyDataSetChanged();
                    circleIndicator.refreshIndicator();
                }
            }

            Address address = user.getAddress();

            if(name != null && !name.isEmpty())
                etProfileName.setText(name);

            if(email!=null && !email.isEmpty())
                etProfileEmail.setText(email);

            if(bio != null && !bio.isEmpty())
                etAboutMe.setText(bio);

            if(skillset != null && !skillset.isEmpty()) {

                String skills ="";
                for (int i=0; i< skillset.size(); i++) {
                    if(i != skillset.size() -1)
                        skills += String.format("%s\n",skillset.get(i));
                    else
                        skills += String.format("%s",skillset.get(i));
                }
                etSkills.setText(skills);
            }

            //handle media urls


            if(address != null) {
                this.address = address;
                tvProfileAddress.setText(Address.getDisplayString(address));
            }



        } catch (Exception ex) {
            Timber.e("Error populating user data onto screen", ex);
        }
    }

    @OnClick(R.id.tvProfileAddress)
    public void startAddressFragment() {
        try {
            FragmentManager fm = getActivity().getSupportFragmentManager();

            AddressFragment addressFragment =
                    AddressFragment.newInstance(address);
            addressFragment.setTargetFragment(this, 0);

            addressFragment.show(fm, "fragment_address");
        } catch (Exception ex) {
            Timber.e("Error in Address fragment");
        }
    }

    @Override
    public void onFinishAddressFragment(Address address) {
        try {
            this.address = address;
            tvProfileAddress.setText(Address.getDisplayString(address));
        } catch (Exception ex) {
            Timber.e("User entered address could not be parsed");
        }
    }

    @OnClick(R.id.ibProfileEdit)
    public void startEditProfile()
    {
        try
        {
            setEditModeFieldsState(true);
            ibProfileEdit.setVisibility(View.GONE);
            ibProfileSave.setVisibility(View.VISIBLE);
            ibAddConnection.setVisibility(View.GONE);
            ibRemoveConnection.setVisibility(View.GONE);
        }
        catch (Exception ex)
        {
            Timber.e("There was an error starting edit profile", ex);
        }
    }

    boolean isConnectionAdded = false;
    @OnClick(R.id.ibAddConnection)
    public void startAddConnection()
    {
        try
        {
            isConnectionAdded = true;

            ibAddConnection.setVisibility(View.GONE);
            ibRemoveConnection.setVisibility(View.VISIBLE);

            Toast.makeText(getActivity(), "Added to your connections!", Toast.LENGTH_SHORT).show();

            writeAddRemoveConnection(isConnectionAdded);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting add connection", ex);
        }
    }

    @OnClick(R.id.ibRemoveConnection)
    public void startRemoveConnection()
    {
        try
        {
            isConnectionAdded = false;

            ibAddConnection.setVisibility(View.VISIBLE);
            ibRemoveConnection.setVisibility(View.GONE);

            Toast.makeText(getActivity(), "Removed from your connections!", Toast.LENGTH_SHORT).show();
            writeAddRemoveConnection(isConnectionAdded);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting remove connection", ex);
        }
    }

    User authUserData;
    ArrayList<String> authUserconnections;

    private void getAuthUserData() {
        try {

            mDatabase.child(Constants.TABLE_USERS)
                    .child(currentAuthorizedUId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            authUserData = dataSnapshot.getValue(User.class);
                            authUserconnections = (ArrayList<String>) authUserData.getConnections();

                            if(!currentAuthorizedUId.equals(onScreenUserId)) {
                                try {
                                    if (authUserconnections.contains(onScreenUserId)) //if onscreen user is in current authorized user's authUserconnections
                                    {
                                        ibAddConnection.setVisibility(View.GONE);
                                        ibRemoveConnection.setVisibility(View.VISIBLE);
                                    } else {
                                        ibAddConnection.setVisibility(View.VISIBLE);
                                        ibRemoveConnection.setVisibility(View.GONE);
                                    }
                                }
                                catch (Exception ex)
                                {
                                    Timber.e(ex.toString());
                                }
                            }
                            else
                            {
                                populateUserData(authUserData);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        } catch (Exception ex) {
            Timber.e("Error getting authorized user data", ex);
        }
    }

    private void writeAddRemoveConnection(final boolean isConnectionAdded)
    {
        try
        {

            if(isConnectionAdded)
            {
                authUserconnections.add(onScreenUserId);
            }
            else
            {
                if(authUserconnections.contains(onScreenUserId))
                    authUserconnections.remove(onScreenUserId);
            }

            mDatabase.child(Constants.TABLE_USERS)
                    .child(currentAuthorizedUId)
                    .child(getString(R.string.child_connections))
                    .setValue(authUserconnections);
        }
        catch (Exception ex)
        {
            Timber.e("Error writing add connection status", ex);
        }
    }


    @OnClick(R.id.ibProfileSave)
    public void startSaveProfile()
    {
        try
        {
            User user;
            if(profileMode == ProfileActivity.ProfileMode.ME_CREATE
                    || profileMode == ProfileActivity.ProfileMode.ME_EDIT
                    ||profileMode == ProfileActivity.ProfileMode.ME_VIEW ) {
                 user = getMeUser();
                writeUser(user);
                Toast.makeText(getActivity(), "User data has been saved!", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
        catch (Exception ex)
        {
            Timber.e("Error when saving profile", ex);
        }
    }

    private void writeUser(User user)
    {
        try
        {
            if(user == null)
                return;
            mDatabase
                    .child(Constants.TABLE_USERS)
                    .child(onScreenUserId)
                    .setValue(user);

        }
        catch (Exception ex)
        {
            Timber.e("Error writing user data to firebase",ex);
        }

    }

    private User getMeUser()
    {
        try {
            String name = etProfileName.getText().toString();
            if(name.isEmpty())
                name = authUserData.getName();
            String email = etProfileEmail.getText().toString();
            if(email.isEmpty())
                email = authUserData.getEmail();
            String bio = etAboutMe.getText().toString();
            String skillsString = etSkills.getText().toString();
            ArrayList<String> skillSet = new ArrayList<>();
            String[] splitSkills = skillsString.split("\n");

            for (String skill : splitSkills) {
                skillSet.add(skill);
            }

            Address address = this.address;
            List<String> bookmarks = new ArrayList<>();
            String contactNo = getString(R.string.default_contact_no);
            Float earnings = 0f;

            String photo = authUserData.getPhoto();
            ArrayList<String> connections = new ArrayList<>();

            ArrayList<String> media = mediaUrls;//new ArrayList<>();
            Float rating = 0f;
            User user = new User(
                    bookmarks,
                    contactNo,
                    earnings,
                    email,
                    media,
                    name,
                    photo,
                    rating,
                    skillSet,
                    address,
                    bio,
                    connections
            );
            return user;
        }
        catch (Exception ex)
        {
            Timber.e("Error when building ME_CREATE user object from screen data", ex);
        }

        return null;
    }


    private void setupViewPager() {
        mediaPagerAdapter = new AdvancedMediaPagerAdapter(getActivity(), this, this, this);

        mVpMedia.setAdapter(mediaPagerAdapter);
        circleIndicator = new CircleIndicator(mViewPagerCountDots, mVpMedia);
        circleIndicator.setViewPagerIndicator();
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
                launchCamera();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1035;

    private Uri fileUri;

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = CameraUtils.getOutputMediaFileUri(CameraUtils.MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == getActivity().RESULT_OK) {

                    startImageUpload(fileUri);

                    mediaPagerAdapter.insert(fileUri, pageIndex);
                    mediaPagerAdapter.notifyDataSetChanged();
                    circleIndicator.refreshIndicator();

                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    // User cancelled the image capture
                } else { // Result was a failure
                    Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
            }

            EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    //Some error handling
                }

                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                    Uri fileUri = Uri.fromFile(imageFile);
                    startImageUpload(fileUri);
                    //Handle the image
                    mediaPagerAdapter.insert(Uri.parse(imageFile.getAbsolutePath()), pageIndex);
                    mediaPagerAdapter.notifyDataSetChanged();
                    circleIndicator.refreshIndicator();
                }

                @Override
                public void onCanceled(EasyImage.ImageSource source, int type) {
                    //Cancel handling, you might wanna remove taken photo if it was canceled
                    if (source == EasyImage.ImageSource.CAMERA) {
                        File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }


//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        // get the file url
//        fileUri = savedInstanceState.getParcelable("file_uri");
//    }

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
    public void onStartVideoCapture(int pageIndex) {
        try
        {

        }
        catch (Exception ex)
        {

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


    private void startImageUpload(final Uri fileUri) {
        try {

            String filename = getFileName(fileUri);
            if (filename == null || filename.isEmpty()) {
//                Toast
//                        .makeText(CreateTaskActivity.this, "Could not get file name!", Toast.LENGTH_SHORT).show();
                return;
            }
            StorageReference imagesStorageReference = storageReference.child("images");
            StorageReference imageStorageReference = imagesStorageReference.child(filename);
            InputStream stream = new FileInputStream(new File(fileUri.getPath()));

            UploadTask uploadTask = imageStorageReference.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null)

                        mediaUrls.add(downloadUrl.toString());
//                        Toast.makeText(CreateTaskActivity.this,
//                            String.format("Image uploaded to : %s", downloadUrl.toString()), Toast.LENGTH_SHORT).show();


                }
            });

        } catch (Exception ex) {
            Timber.e("Error uploading image", ex);
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
