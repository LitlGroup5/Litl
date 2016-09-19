package com.litlgroup.litl.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.R;
import com.litlgroup.litl.activities.MediaFullScreenActivity;
import com.litlgroup.litl.activities.ProfileActivity;
import com.litlgroup.litl.interfaces.OnBackPressedListener;
import com.litlgroup.litl.models.Address;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.AdvancedMediaPagerAdapter;
import com.litlgroup.litl.utils.AppBarStateChangeListener;
import com.litlgroup.litl.utils.CircleIndicator;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.DateUtils;
import com.litlgroup.litl.utils.ImageUtils;
import com.litlgroup.litl.utils.ZoomOutPageTransformer;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

public class TaskOffersFragment
        extends Fragment
        implements AdvancedMediaPagerAdapter.StartOnItemViewClickListener, OnBackPressedListener {

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvPostedDate)
    TextView mTvPostedDate;
    @BindView(R.id.tvPrice)
    TextView mTvPrice;
    @BindView(R.id.tvDescription)
    TextView mTvDescription;
    @BindView(R.id.ivViewedBy)
    ImageView mTvViewedBy;
    @BindView(R.id.tvViewedByCount)
    TextView mTvViewedByCount;
    @BindView(R.id.tvBidByCount)
    TextView mTvBidByCount;
    @BindView(R.id.ivBidBy)
    ImageView mTvBidBy;
    @BindView(R.id.vpMedia)
    ViewPager mVpMedia;
    @BindView(R.id.ivProfileImage)
    ImageView mIvProfileImage;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.vpIndicator)
    LinearLayout mViewPagerCountDots;
    @BindView(R.id.ivMaps)
    ImageView mIvMaps;
    @BindView(R.id.btnBidNow)
    Button mBtnBidNow;

    @BindColor(android.R.color.transparent)
    int mTransparent;
    @BindColor(R.color.colorAccent)
    int accentColor;
    @BindColor(R.color.colorPrimaryDark)
    int mPrimaryDark;
    @BindColor(R.color.colorPrimary)
    int mColorPrimary;
    @BindView(R.id.view)
    AppBarLayout mAppBarLayout;

    private Unbinder unbinder;

    private DatabaseReference mDatabase;

    AdvancedMediaPagerAdapter mMediaPagerAdapter;

    private CircleIndicator mCircleIndicator;

    private Task mTask;

    private Menu mMenu;

    private Transition.TransitionListener mEnterTransitionListener;

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
//            Timber.d("Key: " + dataSnapshot.getKey());
//            Timber.d("Value: " + String.valueOf(dataSnapshot.getValue()));

            mTask = dataSnapshot.getValue(Task.class);
            mTask.setId(dataSnapshot.getKey());

            setData(mTask);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public TaskOffersFragment() {
    }

    public static TaskOffersFragment newInstance(Task task) {
        TaskOffersFragment fragment = new TaskOffersFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.TASK, Parcels.wrap(task));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_bookmark: {
                updateBookmark();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mTask = Parcels.unwrap(getArguments().getParcelable(Constants.TASK));
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task_offers, container, false);
        unbinder = ButterKnife.bind(this, view);

        mCollapsingToolbar.setExpandedTitleColor(mTransparent);
        mCollapsingToolbar.setContentScrimColor(mPrimaryDark);
        mCollapsingToolbar.setStatusBarScrimColor(mPrimaryDark);
        mCollapsingToolbar.setTitleEnabled(false);

        initToolbar();
        setupViewPager();

        getTaskData();

        setupTransitionListener();

        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State lastState, State currentState) {
                Timber.d("STATE " +  currentState.name() + " " + lastState.name());
                if (currentState == State.COLLAPSED) {
                    mToolbar.setTitle(mTask.getTitle());
                }
                else if (currentState == State.EXPANDED) {
                    mToolbar.setTitle("");
                    enterReveal();
                } else if (currentState == State.IDLE) {
                    if (lastState == State.EXPANDED)
                        exitReveal();
                    else if (lastState == State.COLLAPSED)
                        mToolbar.setTitle("");
                }

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_offers_menu, menu);

        mMenu = menu;

        initBookmark();

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getTaskData() {
        mDatabase.child(Constants.TABLE_TASKS).child(mTask.getId()).addValueEventListener(valueEventListener);
    }

    private void setData(Task task) {

        if (task != null && unbinder != null) {

            if (task.getTitle() != null)
                mTvTitle.setText(task.getTitle());

            if (task.getDeadlineDate() != null) {
                mTvPostedDate.setText(DateUtils.getRelativeTimeAgo(task.getDeadlineDate()));
            }

            if (task.getAddress() != null)
                ImageUtils.setMapBackground(task.getAddress(), mIvMaps);

            if (task.getDescription() != null)
                mTvDescription.setText(task.getDescription());

            if (task.getUser() != null && task.getUser().getPhoto() != null)
                ImageUtils.setCircularImage(mIvProfileImage, task.getUser().getPhoto());

            if (task.getBidBy() != null)
                mTvBidByCount.setText(String.valueOf(task.getBidBy()));
            else
                mTvBidByCount.setText("0");

            if (task.getViewedBy() != null)
                mTvViewedByCount.setText(String.valueOf(task.getViewedBy()));
            else
                mTvViewedByCount.setText("0");

            if (task.getPrice() != null)
                mTvPrice.setText(task.getPrice());

            if (task.getMedia() != null) {
                mMediaPagerAdapter.removeAll();

                for (String url : task.getMedia()) {
                    mMediaPagerAdapter.addImage(url);
                    mMediaPagerAdapter.notifyDataSetChanged();
                }

                mCircleIndicator.refreshIndicator();
            }

            if (task.getStatus() != null &&
                    task.getStatus().equals(Task.State.SUCCESSFULLY_ACCEPTED.toString())
                    &&
                    task.getStatus().equals(Task.State.COMPLETE.toString())
                    ) {
                mBtnBidNow.setEnabled(false);
            }
        }
    }

    private void setupViewPager() {
        mMediaPagerAdapter = new AdvancedMediaPagerAdapter(getActivity(), false, true, this);
        mVpMedia.setAdapter(mMediaPagerAdapter);
        mVpMedia.setPageTransformer(true, new ZoomOutPageTransformer());
        mCircleIndicator = new CircleIndicator(mViewPagerCountDots, mVpMedia);
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        mToolbar.setTitle("");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Glide.clear(mIvProfileImage);

        mDatabase.removeEventListener(valueEventListener);
        valueEventListener = null;

        unbinder.unbind();
        unbinder = null;

        mMediaPagerAdapter = null;
        mCircleIndicator = null;

        mMenu = null;
    }

    @OnClick(R.id.btnBidNow)
    public void bidNow() {
        Timber.d("Bid Now Clicked");

        PlaceBidFragment myDialog = PlaceBidFragment.newInstance(mTask.getId(), mTask.getBidBy());

        FragmentManager fm = getActivity().getSupportFragmentManager();
        myDialog.show(fm, Constants.BID_NOW_FRAGMENT);
    }

    @OnClick(R.id.ivProfileImage)
    public void startUserProfileScreen() {
        try {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra(getString(R.string.user_id), mTask.getUser().getId());
            intent.putExtra("profileMode", ProfileActivity.ProfileMode.OTHER);

            startActivity(intent);
        } catch (Exception ex) {
            Timber.e("Error launching user profile screen", ex);
        }
    }

    @OnClick(R.id.ivMaps)
    public void launchMaps() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Address.getMapAddress(mTask.getAddress()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void startFullScreenMedia(int pageIndex) {
        try {
            Intent intent = new Intent(getActivity(), MediaFullScreenActivity.class);
            intent.putExtra("urls", (ArrayList) mMediaPagerAdapter.getUrls());
            intent.putExtra("pageIndex", pageIndex);
            startActivity(intent);
        } catch (Exception ex) {
            Timber.e("Error starting full screen media");
        }
    }


    @Override
    public void onStartItemViewClicked(int pageIndex) {
        try {
            startFullScreenMedia(pageIndex);
        } catch (Exception ex) {
            Timber.e("Error launching full screen media", ex);
        }
    }

    private void initBookmark() {
        if (Task.isBookmarked(mTask)) {
            mMenu.getItem(0).setIcon(R.drawable.ic_menu_bookmark_filled);
        } else {
            mMenu.getItem(0).setIcon(R.drawable.ic_menu_bookmark);
        }
    }

    private void updateBookmark() {
        if (Task.isBookmarked(mTask)) {
            mMenu.getItem(0).setIcon(R.drawable.ic_menu_bookmark);
            Task.updateBookmark(mTask, false);
        } else {
            mMenu.getItem(0).setIcon(R.drawable.ic_menu_bookmark_filled);
            Task.updateBookmark(mTask, true);
        }
    }

    void enterReveal() {
        // previously invisible view
        final View myView = mIvProfileImage;

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        myView.setVisibility(View.VISIBLE);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getActivity().getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    void exitReveal() {
        // previously visible view
        final View myView = mIvProfileImage;

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    private void setupTransitionListener() {
        mEnterTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                enterReveal();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };
        getActivity().getWindow().getEnterTransition().addListener(mEnterTransitionListener);
    }

    @Override
    public void onBackPressed() {
        exitReveal();
    }
}
