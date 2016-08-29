package com.litlgroup.litl.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.litlgroup.litl.R;
import com.litlgroup.litl.models.Bids;
import com.litlgroup.litl.models.UserSummary;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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

            UserSummary user = bid.getUser();
            holder.tvUsername.setText(user.getName());

            String profileImageUrl = user.getPhoto();

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Glide.with(getContext())
                        .load(profileImageUrl)
                        .placeholder(R.drawable.offer_profile_image)
                        .into(holder.ivProfileImage);
            }

            if(acceptedBidListIndex == -1)
            {
                holder.ibOfferAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.ibOfferReject.setAlpha(0.3f);
                        holder.ibOfferAccept.setAlpha(1f);

                        AcceptBidListener listener = (AcceptBidListener) mContext;
                        Toast.makeText(getContext(), "Offer accepted!", Toast.LENGTH_SHORT).show();
                        listener.onAcceptBidListener(position);
                    }
                });

                holder.ibOfferReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.ibOfferAccept.setAlpha(0.3f);
                        holder.ibOfferReject.setAlpha(1f);
                        Toast.makeText(getContext(), "Offer rejected!", Toast.LENGTH_SHORT).show();
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
            ex.printStackTrace();
        }
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
}
