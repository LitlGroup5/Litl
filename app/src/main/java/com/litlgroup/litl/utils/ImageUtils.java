package com.litlgroup.litl.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.litlgroup.litl.R;
import com.litlgroup.litl.models.Address;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import timber.log.Timber;

/**
 * Created by monusurana on 8/20/16.
 */
public class ImageUtils {

    public static void setCircularImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_profile_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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


    public static void setBlurredMapBackground(Address address, ImageView imageView)
    {
        try {
            String mapAddressQuery = "";

            String streetAddress= address.getStreetAddress();
            String city = address.getCity();
            String state = address.getState();

            if(streetAddress == null || streetAddress.isEmpty())
                streetAddress = " ";

            if(city == null || city.isEmpty())
                city = " ";

            if(state == null || state.isEmpty())
                state = " ";

            Context context = imageView.getContext();
            String baseUrl = context.getString(R.string.static_map_base_url);
            String scale = "2";
            String mapType = "hybrid";
            String zoom = "10";
            String size = "400x640";
            String apiKey = context.getString(R.string.static_map_api_key);


            if (!streetAddress.isEmpty() && !city.isEmpty() && !state.isEmpty())
                mapAddressQuery = String.format("%s,%s,%s", address.getStreetAddress(), address.getCity(), address.getState());
            else if(streetAddress.equals(" ") && city.equals(" ") && state.equals(" "))
            {
                mapAddressQuery = "usa";
                zoom = "4";
            }

            if (mapAddressQuery.isEmpty())
                return;
            String url = String.format("%s?maptype=%s&scale=%s&center=%s&zoom=%s&size=%s&key=%s",
                    baseUrl,
                    mapType,
                    scale,
                    mapAddressQuery,
                    zoom,
                    size,
                    apiKey
            );

            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .bitmapTransform(new BlurTransformation(context, 7, 1))
                    .into(imageView);
        }
        catch (Exception ex)
        {
            Timber.e("Error setting map background");
        }
    }


}
