package com.litlgroup.litl.utils;

import android.content.Context;
import android.media.MediaPlayer;
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
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Hari on 8/26/2016.
 */
public class AdvancedMediaPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mImageUrls = new ArrayList<>();

    StartImageCaptureListener startImageCaptureListener;
    StartVideoCaptureListener startVideoCaptureListener;
    StartImageSelectListener startImageSelectListener;

    public AdvancedMediaPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageUrls.add(null);
    }

    public AdvancedMediaPagerAdapter(Context context,
                                     StartImageCaptureListener startImageCaptureListener,
                                     StartVideoCaptureListener startVideoCaptureListener,
                                     StartImageSelectListener startImageSelectListener) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageUrls.add(null);
        this.startImageCaptureListener = startImageCaptureListener;
        this.startVideoCaptureListener = startVideoCaptureListener;
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
        final ScalableVideoView mVideoView = (ScalableVideoView) itemView.findViewById(R.id.video_view);
        String url = mImageUrls.get(position);


        final ImageButton ibSelectImage = (ImageButton) itemView.findViewById(R.id.ibSelectImage);
        final ImageButton ibCaptureImage = (ImageButton) itemView.findViewById(R.id.ibCaptureImage);
        final ImageButton ibCaptureVideo = (ImageButton) itemView.findViewById(R.id.ibCaptureVideo);

        if(url != null && !url.isEmpty()) {

            if(isImageFile(url)) {
                Glide.with(mContext)
                        .load(url)
                        .fitCenter()
                        .sizeMultiplier(0.5f)
                        .centerCrop()
                        .into(ivMediaImage);

                ibCaptureImage.setVisibility(View.INVISIBLE);
                ibSelectImage.setVisibility(View.INVISIBLE);
                ibCaptureVideo.setVisibility(View.INVISIBLE);
            }
            else if (isVideoFile(url))
            {
                try {
                    mVideoView.setDataSource(url);
                    mVideoView.setLooping(true);
                    ibCaptureImage.setVisibility(View.INVISIBLE);
                    ibSelectImage.setVisibility(View.INVISIBLE);

                    mVideoView.prepare(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mVideoView.start();
                            ibCaptureVideo.setVisibility(View.INVISIBLE);
                        }
                    });
                } catch (IOException ioe) {
                    Timber.e("Error playing video");
                }
            }
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


            ibCaptureVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ibCaptureVideo.setAlpha(0.3f);
                        if(startImageCaptureListener != null)
                        {
                            startVideoCaptureListener.onStartVideoCapture(position);
                        }
                        else {
                            StartVideoCaptureListener listener = (StartVideoCaptureListener) mContext;
                            listener.onStartVideoCapture(position);
                        }
                        ibCaptureVideo.setAlpha(1f);
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

    public boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("image") == 0;
    }

    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("video") == 0;
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

    public interface StartVideoCaptureListener
    {
        void onStartVideoCapture(int pageIndex);
    }

    public interface StartImageSelectListener
    {
        void onStartImageSelect(int pageIndex);
    }

}
