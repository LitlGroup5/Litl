package com.litlgroup.litl.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.BookmarksFragment;
import com.litlgroup.litl.fragments.CategorySelectionFragment;
import com.litlgroup.litl.fragments.WallFragment;
import com.litlgroup.litl.models.Address;
import com.litlgroup.litl.models.Notifications;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.ImageUtils;
import com.sdsmdg.tastytoast.TastyToast;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindColor;
import butterknife.ButterKnife;
import timber.log.Timber;

public class WallActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, CategorySelectionFragment.BottomSheetFragmentListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView ivProfileImage;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private static String BOOKMARKS_TITLE = "Bookmarks";
    private static String ALL_CATEGORIES_TITLE = "All Categories";
    private GoogleApiClient mGoogleApiClient;

    int selectedCategoryIndex = 0;

    @BindColor(R.color.colorAccent)
    int mAccentColor;

    private ValueEventListener notificationListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Timber.d("Key: " + dataSnapshot.getKey());
            Timber.d("Value: " + String.valueOf(dataSnapshot.getValue()));

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Notifications notifications = snapshot.getValue(Notifications.class);
                notifications.setId(snapshot.getKey());

                getTask(notifications);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

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

        setupNotificationListener();

        returnToWallFragment(null);
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
        ivProfileImage = (ImageView) headerLayout.findViewById(R.id.ivProfileImage_header);
        ImageUtils.setCircularImage(ivProfileImage, mFirebaseUser.getPhotoUrl().toString());

        TextView userName = (TextView) headerLayout.findViewById(R.id.userName);
        String fullName = mFirebaseUser.getDisplayName();
        userName.setText(fullName);

        TextView email = (TextView) headerLayout.findViewById(R.id.tvUserEmail);
        email.setText(mFirebaseUser.getEmail());

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserProfileScreen();
            }
        });
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
        selectedCategoryIndex = categoryStrings.indexOf(selectedTitle);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        CharSequence categorySpinnerTitle = menuItem.getTitle();
        String selectedCategory = menuItem.toString();
        if (selectedCategory.equalsIgnoreCase("Home")) {
            selectedCategory = ALL_CATEGORIES_TITLE;
            categorySpinnerTitle = ALL_CATEGORIES_TITLE;
        }
        String toolbarTitle = "Litl";

        switch (menuItem.getItemId()) {
            case R.id.nav_bookmarks:
                fragment = BookmarksFragment.newInstance(ALL_CATEGORIES_TITLE);
                toolbarTitle = BOOKMARKS_TITLE;
                categorySpinnerTitle = ALL_CATEGORIES_TITLE;
                break;
            case R.id.nav_history:
                TastyToast.makeText(this, "History is coming!", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                return;
            case R.id.nav_settings:
                TastyToast.makeText(this, "Settings is coming!", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                return;
            case R.id.nav_logout:
                signOutDialog();
                return;
            default:
                fragment = WallFragment.newInstance(selectedCategory);
        }

        toolbar.setTitle(toolbarTitle);

        // Insert the fragment by replacing any existing fragment
        loadFragmentIntoFrameLayout(fragment);

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);

        // Set spinner category
        setSpinnerSelectedItem(categorySpinnerTitle);

        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    private void startUserProfileScreen() {
        try {
            String userId = mFirebaseUser.getUid();
            Intent intent = new Intent(WallActivity.this, ProfileActivity.class);
            intent.putExtra(getString(R.string.user_id), userId);
            intent.putExtra("profileMode", ProfileActivity.ProfileMode.ME_VIEW);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, (View)ivProfileImage ,"profile");

            startActivity(intent, options.toBundle());
        } catch (Exception ex) {
            Timber.e("Error launching user profile screen", ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wall, menu);

        return true;
    }

    private void returnToWallFragment(String selectedCategory) {
        if (selectedCategory != null && selectedCategory.equalsIgnoreCase("All Categories")) {
            selectedCategory = null;
        }

        loadFragmentIntoFrameLayout(WallFragment.newInstance(selectedCategory));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.spinner_wall: {
                CategorySelectionFragment myDialog = CategorySelectionFragment.newInstance(selectedCategoryIndex);
                myDialog.setBottomSheetFragmentListener(this);

                FragmentManager fm = getSupportFragmentManager();
                myDialog.show(fm, Constants.CATEGORY_FRAGMENT);
            }
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

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
        TastyToast.makeText(this, "Signed Out!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
    }

    public void signOutDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_signout, null);

        builder.setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        AlertDialog dialog = builder.create();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    Button negativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                    Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                    negativeButton.setTextColor(mAccentColor);
                    positiveButton.setTextColor(mAccentColor);
                }
            });
        }

        dialog.show();
    }

    private void getTask(final Notifications notifications) {
        mDatabase.child(Constants.TABLE_TASKS).child(notifications.getTaskId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);

                Timber.d("Notification: " + task.getTitle());

                showNotification(notifications, task);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupNotificationListener() {
        String uid = mFirebaseUser.getUid();

        mDatabase.child(Constants.TABLE_NOTIFICATIONS).orderByChild(uid)
                .equalTo(true)
                .addValueEventListener(notificationListener);
    }

    private void showNotification(Notifications notifications, Task task) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        task.setId(notifications.getTaskId());
        task.setType(Task.Type.OFFER);
        intent.putExtra(Constants.SELECTED_TASK, Parcels.wrap(task));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TaskDetailActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent detailPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel: 8587768432"));
        PendingIntent callPendingIntent = PendingIntent.getActivity(this, 1, callIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Address.getMapAddress(task.getAddress()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        PendingIntent mapPendingIntent = PendingIntent.getActivity(this, 2, mapIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action callAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_phone, "Call", callPendingIntent)
                .build();

        NotificationCompat.Action mapAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_person_location, "Directions", mapPendingIntent)
                .build();

        String state = "rejected";

        if (notifications.getAccepted())
            state = "accepted";

        NotificationCompat.Builder n  = new NotificationCompat.Builder(this)
                .setContentTitle("Offer " + state)
                .setContentText("Your offer for Litl task " + task.getTitle() + " has been " + state)
                .setSmallIcon(R.drawable.proposals_icon)
                .setContentIntent(detailPendingIntent)
                .setColor(mAccentColor)
                .addAction(callAction)
                .addAction(mapAction)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n.build());

        mDatabase.child(Constants.TABLE_NOTIFICATIONS).child(notifications.getId()).removeValue();
    }

    @Override
    public void onClick(int position, String category) {
        selectedCategoryIndex = position;

        if (toolbar.getTitle().toString().equalsIgnoreCase(BOOKMARKS_TITLE)) {
            loadFragmentIntoFrameLayout(BookmarksFragment.newInstance(category));
        } else {
            returnToWallFragment(category);
        }
    }
}