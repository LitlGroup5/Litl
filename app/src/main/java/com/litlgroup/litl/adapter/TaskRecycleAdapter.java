package com.litlgroup.litl.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
            private ImageView ivBackground;
            private ImageView ivAvatar;
            private ImageButton ibBookmark;
            private TextView tvDescription;

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

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
