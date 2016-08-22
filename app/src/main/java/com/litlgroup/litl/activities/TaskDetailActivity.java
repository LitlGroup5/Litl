package com.litlgroup.litl.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.litlgroup.litl.R;
import com.litlgroup.litl.fragment.TaskOffersFragment;
import com.litlgroup.litl.fragment.TaskProposalFragment;
import com.litlgroup.litl.utils.Constants.DetailViewType;

public class TaskDetailActivity extends AppCompatActivity {

    private DetailViewType mType = DetailViewType.OFFERS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        loadFragment();
    }

    private void loadFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (mType == DetailViewType.PROPOSALS) {
            TaskProposalFragment fragmentDemo = TaskProposalFragment.newInstance();
            ft.replace(R.id.fragment_placeholder, fragmentDemo);
        } else if (mType == DetailViewType.OFFERS){
            TaskOffersFragment fragmentDemo = TaskOffersFragment.newInstance();
            ft.replace(R.id.fragment_placeholder, fragmentDemo);
        }

        ft.commit();
    }
}
