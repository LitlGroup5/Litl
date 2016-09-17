package com.litlgroup.litl.adapters;

/**
 * Created by Hari on 9/17/2016.
 */

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.litlgroup.litl.R;
import com.litlgroup.litl.interfaces.SkillCardAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkillsCardPagerAdapter extends PagerAdapter implements SkillCardAdapter {

    private List<CardView> mViews;
    private List<String> mSkills;
    private float mBaseElevation;
    Context context;
    HashMap<String, Integer> mSkillsHash;
    public SkillsCardPagerAdapter(Context context) {

        this.context = context;
        mSkills = new ArrayList<>();
        mViews = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
//            mSkills.add("");
//            mViews.add(null);
        }

        initializeSkillsHashMap();
    }

    private void initializeSkillsHashMap()
    {
        mSkillsHash = new HashMap<>();
        mSkillsHash.put(context.getString(R.string.automotive), R.drawable.automobile);
        mSkillsHash.put(context.getString(R.string.decorating), R.drawable.decorating);
        mSkillsHash.put(context.getString(R.string.electronics), R.drawable.electronics);
        mSkillsHash.put(context.getString(R.string.arts_and_crafts), R.drawable.arts_and_crafts);
        mSkillsHash.put(context.getString(R.string.gardening), R.drawable.gardening);
        mSkillsHash.put(context.getString(R.string.woodworking), R.drawable.woodworking);
        mSkillsHash.put(context.getString(R.string.house_cleaning), R.drawable.house_cleaning);
        mSkillsHash.put(context.getString(R.string.recreation), R.drawable.recreation);
        mSkillsHash.put(context.getString(R.string.plumbing), R.drawable.plumbing);
        mSkillsHash.put(context.getString(R.string.groceries), R.drawable.groceries);
        mSkillsHash.put(context.getString(R.string.random), R.drawable.shrug_icon);

    }

    public void add(String skill)
    {
        mSkills.add(skill);
        mViews.add(null);
    }

    public void addAll (List<String> skills)
    {
        mSkills.addAll(skills);

        for(int i =0 ; i < skills.size(); i++)
        {
            mViews.add(null);
        }
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mSkills.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.profile_skill_card, container, false);
        container.addView(view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        ImageView ivSkill = (ImageView) cardView.findViewById(R.id.ivSkill);
        TextView tvSkill = (TextView) cardView.findViewById(R.id.tvSkill);

        String skill = mSkills.get(position);

        tvSkill.setText(skill);

        if(mSkillsHash.containsKey(skill))
        {
            ivSkill.setImageResource(mSkillsHash.get(skill));
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(context.getResources().getColor(R.color.colorSkyBlue),
                    PorterDuff.Mode.SRC_ATOP);
            ivSkill.setColorFilter(porterDuffColorFilter);
        }


        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

}
