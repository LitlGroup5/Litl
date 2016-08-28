package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.R;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.CircleIndicator;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.ImageUtils;
import com.litlgroup.litl.utils.MediaPagerAdapter;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class TaskProposalFragment extends Fragment {

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

    private MediaPagerAdapter mMediaPagerAdapter;

    private CircleIndicator mCircleIndicator;

    public TaskProposalFragment() {
    }

    public static TaskProposalFragment newInstance(String taskid) {
        TaskProposalFragment fragment = new TaskProposalFragment();

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

        View view = inflater.inflate(R.layout.fragment_task_proposal, container, false);
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
        inflater.inflate(R.menu.task_proposal_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getTaskData() {
        mDatabase.child(Constants.TABLE_TASKS).child(mTaskId).addValueEventListener(new ValueEventListener() {
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
        });
    }

    private void setData(Task task) {

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

    private void setupViewPager() {
        mMediaPagerAdapter = new MediaPagerAdapter(getActivity());
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
}
