package com.litlgroup.litl.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.litlgroup.litl.R;
import com.litlgroup.litl.activities.TaskDetailActivity;
import com.litlgroup.litl.adapters.TaskRecycleAdapter;
import com.litlgroup.litl.behaviors.EndlessRecyclerViewScrollListener;
import com.litlgroup.litl.interfaces.InfiniteScrollListener;
import com.litlgroup.litl.interfaces.SwipeToRefreshListener;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.ItemClickSupport;
import com.sdsmdg.tastytoast.TastyToast;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {

    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Task> tasks;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvTasks;

    public TaskRecycleAdapter taskRecycleAdapter;
    public String chosenCategory;
    public InfiniteScrollListener infiniteScrollListener;
    public SwipeToRefreshListener swipeToRefreshListener;
    public boolean tasksForSpecificCategoryIsEmpty = false;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (!tasksForSpecificCategoryIsEmpty) {
            view = inflater.inflate(R.layout.fragment_task, container, false);
            setUpRecycleView(view);
            setupSwipeToRefresh(view);
        } else {
            view = inflater.inflate(R.layout.fragment_no_tasks, container, false);
            TastyToast.makeText(getActivity(), "No Tasks in this category. You should tap the blue button and create one", TastyToast.LENGTH_LONG, TastyToast.DEFAULT);
        }

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
        tasks.addAll(moreTasks);
        taskRecycleAdapter.notifyItemRangeChanged(startingPoint, moreTasks.size());
    }

    public void addAllNewTasksForRefresh(ArrayList<Task> newTasks) {
        tasks.addAll(0, newTasks);
        taskRecycleAdapter.notifyItemRangeInserted(0, newTasks.size() - 1);

        if (newTasks.size() > 0) {
            linearLayoutManager.scrollToPosition(0);
        }
        swipeContainer.setRefreshing(false);
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

        rvTasks.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                infiniteScrollListener.userScrolledPastBenchmark(TaskFragment.this, totalItemsCount - 1);
            }
        });
    }

    private void implementRecyclerViewAnimations() {
        //RecyclerView Animations from Library https://github.com/wasabeef/recyclerview-animators
        rvTasks.setItemAnimator(new FlipInTopXAnimator(new OvershootInterpolator()));
        rvTasks.getItemAnimator().setAddDuration(1000);
        rvTasks.getItemAnimator().setRemoveDuration(1000);
        rvTasks.getItemAnimator().setMoveDuration(1000);
        rvTasks.getItemAnimator().setChangeDuration(1000);

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
