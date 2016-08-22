package com.litlgroup.litl.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;
import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.PlaceBidFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btnBiddersScreen)
    Button btnBiddersScreen;

    @BindView(R.id.btnPlaceBid)
    Button btnPlaceBid;

    private String userKey = "2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        btnBiddersScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, BidSelectScreenActivity.class);
                startActivity(intent);
            }
        });


        btnPlaceBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String taskkey = FirebaseDatabase.getInstance().getReference().child("Offers").push().getKey();
                FragmentManager fm = getSupportFragmentManager(); //getSupportFragmentManager();
                PlaceBidFragment placeBidFragment = PlaceBidFragment.newInstance(taskkey, userKey);
                placeBidFragment.show(fm,"compose_tweet_fragment");
            }
        });

    }
}
