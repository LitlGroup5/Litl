package com.litlgroup.litl.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.litlgroup.litl.R;
import com.litlgroup.litl.models.UserSummary;
import com.litlgroup.litl.utils.Constants;
import com.litlgroup.litl.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Hari on 9/16/2016.
 */
public class TaskMarkedCompleteFragment extends DialogFragment {


    public TaskMarkedCompleteFragment() {

    }

    public static TaskMarkedCompleteFragment newInstance(UserSummary acceptedUser) {
        TaskMarkedCompleteFragment frag = new TaskMarkedCompleteFragment();

        frag.acceptedUser = acceptedUser;
        return frag;
    }

    private UserSummary acceptedUser;

    private String acceptedUserId;


    @BindView(R.id.srbRating)
    SimpleRatingBar srbRating;

    @BindView(R.id.tvBody)
    TextView tvBody;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.ibPaypal)
    ImageButton ibPaypal;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @BindView(R.id.btnCancel)
    Button btnCancel;

    private int userRating;

    private Unbinder unbinder;

    private DatabaseReference mDatabase;

    List<String> currentRatings;
    List<String> newRatings;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_marked_complete, container);
        unbinder = ButterKnife.bind(this, view);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRatingBar();
        getAcceptedUserData();
    }

    private void setupRatingBar()
    {
        SimpleRatingBar.AnimationBuilder builder =
                srbRating.getAnimationBuilder()
                        .setRatingTarget(5)
                        .setDuration(1000)
                        .setInterpolator(new DecelerateInterpolator())
                        .setRepeatCount(1);
        builder.start();

        srbRating.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {

                if(!fromUser ||rating <= 0) {
                    if(rating == 0)
                        btnSubmit.setEnabled(false);
                    else
                        btnSubmit.setEnabled(true);
                    return;
                }
                btnSubmit.setEnabled(true);
                userRating = (int)rating;

                int numberSimilarRatings = Integer.parseInt(currentRatings.get(userRating - 1));
                newRatings = new ArrayList<String>();
                newRatings.addAll(currentRatings);
                newRatings.set(userRating - 1, String.valueOf(numberSimilarRatings+1));


            }
        });
    }

    @OnClick(R.id.ibPaypal)
    public void startPaypalWeb()
    {
        try
        {

            Uri uri = Uri.parse("https://www.paypal.com");

// create an intent builder
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

// Begin customizing
// set toolbar colors
            intentBuilder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

// set start and exit animations
            intentBuilder.setStartAnimations(getActivity(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            intentBuilder.setExitAnimations(getActivity(), android.R.anim.fade_in,
                    android.R.anim.fade_out);

// build custom tabs intent
            CustomTabsIntent customTabsIntent = intentBuilder.build();

// launch the url
            customTabsIntent.launchUrl(getActivity(), uri);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting Paypal web");
        }
    }

    @OnClick(R.id.btnSubmit)
    public void startSubmitRate()
    {
        try
        {
            mDatabase.child(Constants.TABLE_USERS)
                    .child(acceptedUserId)
                    .child(getString(R.string.user_rating_child))
                    .setValue(newRatings);
            dismiss();
        }
        catch (Exception ex) {
            Timber.e("Error starting submit rate");
        }

    }

    @OnClick(R.id.btnCancel)
    public void startCancel()
    {
        dismiss();
    }


    private void getAcceptedUserData()
    {
        try
        {

            acceptedUserId = acceptedUser.getId();
            ImageUtils.setCircularImage(ivProfileImage, acceptedUser.getPhoto());

            String bodyString = String.format("How did %s do?", acceptedUser.getName());
            tvBody.setText(bodyString);

            mDatabase.child(Constants.TABLE_USERS)
                    .child(acceptedUserId)
                    .child("rating")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            currentRatings = (List<String>) dataSnapshot.getValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        catch (Exception ex)
        {
            Timber.e("Error getting accepted user's data");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



}
