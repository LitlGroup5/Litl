package com.litlgroup.litl.behaviors;

import com.litlgroup.litl.fragments.OffersFragment;
import com.litlgroup.litl.fragments.TaskFragment;
import com.litlgroup.litl.interfaces.InfiniteScrollListener;

/**
 * Created by andrj148 on 8/24/16.
 */
public class OffersWallScrolling implements InfiniteScrollListener {
    @Override
    public void userScrolledPastBenchmark(TaskFragment taskFragment, int startingPosition) {
        OffersFragment offersFragment = (OffersFragment) taskFragment;
        offersFragment.getData();
    }
}
