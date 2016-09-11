package com.litlgroup.litl.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.litlgroup.litl.R;
import com.litlgroup.litl.activities.CreateTaskActivity;
import com.litlgroup.litl.utils.ZoomOutPageTransformer;

/**
 * A simple {@link Fragment} subclass.
 */
public class WallFragment extends Fragment
{
    private String chosenCategory;

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

        getActivity().setTitle("Offers");

        setupViewPagerAndSlidingTabs(view);
        setupFloatingActionButton(view);

        return view;
    }

    private void setupViewPagerAndSlidingTabs(View v) {
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TaskPagerAdapter(getActivity().getSupportFragmentManager(), chosenCategory));
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    getActivity().setTitle("Offers");
                else
                    getActivity().setTitle("Proposals");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);
    }

    private void setupFloatingActionButton(View view) {
        FloatingActionButton fabCreateTask = (FloatingActionButton) view.findViewById(R.id.fabCreateTask);
        fabCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCreateTaskActivity();
            }
        });

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fabCreateTask.getLayoutParams();
        lp.setAnchorId(View.NO_ID);
        lp.gravity = Gravity.BOTTOM | GravityCompat.END;
        fabCreateTask.setLayoutParams(lp);
    }

    public void launchCreateTaskActivity() {
        Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
        startActivity(intent);
    }

    public class TaskPagerAdapter extends FragmentStatePagerAdapter
            implements PagerSlidingTabStrip.IconTabProvider

    {
        private String tabTitles[] = {"Offers", "Proposals"};
        private String chosenCategory;


        public TaskPagerAdapter(FragmentManager fm, String category) {
            super(fm);
            chosenCategory = category;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return OffersFragment.newInstance(chosenCategory);
            } else {
                return ProposalsFragment.newInstance(chosenCategory);
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }


        private int[] tabIcons =
                {
                        R.drawable.tabicon_proposals_vector,
                        R.drawable.tabicon_offers_vector

                };

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return tabTitles[position];
//        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getPageIconResId(int position) {
            return  tabIcons[position];
        }
    }
}
