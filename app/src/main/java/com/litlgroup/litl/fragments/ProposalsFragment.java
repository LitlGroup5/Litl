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

    public static ProposalsFragment newInstance(String category) {
        ProposalsFragment fragment = new ProposalsFragment();
        fragment.chosenCategory = category;

        return fragment;
    }

    private void setupBehaviors() {
        infiniteScrollListener = new ProposalWallScrolling();
        swipeToRefreshListener = new ProposalsPullDownToRefresh();
    }

    public void setupFakeData(boolean isRefresh) {
        if (chosenCategory == null) {
            addAll(Task.getFakeTaskDataProposals(), isRefresh);
        } else {
            addAll(Task.getSortedTasks(chosenCategory, "proposal"), isRefresh);
        }
    }
}
