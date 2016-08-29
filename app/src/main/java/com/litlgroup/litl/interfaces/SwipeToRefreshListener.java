package com.litlgroup.litl.interfaces;

import com.litlgroup.litl.fragments.TaskFragment;

/**
 * Created by andrj148 on 8/25/16.
 */
public interface SwipeToRefreshListener {
    void userPulledDownRecycleViewToRefresh(TaskFragment taskFragment);
}
