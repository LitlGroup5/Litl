package com.litlgroup.litl.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.litlgroup.litl.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Hari on 9/14/2016.
 */
public class RatingRadarChartFragment extends DialogFragment{


    public RatingRadarChartFragment()
    {

    }

    public static RatingRadarChartFragment newInstance(List<String> ratings, float avgRating)
    {
        RatingRadarChartFragment frag = new RatingRadarChartFragment();

        frag.ratings = ratings;
        frag.avgRating = avgRating;
        return frag;
    }


    @BindView(R.id.chartRatingRadar)
    RadarChart radarChart;

    @BindView(R.id.srbProfileRating)
    SimpleRatingBar srbProfileRating;


    List<String> ratings;
    Float avgRating;

    private Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating_radar, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        radarChart = (RadarChart) view.findViewById(R.id.chartRatingRadar);
//        radarChart.setBackgroundColor(Color.rgb(60, 65, 82));

        radarChart.setDescription("");

        radarChart.setWebLineWidth(1f);
        radarChart.setWebColor(Color.LTGRAY);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColorInner(Color.LTGRAY);
        radarChart.setWebAlpha(100);

        setData();

        radarChart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);


        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setTextSize(15f);
        xAxis.setYOffset(2f);
        xAxis.setXOffset(2f);
        xAxis.setValueFormatter(new AxisValueFormatter() {

            private String[] mActivities
                    = new String[]{"1", "2", "3", "4", "5"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        xAxis.setTextColor(getResources().getColor(R.color.colorAccent));

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setTextSize(9f);
        yAxis.setTextColor(Color.WHITE);
        yAxis.setAxisMinValue(0f);


        initializeRatingBar();

    }


    private void initializeRatingBar()
    {
        try
        {
            if(avgRating >= 0) {
                SimpleRatingBar.AnimationBuilder builder =
                        srbProfileRating.getAnimationBuilder()
                                .setRatingTarget(avgRating)
                                .setDuration(2000)
                                .setInterpolator(new LinearInterpolator())
                                .setRepeatCount(0);
                builder.start();
            }
        }
        catch (Exception ex)
        {
            Timber.e("Error initializing rating bar");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void setData() {

        int cnt = ratings.size();

        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < cnt; i++) {

            float val1 = Float.parseFloat(ratings.get(i));
            entries1.add(new RadarEntry(val1));
        }

        RadarDataSet set1 = new RadarDataSet(entries1, "Ratings");
        set1.setColor(getResources().getColor(R.color.colorAccent));
//        set1.setFillColor(Color.rgb(103, 110, 129));
        set1.setFillColor(getResources().getColor(R.color.colorAccent));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(Typeface.DEFAULT);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        radarChart.setData(data);
        radarChart.invalidate();
    }
}
