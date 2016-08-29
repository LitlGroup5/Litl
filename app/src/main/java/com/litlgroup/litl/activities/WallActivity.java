package com.litlgroup.litl.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.PlaceBidFragment;
import com.litlgroup.litl.fragments.TaskOffersFragment;
import com.litlgroup.litl.fragments.TaskProposalFragment;
import com.litlgroup.litl.fragments.WallFragment;
import com.litlgroup.litl.model.User;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class WallActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    private String mUsername;
    private String mPhotoUrl;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//
//        if (mFirebaseUser == null) {
//            // Not signed in, launch the Sign In activity
//            startActivity(new Intent(this, SignInActivity.class));
//            finish();
//            return;
//        } else {
//            mUsername = mFirebaseUser.getDisplayName();
//            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
//
//            Map<String, Object> userDetails = new HashMap<>();
//            userDetails.put("email", mFirebaseUser.getEmail());
//            userDetails.put("name", mFirebaseUser.getDisplayName());
//            userDetails.put("photo", mFirebaseUser.getPhotoUrl().toString());
//
//            mDatabase.child("Users").child(mFirebaseUser.getUid()).setValue(userDetails);
//        }
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* WallActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API)
//                .build();


        ButterKnife.bind(this);

        setupNavigationDrawerLayout();
        loadFragmentIntoFrameLayout(new WallFragment());
    }

    private void setupNavigationDrawerLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(navigationView);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
        setupHeaderDrawerLayout(headerLayout);
    }

    private void setupHeaderDrawerLayout(View headerLayout) {
        User user = User.getFakeUser();

        ImageView ivProfileImage = (ImageView) headerLayout.findViewById(R.id.ivProfileImage_header);
        Glide.with(this).load(user.getProfileImageURL()).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivProfileImage);

        TextView userName = (TextView) headerLayout.findViewById(R.id.userName);
        String fullName = user.getFirstName() + user.getLastName();
        userName.setText(fullName);

        TextView email = (TextView) headerLayout.findViewById(R.id.tvUserEmail);
        email.setText(user.getEmail());

        TextView cityState = (TextView) headerLayout.findViewById(R.id.userCityState);
        cityState.setText("San Diego, CA");
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void loadFragmentIntoFrameLayout(Fragment displayFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, displayFragment).commit();
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_create_Task:
                launchCreateTaskActivity();
                return;
            case R.id.nav_bookmarks:
                Toast.makeText(WallActivity.this, "Bookmarks is coming!", Toast.LENGTH_SHORT).show();
                return;
            case R.id.nav_profile:
                Toast.makeText(WallActivity.this, "User Profile is coming!", Toast.LENGTH_SHORT).show();
                return;
            case R.id.nav_history:
                Toast.makeText(WallActivity.this, "History is coming!", Toast.LENGTH_SHORT).show();
                return;
            case R.id.nav_settings:
                Toast.makeText(WallActivity.this, "Settings is coming!", Toast.LENGTH_SHORT).show();
                return;
            case R.id.nav_logout:
                Toast.makeText(WallActivity.this, "Logout just needs t be plugged up!", Toast.LENGTH_SHORT).show();
                return;
            default:
                fragment = WallFragment.newInstance(menuItem.toString());
        }

        // Insert the fragment by replacing any existing fragment
        loadFragmentIntoFrameLayout(fragment);

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);

        // Set action bar title
        setTitle(menuItem.getTitle());

        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    public void launchCreateTaskActivity() {
        Intent intent = new Intent(WallActivity.this, CreateTaskActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wall, menu);
        MenuItem spinnerItem = menu.findItem(R.layout.spinner_category);
        Spinner categorySpinner = (Spinner) MenuItemCompat.getActionView(spinnerItem);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Timber.d("spinner selected, gotta get string");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.d("onConnectionFailed:" + connectionResult);
    }
}
