package com.litlgroup.litl.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.astuetz.PagerSlidingTabStrip;
import com.litlgroup.litl.R;
import com.litlgroup.litl.fragments.OffersFragment;
import com.litlgroup.litl.fragments.ProposalsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WallActivity extends AppCompatActivity {


    @BindView(R.id.fabCreateTask)
    android.support.design.widget.FloatingActionButton fabCreateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        ButterKnife.bind(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TaskPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);
    }


    public class TaskPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Proposals", "Offers" };

        public TaskPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new ProposalsFragment();
            } else {
                return new OffersFragment();
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

    @OnClick(R.id.fabCreateTask)
    public void launchCreateTaskActivity()
    {
        Intent intent = new Intent(WallActivity.this, CreateTaskActivity.class);
        startActivity(intent);

    }
}
