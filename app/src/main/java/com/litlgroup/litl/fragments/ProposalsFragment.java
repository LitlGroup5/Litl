package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.litlgroup.litl.behaviors.ProposalWallScrolling;
import com.litlgroup.litl.model.Task;

/**
 * Created by andrj148 on 8/17/16.
 */
public class ProposalsFragment extends TaskFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFakeData();
        setupBehaviors();
    }

    private void setupBehaviors() {
        infiniteScrollListener = new ProposalWallScrolling();
    }

    public void setupFakeData() {
        addAll(Task.getFakeTaskDataProposals());
    }
}
