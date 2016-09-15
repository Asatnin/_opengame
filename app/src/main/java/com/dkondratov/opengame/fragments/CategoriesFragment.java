package com.dkondratov.opengame.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.activities.MainActivity;
import com.dkondratov.opengame.adapters.CategoriesListAdapter;
import com.dkondratov.opengame.model.Category;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView categoriesList;
    private CategoriesListAdapter adapter;
    private MainActivity mainActivivty;

    private CategoriesFragmentCallbacks mCallbacks;

    private List<Category> categories;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (CategoriesFragmentCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categories = getArguments().getParcelableArrayList("categories");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_categories, container, false);
        mainActivivty = (MainActivity) getActivity();
        setupCategoriesList(fragmentView);
        return fragmentView;
    }

    private void setupCategoriesList(View fragmentView) {
        categoriesList = (ListView) fragmentView.findViewById(R.id.categories_list);
        adapter = new CategoriesListAdapter(categories, getActivity().getApplicationContext());
        categoriesList.setAdapter(adapter);

        // make this fragment as item's click listener
        categoriesList.setOnItemClickListener(CategoriesFragment.this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_SHORT).show();
        mCallbacks.onCategoryItemSelected(adapter.getItem(position), position);
    }

    public interface CategoriesFragmentCallbacks {
        void onCategoryItemSelected(Category category, int position);
    }
}
