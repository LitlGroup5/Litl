package com.litlgroup.litl.fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.litlgroup.litl.R;
import com.litlgroup.litl.adapters.BottomSheetAdapter;
import com.litlgroup.litl.models.BottomSheetItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by monusurana on 9/17/16.
 */
public class CategorySelectionFragment extends BottomSheetDialogFragment implements BottomSheetAdapter.BottomSheetItemClickListener {

    @BindView(R.id.grid_items)
    GridView gridItems;
    private Integer index;

    BottomSheetFragmentListener mListener;

    ArrayList<BottomSheetItem> items = new ArrayList<>();

    public interface BottomSheetFragmentListener {
        void onClick(int position, String category);
    }

    public void setBottomSheetFragmentListener(BottomSheetFragmentListener listener) {
        mListener = listener;
    }

    public CategorySelectionFragment() {

    }

    public static CategorySelectionFragment newInstance(Integer index) {
        CategorySelectionFragment frag = new CategorySelectionFragment();

        frag.index = index;
        return frag;
    }


    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        unbinder = ButterKnife.bind(this, view);

        List<String> categoryStrings = Arrays.asList(getResources().getStringArray(R.array.categories_array_values));

        TypedArray drawables = getResources().obtainTypedArray(R.array.categories_drawable_values);

        for (int i = 0; i < categoryStrings.size(); i++) {
            items.add(new BottomSheetItem(drawables.getResourceId(i, -1), categoryStrings.get(i)));
        }

        BottomSheetAdapter adapter = new BottomSheetAdapter(getActivity(), items, index);
        adapter.setBottomSheetItemClickListener(this);

        gridItems.setAdapter(adapter);
        gridItems.setNumColumns(3);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(int position) {
        mListener.onClick(position, items.get(position).getText());
        dismiss();
    }
}
