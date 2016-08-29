package com.litlgroup.litl.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.litlgroup.litl.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Hari on 8/26/2016.
 */
public class AdvancedMediaPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mImageUrls = new ArrayList<>();

    StartImageCaptureListener startImageCaptureListener;

    StartImageSelectListener startImageSelectListener;

    public AdvancedMediaPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageUrls.add(null);
    }

    public AdvancedMediaPagerAdapter(Context context, StartImageCaptureListener startImageCaptureListener, StartImageSelectListener startImageSelectListener) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageUrls.add(null);
        this.startImageCaptureListener = startImageCaptureListener;
        this.startImageSelectListener =  startImageSelectListener;
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
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.media_image_item, container, false);

        ImageView ivMediaImage = (ImageView) itemView.findViewById(R.id.ivMediaImage);
        String url = mImageUrls.get(position);


        final ImageButton ibSelectImage = (ImageButton) itemView.findViewById(R.id.ibSelectImage);
        final ImageButton ibCaptureImage = (ImageButton) itemView.findViewById(R.id.ibCaptureImage);

        if(url != null && !url.isEmpty()) {

            Glide.with(mContext)
                            .load(url)
                            .fitCenter()
                            .sizeMultiplier(0.5f)
                            .centerCrop()
                            .into(ivMediaImage);

            ibCaptureImage.setVisibility(View.INVISIBLE);
            ibSelectImage.setVisibility(View.INVISIBLE);
        }
        else {

            ibSelectImage.setVisibility(View.VISIBLE);

            ibSelectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ibSelectImage.setAlpha(0.3f);
                    if(startImageSelectListener != null)
                    {
                        startImageSelectListener.onStartImageSelect(position);
                    }
                    else {
                        StartImageSelectListener listener = (StartImageSelectListener) mContext;
                        listener.onStartImageSelect(position);
                    }
                    ibSelectImage.setAlpha(1f);
                }
            });

            ibCaptureImage.setVisibility(View.VISIBLE);

            ibCaptureImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ibCaptureImage.setAlpha(0.3f);
                        if(startImageCaptureListener != null)
                        {
                            startImageCaptureListener.onStartImageCapture(position);
                        }
                        else {
                            StartImageCaptureListener listener = (StartImageCaptureListener) mContext;
                            listener.onStartImageCapture(position);
                        }
                        ibCaptureImage.setAlpha(1f);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        ButterKnife.bind(this, itemView);
        container.addView(itemView);

        return itemView;
    }

    public void addImage(String url) {
        mImageUrls.add(url);
        mImageUrls.add(null);
    }

    public void insert(Uri uri, int index)
    {
        mImageUrls.add(index, uri.getPath());
        if(index == mImageUrls.size() - 1)
        {
            mImageUrls.add(null);
        }
    }

    public void insertUri(Uri uri, int index)
    {
        mImageUrls.add(index, uri.toString());
        if(index == mImageUrls.size() - 1)
        {
            mImageUrls.add(null);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface StartImageCaptureListener
    {
        void onStartImageCapture(int pageIndex);
    }

    public interface StartImageSelectListener
    {
        void onStartImageSelect(int pageIndex);
    }

}
