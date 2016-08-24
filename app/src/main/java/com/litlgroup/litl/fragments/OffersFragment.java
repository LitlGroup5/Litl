package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.litlgroup.litl.behaviors.OffersWallScrolling;
import com.litlgroup.litl.model.Task;

/**
 * Created by andrj148 on 8/17/16.
 */
public class OffersFragment extends TaskFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFakeData();
        setupBehaviors();
    }

    private void setupBehaviors() {
        infiniteScrollListener = new OffersWallScrolling();
    }

    public void setupFakeData() {
        addAll(Task.getFakeTaskDataOfferss());
    }
}
