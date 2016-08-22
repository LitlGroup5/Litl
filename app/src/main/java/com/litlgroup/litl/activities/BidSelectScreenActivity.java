package com.litlgroup.litl.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.model.Offer;
import com.litlgroup.litl.model.User;
import com.litlgroup.litl.R;
import com.litlgroup.litl.adapter.OffersAdapter;
import com.litlgroup.litl.helpers.SpacesItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BidSelectScreenActivity extends AppCompatActivity {

    ArrayList<Offer> offers;

    OffersAdapter adapter;

    @BindView(R.id.rvOffers)
    RecyclerView rvOffers;

    private String thisTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_select_screen);

        thisTaskId = getIntent().getStringExtra("taskId");

        ButterKnife.bind(this);
        offers = new ArrayList<>();
        adapter = new OffersAdapter(this, offers);
        rvOffers.setAdapter(adapter);
        rvOffers.setLayoutManager(new LinearLayoutManager(this));

        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        rvOffers.addItemDecoration(decoration);

        GetData();
    }


    private void GetData()
    {
        try {

            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    adapter.clear();
                    ArrayList<Offer> offersList = Offer.fromDataSnapshot(dataSnapshot.child("Offers"));
                    Log.d("Fetch Offers", dataSnapshot.toString());

                    if(offersList == null || offersList.isEmpty())
                        return;
                    ArrayList<Offer> filteredOffers = new ArrayList<Offer>();
                    for (Offer offer : offersList) {

                        try {
                            if (offer.getUser() != null) {
                                User user = dataSnapshot.child("Users").child(offer.getUser()).getValue(User.class);
                                offer.setUserObject(user);
                                if(offer.getTask().equals(thisTaskId))
                                    filteredOffers.add(offer);
                            }
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    adapter.addAll(filteredOffers);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(BidSelectScreenActivity.this, "There was an error when fetching Offers data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
