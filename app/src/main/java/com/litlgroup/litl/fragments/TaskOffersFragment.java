package com.litlgroup.litl.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;
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
import com.litlgroup.litl.activities.BidSelectScreenActivity;
import com.litlgroup.litl.activities.MediaFullScreenActivity;
import com.litlgroup.litl.activities.ProfileActivity;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.AdvancedMediaPagerAdapter;
import com.litlgroup.litl.utils.CircleIndicator;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.ImageUtils;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class TaskOffersFragment
        extends Fragment
        implements AdvancedMediaPagerAdapter.StartOnItemViewClickListener
{

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvPostedDate)
    TextView mTvPostedDate;
    @BindView(R.id.tvPrice)
    TextView mTvPrice;
    @BindView(R.id.tvDescription)
    TextView mTvDescription;
    @BindView(R.id.tvViewedBy)
    TextView mTvViewedBy;
    @BindView(R.id.tvViewedByCount)
    TextView mTvViewedByCount;
    @BindView(R.id.tvBidByCount)
    TextView mTvBidByCount;
    @BindView(R.id.tvBidBy)
    TextView mTvBidBy;
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

    @BindColor(android.R.color.transparent)
    int mTransparent;
    @BindColor(R.color.colorAccent)
    int accentColor;
    @BindColor(R.color.colorPrimaryDark)
    int mPrimaryDark;
    @BindColor(R.color.colorPrimary)
    int mColorPrimary;

    private String mTaskId;

    private DatabaseReference mDatabase;

    AdvancedMediaPagerAdapter mMediaPagerAdapter;


    private CircleIndicator mCircleIndicator;

    private Task mTask;

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Timber.d("Key: " + dataSnapshot.getKey());
            Timber.d("Value: " + String.valueOf(dataSnapshot.getValue()));

            Task task = dataSnapshot.getValue(Task.class);
            setData(task);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public TaskOffersFragment() {
    }

    public static TaskOffersFragment newInstance(String taskid) {
        TaskOffersFragment fragment = new TaskOffersFragment();

        Bundle args = new Bundle();
        args.putString(Constants.TASK_ID, taskid);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mTaskId = getArguments().getString(Constants.TASK_ID);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task_offers, container, false);
        ButterKnife.bind(this, view);

        mCollapsingToolbar.setExpandedTitleColor(mTransparent);
        mCollapsingToolbar.setContentScrimColor(mColorPrimary);
        mCollapsingToolbar.setStatusBarScrimColor(mPrimaryDark);

        initToolbar();
        setupViewPager();

        getTaskData();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_offers_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getTaskData() {
        mDatabase.child(Constants.TABLE_TASKS).child(mTaskId).addValueEventListener(valueEventListener);
    }

    private void setData(Task task) {

        if (task != null) {
            if (task.getTitle() != null)
                mTvTitle.setText(task.getTitle());

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
                for (String url : task.getMedia()) {
                    mMediaPagerAdapter.addImage(url);
                    mMediaPagerAdapter.notifyDataSetChanged();
                }

                mCircleIndicator.refreshIndicator();
            }
        }
    }

    private void setupViewPager() {
        mMediaPagerAdapter = new AdvancedMediaPagerAdapter(getActivity(), false, true, this);
        mVpMedia.setAdapter(mMediaPagerAdapter);
        mCircleIndicator = new CircleIndicator(mViewPagerCountDots, mVpMedia);
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Glide.clear(mIvProfileImage);
    }

    @OnClick(R.id.btnBidNow)
    public void bidNow() {
        Timber.d("Bid Now Clicked");

        PlaceBidFragment myDialog = PlaceBidFragment.newInstance(mTaskId, mTask.getBidBy());

        FragmentManager fm = getActivity().getSupportFragmentManager();
        myDialog.show(fm, Constants.BID_NOW_FRAGMENT);
    }

    @OnClick({R.id.tvBidBy, R.id.tvBidByCount})
    public void bidBy() {
        Intent i = new Intent(getActivity(), BidSelectScreenActivity.class);
        i.putExtra(Constants.TASK_ID, mTaskId);
        startActivity(i);
    }

    @OnClick(R.id.ivProfileImage)
    public void startUserProfileScreen()
    {
        try
        {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra(getString(R.string.user_id), "GiJcFd59PlMK31bV17w6qV0GDn93");
            intent.putExtra("profileMode", ProfileActivity.ProfileMode.OTHER);

            startActivity(intent);
        }
        catch (Exception ex)
        {
            Timber.e("Error launching user profile screen",ex);
        }
    }

    public void startFullScreenMedia()
    {
        try
        {
            Intent intent = new Intent(getActivity(), MediaFullScreenActivity.class);
            intent.putExtra("urls", (ArrayList)mMediaPagerAdapter.getUrls());
            startActivity(intent);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting full screen media");
        }
    }


    @Override
    public void onStartItemViewClicked(int pageIndex) {
        try
        {
            startFullScreenMedia();
        }
        catch (Exception ex)
        {
            Timber.e("Error launching full screen media", ex);
        }
    }
}
