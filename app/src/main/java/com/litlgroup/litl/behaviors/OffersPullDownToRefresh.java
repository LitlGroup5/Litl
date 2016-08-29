package com.litlgroup.litl.behaviors;

import com.litlgroup.litl.fragments.OffersFragment;
import com.litlgroup.litl.fragments.TaskFragment;
import com.litlgroup.litl.interfaces.SwipeToRefreshListener;

/**
 * Created by andrj148 on 8/25/16.
 */
public class OffersPullDownToRefresh implements SwipeToRefreshListener {
    @Override
    public void userPulledDownRecycleViewToRefresh(TaskFragment taskFragment) {
        OffersFragment offersFragment = (OffersFragment) taskFragment;
        offersFragment.setupFakeData(true);
    }
}
