package com.litlgroup.litl.utils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.litlgroup.litl.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monusurana on 8/20/16.
 */
public class MediaPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mImageUrls = new ArrayList<>();

    public MediaPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.view_pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.ivMedia);
        ImageUtils.setImage(imageView, mImageUrls.get(position));
        container.addView(itemView);

        return itemView;
    }

    public void addImage(String url) {
        mImageUrls.add(url);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
