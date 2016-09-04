package com.litlgroup.litl.fragments;

import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.R;
import com.litlgroup.litl.activities.BidSelectScreenActivity;
import com.litlgroup.litl.activities.CreateTaskActivity;
import com.litlgroup.litl.activities.MediaFullScreenActivity;
import com.litlgroup.litl.activities.ProfileActivity;
import com.litlgroup.litl.models.Address;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.AdvancedMediaPagerAdapter;
import com.litlgroup.litl.utils.CircleIndicator;
import com.litlgroup.litl.utils.Constants;
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

public class TaskProposalFragment
        extends Fragment
        implements AdvancedMediaPagerAdapter.StartOnItemViewClickListener {

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
    @BindView(R.id.tvLocation)
    TextView mTvLocation;

    @BindColor(android.R.color.transparent)
    int mTransparent;
    @BindColor(R.color.colorAccent)
    int accentColor;
    @BindColor(R.color.colorPrimaryDark)
    int mPrimaryDark;
    @BindColor(R.color.colorPrimary)
    int mColorPrimary;

    private Unbinder unbinder;

    private DatabaseReference mDatabase;

    private Task mTask;

    private Menu mMenu;

    private AdvancedMediaPagerAdapter mMediaPagerAdapter;

    private CircleIndicator mCircleIndicator;

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Timber.d("Key: " + dataSnapshot.getKey());
            Timber.d("Value: " + String.valueOf(dataSnapshot.getValue()));

            mTask = dataSnapshot.getValue(Task.class);
            mTask.setId(dataSnapshot.getKey());

            setData(mTask);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public TaskProposalFragment() {
    }

    public static TaskProposalFragment newInstance(Task task) {
        TaskProposalFragment fragment = new TaskProposalFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.TASK, Parcels.wrap(task));
        fragment.setArguments(args);

        return fragment;
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

        View view = inflater.inflate(R.layout.fragment_task_proposal, container, false);
        unbinder = ButterKnife.bind(this, view);

        mCollapsingToolbar.setExpandedTitleColor(mTransparent);
        mCollapsingToolbar.setContentScrimColor(mPrimaryDark);
        mCollapsingToolbar.setStatusBarScrimColor(mPrimaryDark);

        initToolbar();
        setupViewPager();

        getTaskData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Glide.clear(mIvProfileImage);

        unbinder.unbind();
        unbinder = null;

        mDatabase.removeEventListener(valueEventListener);
        valueEventListener = null;

        mMediaPagerAdapter = null;
        mCircleIndicator = null;

        mMenu = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete: {
                mDatabase.removeEventListener(valueEventListener);
                valueEventListener = null;

                mDatabase.child(Constants.TABLE_TASKS).child(mTask.getId()).removeValue();
                Toast.makeText(getActivity(), "Task Deleted", Toast.LENGTH_SHORT).show();
                getActivity().finish();

                break;
            }

            case R.id.action_edit: {
                Intent i = new Intent(getActivity(), CreateTaskActivity.class);
                i.putExtra(Constants.TASK_ID, mTask.getId());
                startActivity(i);

                break;
            }

            case R.id.action_bookmark: {
                updateBookmark();

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_proposal_menu, menu);
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

            if (task.getAddress() != null)
                mTvLocation.setText(Address.getDisplayString(task.getAddress()));

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

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick({R.id.ivBidBy, R.id.tvBidByCount})
    public void bidBy() {
        Intent i = new Intent(getActivity(), BidSelectScreenActivity.class);
        i.putExtra(Constants.TASK_ID, mTask.getId());
        startActivity(i);
    }

    @OnClick(R.id.ivProfileImage)
    public void startUserProfileScreen() {
        try {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra(getString(R.string.user_id), "GiJcFd59PlMK31bV17w6qV0GDn93");
            intent.putExtra("profileMode", ProfileActivity.ProfileMode.OTHER);

            startActivity(intent);
        } catch (Exception ex) {
            Timber.e("Error launching user profile screen", ex);
        }

    }

    public void startFullScreenMedia() {
        try {
            Intent intent = new Intent(getActivity(), MediaFullScreenActivity.class);
            intent.putExtra("urls", (ArrayList) mMediaPagerAdapter.getUrls());
            startActivity(intent);
        } catch (Exception ex) {
            Timber.e("Error starting full screen media");
        }
    }

    @Override
    public void onStartItemViewClicked(int pageIndex) {
        try {
            startFullScreenMedia();
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
}
