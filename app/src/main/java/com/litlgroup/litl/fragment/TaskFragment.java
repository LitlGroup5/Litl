package com.litlgroup.litl.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.litlgroup.litl.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {

    private RecyclerView rvTasks;
    private LinearLayoutManager linearLayoutManager;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        setUpRecycleView(view);
        return view;
    }

    private void setUpRecycleView(View v) {
        rvTasks= (RecyclerView) v.findViewById(R.id.taskRecycleView);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvTasks.setLayoutManager(linearLayoutManager);

    }
}
