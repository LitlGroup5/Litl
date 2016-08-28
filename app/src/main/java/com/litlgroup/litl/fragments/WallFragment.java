package com.litlgroup.litl.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.litlgroup.litl.R;
import com.litlgroup.litl.activities.CreateTaskActivity;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class WallFragment extends Fragment {
    private String chosenCategory;

    @BindView(R.id.fabCreateTask)
    android.support.design.widget.FloatingActionButton fabCreateTask;

    public WallFragment() {
        // Required empty public constructor
    }

    public static WallFragment newInstance(String category) {

        WallFragment wallFragment = new WallFragment();
        wallFragment.chosenCategory = category;

        return wallFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        setupViewPagerAndSlidingTabs(view);
        return view;
    }

    private void setupViewPagerAndSlidingTabs(View v) {
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TaskPagerAdapter(getActivity().getSupportFragmentManager(), chosenCategory));

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);
    }

    @OnClick(R.id.fabCreateTask)
    public void launchCreateTaskActivity() {
        Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
        startActivity(intent);
    }

    public class TaskPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Proposals", "Offers" };
        private String chosenCategory;


        public TaskPagerAdapter(FragmentManager fm, String category) {
            super(fm);
            chosenCategory = category;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return ProposalsFragment.newInstance(chosenCategory);
            } else {
                return OffersFragment.newInstance(chosenCategory);
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
