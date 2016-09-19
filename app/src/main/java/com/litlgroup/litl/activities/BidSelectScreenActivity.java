package com.litlgroup.litl.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.github.jinatonic.confetti.CommonConfetti;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.R;
import com.litlgroup.litl.adapters.BidsAdapter;
import com.litlgroup.litl.models.Bids;
import com.litlgroup.litl.models.Notifications;
import com.litlgroup.litl.models.Task;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.SpacesItemDecoration;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BidSelectScreenActivity
        extends AppCompatActivity
    implements BidsAdapter.AcceptBidListener,
        BidsAdapter.RejectBidListener,
        BidsAdapter.LaunchProfileListener
{

    ArrayList<Bids> mBids;
    ArrayList<String> bidIds;
    BidsAdapter adapter;

    @BindView(R.id.rvOffers)
    RecyclerView rvOffers;

    @BindView(R.id.rlContainer)
    RelativeLayout rlContainer;

    @BindColor(android.R.color.transparent)
    int mTransparent;
    @BindColor(R.color.colorPrimaryDark)
    int mPrimaryDark;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    private String thisTaskId;
    DatabaseReference database;

    boolean isTaskBidAccepted = false;
    String acceptedBidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_select_screen);

        thisTaskId = getIntent().getStringExtra(Constants.TASK_ID);

        ButterKnife.bind(this);
        mBids = new ArrayList<>();
        bidIds = new ArrayList<>();

        adapter = new BidsAdapter(this, mBids);
        rvOffers.setAdapter(adapter);
        rvOffers.setLayoutManager(new LinearLayoutManager(this));

        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        rvOffers.addItemDecoration(decoration);
        database = FirebaseDatabase.getInstance().getReference();

        GetThisTaskData();
        GetBidsData();

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }


    private void GetThisTaskData()
    {
        try
        {
            database.child(Constants.TABLE_TASKS)
                    .child(thisTaskId)
                    .child(getString(R.string.accepted_offer_id))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null) {
                                String bidId = dataSnapshot.getValue().toString();
                                if (!bidId.equals("-1")) {
                                    isTaskBidAccepted = true;
                                    acceptedBidId = bidId;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }
        catch (Exception ex)
        {
            Timber.e("Error getting current task's data");
        }

    }

    private void GetBidsData() {
        try {

            database.child(Constants.TABLE_BIDS)
                    .orderByChild(Constants.TABLE_BIDS_COLUMN_TASK)
                    .startAt(thisTaskId)
                    .endAt(thisTaskId)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Timber.d(dataSnapshot.toString());
                    mBids.clear();
                    bidIds.clear();
                    int index = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String bidId = snapshot.getKey();
                        if(isTaskBidAccepted)
                        {
                            if(bidId.equals(acceptedBidId))
                            {
                                adapter.acceptedBidListIndex = index;
                            }
                        }
                        mBids.add(snapshot.getValue(Bids.class));
                        bidIds.add(bidId);
                        index++;
                    }

                    adapter.addAll(mBids);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    TastyToast.makeText(getApplicationContext(), "There was an error when fetching bids data", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateAcceptedBidId(int bidIndex)
    {
        try
        {
            String bidId = bidIds.get(bidIndex);

            database.child(Constants.TABLE_TASKS)
                    .child(thisTaskId)
                    .child(getString(R.string.accepted_offer_id))
                    .setValue(bidId);
        }
        catch (Exception ex)
        {
            Timber.e("Error writing accepted bid id to firebase",ex);
        }
    }

    private void updateBidStatus(String status)
    {
        try
        {

            database.child(Constants.TABLE_TASKS)
                    .child(thisTaskId)
                    .child(getString(R.string.task_status_child))
                    .setValue(status);
        }
        catch (Exception ex)
        {
            Timber.e("Error writing accepted bid id to firebase",ex);
        }
    }


    @Override
    public void onRejectBidListener(int position) {
        updateNotifications(false, position);
    }

    @Override
    public void onAcceptBidListener(int bidIndex) {
        try {
            updateNotifications(true, bidIndex);

            CommonConfetti.rainingConfetti(rlContainer, new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.CYAN })
                    .infinite();
            updateAcceptedBidId(bidIndex);
            String state = Task.State.SUCCESSFULLY_ACCEPTED.toString();
            updateBidStatus(state);


        }
        catch (Exception ex)
        {
            Timber.e("Error in onAcceptBidListener", ex);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.onDisconnect();
    }

    @Override
    public void onLaunchProfileListener(String userId) {

        try
        {
            Intent intent = new Intent(BidSelectScreenActivity.this, ProfileActivity.class);
            intent.putExtra(getString(R.string.user_id), userId);
            intent.putExtra("profileMode", ProfileActivity.ProfileMode.OTHER);
            startActivity(intent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNotifications(boolean isTaskBidAccepted, int position) {

        String key = database.child(Constants.TABLE_NOTIFICATIONS).push().getKey();

        Notifications notifications = new Notifications();

        if (isTaskBidAccepted)
            notifications.setType("ACCEPTED");
        else
            notifications.setType("REJECTED");

        notifications.setTaskId(thisTaskId);

        database.child(Constants.TABLE_NOTIFICATIONS).child(key).setValue(notifications);

        HashMap<String, Object> usermap = new HashMap<>();
        usermap.put(mBids.get(position).getUser().getId(), true);
        database.child(Constants.TABLE_NOTIFICATIONS).child(key).updateChildren(usermap);
    }
}
