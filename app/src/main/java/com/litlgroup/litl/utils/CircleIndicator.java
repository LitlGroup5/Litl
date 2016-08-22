package com.litlgroup.litl.utils;

import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.litlgroup.litl.R;

import butterknife.BindDrawable;
import butterknife.ButterKnife;

/**
 * Created by monusurana on 8/20/16.
 */
public class CircleIndicator {
    private int mCount;
    private LinearLayout mLinearLayout;
    private ViewPager mViewPager;
    private ImageView[] dots;

    @BindDrawable(R.drawable.dot_selected)
    protected Drawable mDotSelected;
    @BindDrawable(R.drawable.dot_unselected)
    protected Drawable mDotUnselected;

    public CircleIndicator(LinearLayout linearLayout, ViewPager viewPager) {
        mLinearLayout = linearLayout;
        mViewPager = viewPager;
        ButterKnife.bind(this, linearLayout);
    }

    public void setViewPagerIndicator() {
        mCount = mViewPager.getAdapter().getCount();
        dots = new ImageView[mCount];

        for (int i = 0; i < mCount; i++) {
            dots[i] = new ImageView(mLinearLayout.getContext());
            dots[i].setImageDrawable(mDotUnselected);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            mLinearLayout.addView(dots[i], params);
        }

        dots[mViewPager.getCurrentItem()].setImageDrawable(mDotSelected);

        setViewPagerPageListener();
    }

    private void setViewPagerPageListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mCount; i++) {
                    if (i == position)
                        dots[position].setImageDrawable(mDotSelected);
                    else
                        dots[i].setImageDrawable(mDotUnselected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
