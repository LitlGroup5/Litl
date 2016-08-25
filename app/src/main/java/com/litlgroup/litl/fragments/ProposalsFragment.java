package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.litlgroup.litl.behaviors.ProposalWallScrolling;
import com.litlgroup.litl.behaviors.ProposalsPullDownToRefresh;
import com.litlgroup.litl.model.Task;

/**
 * Created by andrj148 on 8/17/16.
 */
public class ProposalsFragment extends TaskFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFakeData(false);
        setupBehaviors();
    }

    private void setupBehaviors() {
        infiniteScrollListener = new ProposalWallScrolling();
        swipeToRefreshListener = new ProposalsPullDownToRefresh();
    }

    public void setupFakeData(boolean isRefresh) {
        addAll(Task.getFakeTaskDataProposals(), isRefresh);
    }
}
