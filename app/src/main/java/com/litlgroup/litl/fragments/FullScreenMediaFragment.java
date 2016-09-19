package com.litlgroup.litl.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.pwittchen.swipe.library.Swipe;
import com.github.pwittchen.swipe.library.SwipeListener;
import com.litlgroup.litl.R;
import com.litlgroup.litl.activities.MediaFullScreenActivity;
import com.litlgroup.litl.utils.AdvancedMediaPagerAdapter;
import com.litlgroup.litl.utils.CameraUtils;
import com.litlgroup.litl.utils.CanvasView;
import com.litlgroup.litl.utils.CircleIndicator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Hari on 8/30/2016.
 */
public class FullScreenMediaFragment
        extends Fragment
{

    List<String> mediaUrls;
    Boolean isEditMode;
    int pageIndex;

    public static FullScreenMediaFragment newInstance(List<String> mediaUrls, Boolean isEditMode, int pageIndex)
    {
        FullScreenMediaFragment frag = new FullScreenMediaFragment();

        frag.mediaUrls = mediaUrls;
        frag.isEditMode = isEditMode;
        frag.pageIndex = pageIndex;

        return frag;
    }

    @BindView(R.id.vpframe)
    FrameLayout vpframe;

    @BindView(R.id.vpMedia)
    ViewPager mVpMedia;

    @BindView(R.id.vpIndicator)
    LinearLayout mViewPagerCountDots;

    @BindView(R.id.flAnnotationControls)
    FrameLayout flAnnotationControls;

    @BindView(R.id.canvas)
    CanvasView canvas;

    @BindView(R.id.ibDraw)
    ImageButton ibDraw;

    @BindView(R.id.ibText)
    ImageButton ibText;

    @BindView(R.id.ibRectangle)
    ImageButton ibRectangle;

    @BindView(R.id.ibEllipse)
    ImageButton ibEllipse;

    @BindView(R.id.ibLine)
    ImageButton ibLine;

    @BindView(R.id.ibClear)
    ImageButton ibClear;

    @BindView(R.id.ibClose)
    ImageButton ibClose;

    @BindView(R.id.ibSave)
    ImageButton ibSave;

    @BindView(R.id.etAnnotationText)
    EditText etAnnotationText;

    CircleIndicator circleIndicator;
    AdvancedMediaPagerAdapter mediaPagerAdapter;

    private Unbinder unbinder;

    public Swipe swipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_full_screen, container, false);
        unbinder = ButterKnife.bind(this, view);
        try {

            if(savedInstanceState != null)
            {
                mediaUrls = savedInstanceState.getStringArrayList("mediaUrls");
                isEditMode = savedInstanceState.getBoolean("isEditMode");
            }

            setupViewPager();

            mediaPagerAdapter.removeAll();
            mediaPagerAdapter.addAll(mediaUrls);

            mediaPagerAdapter.notifyDataSetChanged();
            mVpMedia.setCurrentItem(pageIndex);

            circleIndicator.refreshIndicator();

            swipe = new Swipe();

            swipe.addListener(swipeListener);

            initializeCanvas();
        }
        catch (Exception ex)
        {
            Timber.e("Error creating View", ex);
        }
        return view;
    }

    private void initializeCanvas()
    {
        canvas.setVisibility(View.INVISIBLE);
        canvas.setMode(CanvasView.Mode.ERASER);
        canvas.setPaintStrokeWidth(0F);
    }


    SwipeListener swipeListener = new SwipeListener() {
        @Override
        public void onSwipingLeft(MotionEvent event) {

        }

        @Override
        public void onSwipedLeft(MotionEvent event) {

        }

        @Override
        public void onSwipingRight(MotionEvent event) {

        }

        @Override
        public void onSwipedRight(MotionEvent event) {

        }

        @Override
        public void onSwipingUp(MotionEvent event) {

        }

        @Override
        public void onSwipedUp(MotionEvent event) {
            if(isEditMode) {
                canvas.setVisibility(View.VISIBLE);
                flAnnotationControls.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onSwipingDown(MotionEvent event) {

        }

        @Override
        public void onSwipedDown(MotionEvent event) {

        }
    };

    float defaultAlpha = 0.5f;
    float highlightAlpha = 1.0f;
    float defaultStrokeWidth = 12F;

    private void removeHighlight()
    {
        try
        {
            ibDraw.setAlpha(defaultAlpha);
            ibText.setAlpha(defaultAlpha);
            ibRectangle.setAlpha(defaultAlpha);
            ibEllipse.setAlpha(defaultAlpha);
            ibLine.setAlpha(defaultAlpha);
            ibClear.setAlpha(defaultAlpha);
        }
        catch (Exception ex)
        {
            Timber.e(ex.toString());
        }
    }

    private void highlightIB(ImageButton ib)
    {
        try
        {
            ib.setAlpha(highlightAlpha);
        }catch (Exception ex)
        {
            Timber.e(ex.toString());
        }
    }

    private void resetAnnotationControlsToInitialState()
    {
        removeHighlight();
        etAnnotationText.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.ibClose)
    public void startClose()
    {
        try
        {
            flAnnotationControls.setVisibility(View.INVISIBLE);
            removeHighlight();
            initializeCanvas();
        }
        catch (Exception ex)
        {
            Timber.e("Error closing annotations control panel");
        }
    }


    @OnClick(R.id.ibDraw)
    public void startDraw()
    {
        try
        {
            resetAnnotationControlsToInitialState();

            highlightIB(ibDraw);

            canvas.setPaintStrokeWidth(defaultStrokeWidth);
            canvas.setPaintStyle(Paint.Style.STROKE);

            canvas.setDrawer(CanvasView.Drawer.PEN);
            canvas.setMode(CanvasView.Mode.DRAW);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting Draw function");
        }
    }

    @OnClick(R.id.ibText)
    public void startText()
    {
        try
        {
            resetAnnotationControlsToInitialState();
            highlightIB(ibText);
            canvas.setFontFamily(Typeface.DEFAULT_BOLD);
            canvas.setFontSize(96F);
            canvas.setPaintStyle(Paint.Style.FILL_AND_STROKE);


            etAnnotationText.setVisibility(View.VISIBLE);
            etAnnotationText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    canvas.setText(etAnnotationText.getText().toString().trim());
                }
            });
            canvas.setMode(CanvasView.Mode.TEXT);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting Text function");
        }
    }

    @OnClick(R.id.ibRectangle)
    public void startRectangle()
    {
        try
        {
            resetAnnotationControlsToInitialState();
            highlightIB(ibRectangle);
            canvas.setPaintStrokeWidth(defaultStrokeWidth);
            canvas.setPaintStyle(Paint.Style.STROKE);


            canvas.setMode(CanvasView.Mode.DRAW);
            canvas.setDrawer(CanvasView.Drawer.RECTANGLE);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting Rectangle function");
        }
    }


    @OnClick(R.id.ibEllipse)
    public void startEllipse()
    {
        try
        {
            resetAnnotationControlsToInitialState();
            highlightIB(ibEllipse);
            canvas.setPaintStrokeWidth(defaultStrokeWidth);
            canvas.setPaintStyle(Paint.Style.STROKE);

            canvas.setMode(CanvasView.Mode.DRAW);
            canvas.setDrawer(CanvasView.Drawer.ELLIPSE);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting Ellipse function");
        }
    }

    @OnClick(R.id.ibLine)
    public void startLine()
    {
        try
        {
            resetAnnotationControlsToInitialState();
            highlightIB(ibLine);
            canvas.setPaintStrokeWidth(defaultStrokeWidth);
            canvas.setPaintStyle(Paint.Style.STROKE);


            canvas.setMode(CanvasView.Mode.DRAW);
            canvas.setDrawer(CanvasView.Drawer.LINE);
        }
        catch (Exception ex)
        {
            Timber.e("Error starting Text function");
        }
    }

    @OnClick(R.id.ibClear)
    public void startClear()
    {
        try
        {
            resetAnnotationControlsToInitialState();
            highlightIB(ibClear);
            canvas.setMode(CanvasView.Mode.ERASER);
            canvas.setPaintStrokeWidth(0F);
            canvas.clear();
            removeHighlight();
        }
        catch (Exception ex)
        {
            Timber.e("Error starting Clear function");
        }
    }

    @OnClick(R.id.ibSave)
    public void startSave()
    {
        try
        {
            saveAnnotatedImage();
            startClose();
        }
        catch (Exception ex)
        {
            Timber.e(ex.toString());
        }
    }

    ArrayList<Integer> updatedIndices = new ArrayList<>();

    private void saveAnnotatedImage()
    {
        try
        {
            mViewPagerCountDots.setVisibility(View.INVISIBLE);
            flAnnotationControls.setVisibility(View.INVISIBLE);

            vpframe.setVisibility(View.VISIBLE);
            mVpMedia.setVisibility(View.VISIBLE);
            canvas.setVisibility(View.VISIBLE);

            Bitmap annotatedBmp = viewToBitmap(vpframe);

            Uri fileUri = CameraUtils.getOutputMediaFileUri(CameraUtils.MEDIA_TYPE_IMAGE);

            int pageIndex = mVpMedia.getCurrentItem();
            updatedIndices.add(pageIndex);

            mediaUrls.set(pageIndex, fileUri.toString());

            mediaPagerAdapter.remove(pageIndex);
            mediaPagerAdapter.insert(fileUri, pageIndex);

            try {
                FileOutputStream outputJpg = new FileOutputStream(fileUri.getPath());
                annotatedBmp.compress(Bitmap.CompressFormat.JPEG, 100, outputJpg);
                outputJpg.close();
                mediaPagerAdapter.notifyDataSetChanged();
                ((MediaFullScreenActivity)getActivity()).updateMediaUrls(mediaUrls, updatedIndices);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mViewPagerCountDots.setVisibility(View.VISIBLE);
            initializeCanvas();
            resetAnnotationControlsToInitialState();
        }
        catch (Exception ex)
        {
            Timber.e(ex.toString());
        }
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
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
        outState.putStringArrayList("mediaUrls", (ArrayList) mediaUrls);
        outState.putBoolean("isEditMode", isEditMode);

    }


}
