package com.litlgroup.litl.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.FullScreenMediaFragment;

import java.util.ArrayList;
import java.util.List;

public class MediaFullScreenActivity extends AppCompatActivity {

    List<String> mediaUrls;

    boolean isEditMode;
    FullScreenMediaFragment fullScreenMediaFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_media_full_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mediaUrls = (List<String>) getIntent().getExtras().get("urls");

        isEditMode = getIntent().getBooleanExtra("isEditMode", false);

        if(savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            fullScreenMediaFragment
                    = FullScreenMediaFragment.newInstance(mediaUrls, isEditMode);

            ft.replace(R.id.flFullScreenMedia, fullScreenMediaFragment);
            ft.commit();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        fullScreenMediaFragment = (FullScreenMediaFragment)getSupportFragmentManager().findFragmentById(R.id.flFullScreenMedia);
        fullScreenMediaFragment.swipe.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putStringArrayList("mediaUrls", (ArrayList)mediaUrls);
        outState.putBoolean("isEditMode", isEditMode);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        mediaUrls = savedInstanceState.getStringArrayList("mediaUrls");
        isEditMode = savedInstanceState.getBoolean("isEditMode");
    }

}
