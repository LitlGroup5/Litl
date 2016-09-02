package com.litlgroup.litl.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.litlgroup.litl.R;
import com.litlgroup.litl.activities.TaskDetailActivity;
import com.litlgroup.litl.adapters.TaskRecycleAdapter;
import com.litlgroup.litl.behaviors.EndlessRecyclerViewScrollListener;
import com.litlgroup.litl.interfaces.InfiniteScrollListener;
import com.litlgroup.litl.interfaces.SwipeToRefreshListener;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.ItemClickSupport;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {

    private RecyclerView rvTasks;
    private LinearLayoutManager linearLayoutManager;
    private TaskRecycleAdapter taskRecycleAdapter;
    private ArrayList<Task> tasks;
    private SwipeRefreshLayout swipeContainer;
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
        if (tasksForSpecificCategoryIsEmpty == false) {
            view = inflater.inflate(R.layout.fragment_task, container, false);
            setUpRecycleView(view);
            setupSwipeToRefresh(view);
        } else {
            view = inflater.inflate(R.layout.fragment_no_tasks, container, false);
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasks = new ArrayList<>();
        taskRecycleAdapter = new TaskRecycleAdapter(tasks);
    }

    public void addAll(ArrayList<Task> newTasks, boolean isRefresh) {
        if (isRefresh) {
            tasks.addAll(0, newTasks);
            taskRecycleAdapter.notifyItemRangeInserted(0, newTasks.size() - 1);
            linearLayoutManager.scrollToPosition(0);
            swipeContainer.setRefreshing(false);
        } else {
            tasks.addAll(newTasks);
            taskRecycleAdapter.notifyItemRangeInserted(taskRecycleAdapter.getItemCount(), tasks.size() - 1);
        }
    }

    private void setUpRecycleView(View v) {
        rvTasks= (RecyclerView) v.findViewById(R.id.taskRecycleView);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvTasks.setLayoutManager(linearLayoutManager);
        rvTasks.setAdapter(taskRecycleAdapter);
        ItemClickSupport.addTo(rvTasks).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                               navigateToTaskDetailActivity(position);
                    }
                });
        rvTasks.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                infiniteScrollListener.userScrolledPastBenchmark(TaskFragment.this, totalItemsCount - 1);
            }
        });
    }

    private void setupSwipeToRefresh(View view) {
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

    private void navigateToTaskDetailActivity(int position) {
        Intent i = new Intent(getActivity(), TaskDetailActivity.class);
        i.putExtra(Constants.SELECTED_TASK, Parcels.wrap(tasks.get(position)));
        startActivity(i);
    }

}
