package com.litlgroup.litl.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.ProfileHeaderFragment;

import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    public enum ProfileMode { ME_CREATE, ME_VIEW, ME_EDIT, CONNECTION, OTHER }

    ProfileMode profileMode;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String currentAuthorizedUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userId = getIntent().getStringExtra(getString(R.string.user_id));
        profileMode =(ProfileMode) getIntent().getExtras().get("profileMode");

        if(currentAuthorizedUId.equals(userId))
            profileMode = ProfileMode.ME_VIEW;
        else
        {
            if(profileMode == ProfileMode.ME_VIEW) //if incorrectly set
                profileMode = ProfileMode.OTHER;
        }

        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ProfileHeaderFragment fragmentProfileHeader
                    = ProfileHeaderFragment
                    .newInstance(userId, profileMode);
            ft.replace(R.id.flProfileHeader, fragmentProfileHeader);

            ft.commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
