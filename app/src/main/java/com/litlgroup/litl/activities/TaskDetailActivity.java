package com.litlgroup.litl.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.TaskOffersFragment;
import com.litlgroup.litl.fragments.TaskProposalFragment;
import com.litlgroup.litl.model.Task.Type;
import com.litlgroup.litl.utils.Constants;

public class TaskDetailActivity extends AppCompatActivity {

    private String mTaskId;
    private Type mType = Type.OFFER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        mTaskId = getIntent().getStringExtra(Constants.TASK_ID);
        mType = (Type) getIntent().getSerializableExtra(Constants.TASK_TYPE);

        loadFragment();
    }

    private void loadFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (mType == Type.PROPOSAL) {
            TaskProposalFragment fragmentDemo = TaskProposalFragment.newInstance(mTaskId);
            ft.replace(R.id.fragment_placeholder, fragmentDemo);
        } else if (mType == Type.OFFER){
            TaskOffersFragment fragmentDemo = TaskOffersFragment.newInstance(mTaskId);
            ft.replace(R.id.fragment_placeholder, fragmentDemo);
        }

        ft.commit();
    }
}
