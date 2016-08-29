package com.litlgroup.litl.interfaces;

import com.litlgroup.litl.fragments.TaskFragment;

/**
 * Created by andrj148 on 8/24/16.
 */
public interface InfiniteScrollListener {
    void userScrolledPastBenchmark(TaskFragment taskFragment, int StartingPosition);
}
