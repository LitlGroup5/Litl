package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.R;
import com.litlgroup.litl.adapters.TaskRecycleAdapter;
import com.litlgroup.litl.behaviors.BookmarksPullDownToRefresh;
import com.litlgroup.litl.behaviors.BookmarksWallScrolling;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.Constants;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookmarksFragment extends TaskFragment {

    ArrayList<Task> mBookmarks = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData(false);
        setupBehaviors();
        setupRemoveBookmarkListenerImplementation();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (!tasksForSpecificCategoryIsEmpty) {
            view = inflater.inflate(R.layout.fragment_task, container, false);
            setUpRecycleView(view);
            setupSwipeToRefresh(view);
        } else {
            view = inflater.inflate(R.layout.fragment_no_bookmarks, container, false);
            TastyToast.makeText(getActivity(), "No Bookmarks in this category", TastyToast.LENGTH_LONG, TastyToast.DEFAULT);
        }

        return view;
    }

    private void setupRemoveBookmarkListenerImplementation() {
        taskRecycleAdapter.setRemoveBookmarkListener(new TaskRecycleAdapter.RemoveBookmarkListener() {
            @Override
            public void onClick(int position, boolean isBookmarked) {
                if (!isBookmarked) {
                    removeTaskWithAnimation(position);
                }
            }
        });
    }

    private void setupBehaviors() {
        infiniteScrollListener = new BookmarksWallScrolling();
        swipeToRefreshListener = new BookmarksPullDownToRefresh();
    }

    public void getData(final boolean isRefresh) {
        try {
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String node = Constants.TABLE_TASKS_COLUMN_BOOKMARKS + "/" + uid;

            database.child(Constants.TABLE_TASKS).orderByChild(node)
                    .equalTo(true)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Timber.d(dataSnapshot.toString());
                            int currentNumberOfBookmarks = mBookmarks.size();
                            mBookmarks.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Task task = snapshot.getValue(Task.class);
                                task.setId(snapshot.getKey());

                                if (task.getUser().getId().equals(uid))
                                    task.setType(Task.Type.PROPOSAL);
                                else
                                    task.setType(Task.Type.OFFER);

                                mBookmarks.add(task);
                            }
                            if (mBookmarks.size() != currentNumberOfBookmarks) {
                                setupData(isRefresh);
                            }

                            swipeContainer.setRefreshing(false);
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
            addAllNewTasksForRefresh(mBookmarks);
        } else {
            addMoreTasksForEndlessScrolling(mBookmarks);
        }
    }
}