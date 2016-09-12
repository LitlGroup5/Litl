package com.litlgroup.litl.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.litlgroup.litl.R;
import com.litlgroup.litl.adapters.TaskRecycleAdapter;
import com.litlgroup.litl.behaviors.EndlessRecyclerViewScrollListener;
import com.litlgroup.litl.interfaces.InfiniteScrollListener;
import com.litlgroup.litl.interfaces.SwipeToRefreshListener;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.SpacesItemDecoration;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {

    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Task> tasks;
    public SwipeRefreshLayout swipeContainer;
    private RecyclerView rvTasks;

    public TaskRecycleAdapter taskRecycleAdapter;
    public String chosenCategory;
    public InfiniteScrollListener infiniteScrollListener;
    public SwipeToRefreshListener swipeToRefreshListener;

    public static String NO_TASKS_MESSAGE = "No Tasks in this category. You should tap the blue button and create one";
    public TaskFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
            setUpRecycleView(view);
            setupSwipeToRefresh(view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasks = new ArrayList<>();
        taskRecycleAdapter = new TaskRecycleAdapter(tasks);
    }

    public void addMoreTasksForEndlessScrolling(ArrayList<Task> moreTasks) {
        int startingPoint = tasks.size();
        Collections.reverse(moreTasks);
        tasks.addAll(moreTasks);
        taskRecycleAdapter.notifyItemRangeChanged(startingPoint, moreTasks.size());
    }

    public void addAllNewTasksForRefresh(ArrayList<Task> newTasks) {
        Collections.reverse(newTasks);
        tasks.addAll(0, newTasks);
        taskRecycleAdapter.notifyItemRangeInserted(0, newTasks.size() - 1);

        if (newTasks.size() > 0) {
            linearLayoutManager.scrollToPosition(0);
        }
    }

    public void removeTaskWithAnimation(int position) {
        tasks.remove(position);
        taskRecycleAdapter.notifyItemRemoved(position);
    }

    public void setUpRecycleView(View v) {
        rvTasks = (RecyclerView) v.findViewById(R.id.taskRecycleView);
        implementRecyclerViewAnimations();

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvTasks.setLayoutManager(linearLayoutManager);

        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        rvTasks.addItemDecoration(decoration);

        rvTasks.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                infiniteScrollListener.userScrolledPastBenchmark(TaskFragment.this, totalItemsCount - 1);
            }
        });
    }

    public void displayNoTasksView(String noTaskMessage) {
        TastyToast.makeText(getActivity(), noTaskMessage, TastyToast.LENGTH_SHORT, TastyToast.DEFAULT);
    }

    private void implementRecyclerViewAnimations() {
        SlideInBottomAnimationAdapter animator = new SlideInBottomAnimationAdapter(taskRecycleAdapter);
        animator.setDuration(1000);
        animator.setFirstOnly(false);
        animator.setInterpolator(new OvershootInterpolator(1f));

        AlphaInAnimationAdapter alphaAnimator = new AlphaInAnimationAdapter(animator);
        alphaAnimator.setFirstOnly(false);
        alphaAnimator.setDuration(600);

        rvTasks.setAdapter(new ScaleInAnimationAdapter(alphaAnimator));

    }



    private void implementRecyclerViewAnimationsOld() {

        int animationTime = 500;
        //RecyclerView Animations from Library https://github.com/wasabeef/recyclerview-animators
        rvTasks.setItemAnimator(new FlipInTopXAnimator(new OvershootInterpolator()));
        rvTasks.getItemAnimator().setAddDuration(animationTime);
        rvTasks.getItemAnimator().setRemoveDuration(animationTime);
        rvTasks.getItemAnimator().setMoveDuration(animationTime);
        rvTasks.getItemAnimator().setChangeDuration(animationTime);

        ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(taskRecycleAdapter);
        scaleInAdapter.setDuration(1500);
        rvTasks.setAdapter(scaleInAdapter);
    }

    public void setupSwipeToRefresh(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeToRefreshListener.userPulledDownRecycleViewToRefresh(TaskFragment.this);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorSkyBlue,
                R.color.colorLight,
                R.color.colorAccent);
    }
}
