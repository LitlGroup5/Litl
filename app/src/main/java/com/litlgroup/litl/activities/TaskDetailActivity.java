package com.litlgroup.litl.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.TaskOffersFragment;
import com.litlgroup.litl.fragments.TaskProposalFragment;
import com.litlgroup.litl.model.Task;

import org.parceler.Parcels;

public class TaskDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Task selectedTask = (Task) Parcels.unwrap(getIntent().getParcelableExtra("selectedTask"));
        loadFragment(selectedTask);
    }

    private void loadFragment(Task selectedTask) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (selectedTask.getType() == Task.Type.PROPOSAL) {
            TaskProposalFragment fragmentDemo = TaskProposalFragment.newInstance();
            ft.replace(R.id.fragment_placeholder, fragmentDemo);
        } else if (selectedTask.getType() == Task.Type.OFFER){
            TaskOffersFragment fragmentDemo = TaskOffersFragment.newInstance();
            ft.replace(R.id.fragment_placeholder, fragmentDemo);
        }

        ft.commit();
    }
}
