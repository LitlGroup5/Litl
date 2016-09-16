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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.litlgroup.litl.R;
import com.wang.avi.AVLoadingIndicatorView;
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
    StartOnItemViewClickListener startOnItemViewClickListener;

    boolean allowCapture = true;
    boolean allowTabClickListener = true;

    public AdvancedMediaPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageUrls.add(null);
    }


    public AdvancedMediaPagerAdapter(Context context, boolean allowCapture, boolean allowTabClickListener) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(allowCapture)
            mImageUrls.add(null);
        this.allowCapture = allowCapture;
        this.allowTabClickListener = allowTabClickListener;
    }

    public AdvancedMediaPagerAdapter(Context context, boolean allowCapture,
                                     boolean allowTabClickListener,
                                     StartOnItemViewClickListener startOnItemViewClickListener) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(allowCapture)
            mImageUrls.add(null);
        this.allowCapture = allowCapture;
        this.allowTabClickListener = allowTabClickListener;

        this.startOnItemViewClickListener = startOnItemViewClickListener;
    }

    public AdvancedMediaPagerAdapter(Context context,
                                     StartImageCaptureListener startImageCaptureListener,
                                     StartVideoCaptureListener startVideoCaptureListener,
                                     StartImageSelectListener startImageSelectListener,
                                     boolean allowCapture,
                                     boolean allowTabClickListener

    ) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(allowCapture)
            mImageUrls.add(null);
        this.startImageCaptureListener = startImageCaptureListener;
        this.startVideoCaptureListener = startVideoCaptureListener;
        this.startImageSelectListener =  startImageSelectListener;

        this.allowCapture = allowCapture;
        this.allowTabClickListener = allowTabClickListener;
    }

    public AdvancedMediaPagerAdapter(Context context,
                                     StartImageCaptureListener startImageCaptureListener,
                                     StartVideoCaptureListener startVideoCaptureListener,
                                     StartImageSelectListener startImageSelectListener,
                                     StartOnItemViewClickListener startOnItemViewClickListener,
                                     boolean allowCapture,
                                     boolean allowTabClickListener

    ) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(allowCapture)
            mImageUrls.add(null);
        this.startImageCaptureListener = startImageCaptureListener;
        this.startVideoCaptureListener = startVideoCaptureListener;
        this.startImageSelectListener =  startImageSelectListener;
        this.startOnItemViewClickListener = startOnItemViewClickListener;

        this.allowCapture = allowCapture;
        this.allowTabClickListener = allowTabClickListener;
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
        View itemView = mLayoutInflater.inflate(R.layout.media_image_video_item, container, false);

        setupViewClickListener(itemView, position);
        ImageView ivMediaImage = (ImageView) itemView.findViewById(R.id.ivMediaImage);
        final ScalableVideoView mVideoView = (ScalableVideoView) itemView.findViewById(R.id.video_view);
        String url = mImageUrls.get(position);

        final ImageButton ibSelectImage = (ImageButton) itemView.findViewById(R.id.ibSelectImage);
        final ImageButton ibCaptureImage = (ImageButton) itemView.findViewById(R.id.ibCaptureImage);
        final ImageButton ibCaptureVideo = (ImageButton) itemView.findViewById(R.id.ibCaptureVideo);

        final AVLoadingIndicatorView avi = (AVLoadingIndicatorView) itemView.findViewById(R.id.avi);

        ibCaptureImage.setVisibility(View.INVISIBLE);
        ibSelectImage.setVisibility(View.INVISIBLE);
        ibCaptureVideo.setVisibility(View.INVISIBLE);

        if(url != null && !url.isEmpty()) {

            if(isImageFile(url)) {

                if(avi.getVisibility() == View.INVISIBLE | avi.getVisibility() == View.GONE)
                    avi.show();
                avi.setIndicatorColor(R.color.colorImageLoadingIndicator);
                mVideoView.setVisibility(View.GONE);
                ivMediaImage.setVisibility(View.VISIBLE);

                ibCaptureImage.setVisibility(View.GONE);
                ibSelectImage.setVisibility(View.GONE);
                ibCaptureVideo.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load(url)
                        .fitCenter()
                        .sizeMultiplier(0.5f)
                        .centerCrop()
//                        .placeholder(R.drawable.load_placeholder)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                avi.hide();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                avi.hide();
                                return false;
                            }
                        })
                        .into(ivMediaImage);

            }
            else if (isVideoFile(url))
            {
                try {

                    ivMediaImage.setVisibility(View.GONE);
                    mVideoView.setVisibility(View.VISIBLE);

                    ibCaptureImage.setVisibility(View.GONE);
                    ibSelectImage.setVisibility(View.GONE);
                    ibCaptureVideo.setVisibility(View.GONE);

                    avi.setIndicatorColor(R.color.colorVideoLoadingIndicator);
                    if(avi.getVisibility() == View.INVISIBLE | avi.getVisibility() == View.GONE)
                        avi.show();
                    mVideoView.setDataSource(url);

                    mVideoView.prepareAsync(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mVideoView.start();
                            avi.hide();
                        }
                    });
                } catch (IOException ioe) {
                    Timber.e("Error Starting video");
                }
                setupVideoViewClickListener(mVideoView);
            }
        }
        else {

            avi.hide();
            if(allowCapture) {

                ibSelectImage.setVisibility(View.VISIBLE);
                ibCaptureImage.setVisibility(View.VISIBLE);
                ibCaptureVideo.setVisibility(View.VISIBLE);

                ibSelectImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ibSelectImage.setAlpha(0.3f);
                        if (startImageSelectListener != null) {
                            startImageSelectListener.onStartImageSelect(position);
                        } else {
                            StartImageSelectListener listener = (StartImageSelectListener) mContext;
                            listener.onStartImageSelect(position);
                        }
                        ibSelectImage.setAlpha(1f);
                    }
                });


                ibCaptureImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ibCaptureImage.setAlpha(0.3f);
                            if (startImageCaptureListener != null) {
                                startImageCaptureListener.onStartImageCapture(position);
                            } else {
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
                            if (startImageCaptureListener != null) {
                                startVideoCaptureListener.onStartVideoCapture(position);
                            } else {
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
            else
            {
                ibSelectImage.setVisibility(View.GONE);
                ibCaptureImage.setVisibility(View.GONE);
                ibCaptureVideo.setVisibility(View.GONE);
            }
        }

        ButterKnife.bind(this, itemView);
        container.addView(itemView);

        return itemView;
    }

    private void setupVideoViewClickListener(final ScalableVideoView mVideoView)
    {
        try {
            final boolean[] isPlaying = {false};
            mVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        if (isPlaying[0]) {
                            mVideoView.pause();
                            isPlaying[0] = false;
                        } else {
                            mVideoView.start();
                            isPlaying[0] = true;
                        }

                    } catch (Exception ex) {
                        Timber.e("Error playing video", ex);
                    }
                }
            });
        }
        catch (Exception ex)
        {
            Timber.e(ex.toString());
        }
    }

    private void setupViewClickListener(View itemView, final int position)
    {
        if(allowTabClickListener)
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mImageUrls != null && mImageUrls.get(position) != null && !mImageUrls.get(position).isEmpty()) {

                    if(startOnItemViewClickListener != null)
                    {
                        startOnItemViewClickListener.onStartItemViewClicked(position);

                    }
                    else {
                        StartOnItemViewClickListener listener = (StartOnItemViewClickListener) mContext;
                        listener.onStartItemViewClicked(position);
                    }
                }
            }
        });
    }

    public void addImage(String url) {

        if(url == null)
            return;

        if(mImageUrls.contains(null)) //usually happens in capture mode
            mImageUrls.remove(null);
        mImageUrls.add(url);
        if(allowCapture)
            mImageUrls.add(null);
    }

    public void addAll(List<String> urls)
    {
        if(mImageUrls.contains(null))
            mImageUrls.remove(null);
        mImageUrls.addAll(urls);
        if(allowCapture)
            mImageUrls.add(null);
    }

    public void insert(Uri uri, int index)
    {
        mImageUrls.add(index, uri.getPath());

        if(allowCapture && index == mImageUrls.size() - 1)
        {
            mImageUrls.add(null);
        }
    }

    public void insertUri(Uri uri, int index)
    {
        mImageUrls.add(index, uri.toString());
        if(allowCapture && index == mImageUrls.size() - 1)
        {
            mImageUrls.add(null);
        }
    }

    public void remove(int index)
    {
        mImageUrls.remove(index);

        if(allowCapture && index == mImageUrls.size() - 1)
        {
            if(!mImageUrls.contains(null))
                mImageUrls.add(null);
        }
    }

    public void removeAll()
    {
        mImageUrls.clear();
    }

    public boolean isImageFile(String path) {
        if(path == null || path.isEmpty())
            return false;
        Uri uri = Uri.parse(path);
        if(uri != null &&
                uri.getAuthority() != null &&
                uri.getAuthority().contains("firebase")) { //if loading from firebase url
            if(path.contains("image") || path.contains("jpg"))
                return true;
        }
        else {


            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.indexOf("image") == 0;
        }
        return false;
    }

    public boolean isVideoFile(String path) {

        if(path == null || path.isEmpty())
            return false;
        Uri uri = Uri.parse(path);
        if(     uri != null &&
                uri.getAuthority() != null &&
                uri.getAuthority().contains("firebase")) { //if loading from firebase url
            if(path.contains("video") || path.contains("mp4"))
                return true;
        }
        else { //if loading from device
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.indexOf("video") == 0;
        }
        return false;
    }

    public List<String> getUrls()
    {
        return mImageUrls;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setAllowCapture(boolean state)
    {
        allowCapture = state;
        if(state) {
            if(mImageUrls.size() >= 1) {
                if (mImageUrls.get(mImageUrls.size() - 1) != null)
                    mImageUrls.add(null);
            }
            else
                mImageUrls.add(null);

        }
        else
        {
            if (mImageUrls.get(mImageUrls.size() - 1) == null)
                mImageUrls.remove(null);
        }
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

    public interface StartOnItemViewClickListener
    {
        void onStartItemViewClicked(int pageIndex);
    }

}
