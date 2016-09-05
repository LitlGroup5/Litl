package com.litlgroup.litl.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

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
import com.litlgroup.litl.activities.MediaFullScreenActivity;
import com.litlgroup.litl.activities.ProfileActivity;
import com.litlgroup.litl.models.Address;
import com.litlgroup.litl.models.User;
import com.litlgroup.litl.utils.AdvancedMediaPagerAdapter;
import com.litlgroup.litl.utils.CameraUtils;
import com.litlgroup.litl.utils.CircleIndicator;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.ImageUtils;
import com.litlgroup.litl.utils.Permissions;
import com.litlgroup.litl.utils.ZoomOutPageTransformer;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
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
        AdvancedMediaPagerAdapter.StartImageSelectListener,
        AdvancedMediaPagerAdapter.StartOnItemViewClickListener
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

    @BindView(R.id.ibContactPhone)
    ImageButton ibContactPhone;

    @BindView(R.id.etContactNo)
    EditText etContactNo;

    @BindView(R.id.tvProfileAddress)
    TextView tvProfileAddress;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.rbUserRating)
    RatingBar rbUserRating;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.vpMedia)
    ViewPager mVpMedia;

    @BindView(R.id.vpIndicator)
    LinearLayout mViewPagerCountDots;

    @BindView(R.id.ivDataBackground)
    ImageView ivDataBackground;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindColor(android.R.color.transparent)
    int mTransparent;
    @BindColor(R.color.colorPrimaryDark)
    int mPrimaryDark;



    ArrayList<String> mediaUrls;

    CircleIndicator circleIndicator;

    AdvancedMediaPagerAdapter mediaPagerAdapter;

    Permissions permissions;
    StorageReference storageReference;


    DatabaseReference mDatabase;
    String onScreenUserId;
    ProfileActivity.ProfileMode profileMode;
    Address address;
    float userRating;

    //User object corresponding to the data on screen currently being viewed
    User onScreenUser;

    String currentAuthorizedUId;

    ArrayList<String> fileLocalUris;

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
            setEventListeners();
            setupViewPager();
            profileModeLayoutChanges();
            setupActionBar();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null)
        {
            fileUri = savedInstanceState.getParcelable("file_uri");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference =
                storage.
                        getReferenceFromUrl(getString(R.string.storage_reference_url))
                        .child(getString(R.string.storage_reference_users_child));
        mediaUrls = new ArrayList<>();
        fileLocalUris = new ArrayList<>();
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

    private void setupActionBar()
    {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mCollapsingToolbar.setExpandedTitleColor(mTransparent);
        mCollapsingToolbar.setContentScrimColor(mPrimaryDark);
        mCollapsingToolbar.setStatusBarScrimColor(mPrimaryDark);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setEventListeners() {
        rbUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                userRating = v;

                try
                {
                    mDatabase.child(Constants.TABLE_USERS)
                            .child(onScreenUserId)
                            .child(getString(R.string.user_rating_child))
                            .setValue(userRating);
                }
                catch (Exception ex)
                {
                    Timber.e("Error writing user rating to firebase");
                }
            }
        });
    }

    private void profileModeLayoutChanges()
    {
        try
        {
            switch (profileMode)
            {
                case ME_CREATE:
                    ibProfileEdit.setVisibility(View.INVISIBLE);
                    ibProfileSave.setVisibility(View.VISIBLE);
                    ibAddConnection.setVisibility(View.INVISIBLE);
                    ibRemoveConnection.setVisibility(View.INVISIBLE);

                    setPrivateInfoVisibility(true);

                    setEditModeFieldsState(true);
                    break;
                case ME_VIEW:
                    ibProfileEdit.setVisibility(View.VISIBLE);
                    ibProfileSave.setVisibility(View.INVISIBLE);
                    ibAddConnection.setVisibility(View.INVISIBLE);
                    ibRemoveConnection.setVisibility(View.INVISIBLE);

                    etProfileEmail.setVisibility(View.VISIBLE);
                    etContactNo.setVisibility(View.VISIBLE);
                    ibContactPhone.setVisibility(View.VISIBLE);
                    setPrivateInfoVisibility(true);
                    setEditModeFieldsState(false);
                    break;
                case ME_EDIT:
                    ibProfileEdit.setVisibility(View.INVISIBLE);
                    ibProfileSave.setVisibility(View.VISIBLE);
                    ibAddConnection.setVisibility(View.INVISIBLE);
                    ibRemoveConnection.setVisibility(View.INVISIBLE);

                    etProfileEmail.setVisibility(View.VISIBLE);
                    etContactNo.setVisibility(View.VISIBLE);
                    ibContactPhone.setVisibility(View.VISIBLE);

                    setPrivateInfoVisibility(true);
                    setEditModeFieldsState(true);

                    break;
                case CONNECTION:
                    ibProfileEdit.setVisibility(View.INVISIBLE);
                    ibProfileSave.setVisibility(View.INVISIBLE);
                    ibAddConnection.setVisibility(View.INVISIBLE);
                    ibRemoveConnection.setVisibility(View.VISIBLE);
                    etProfileEmail.setVisibility(View.INVISIBLE);
                    rbUserRating.setIsIndicator(false);

                    setEditModeFieldsState(false);
                    break;
                case OTHER:
                    ibProfileEdit.setVisibility(View.INVISIBLE);
                    ibProfileSave.setVisibility(View.INVISIBLE);

                    //Set the following two to invisible now and then reset when user data has been fetched
                    ibAddConnection.setVisibility(View.INVISIBLE);
                    ibRemoveConnection.setVisibility(View.INVISIBLE);
                    rbUserRating.setIsIndicator(false);
                    etProfileEmail.setVisibility(View.INVISIBLE);
                    setPrivateInfoVisibility(false);
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
//            etProfileName.setEnabled(isEditMode);
//            etProfileEmail.setEnabled(isEditMode);
            etContactNo.setEnabled(isEditMode);
            etAboutMe.setEnabled(isEditMode);
            etSkills.setEnabled(isEditMode);


            if(isEditMode) {
                ibAddConnection.setVisibility(View.GONE);
                etContactNo.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                etAboutMe.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                etSkills.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            else
            {
                etContactNo.setBackgroundColor(Color.TRANSPARENT);
                etAboutMe.setBackgroundColor(Color.TRANSPARENT);
                etSkills.setBackgroundColor(Color.TRANSPARENT);
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
                            TastyToast.makeText(getActivity(), "There was an error when fetching user data", TastyToast.LENGTH_LONG, TastyToast.ERROR);
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
            String contactNo = user.getContactNo();
            String profileImageUrl = user.getPhoto();

            ImageUtils.setCircularImage(ivProfileImage, profileImageUrl);
            ArrayList<String> skillset = null;
            if(user.getSkillSet() != null)
                skillset = (ArrayList<String>) user.getSkillSet();

            ArrayList<String> media;
            if(user.getMedia() != null) {
                media = (ArrayList<String>) user.getMedia();
                mediaUrls = media;
                fileLocalUris = media;
                for (int i =0 ; i  < mediaUrls.size(); i++) {
                    mediaPagerAdapter.insertUri(Uri.parse(mediaUrls.get(i)), i);

                    circleIndicator.refreshIndicator();
                }
                mediaPagerAdapter.notifyDataSetChanged();
            }

            Address address = user.getAddress();

            if(name != null && !name.isEmpty())
                etProfileName.setText(name);

            if(email!=null && !email.isEmpty())
                etProfileEmail.setText(email);

            if(bio != null && !bio.isEmpty())
                etAboutMe.setText(bio);

            if(contactNo != null && !contactNo.isEmpty())
                etContactNo.setText(contactNo);

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

            if(user.getRating()!=null)
                rbUserRating.setRating(user.getRating());

            if(address != null) {
                this.address = address;
                tvProfileAddress.setText(Address.getDisplayString(address));
                ImageUtils.setBlurredMapBackground(address, ivDataBackground);
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
                    AddressFragment.newInstance(address, AddressFragment.AddressValidationMode.PROFILE_ADDRESS_MODE);
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

            ImageUtils.setBlurredMapBackground(address, ivDataBackground);
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

            mediaPagerAdapter.setAllowCapture(true);
            mediaPagerAdapter.notifyDataSetChanged();//to force capture controls to be displayed

            ibProfileEdit.setVisibility(View.INVISIBLE);
            ibProfileSave.setVisibility(View.VISIBLE);
            ibAddConnection.setVisibility(View.INVISIBLE);
            ibRemoveConnection.setVisibility(View.INVISIBLE);
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

            ibAddConnection.setVisibility(View.INVISIBLE);
            ibRemoveConnection.setVisibility(View.VISIBLE);

            TastyToast.makeText(getActivity(), "Added to your connections", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

            writeAddRemoveConnection(isConnectionAdded);
            setPrivateInfoVisibility(true);
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
            ibRemoveConnection.setVisibility(View.INVISIBLE);

            TastyToast.makeText(getActivity(), "Removed from your connections", TastyToast.LENGTH_LONG, TastyToast.INFO);

            writeAddRemoveConnection(isConnectionAdded);
            setPrivateInfoVisibility(false);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting remove connection", ex);
        }
    }

    private void setPrivateInfoVisibility(boolean isSetVisible)
    {
        try
        {
            if (!onScreenUser.getConnections().contains(currentAuthorizedUId))
                return;

            if(isSetVisible) {
                etProfileEmail.setVisibility(View.VISIBLE);
                etContactNo.setVisibility(View.VISIBLE);
                ibContactPhone.setVisibility(View.VISIBLE);
            }
            else
            {
                etProfileEmail.setVisibility(View.GONE);
                etContactNo.setVisibility(View.GONE);
                ibContactPhone.setVisibility(View.GONE);
            }

        }
        catch (Exception ex)
        {
            Timber.e("Error setting visiblity of private information");
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

                                        ibAddConnection.setVisibility(View.INVISIBLE);
                                        ibRemoveConnection.setVisibility(View.VISIBLE);
                                        setPrivateInfoVisibility(true);

                                    } else {
                                        ibAddConnection.setVisibility(View.VISIBLE);
                                        ibRemoveConnection.setVisibility(View.INVISIBLE);
                                        setPrivateInfoVisibility(false);
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
            boolean isDataValid = validateProfileData();
            if(!isDataValid)
                return;
            mediaPagerAdapter.setAllowCapture(false);
            User user;
            if(profileMode == ProfileActivity.ProfileMode.ME_CREATE
                    || profileMode == ProfileActivity.ProfileMode.ME_EDIT
                    ||profileMode == ProfileActivity.ProfileMode.ME_VIEW ) {
                 user = getMeUser();
                writeUser(user);
                TastyToast.makeText(getActivity(), "User data has been saved!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                getActivity().finish();
            }
        }
        catch (Exception ex)
        {
            Timber.e("Error when saving profile", ex);
        }
    }

    private boolean validateProfileData()
    {
        try
        {
            String name = etProfileName.getText().toString();

            String email = etProfileEmail.getText().toString();

            String bio = etAboutMe.getText().toString();
            String contactNo = etContactNo.getText().toString();
            String skillsString = etSkills.getText().toString();

            String address = tvProfileAddress.getText().toString();

            boolean isValid = true;
            if(name == null || name.trim().isEmpty())
            {
                etProfileName.setError("Name is required");
                isValid = false;
            }

            if(email == null || email.trim().isEmpty())
            {
                etProfileEmail.setError("Email is required");
                isValid = false;
            }

            if(contactNo == null || contactNo.trim().isEmpty())
            {
                etContactNo.setError("Contact number is required");
                isValid = false;
            }

            if(bio == null || bio.trim().isEmpty())
            {
                etAboutMe.setError("About me is required");
                isValid = false;
            }

            if(skillsString == null || skillsString.trim().isEmpty())
            {
                etSkills.setError("Skills are required");
                isValid = false;
            }

            if(address == null || address.trim().isEmpty())
            {
                tvProfileAddress.setError("Address is required");
                isValid = false;
            }
            return isValid;
        }
        catch (Exception ex)
        {
            Timber.e("Error validating profile data");
            return false;
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
            String contactNo = etContactNo.getText().toString();
            String bio = etAboutMe.getText().toString();
            String skillsString = etSkills.getText().toString();
            ArrayList<String> skillSet = new ArrayList<>();
            String[] splitSkills = skillsString.split("\n");

            for (String skill : splitSkills) {
                skillSet.add(skill);
            }

            Address address = this.address;
            List<String> bookmarks = new ArrayList<>();

            Float earnings = 0f;

            String photo = authUserData.getPhoto();
            ArrayList<String> connections = new ArrayList<>();

            ArrayList<String> media = mediaUrls;//new ArrayList<>();
            Float rating = authUserData.getRating(); //rbUserRating.getRating();
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

        if(profileMode == ProfileActivity.ProfileMode.ME_EDIT ||
                profileMode == ProfileActivity.ProfileMode.ME_CREATE) {
            mediaPagerAdapter = new AdvancedMediaPagerAdapter(getActivity(), this, this, this, this, true, true);
        }
        else
        {
            mediaPagerAdapter = new AdvancedMediaPagerAdapter(getActivity(), this, this, this, this, false, true);
        }

        mVpMedia.setAdapter(mediaPagerAdapter);
        mVpMedia.setPageTransformer(true, new ZoomOutPageTransformer());
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
                launchCameraForImage();
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
                if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
                    launchCameraForVideo();
                else
                {
                    TastyToast.makeText(getActivity(), "No camera on device", TastyToast.LENGTH_LONG, TastyToast.DEFAULT);
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // setEventListeners the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public void launchCameraForVideo() {
        // create Intent to capture a video and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = CameraUtils.getOutputMediaFileUri(CameraUtils.MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // setEventListeners the video file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to capture video
            startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == getActivity().RESULT_OK) {

                    fileLocalUris.add(fileUri.toString());
                    startFileUpload(fileUri, true);

                    mediaPagerAdapter.insert(fileUri, pageIndex);
                    mediaPagerAdapter.notifyDataSetChanged();
                    circleIndicator.refreshIndicator();

                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    // User cancelled the image capture
                } else { // Result was a failure
                    TastyToast.makeText(getActivity(), "Picture waas not taken!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
            }
            else if(requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE)
            {
                if (resultCode == getActivity().RESULT_OK) {

                    fileLocalUris.add(fileUri.toString());
                    startFileUpload(fileUri, false);


                    mediaPagerAdapter.insert(fileUri, pageIndex);
                    mediaPagerAdapter.notifyDataSetChanged();
                    circleIndicator.refreshIndicator();

                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    // User cancelled the video capture
                } else {
                    TastyToast.makeText(getActivity(), "Failed to record video", TastyToast.LENGTH_LONG, TastyToast.ERROR);
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
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    TastyToast.makeText(getActivity(), "File failed to upload", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null)

                        mediaUrls.add(downloadUrl.toString());

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
    public void onStartItemViewClicked(int pageIndex) {
        startFullScreenMedia();
    }


    public void startFullScreenMedia()
    {
        try
        {
            Intent intent = new Intent(getActivity(), MediaFullScreenActivity.class);
            intent.putExtra("urls", fileLocalUris);
            startActivity(intent);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting full screen media");
        }
    }

}
