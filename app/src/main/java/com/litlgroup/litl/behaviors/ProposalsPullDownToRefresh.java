package com.litlgroup.litl.behaviors;

import com.litlgroup.litl.fragments.ProposalsFragment;
import com.litlgroup.litl.fragments.TaskFragment;
import com.litlgroup.litl.interfaces.SwipeToRefreshListener;

/**
 * Created by andrj148 on 8/25/16.
 */
public class ProposalsPullDownToRefresh implements SwipeToRefreshListener {
    @Override
    public void userPulledDownRecycleViewToRefresh(TaskFragment taskFragment) {
        ProposalsFragment proposalsFragment = (ProposalsFragment) taskFragment;
        proposalsFragment.getData(true);
    }
}
