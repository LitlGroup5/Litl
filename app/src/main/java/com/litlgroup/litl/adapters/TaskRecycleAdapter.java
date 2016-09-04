package com.litlgroup.litl.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.litlgroup.litl.R;
import com.litlgroup.litl.interfaces.RemoveBookmarkListener;
import com.litlgroup.litl.models.Address;
import com.litlgroup.litl.models.Task;

import java.util.ArrayList;

/**
 * Created by andrj148 on 8/20/16.
 */
public class TaskRecycleAdapter extends RecyclerView.Adapter<TaskRecycleAdapter.ViewHolder> {
    private ArrayList<Task> tasks;
    private Context thisContext;
    private RemoveBookmarkListener removeBookmarkListener;

    public ImageView ivBackground;

    public static interface RemoveBookmarkListener {
        public void onClick(int position, boolean isBookmarked);
    }

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

    public void setRemoveBookmarkListener(RemoveBookmarkListener listener) {
        removeBookmarkListener = listener;
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

    public ImageView getIvBackground() {
        return ivBackground;
    }

    private void setSubviews(ViewHolder viewHolder, final Task task, final int position) {
        final CardView taskCardView = viewHolder.cardView;
        taskCardView.setTag(position);

        ivBackground = (ImageView) taskCardView.findViewById(R.id.ivBackground);

        if (task.getMedia().size() > 0) {
            Glide.with(taskCardView.getContext()).load(task.getMedia().get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivBackground);
            convertClosedTaskBackgroundImageToBlackAndWhite(ivBackground, task.getType());
        }

        final ImageView ivAvatar = (ImageView) taskCardView.findViewById(R.id.ivAvatar);
        Glide.with(taskCardView.getContext()).load(task.getUser().getPhoto()).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(new BitmapImageViewTarget(ivAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(taskCardView.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ivAvatar.setImageDrawable(circularBitmapDrawable);
            }
        });

        final TextView tvDescription = (TextView) taskCardView.findViewById(R.id.tvDescription);
        tvDescription.setText(task.getDescription());

        TextView tvAddress = (TextView) taskCardView.findViewById(R.id.tvAddress);
        tvAddress.setText(Address.getDisplayString(task.getAddress()));

        TextView tvDeadlineDate = (TextView) taskCardView.findViewById(R.id.tvDeadlineDate);
        tvDeadlineDate.setText(task.getDeadlineDate());

        LikeButton bookmarkButton = (LikeButton) taskCardView.findViewById(R.id.btnBookmark);
        setUpAndManageBookmarkButtonState(bookmarkButton, task, position);
    }

    private void setUpAndManageBookmarkButtonState(final LikeButton bookmarkButton, final Task task, final int position) {
        bookmarkButtonAnimation(bookmarkButton, task);

        bookmarkButton.setOnLikeListener(new OnLikeListener() {
            Task selectedTask = tasks.get(position);

            @Override
            public void liked(LikeButton likeButton) {
                if (removeBookmarkListener != null) {
                    removeBookmarkListener.onClick(position, true);
                }
                changeTaskBookmarkStatus(selectedTask);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (removeBookmarkListener != null) {
                    removeBookmarkListener.onClick(position, false);
                }
                changeTaskBookmarkStatus(selectedTask);
            }
        });
    }

    private void changeTaskBookmarkStatus(Task selectedTask) {
        if (Task.isBookmarked(selectedTask)) {
            Task.updateBookmark(selectedTask, false);
        } else {
            Task.updateBookmark(selectedTask, true);
        }
    }

    private void bookmarkButtonAnimation(LikeButton bookmarkButton, Task task) {
        if (Task.isBookmarked(task) && task.getType() != Task.Type.CLOSED) {
            bookmarkButton.setLiked(true);
        } else {
            bookmarkButton.setLiked(false);
        }
    }

    private void convertClosedTaskBackgroundImageToBlackAndWhite(ImageView closedTaskImageView, Task.Type type) {
        ColorMatrix matrix = new ColorMatrix();

        if (type == Task.Type.CLOSED) {
            matrix.setSaturation(0);
        } else {
            matrix.setSaturation(1);
        }
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        closedTaskImageView.setColorFilter(filter);
    }
}
