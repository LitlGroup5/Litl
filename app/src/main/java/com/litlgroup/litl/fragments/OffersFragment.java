package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.litlgroup.litl.behaviors.OffersPullDownToRefresh;
import com.litlgroup.litl.behaviors.OffersWallScrolling;
import com.litlgroup.litl.model.Task;

/**
 * Created by andrj148 on 8/17/16.
 */
public class OffersFragment extends TaskFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFakeData(false);
        setupBehaviors();
    }

    private void setupBehaviors() {
        swipeToRefreshListener = new OffersPullDownToRefresh();
        infiniteScrollListener = new OffersWallScrolling();
    }

    public void setupFakeData(boolean isRefresh) {
        addAll(Task.getFakeTaskDataOfferss(), isRefresh);
    }
}
