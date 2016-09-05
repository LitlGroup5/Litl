package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.behaviors.OffersPullDownToRefresh;
import com.litlgroup.litl.behaviors.OffersWallScrolling;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.Constants;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by andrj148 on 8/17/16.
 */
public class OffersFragment extends TaskFragment {
    ArrayList<Task> mOffers = new ArrayList<>();

    public static OffersFragment newInstance(String category) {
        OffersFragment fragment = new OffersFragment();
        fragment.chosenCategory = category;
        if (category != null && !category.equalsIgnoreCase("All Categories")) {
            fragment.tasksForSpecificCategoryIsEmpty = true;
        } else {
            fragment.tasksForSpecificCategoryIsEmpty = false;
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData(false);
        setupBehaviors();
    }

    private void setupBehaviors() {
        swipeToRefreshListener = new OffersPullDownToRefresh();
        infiniteScrollListener = new OffersWallScrolling();
    }

    public void getData(final boolean isRefresh) {
        try {
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String node = Constants.TABLE_TASKS_COLUMN_USER + "/" + uid;

            database.child(Constants.TABLE_TASKS).orderByChild(node)
                    .equalTo(null)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Timber.d(dataSnapshot.toString());
                            int currentNumberOfOffers = mOffers.size();
                            mOffers.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Task task = snapshot.getValue(Task.class);
                                task.setId(snapshot.getKey());
                                task.setType(Task.Type.OFFER);
                                mOffers.add(task);
                            }

                            if (currentNumberOfOffers != mOffers.size()) {
                                setupData(isRefresh);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setupData(boolean isRefresh) {
        if (isRefresh) {
            addAllNewTasksForRefresh(Task.getSortedTasks(mOffers, chosenCategory));
        } else {
            addMoreTasksForEndlessScrolling(mOffers);
        }
    }
}
