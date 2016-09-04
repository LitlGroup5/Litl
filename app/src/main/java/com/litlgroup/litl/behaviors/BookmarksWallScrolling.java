package com.litlgroup.litl.behaviors;

import com.litlgroup.litl.fragments.BookmarksFragment;
import com.litlgroup.litl.fragments.TaskFragment;
import com.litlgroup.litl.interfaces.InfiniteScrollListener;

/**
 * Created by msurana on 9/2/16.
 */
public class BookmarksWallScrolling implements InfiniteScrollListener {
    @Override
    public void userScrolledPastBenchmark(TaskFragment taskFragment, int startingPosition) {
        BookmarksFragment bookmarksFragment = (BookmarksFragment) taskFragment;
        bookmarksFragment.getData(false);
    }
}
