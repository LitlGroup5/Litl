package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.litlgroup.litl.Model.Offer;
import com.litlgroup.litl.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Hari on 8/20/2016.
 */
public class PlaceBidFragment extends BottomSheetDialogFragment
{
    public PlaceBidFragment()
    {

    }

    public static PlaceBidFragment newInstance(String taskKey, String userKey)
    {
        PlaceBidFragment frag = new PlaceBidFragment();
        frag.taskKey = taskKey;
        return frag;
    }


    @BindView(R.id.etBidPrice)
    EditText etBidPrice;

    @BindView(R.id.btnPlaceBid)
    Button btnPlaceBid;

    private String taskKey;
    private String userKey;

    private Unbinder unbinder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bid, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }



    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private DatabaseReference mDatabase;

    @OnClick(R.id.btnPlaceBid)
    public void  onPlaceBid()
    {
        try
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String bidPriceText = etBidPrice.getText().toString().replace("$","");
            float price = Float.parseFloat(bidPriceText);
            writeNewOffer(price, taskKey, userKey);
            dismiss();
            Toast.makeText(getActivity(), "The bid has been placed!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void writeNewOffer(float price, String taskId, String userId)
    {
        try {
            String key = mDatabase.child("Offers").push().getKey();
            Offer offer = new Offer(price, taskId, userId);
            Map<String, Object> offerValues = offer.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/Offers/" + key, offerValues);

            childUpdates.put("/Tasks/" + taskId + "/" + "offers" + "/" + key, "true");

            mDatabase.updateChildren(childUpdates);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }



    }
