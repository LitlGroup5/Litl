package com.litlgroup.litl.adapters;

/**
 * Created by monusurana on 9/17/16.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.litlgroup.litl.R;
import com.litlgroup.litl.models.BottomSheetItem;

import java.util.List;

public class BottomSheetAdapter extends BaseAdapter {
    protected List<BottomSheetItem> bsItems;
    protected LayoutInflater inflater;

    BottomSheetItemClickListener mListener;
    int mSelectedIndex;

    static class ViewHolder {
        ImageView image;
        TextView text;
    }

    public interface BottomSheetItemClickListener {
        void onClick(int position);
    }

    public void setBottomSheetItemClickListener(BottomSheetItemClickListener listener) {
        mListener = listener;
    }


    public BottomSheetAdapter(Context context, List bsItems, int selectedIndex) {
        this.bsItems = bsItems;
        this.inflater = LayoutInflater.from(context);
        mSelectedIndex = selectedIndex;
    }

    @Override
    public int getCount() {
        return bsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return bsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        BottomSheetItem item = (BottomSheetItem) bsItems.get(position);
        return item.getImage();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        BottomSheetItem item = (BottomSheetItem) bsItems.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.bottom_sheet_item, parent, false);
            convertView.setTag(holder);

            holder.image = (ImageView) convertView.findViewById(R.id.bs_image);
            holder.text = (TextView) convertView.findViewById(R.id.bs_text);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageResource(item.getImage());

        if (position == mSelectedIndex) {
            holder.image.setColorFilter(ContextCompat.getColor(holder.image.getContext(), R.color.colorAccent));
            holder.image.setBackground(ContextCompat.getDrawable(holder.image.getContext(), R.drawable.category_background));
            holder.image.getBackground().setAlpha(70);
        }

        holder.text.setText(item.getText());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(position);
            }
        });

        return convertView;
    }
}