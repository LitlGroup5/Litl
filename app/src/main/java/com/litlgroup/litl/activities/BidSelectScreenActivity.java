package com.litlgroup.litl.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.R;
import com.litlgroup.litl.adapters.BidsAdapter;
import com.litlgroup.litl.models.Bids;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.SpacesItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BidSelectScreenActivity
        extends AppCompatActivity
    implements BidsAdapter.AcceptBidListener
{

    ArrayList<Bids> mBids;
    ArrayList<String> bidIds;
    BidsAdapter adapter;

    @BindView(R.id.rvOffers)
    RecyclerView rvOffers;

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
                            String bidId = dataSnapshot.getValue().toString();
                            if(!bidId.equals("-1")) {
                                isTaskBidAccepted = true;
                                acceptedBidId = bidId;
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
                    Toast.makeText(BidSelectScreenActivity.this, "There was an error when fetching bids data", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onAcceptBidListener(int bidIndex) {
        try
        {
            updateAcceptedBidId(bidIndex);
            finish();
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
}
