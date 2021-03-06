package com.litlgroup.litl.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
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
import com.litlgroup.litl.activities.CreateTaskActivity;
import com.litlgroup.litl.activities.MediaFullScreenActivity;
import com.litlgroup.litl.activities.ProfileActivity;
import com.litlgroup.litl.interfaces.OnBackPressedListener;
import com.litlgroup.litl.models.Address;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.models.UserSummary;
import com.litlgroup.litl.utils.AdvancedMediaPagerAdapter;
import com.litlgroup.litl.utils.AppBarStateChangeListener;
import com.litlgroup.litl.utils.CircleIndicator;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.DateUtils;
import com.litlgroup.litl.utils.ImageUtils;
import com.litlgroup.litl.utils.ZoomOutPageTransformer;
import com.sdsmdg.tastytoast.TastyToast;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

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
    @BindView(R.id.ivTaskCompletedIndicator)
    ImageView ivTaskCompletedIndicator;
    @BindView(R.id.view)
    AppBarLayout mAppBarLayout;

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

    private Transition.TransitionListener mEnterTransitionListener;

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
//            Timber.d("Key: " + dataSnapshot.getKey());
//            Timber.d("Value: " + String.valueOf(dataSnapshot.getValue()));

            if (dataSnapshot.getValue() != null) {
                mTask = dataSnapshot.getValue(Task.class);
                mTask.setId(dataSnapshot.getKey());
                setData(mTask);
            }
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
        mCollapsingToolbar.setTitleEnabled(false);
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

                showConfirmDeleteDialog();

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

            case R.id.action_mark_complete:
            {
                startMarkComplete();
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

        //hide mark-complete option if a bid has not been accepted yet

        if(mTask.getStatus().equals(Task.State.IN_BIDDING_PROCESS.toString())
            ||
                mTask.getStatus().equals(Task.State.COMPLETE.toString()))
        {
            setMarkCompleteOptionVisibility(false);

        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setMarkCompleteOptionVisibility(boolean isVisible)
    {
        try {
            if (mMenu != null) {
                MenuItem markComplete = mMenu.findItem(R.id.action_mark_complete);
                markComplete.setVisible(isVisible);
            }
        }
        catch (Exception ex)
        {
            Timber.e("Error setting visibility on mark complete menu option");
        }
    }

    private void showConfirmDeleteDialog() {

        new LovelyStandardDialog(getActivity())
                .setTopColorRes(android.R.color.holo_orange_dark)
                .setTopTitle("Are you sure?")
                .setTopTitleColor(Color.WHITE)
                .setButtonsColorRes(R.color.colorAccent)
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.removeEventListener(valueEventListener);

                        mDatabase.child(Constants.TABLE_TASKS).child(mTask.getId()).removeValue();
                        TastyToast.makeText(getActivity(), "The task has been deleted", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                        Intent data = new Intent();
                        data.putExtra("isDeleted", true);
                        data.putExtra("Task", Parcels.wrap(mTask));
                        getActivity().setResult(getActivity().RESULT_OK, data);
                        getActivity().finish();

                        ivTaskCompletedIndicator.setVisibility(View.VISIBLE);

                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
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

            if(task.getStatus().equals(Task.State.IN_BIDDING_PROCESS.toString())
                    ||
                    task.getStatus().equals(Task.State.COMPLETE.toString()))
            {
                setMarkCompleteOptionVisibility(false);

            }
            else
            {
                setMarkCompleteOptionVisibility(true);
            }

            if(task.getStatus().equals(Task.State.COMPLETE.toString()))
            {
                ivTaskCompletedIndicator.setVisibility(View.VISIBLE);
            }
            else
            {
                ivTaskCompletedIndicator.setVisibility(View.INVISIBLE);
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

    @OnClick(R.id.btnViewBids)
    public void bidBy() {
        Intent i = new Intent(getActivity(), BidSelectScreenActivity.class);
        i.putExtra(Constants.TASK_ID, mTask.getId());
        startActivity(i);
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


    private void startMarkComplete()
    {
        try
        {
            new LovelyStandardDialog(getActivity())
                    .setTopColorRes(android.R.color.holo_orange_light)
                    .setTopTitle("Are you sure?")
                    .setTopTitleColor(Color.WHITE)
                    .setButtonsColorRes(R.color.colorAccent)
                    .setMessage("Are you sure you want to mark this task complete?")
                    .setPositiveButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String status = Task.State.COMPLETE.toString();
                            mDatabase.child(Constants.TABLE_TASKS)
                                    .child(mTask.getId())
                                    .child(getString(R.string.task_status_child))
                                    .setValue(status);

                            setMarkCompleteOptionVisibility(false);

                            mDatabase.child(Constants.TABLE_BIDS)
                                    .child(mTask.getAcceptedOfferId())
                                    .child(Constants.TABLE_TASKS_COLUMN_USER)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            UserSummary userSummary = dataSnapshot.getValue(UserSummary.class);

                                            FragmentManager fm = getActivity().getSupportFragmentManager();
                                            TaskMarkedCompleteFragment taskMarkedCompleteFragment  =
                                                    TaskMarkedCompleteFragment.newInstance(userSummary);
                                            taskMarkedCompleteFragment.show(fm, "fragment_rating_radar");


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
        catch (Exception ex)
        {
            Timber.e("Error starting to mark complete");
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
