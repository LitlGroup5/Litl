package com.litlgroup.litl.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

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

        userId = getIntent().getStringExtra(getString(R.string.user_id));

        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ProfileHeaderFragment fragmentProfileHeader = ProfileHeaderFragment.newInstance(userId, ProfileMode.ME_VIEW);
            ft.replace(R.id.flProfileHeader, fragmentProfileHeader);

            ft.commit();
        }

    }
}
