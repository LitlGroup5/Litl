package com.litlgroup.litl.behaviors;

import com.litlgroup.litl.fragments.ProposalsFragment;
import com.litlgroup.litl.fragments.TaskFragment;
import com.litlgroup.litl.interfaces.InfiniteScrollListener;

/**
 * Created by andrj148 on 8/24/16.
 */
public class ProposalWallScrolling implements InfiniteScrollListener {
    @Override
    public void userScrolledPastBenchmark(TaskFragment taskFragment, int startingPosition) {
        ProposalsFragment proposalsFragment = (ProposalsFragment) taskFragment;
        proposalsFragment.setupFakeData();
    }
}
