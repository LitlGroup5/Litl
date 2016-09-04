package com.litlgroup.litl.utils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.litlgroup.litl.R;
import com.litlgroup.litl.models.SignupItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monusurana on 8/20/16.
 */
public class SignInPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<SignupItem> mSignupItem = new ArrayList<>();

    public SignInPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mSignupItem.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.signin_vp_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.ivSignInBackground);
        ImageUtils.setBlurImage(imageView, mSignupItem.get(position).getImageResourceId());

        TextView textView = (TextView) itemView.findViewById(R.id.text);
        textView.setText(mSignupItem.get(position).getMainText());

        if (mSignupItem.get(position).getTextColor() != SignupItem.DEFAULT_COLOR)
            textView.setTextColor(mSignupItem.get(position).getTextColor());

        TextView textDescription = (TextView) itemView.findViewById(R.id.textDescription);
        if (mSignupItem.get(position).getDescriptionText() != null) {
            textDescription.setText(mSignupItem.get(position).getDescriptionText());

            if (mSignupItem.get(position).getTextColor() != SignupItem.DEFAULT_COLOR)
                textDescription.setTextColor(mSignupItem.get(position).getTextColor());
        }

        container.addView(itemView);

        return itemView;
    }

    public void addImage(SignupItem signupItem) {
        mSignupItem.add(signupItem);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
