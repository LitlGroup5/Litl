package com.litlgroup.litl.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.litlgroup.litl.R;
import com.litlgroup.litl.models.Bids;
import com.litlgroup.litl.models.UserSummary;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

/**
 * Created by Hari on 8/18/2016.
 */
public class BidsAdapter
        extends RecyclerView.Adapter<BidsAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvUsername)
        TextView tvUsername;

        @BindView(R.id.ivProfileImage)
        CircleImageView ivProfileImage;

        @BindView(R.id.tvOfferValue)
        TextView tvOfferValue;

        @BindView(R.id.ibOfferAccept)
        ImageButton ibOfferAccept;

        @BindView(R.id.ibOfferReject)
        ImageButton ibOfferReject;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setEnabled(boolean isEnabled)
        {
            tvUsername.setEnabled(isEnabled);
            ivProfileImage.setEnabled(isEnabled);
            tvOfferValue.setEnabled(isEnabled);
            ibOfferAccept.setEnabled(isEnabled);
            ibOfferReject.setEnabled(isEnabled);
        }
    }

    private List<Bids> mBids;
    public int acceptedBidListIndex = -1;
    private Context mContext;

    public BidsAdapter(Context context, List<Bids> offers) {
        mBids = offers;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_bid, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        try {
            final Bids bid = mBids.get(position);
            holder.tvOfferValue.setText(String.valueOf(bid.getPrice()));

            final UserSummary user = bid.getUser();
            holder.tvUsername.setText(user.getName());

            String profileImageUrl = user.getPhoto();

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Glide.with(getContext())
                        .load(profileImageUrl)
                        .placeholder(R.drawable.offer_profile_image)
                        .into(holder.ivProfileImage);

                holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try
                        {
                            LaunchProfileListener launchProfileListener = (LaunchProfileListener) mContext;
                            launchProfileListener.onLaunchProfileListener(user.getId());
                        }
                        catch (Exception ex)
                        {
                            Timber.e("Error launching profile screen from bid select screen");
                        }
                    }
                });
            }

            if(acceptedBidListIndex == -1)
            {
                holder.ibOfferAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmAcceptDialog(position);
                    }
                });

                holder.ibOfferReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmRejectDialog(holder);
                    }
                });

            }
            else if(position == acceptedBidListIndex) {
                holder.ibOfferReject.setAlpha(0.2f);
                holder.ibOfferAccept.setAlpha(1f);
            }
            else
            {
                holder.ibOfferReject.setAlpha(0.2f);
                holder.ibOfferAccept.setAlpha(0.2f);
                holder.ibOfferReject.setEnabled(false);
                holder.ibOfferAccept.setEnabled(false);
            }

        } catch (Exception ex) {
            Timber.e(ex.toString());
        }
    }

    private void showConfirmAcceptDialog(final int position)
    {

        new LovelyStandardDialog(mContext)
                .setTopColorRes(android.R.color.holo_orange_light)
                .setButtonsColorRes(R.color.colorAccent)
//                .setIcon(R.drawable.ic_star_border_white_36dp)
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to accept this bid?")
                .setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AcceptBidListener listener = (AcceptBidListener) mContext;
                        listener.onAcceptBidListener(position);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private boolean showConfirmRejectDialog(final ViewHolder holder)
    {
        final boolean[] isConfirmedReject = {false};
        new LovelyStandardDialog(mContext)
                .setTopColorRes(android.R.color.holo_orange_dark)
                .setButtonsColorRes(R.color.colorAccent)
//                .setIcon(R.drawable.ic_star_border_white_36dp)
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to reject this bid?")
                .setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.ibOfferReject.setAlpha(0.1f);
                        holder.ibOfferAccept.setAlpha(0.1f);
                        holder.setEnabled(false);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();

        return isConfirmedReject[0];
    }


    @Override
    public int getItemCount() {
        return mBids.size();
    }


    public void addAll(List<Bids> list) {
        mBids = null;
        mBids = list;
        notifyDataSetChanged();
    }

    public void clear() {
        mBids.clear();
        notifyDataSetChanged();
    }

    public interface AcceptBidListener
    {
        void onAcceptBidListener(int position);
    }

    public interface RejectBidListener
    {
        void onRejectBidListener(int position);
    }

    public interface LaunchProfileListener
    {
        void onLaunchProfileListener(String userId);
    }
}
