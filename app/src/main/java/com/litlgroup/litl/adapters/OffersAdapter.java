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
import com.litlgroup.litl.model.Offer;
import com.litlgroup.litl.model.User;
import com.litlgroup.litl.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Hari on 8/18/2016.
 */
public class OffersAdapter
    extends RecyclerView.Adapter<OffersAdapter.ViewHolder>
{


    public static class ViewHolder extends RecyclerView.ViewHolder
    {


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

    private List<Offer> mOffers;

    private Context mContext;

    public OffersAdapter(Context context, List<Offer> offers)
    {
        mOffers = offers;
        mContext = context;
    }

    private Context getContext()
    {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_offers, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        try {
            final Offer offer = mOffers.get(position);
            holder.tvOfferValue.setText(String.valueOf(offer.getPriceFormatted()));

            User user = offer.getUserObject();
            holder.tvUsername.setText(user.getUserName());

            String profileImageUrl = user.getProfileImageURL();

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Glide.with(getContext())
                        .load(profileImageUrl)
                        .placeholder(R.drawable.offer_profile_image)
                        .into(holder.ivProfileImage);

            }

            holder.ibOfferAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ibOfferReject.setAlpha(0.5f);
                    holder.ibOfferAccept.setAlpha(1f);
                    Toast.makeText(getContext(), "Offer accepted!", Toast.LENGTH_SHORT).show();
                }
            });

            holder.ibOfferReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ibOfferAccept.setAlpha(0.5f);
                    holder.ibOfferReject.setAlpha(1f);
                    Toast.makeText(getContext(), "Offer rejected!", Toast.LENGTH_SHORT).show();
                }
            });

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mOffers.size();
    }


    public void addAll (List<Offer> list)
    {
        mOffers.addAll(list);
        notifyDataSetChanged();
    }

    public void clear()
    {
        mOffers.clear();
        notifyDataSetChanged();
    }
}
