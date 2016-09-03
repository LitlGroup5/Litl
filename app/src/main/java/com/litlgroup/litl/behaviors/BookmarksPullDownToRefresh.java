package com.litlgroup.litl.behaviors;

import com.litlgroup.litl.fragments.BookmarksFragment;
import com.litlgroup.litl.fragments.TaskFragment;
import com.litlgroup.litl.interfaces.SwipeToRefreshListener;

/**
 * Created by msurana on 9/2/16.
 */
public class BookmarksPullDownToRefresh implements SwipeToRefreshListener {
    @Override
    public void userPulledDownRecycleViewToRefresh(TaskFragment taskFragment) {
        BookmarksFragment bookmarksFragment = (BookmarksFragment) taskFragment;
        bookmarksFragment.getData(true);
    }
}
