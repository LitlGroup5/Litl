package com.litlgroup.litl.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.TaskOffersFragment;
import com.litlgroup.litl.fragments.TaskProposalFragment;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.Constants;

import org.parceler.Parcels;

public class TaskDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Task selectedTask = Parcels.unwrap(getIntent().getParcelableExtra(Constants.SELECTED_TASK));

        if (savedInstanceState == null)
            loadFragment(selectedTask);
    }

    private void loadFragment(Task selectedTask) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (selectedTask.getType() == Task.Type.PROPOSAL) {
            TaskProposalFragment fragmentDemo = TaskProposalFragment.newInstance(selectedTask);
            ft.replace(R.id.fragment_placeholder, fragmentDemo);
        } else if (selectedTask.getType() == Task.Type.OFFER) {
            TaskOffersFragment fragmentDemo = TaskOffersFragment.newInstance(selectedTask);
            ft.replace(R.id.fragment_placeholder, fragmentDemo);
        }

        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
