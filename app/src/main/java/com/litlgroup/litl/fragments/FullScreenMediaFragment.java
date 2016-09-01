package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.litlgroup.litl.R;
import com.litlgroup.litl.utils.AdvancedMediaPagerAdapter;
import com.litlgroup.litl.utils.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Hari on 8/30/2016.
 */
public class FullScreenMediaFragment
        extends Fragment
{


    List<String> mediaUrls;

    public static FullScreenMediaFragment newInstance(List<String> mediaUrls)
    {
        FullScreenMediaFragment frag = new FullScreenMediaFragment();

        frag.mediaUrls = mediaUrls;
        return frag;
    }

    @BindView(R.id.vpMedia)
    ViewPager mVpMedia;

    @BindView(R.id.vpIndicator)
    LinearLayout mViewPagerCountDots;

    CircleIndicator circleIndicator;
    AdvancedMediaPagerAdapter mediaPagerAdapter;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_full_screen, container, false);
        unbinder = ButterKnife.bind(this, view);
        try {

            if(savedInstanceState != null)
            {
                mediaUrls = savedInstanceState.getStringArrayList("mediaUrls");
            }

            setupViewPager();

            mediaPagerAdapter.removeAll();
            mediaPagerAdapter.addAll(mediaUrls);

            mediaPagerAdapter.notifyDataSetChanged();
            circleIndicator.refreshIndicator();
        }
        catch (Exception ex)
        {
            Timber.e("Error creating View", ex);
        }
        return view;
    }

    private void setupViewPager() {
        mediaPagerAdapter = new AdvancedMediaPagerAdapter(getActivity(), false, false);
        mVpMedia.setAdapter(mediaPagerAdapter);
        circleIndicator = new CircleIndicator(mViewPagerCountDots, mVpMedia);
        circleIndicator.setViewPagerIndicator();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putStringArrayList("mediaUrls", (ArrayList)mediaUrls);

    }


}
