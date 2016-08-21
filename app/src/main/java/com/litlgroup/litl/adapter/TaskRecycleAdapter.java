package com.litlgroup.litl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.litlgroup.litl.R;
import com.litlgroup.litl.model.Task;

import java.util.ArrayList;

/**
 * Created by andrj148 on 8/20/16.
 */
public class TaskRecycleAdapter extends RecyclerView.Adapter<TaskRecycleAdapter.ViewHolder> {
    private ArrayList<Task> tasks;
    private Context thisContext;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private CardView cardView;

            public ViewHolder(CardView v) {
                super(v);
                cardView = v;
            }
        }

    public TaskRecycleAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public TaskRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false);
        thisContext = parent.getContext();
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = tasks.get(position);
        setSubviews(holder, task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private void setSubviews(ViewHolder viewHolder, final Task task) {
        final CardView taskCardView = viewHolder.cardView;

        ImageView ivBackgrouind = (ImageView) taskCardView.findViewById(R.id.ivBackground);
        Glide.with(taskCardView.getContext()).load(task.getWorkImageURL()).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivBackgrouind);

        final ImageView ivAvatar = (ImageView) taskCardView.findViewById(R.id.ivAvatar);
        Glide.with(taskCardView.getContext()).load(task.getUser().getProfileImageURL()).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(new BitmapImageViewTarget(ivAvatar){
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(taskCardView.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ivAvatar.setImageDrawable(circularBitmapDrawable);
            }
        });

        TextView tvDescription = (TextView) taskCardView.findViewById(R.id.tvDescription);
        tvDescription.setText(getFormattedDescriptionDateAddress(task));

        ImageButton ibBookmark = (ImageButton) taskCardView.findViewById(R.id.ibBookmark);
        ibBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (task.getBookmark().getBookmarked()) {
                    //unfill bookmark

                } else {
                    //fill bookmark
                }
            }
        });
    }

    private String getFormattedDescriptionDateAddress(Task task) {
        String formattedString = task.getDescription()
                + System.getProperty("line.seperator")
                + task.getDeadlineDate()
                + System.getProperty("line.seperator")
                + task.getAddress();

        return formattedString;
    }
}
