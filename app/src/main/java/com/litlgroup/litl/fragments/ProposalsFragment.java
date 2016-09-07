package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.behaviors.ProposalWallScrolling;
import com.litlgroup.litl.behaviors.ProposalsPullDownToRefresh;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.Constants;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by andrj148 on 8/17/16.
 */
public class ProposalsFragment extends TaskFragment {

    ArrayList<Task> mProposals = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData(false);
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

    public void getData(final boolean isRefresh) {
        try {
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String node = Constants.TABLE_TASKS_COLUMN_USER + "/" + uid;

            database.child(Constants.TABLE_TASKS).orderByChild(node)
                    .equalTo(true)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Timber.d(dataSnapshot.toString());
                            int currentNumberOfProposals = mProposals.size();

                            mProposals.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Task task = snapshot.getValue(Task.class);
                                task.setId(snapshot.getKey());
                                task.setType(Task.Type.PROPOSAL);
                                mProposals.add(task);
                            }

                            if (currentNumberOfProposals != mProposals.size()) {
                                if (chosenCategory != null) {
                                    setupData(true);
                                } else {
                                    setupData(isRefresh);
                                }
                            }
                            if (isRefresh) {
                                swipeContainer.setRefreshing(false);
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
            addAllNewTasksForRefresh(Task.getSortedTasks(mProposals, chosenCategory));
        } else {
            addMoreTasksForEndlessScrolling(mProposals);
        }
    }
}
