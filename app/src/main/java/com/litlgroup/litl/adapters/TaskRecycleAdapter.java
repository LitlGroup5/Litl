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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.litlgroup.litl.R;
import com.litlgroup.litl.models.Address;
import com.litlgroup.litl.models.Task;

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

        ImageButton ibBookmark = (ImageButton) taskCardView.findViewById(R.id.ibBookmark);
        setUpAndManageBookmarkButtonState(ibBookmark, task, position);
    }

    private void setUpAndManageBookmarkButtonState(final ImageButton bookmarkButton, Task task, final int position) {
//        if (task.getBookmark().getBookmarked() && task.getType() != Task.Type.CLOSED) {
//            bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled);
//        } else {
//            bookmarkButton.setImageResource(R.drawable.ic_bookmark_border);
//        }
//        bookmarkButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Task selectedTask = tasks.get(position);
//
//                if (selectedTask.getBookmark().getBookmarked()) {
//                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_border);
//                    selectedTask.getBookmark().setBookmarked(false);
//                } else {
//                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled);
//                    selectedTask.getBookmark().setBookmarked(true);
//                }
//            }
//        });
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
