package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.litlgroup.litl.R;
import com.litlgroup.litl.models.Bids;
import com.litlgroup.litl.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Hari on 8/20/2016.
 */
public class PlaceBidFragment extends BottomSheetDialogFragment {
    public PlaceBidFragment() {

    }

    public static PlaceBidFragment newInstance(String taskKey, Integer bidBy) {
        PlaceBidFragment frag = new PlaceBidFragment();

        frag.taskKey = taskKey;
        frag.bidBy = bidBy;

        return frag;
    }

    @BindView(R.id.etBidPrice)
    EditText etBidPrice;

    @BindView(R.id.btnPlaceBid)
    Button btnPlaceBid;

    private String taskKey;
    private Integer bidBy;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bid, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private DatabaseReference mDatabase;

    @OnClick(R.id.btnPlaceBid)
    public void onPlaceBid() {
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();

            String bidPriceText = etBidPrice.getText().toString().replace("$", "");
            float price = Float.parseFloat(bidPriceText);

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                writeNewOffer(price, taskKey, FirebaseAuth.getInstance().getCurrentUser().getUid(), bidBy);
                Toast.makeText(getActivity(), "The bid has been placed!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "User not logged in!", Toast.LENGTH_SHORT).show();
            }

            dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void writeNewOffer(float price, String taskId, String userId, Integer bidBy) {
        try {
            String key = mDatabase.child(Constants.TABLE_BIDS).push().getKey();

            Bids bid = new Bids();
            bid.setPrice(price);
            bid.setTask(taskId);
            bid.setUser(userId);
            bid.setCreatedOn(ServerValue.TIMESTAMP.toString());

            mDatabase.child(Constants.TABLE_BIDS).child(key).setValue(bid);

            mDatabase.child(Constants.TABLE_TASKS).child(taskId).child(Constants.TABLE_TASKS_COLUMN_BID_BY).setValue(bidBy.intValue() + 1);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
