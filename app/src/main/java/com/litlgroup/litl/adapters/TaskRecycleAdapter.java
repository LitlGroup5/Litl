package com.litlgroup.litl.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import com.litlgroup.litl.activities.TaskDetailActivity;
import com.litlgroup.litl.model.Task;
import com.litlgroup.litl.utils.Constants;

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
        setSubviews(holder, task, position);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private void setSubviews(ViewHolder viewHolder, final Task task, final int position) {
        final CardView taskCardView = viewHolder.cardView;
        taskCardView.setTag(position);

        ImageView ivBackground = (ImageView) taskCardView.findViewById(R.id.ivBackground);
        Glide.with(taskCardView.getContext()).load(task.getWorkImageURL()).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivBackground);

        final ImageView ivAvatar = (ImageView) taskCardView.findViewById(R.id.ivAvatar);
        Glide.with(taskCardView.getContext()).load(task.getUser().getProfileImageURL()).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(new BitmapImageViewTarget(ivAvatar){
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(taskCardView.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ivAvatar.setImageDrawable(circularBitmapDrawable);
            }
        });

        final TextView tvDescription = (TextView) taskCardView.findViewById(R.id.tvDescription);
        tvDescription.setText(task.getDescription());

        tvDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(tvDescription.getContext(), TaskDetailActivity.class);
                i.putExtra(Constants.TASK_ID, task.getId());
                i.putExtra(Constants.TASK_TYPE, task.getType());
                tvDescription.getContext().startActivity(i);
            }
        });

        TextView tvAddress = (TextView) taskCardView.findViewById(R.id.tvAddress);
        tvAddress.setText(task.getAddress().getFormattedAddress());

        TextView tvDeadlineDate = (TextView) taskCardView.findViewById(R.id.tvDeadlineDate);
        tvDeadlineDate.setText(task.getDeadline_date());

        final ImageButton ibBookmark = (ImageButton) taskCardView.findViewById(R.id.ibBookmark);
        if (task.getBookmark().getBookmarked()) {
            ibBookmark.setBackgroundColor(ContextCompat.getColor(thisContext, R.color.colorAccent));
        } else {
            ibBookmark.setBackgroundColor(Color.TRANSPARENT);
        }
        ibBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton tappedButton = (ImageButton) view;
                Task selectedTask = tasks.get(position);

                if (selectedTask.getBookmark().getBookmarked()) {
                    tappedButton.setBackgroundColor(Color.TRANSPARENT);
                    selectedTask.getBookmark().setBookmarked(false);
                } else {
                    tappedButton.setBackgroundColor(ContextCompat.getColor(thisContext, R.color.colorAccent));
                    selectedTask.getBookmark().setBookmarked(true);
                }
            }
        });
    }
}
