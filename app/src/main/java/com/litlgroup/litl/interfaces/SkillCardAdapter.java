package com.litlgroup.litl.interfaces;

import android.support.v7.widget.CardView;

/**
 * Created by Hari on 9/17/2016.
 */
public interface SkillCardAdapter {


    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
