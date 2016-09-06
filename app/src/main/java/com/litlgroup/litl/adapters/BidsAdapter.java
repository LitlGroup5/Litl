package com.litlgroup.litl.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.litlgroup.litl.R;
import com.litlgroup.litl.models.Bids;
import com.litlgroup.litl.models.UserSummary;
import com.litlgroup.litl.utils.ImageUtils;
import com.robinhood.ticker.TickerUtils;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
        ImageView ivProfileImage;

        @BindView(R.id.ibOfferAccept)
        ImageButton ibOfferAccept;

        @BindView(R.id.ibOfferReject)
        ImageButton ibOfferReject;

        @BindView(R.id.tickerPrice)
        com.robinhood.ticker.TickerView tickerPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tickerPrice.setCharacterList(TickerUtils.getDefaultListForUSCurrency());
        }

        public void setEnabled(boolean isEnabled)
        {
            tvUsername.setEnabled(isEnabled);
            ivProfileImage.setEnabled(isEnabled);
            ibOfferAccept.setEnabled(isEnabled);
            ibOfferReject.setEnabled(isEnabled);

            if(!isEnabled) {
                ivProfileImage.setAlpha(0.1f);
                tickerPrice.setAlpha(0.1f);
            }

        }

        public void setTickerPrice(float price)
        {
            finalPrice = price;
            handler.post(createRunnable());
        }

        float finalPrice;
        int tickerSteps = 5;
        long tickerStepDelayMillis = 200;

        private Handler handler = new Handler();
        private boolean resumed;

        private Runnable createRunnable() {
            return new Runnable() {
                @Override
                public void run() {
                    onUpdate();
                    if (resumed) {
                        handler.postDelayed(createRunnable(), tickerStepDelayMillis);
                    }
                }
            };
        }

        private float currentPrice = 0;
        private void onUpdate(){
            if(currentPrice < finalPrice) {
                currentPrice += (finalPrice / tickerSteps);
                resumed = true;
            }
            else {
                currentPrice = finalPrice; //if currentPrice somehow went past final price
                resumed = false;
            }
            tickerPrice.setText("$"+ prettyPrint(currentPrice), true);
        }

        public static String prettyPrint(double d) {
            int i = (int) d;
            return d == i ? String.valueOf(i) : String.format("%.2f",d);
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

            holder.setTickerPrice(bid.getPrice());

            final UserSummary user = bid.getUser();
            if(user != null && user.getName()!=null) {
                holder.tvUsername.setText(user.getName());
            }

            if(user != null && user.getPhoto() != null) {
                String profileImageUrl = user.getPhoto();

                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    ImageUtils.setCircularImage(holder.ivProfileImage, profileImageUrl);

                    holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                LaunchProfileListener launchProfileListener = (LaunchProfileListener) mContext;
                                launchProfileListener.onLaunchProfileListener(user.getId());
                            } catch (Exception ex) {
                                Timber.e("Error launching profile screen from bid select screen");
                            }
                        }
                    });
                }
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
                holder.ibOfferReject.setAlpha(0.1f);
                holder.ibOfferAccept.setAlpha(0.1f);
                holder.setEnabled(false);
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