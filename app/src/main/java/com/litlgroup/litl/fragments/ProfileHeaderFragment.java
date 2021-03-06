package com.litlgroup.litl.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.litlgroup.litl.R;
import com.litlgroup.litl.activities.MediaFullScreenActivity;
import com.litlgroup.litl.activities.ProfileActivity;
import com.litlgroup.litl.adapters.SkillsCardPagerAdapter;
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
import com.thomashaertel.widget.MultiSpinner;

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

    @BindView(R.id.srbProfileRating)
    SimpleRatingBar srbProfileRating;

    @BindView(R.id.tvRating)
    TextView tvRating;

    @BindView(R.id.etProfileEmail)
    EditText etProfileEmail;

    @BindView(R.id.etAboutMe)
    EditText etAboutMe;

    @BindView(R.id.etSkills)
    EditText etSkills;

    @BindView(R.id.multispSkills)
    MultiSpinner multiSpSkills;

    @BindView(R.id.ibContactPhone)
    ImageView ibContactPhone;

    @BindView(R.id.ibProfileEmail)
    ImageView ibProfileEmail;

    @BindView(R.id.etContactNo)
    EditText etContactNo;

    @BindView(R.id.tvProfileAddress)
    EditText etProfileAddress;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.vpMedia)
    ViewPager mVpMedia;

    @BindView(R.id.vpIndicator)
    LinearLayout mViewPagerCountDots;

    @BindView(R.id.ivMaps)
    ImageView ivMaps;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindColor(android.R.color.transparent)
    int mTransparent;
    @BindColor(R.color.colorPrimaryDark)
    int mPrimaryDark;

    @BindView(R.id.llEmail)
    LinearLayout llEmail;

    @BindView(R.id.llPhone)
    LinearLayout llPhone;

    @BindView(R.id.llContactUserButtons)
    LinearLayout llContactUserButtons;

    @BindView(R.id.ibCallUser)
    ImageButton ibCallUser;

    @BindView(R.id.ibEmailUser)
    ImageButton ibEmailUser;

    @BindView(R.id.llSkillsDescription)
    LinearLayout llSkillsDescription;

    @BindView(R.id.llSkillsPager)
    LinearLayout llSkillsPager;

    @BindView(R.id.vpProfileSkills)
    ViewPager vpProfileSkills;

    @BindView(R.id.llAboutMeEdit)
    LinearLayout llAboutMeEdit;

    @BindView(R.id.llAboutMeDisplay)
    LinearLayout llAboutMeDisplay;

    @BindView(R.id.tvAboutMe)
    TextView tvAboutMe;

    private SkillsCardPagerAdapter skillsCardPagerAdapter;

    private Menu mMenu;

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

    private ArrayAdapter<String> multiSpAdapter;
    private boolean[] categorySelectedFlags;

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

            setupActionBar();
            initToolbar();

            multiSpAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
            multiSpAdapter.addAll(getResources().getStringArray(R.array.categories_array_values));
            categorySelectedFlags = new boolean[multiSpAdapter.getCount()];
            multiSpSkills.setAdapter(multiSpAdapter, false, onSelectedListener);
            initializeMultiSpSkills();

            setupSkillsPager();

        }
        catch (Exception ex)
        {
            Timber.e("Error creating View", ex);
        }
        return view;
    }

    private void setupSkillsPager()
    {
        try
        {
            skillsCardPagerAdapter = new SkillsCardPagerAdapter(getActivity());
            vpProfileSkills.setAdapter(skillsCardPagerAdapter);
            vpProfileSkills.setOffscreenPageLimit(2);
        }
        catch (Exception ex)
        {
            Timber.e("Error setting up skills pager");
        }
    }

    private void initializeMultiSpSkills()
    {
        try
        {
            String[] categories = getResources().getStringArray(R.array.categories_array_values);
            for (int i= 0; i < categories.length; i++) {
               if(etSkills.getText().toString().contains(categories[i]))
               {
                   categorySelectedFlags[i] = true;
               }
            }
            multiSpSkills.setSelected(categorySelectedFlags);
            multiSpSkills.setText(etSkills.getText().toString());
        }
        catch (Exception ex)
        {
            Timber.e("Error initializing multi spinner");
        }
    }

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items

            StringBuilder builder = new StringBuilder();

            boolean atleastOneSkillSelected = false;
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    atleastOneSkillSelected = true;
                    builder.append(multiSpAdapter.getItem(i)).append("\n");
                }
            }

            if(atleastOneSkillSelected) {
                multiSpSkills.setText(builder.toString());
                etSkills.setText(builder.toString());
            }
            else
            {
                multiSpSkills.setText("Not Listed");
                etSkills.setText("Not Listed");
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        mMenu = menu;
        profileModeMenuOptionsChanges();
        getAuthUserData();
        if(!currentAuthorizedUId.equals(onScreenUserId)) {
            getUserData();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_item_edit: {

                if(iconMode == IconMode.EDIT)
                {
                    startEditProfile();
                }
                else if(iconMode == IconMode.SAVE)
                {
                    startSaveProfile();
                }
                else if(iconMode == IconMode.ADD_CONNECTION)
                {
                    startAddConnection();
                }
                else if(iconMode == IconMode.REMOVE_CONNECTION)
                {
                    startRemoveConnection();

                }


            }
        }

        return super.onOptionsItemSelected(item);
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

        if (mediaPagerAdapter != null)
            mediaPagerAdapter = null;

        if (permissions != null)
            permissions = null;

        if(mMenu != null)
            mMenu = null;

        if (circleIndicator != null)
            circleIndicator = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null)
        {
            fileUri = savedInstanceState.getParcelable("file_uri");
        }

        setHasOptionsMenu(true);
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

        } catch (Exception ex) {
            Timber.e("Error creating ProfileHeaderFragment", ex);
        }
    }

    @OnClick(R.id.llRating)
    public void startRadarChart()
    {
        try
        {
            launchRadarChart();

        }
        catch (Exception ex)
        {
            Timber.e("Error starting radar chart");
        }
    }

    public void launchRadarChart()
    {
        try
        {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            List<String> ratings;
            float avgRating;
            if(profileMode == ProfileActivity.ProfileMode.ME_VIEW) {

                ratings = authUserData.getRating();
                avgRating = authUserData.getAverageRating();
            }
            else
            {
                ratings = onScreenUser.getRating();
                avgRating = onScreenUser.getAverageRating();
            }

            RatingRadarChartFragment ratingRadarChartFragment  =
                    RatingRadarChartFragment.newInstance(ratings, avgRating);
            ratingRadarChartFragment.show(fm, "fragment_rating_radar");


        }
        catch (Exception ex)
        {
            Timber.e("Error launching radar chart dialog");
        }
    }


    @OnClick(R.id.ibCallUser)
    public void startCall()
    {
        try
        {
            String uri = "tel:" + etContactNo.getText().toString().trim();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }
        catch (Exception ex)
        {
            Timber.e("Error setting up call button", ex);
        }
    }

    @OnClick(R.id.ibEmailUser)
    public void startEmail()
    {
        try
        {
            String[] emailRecipient = new String[] {etProfileEmail.getText().toString().trim()};
            String emailSubject = String.format("%s - %s", getString(R.string.email_subject_base), etProfileName.getText().toString().trim());
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, emailRecipient);
            intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        catch (Exception ex)
        {
            Timber.e("Error starting email", ex);
        }
    }

    private void setupActionBar()
    {

        mCollapsingToolbar.setExpandedTitleColor(mTransparent);
        mCollapsingToolbar.setContentScrimColor(mPrimaryDark);
        mCollapsingToolbar.setStatusBarScrimColor(mPrimaryDark);

    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void profileModeMenuOptionsChanges()
    {
        try
        {
            switch (profileMode)
            {
                case ME_CREATE:
                    setProfileSaveIcon();
                    setPrivateInfoVisibility(true);
                    setEditModeFieldsState(true);
                    break;
                case ME_VIEW:
                    setProfileEditIcon();
                    setPrivateInfoVisibility(true);
                    setEditModeFieldsState(false);
                    break;
                case ME_EDIT:
                    setProfileSaveIcon();
                    setPrivateInfoVisibility(true);
                    setEditModeFieldsState(true);

                    break;
                case CONNECTION:
                    setRemoveConnectionIcon();
                    etProfileEmail.setVisibility(View.INVISIBLE);
                    setEditModeFieldsState(false);
                    break;
                case OTHER:
                    setAddConnectionIcon();
                    setPrivateInfoVisibility(false);
                    setEditModeFieldsState(false);
                    break;
            }
        }
        catch (Exception ex)
        {
            Timber.e("Error when making profile-mode based menu options changes",ex);
        }
    }

    enum IconMode {ADD_CONNECTION, REMOVE_CONNECTION, EDIT, SAVE};
    IconMode iconMode;

    private void setAddConnectionIcon()
    {
        mMenu.getItem(0).setIcon(R.drawable.ic_add_to_connections);
        iconMode = IconMode.ADD_CONNECTION;
    }

    private void setRemoveConnectionIcon()
    {
        mMenu.getItem(0).setIcon(R.drawable.ic_connection_added);
        iconMode = IconMode.REMOVE_CONNECTION;
    }

    private void setProfileEditIcon()
    {
        mMenu.getItem(0).setIcon(R.drawable.ic_menu_edit);
        iconMode = IconMode.EDIT;
    }

    private void setProfileSaveIcon()
    {
        mMenu.getItem(0).setIcon(R.drawable.ic_save_white);
        iconMode = IconMode.SAVE;
    }

    private void setEditModeFieldsState(Boolean isEditMode)
    {
        try
        {
            etContactNo.setEnabled(isEditMode);
            etAboutMe.setEnabled(isEditMode);
            etSkills.setEnabled(isEditMode);
            etProfileAddress.setEnabled(isEditMode);

            if(isEditMode) {


                //hide the linear layout that contains the contact image buttons
                llContactUserButtons.setVisibility(View.GONE);


                //About me:
                llAboutMeDisplay.setVisibility(View.GONE);
                llAboutMeEdit.setVisibility(View.VISIBLE);

                //show the linear layouts that contain the editable fields for email and phone
                llEmail.setVisibility(View.VISIBLE);
                llPhone.setVisibility(View.VISIBLE);


                //Display skills related edit fields
                llSkillsDescription.setVisibility(View.VISIBLE);
                llSkillsPager.setVisibility(View.GONE);


                //Add the underbar to necessary edit-texts
                etContactNo.getBackground()
                        .setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                etAboutMe.getBackground()
                        .setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                etProfileAddress.getBackground()
                        .setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

                //Skills
                etSkills.setVisibility(View.INVISIBLE);
                multiSpSkills.setVisibility(View.VISIBLE);
                multiSpSkills.getBackground()
                        .setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);


            }
            else
            {
                //show the linear layout that contains the contact image buttons
                llContactUserButtons.setVisibility(View.VISIBLE);


                //About me:
                llAboutMeDisplay.setVisibility(View.VISIBLE);
                llAboutMeEdit.setVisibility(View.GONE);

                //Hide the linear layouts that contain the editable fields for email and phone
                llEmail.setVisibility(View.GONE);
                llPhone.setVisibility(View.GONE);

                //Display skills related edit fields
                llSkillsDescription.setVisibility(View.GONE);
                llSkillsPager.setVisibility(View.VISIBLE);

                //Remove the underbar from edit-texts
                etContactNo.getBackground()
                        .setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);

                etAboutMe.getBackground()
                        .setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);

                etProfileAddress.getBackground()
                        .setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);

                etSkills.setVisibility(View.VISIBLE);
                multiSpSkills.setVisibility(View.INVISIBLE);
                multiSpSkills.getBackground()
                        .setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
                etSkills.getBackground()
                        .setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
            }

            etProfileAddress.setClickable(isEditMode);
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
//                                if(onScreenUser.getConnections().contains(currentAuthorizedUId))
//                                    setPrivateInfoVisibility(true);
                                populateUserData(onScreenUser);
                            } catch (Exception ex) {
                                Timber.e("Error getting snapshot value", ex);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            TastyToast.makeText(getActivity(), "There was an error when fetching user data", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
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

            if(user.getSkillSet() != null) {
                skillset = (ArrayList<String>) user.getSkillSet();
                skillsCardPagerAdapter.addAll(skillset);
                skillsCardPagerAdapter.notifyDataSetChanged();
                if(skillsCardPagerAdapter.getCount() >= 1) {
                    vpProfileSkills.setCurrentItem(1);
                }
            }

            ArrayList<String> media;
            if(user.getMedia() != null) {
                media = (ArrayList<String>) user.getMedia();
                mediaUrls = media;
                fileLocalUris = media;
                mediaPagerAdapter.removeAll();
                for (int i =0 ; i  < mediaUrls.size(); i++) {
                    try {
                        if (mediaUrls.get(i) == null)
                            continue;
                        mediaPagerAdapter.addImage(mediaUrls.get(i));
                        mediaPagerAdapter.notifyDataSetChanged();
                        circleIndicator.refreshIndicator();
                    }
                    catch (Exception ex)
                    {
                        Timber.e(ex.toString());
                    }
                }
                mediaPagerAdapter.notifyDataSetChanged();
            }

            Address address = user.getAddress();

            if(name != null && !name.isEmpty())
            {
                etProfileName.setText(name);
                getActivity().setTitle(name.trim());
            }

            if(email!=null && !email.isEmpty())
                etProfileEmail.setText(email);

            if(bio != null && !bio.isEmpty()) {
                etAboutMe.setText(bio);
                tvAboutMe.setText(bio);
            }
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
                multiSpSkills.setText(etSkills.getText().toString());
            }

            if(address != null) {
                this.address = address;
                etProfileAddress.setText(Address.getDisplayString(address));
                ImageUtils.setBlurredMapBackground(address, ivMaps);
            }

            Float avgRating = user.getAverageRating();
            int numRatings = user.getNumberRatings();
            if(avgRating >= 0) {
                SimpleRatingBar.AnimationBuilder builder =
                        srbProfileRating.getAnimationBuilder()
                        .setRatingTarget(avgRating)
                        .setDuration(2000)
                        .setInterpolator(new LinearInterpolator())
                        .setRepeatCount(0);
                builder.start();

                String ratingText = String.format("(%d)", numRatings);
                tvRating.setText(ratingText);
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
            etProfileAddress.setText(Address.getDisplayString(address));

            ImageUtils.setBlurredMapBackground(address, ivMaps);
        } catch (Exception ex) {
            Timber.e("User entered address could not be parsed");
        }
    }

    public void startEditProfile()
    {
        try
        {
            setEditModeFieldsState(true);

            mediaPagerAdapter.setAllowCapture(true);
            mediaPagerAdapter.notifyDataSetChanged();//to force capture controls to be displayed
            circleIndicator.refreshIndicator();

            setProfileSaveIcon();

        }
        catch (Exception ex)
        {
            Timber.e("There was an error starting edit profile", ex);
        }
    }

    boolean isConnectionAdded = false;
    public void startAddConnection()
    {
        try
        {
            isConnectionAdded = true;

            setRemoveConnectionIcon();
            TastyToast.makeText(getActivity(), "Added to your connections", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

            writeAddRemoveConnection(isConnectionAdded);
            if(onScreenUser.getConnections().contains(currentAuthorizedUId))
                setPrivateInfoVisibility(true);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting add connection", ex);
        }
    }

    public void startRemoveConnection()
    {
        try
        {
            isConnectionAdded = false;

            setAddConnectionIcon();
            TastyToast.makeText(getActivity(), "Removed from your connections", TastyToast.LENGTH_SHORT, TastyToast.INFO);

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

            if(isSetVisible) {
//                etProfileEmail.setVisibility(View.VISIBLE);
//                ibProfileEmail.setVisibility(View.VISIBLE);
//                etContactNo.setVisibility(View.VISIBLE);
//                ibContactPhone.setVisibility(View.VISIBLE);

                llContactUserButtons.setVisibility(View.VISIBLE);

            }
            else
            {
//                etProfileEmail.setVisibility(View.GONE);
//                etContactNo.setVisibility(View.GONE);
//                ibContactPhone.setVisibility(View.GONE);
//                ibProfileEmail.setVisibility(View.GONE);

                llContactUserButtons.setVisibility(View.GONE);
            }

        }
        catch (Exception ex)
        {
            Timber.e("Error setting visibility of private information");
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

                            if(!currentAuthorizedUId.equals(onScreenUserId)) { //If returned user id is not the authorized user's id
                                try {
                                    if (authUserconnections.contains(onScreenUserId)) //if onscreen user is in current authorized user's authUserconnections
                                    {
                                        setRemoveConnectionIcon();

                                    } else {
                                        setAddConnectionIcon();
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


//    @OnClick(R.id.ibProfileSave)
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
                TastyToast.makeText(getActivity(), "User data has been saved!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
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

            String address = etProfileAddress.getText().toString();

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
                etProfileAddress.setError("Address is required");
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
            List<String> rating = authUserData.getRating(); //rbUserRating.getRating();
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
                    TastyToast.makeText(getActivity(), "No camera on device", TastyToast.LENGTH_SHORT, TastyToast.DEFAULT);
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
                    TastyToast.makeText(getActivity(), "Picture waas not taken!", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
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
                    TastyToast.makeText(getActivity(), "Failed to record video", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
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
                    TastyToast.makeText(getActivity(), "File failed to upload", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
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
        startFullScreenMedia(pageIndex);
    }


    public void startFullScreenMedia(int pageIndex)
    {
        try
        {
            Intent intent = new Intent(getActivity(), MediaFullScreenActivity.class);
            intent.putExtra("urls", fileLocalUris);
            intent.putExtra("pageIndex", pageIndex);
            if(profileMode == ProfileActivity.ProfileMode.ME_CREATE || profileMode == ProfileActivity.ProfileMode.ME_EDIT)
            {
                intent.putExtra("isEditMode", true); //actually means iscreate/isedit mode
            }
            startActivity(intent);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting full screen media");
        }
    }

}
