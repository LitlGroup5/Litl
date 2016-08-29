package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.R;
import com.litlgroup.litl.activities.ProfileActivity;
import com.litlgroup.litl.models.Address;
import com.litlgroup.litl.models.User;
import com.litlgroup.litl.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Hari on 8/28/2016.
 */
public class ProfileHeaderFragment
        extends Fragment
    implements AddressFragment.AddressFragmentListener
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

    @BindView(R.id.tvProfileAddress)
    TextView tvProfileAddress;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    DatabaseReference mDatabase;
    String userId;
    ProfileActivity.ProfileMode profileMode;
    Address address;


    public static ProfileHeaderFragment newInstance(String userId, ProfileActivity.ProfileMode profileMode) {
        Bundle args = new Bundle();
        ProfileHeaderFragment fragment = new ProfileHeaderFragment();
        fragment.userId = userId;
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
        profileModeLayoutChanges();
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

        try {
            getUserData();
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
                    ibProfileEdit.setVisibility(View.INVISIBLE);
                    ibProfileSave.setVisibility(View.VISIBLE);
                    ibAddConnection.setVisibility(View.INVISIBLE);
                    setEditModeFieldsState(true);
                    break;
                case ME_VIEW:
                    ibProfileEdit.setVisibility(View.VISIBLE);
                    ibProfileSave.setVisibility(View.INVISIBLE);
                    ibAddConnection.setVisibility(View.INVISIBLE);
                    setEditModeFieldsState(false);
                    break;
                case ME_EDIT:
                    ibProfileEdit.setVisibility(View.INVISIBLE);
                    ibProfileSave.setVisibility(View.VISIBLE);
                    ibAddConnection.setVisibility(View.INVISIBLE);
                    setEditModeFieldsState(true);
                    break;
                case CONNECTION:
                    ibProfileEdit.setVisibility(View.INVISIBLE);
                    ibProfileSave.setVisibility(View.INVISIBLE);
                    ibAddConnection.setVisibility(View.INVISIBLE);
                    setEditModeFieldsState(false);
                    break;
                case OTHER:
                    ibProfileEdit.setVisibility(View.INVISIBLE);
                    ibProfileSave.setVisibility(View.INVISIBLE);
                    ibAddConnection.setVisibility(View.VISIBLE);
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
            etProfileName.setFocusable(isEditMode);
            etProfileEmail.setFocusable(isEditMode);

            etAboutMe.setFocusable(isEditMode);
            etSkills.setFocusable(isEditMode);

            if(isEditMode)
                ibAddConnection.setVisibility(View.INVISIBLE);

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
                    .child(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                User user = dataSnapshot.getValue(User.class);
                                populateUserData(user);
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
            if(user.getMedia() != null)
                media = (ArrayList<String>)user.getMedia();

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



}
