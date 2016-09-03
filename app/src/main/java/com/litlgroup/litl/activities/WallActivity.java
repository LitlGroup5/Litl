package com.litlgroup.litl.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.WallFragment;
import com.litlgroup.litl.utils.ImageUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import timber.log.Timber;

public class WallActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private Spinner categorySpinner;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("email", mFirebaseUser.getEmail());
            userDetails.put("name", mFirebaseUser.getDisplayName());
            userDetails.put("photo", mFirebaseUser.getPhotoUrl().toString());

            mDatabase.child("Users").child(mFirebaseUser.getUid())
                    .updateChildren(userDetails);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* WallActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        ButterKnife.bind(this);
        setupNavigationDrawerLayout();
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
        headerLayout.setBackgroundResource(R.drawable.wood);
    }

    private void setupHeaderDrawerLayout(View headerLayout) {
        ImageView ivProfileImage = (ImageView) headerLayout.findViewById(R.id.ivProfileImage_header);
        ImageUtils.setCircularImage(ivProfileImage, mFirebaseUser.getPhotoUrl().toString());

        TextView userName = (TextView) headerLayout.findViewById(R.id.userName);
        String fullName = mFirebaseUser.getDisplayName();
        userName.setText(fullName);

        TextView email = (TextView) headerLayout.findViewById(R.id.tvUserEmail);
        email.setText(mFirebaseUser.getEmail());

        TextView cityState = (TextView) headerLayout.findViewById(R.id.userCityState);
            cityState.setText("need getAddress method");
        // need to be able to get city and state for user
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

    private void setSpinnerSelectedItem(CharSequence title) {
        String selectedTitle = (String) title;

        List<String> categoryStrings = Arrays.asList(getResources().getStringArray(R.array.categories_array_values));
        int selectedCategoryIndex = categoryStrings.indexOf(selectedTitle);
        categorySpinner.setSelection(selectedCategoryIndex);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_bookmarks:
                startActivity(new Intent(this, BookmarksActivity.class));
                return;
            case R.id.nav_history:
                Toast.makeText(WallActivity.this, "History is coming!", Toast.LENGTH_SHORT).show();
                return;
            case R.id.nav_settings:
                Toast.makeText(WallActivity.this, "Settings is coming!", Toast.LENGTH_SHORT).show();
                return;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                Toast.makeText(WallActivity.this, "Signed Out!", Toast.LENGTH_SHORT).show();
                return;
            default:
                fragment = WallFragment.newInstance(menuItem.toString());
        }

        // Insert the fragment by replacing any existing fragment
        loadFragmentIntoFrameLayout(fragment);

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);

        // Set spinner category
        setSpinnerSelectedItem(menuItem.getTitle());

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
        MenuItem spinnerItem = menu.findItem(R.id.spinner_wall);
        categorySpinner = (Spinner) MenuItemCompat.getActionView(spinnerItem);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                if (selectedCategory.equalsIgnoreCase("All Categories")) {
                    selectedCategory = null;
                }
                loadFragmentIntoFrameLayout(WallFragment.newInstance(selectedCategory));
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