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
import com.litlgroup.litl.adapters.OffersAdapter;
import com.litlgroup.litl.models.Bids;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.SpacesItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BidSelectScreenActivity extends AppCompatActivity {

    ArrayList<Bids> mBids;

    OffersAdapter adapter;

    @BindView(R.id.rvOffers)
    RecyclerView rvOffers;

    private String thisTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_select_screen);

        thisTaskId = getIntent().getStringExtra(Constants.TASK_ID);

        ButterKnife.bind(this);
        mBids = new ArrayList<>();

        adapter = new OffersAdapter(this, mBids);
        rvOffers.setAdapter(adapter);
        rvOffers.setLayoutManager(new LinearLayoutManager(this));

        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        rvOffers.addItemDecoration(decoration);

        GetData();
    }

    private void GetData() {
        try {
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            database.child(Constants.TABLE_BIDS)
                    .orderByChild(Constants.TABLE_BIDS_COLUMN_TASK)
                    .startAt(thisTaskId)
                    .endAt(thisTaskId)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Timber.d(dataSnapshot.toString());

                    mBids.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mBids.add(snapshot.getValue(Bids.class));
                    }

                    adapter.addAll(mBids);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(BidSelectScreenActivity.this, "There was an error when fetching Offers data", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
