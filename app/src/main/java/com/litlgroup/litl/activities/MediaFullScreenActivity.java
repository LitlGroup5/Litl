package com.litlgroup.litl.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.FullScreenMediaFragment;

import java.util.ArrayList;
import java.util.List;

public class MediaFullScreenActivity extends AppCompatActivity {

    List<String> mediaUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_media_full_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mediaUrls = (List<String>) getIntent().getExtras().get("urls");

        if(savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            FullScreenMediaFragment fullScreenMediaFragment
                    = FullScreenMediaFragment.newInstance(mediaUrls);

            ft.replace(R.id.flFullScreenMedia, fullScreenMediaFragment);
            ft.commit();

        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putStringArrayList("mediaUrls", (ArrayList)mediaUrls);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        mediaUrls = savedInstanceState.getStringArrayList("mediaUrls");
    }

}
