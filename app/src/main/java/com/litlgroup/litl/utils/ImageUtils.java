package com.litlgroup.litl.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.litlgroup.litl.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by monusurana on 8/20/16.
 */
public class ImageUtils {

    public static void setCircularImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_profile_placeholder)
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .into(imageView);
    }

    public static void setImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }
}
