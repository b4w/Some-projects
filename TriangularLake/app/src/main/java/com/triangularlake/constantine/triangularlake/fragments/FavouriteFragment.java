package com.triangularlake.constantine.triangularlake.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.triangularlake.constantine.triangularlake.R;
import com.triangularlake.constantine.triangularlake.adapters.FavouriteProblemsAdapter;
import com.triangularlake.constantine.triangularlake.data.common.CommonDao;
import com.triangularlake.constantine.triangularlake.data.dto.Problem;
import com.triangularlake.constantine.triangularlake.data.helpers.OrmConnect;

import java.sql.SQLException;
import java.util.List;

public class FavouriteFragment extends Fragment {
    private static final String TAG = FavouriteFragment.class.getSimpleName();
    private static final String FAVOURITE = "favourite";

    private RecyclerView fragmentFavoritesProblems;
    private FavouriteProblemsAdapter favouriteProblemsAdapter;

    public static FavouriteFragment newInstance() {
        FavouriteFragment fragment = new FavouriteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initXmlFields();
        initListeners();
    }

    private void initXmlFields() {
        Log.d(TAG, "initXmlFields() start");
        fragmentFavoritesProblems = (RecyclerView) getActivity().findViewById(R.id.fragment_favorites_problems);
        Log.d(TAG, "initXmlFields() done");
    }

    private void initListeners() {
        Log.d(TAG, "initListeners() start");
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentFavoritesProblems.setLayoutManager(linearLayoutManager);
        fragmentFavoritesProblems.setAdapter(favouriteProblemsAdapter);
        Log.d(TAG, "initListeners() done");
    }

    private void loadData() {
        Log.d(TAG, "loadData() start");
        new AsyncLoadProblems().execute();
        Log.d(TAG, "loadData() done");
    }

    class AsyncLoadProblems extends AsyncTask <Void, Void, List<Problem>> {
        // TODO: С кешем было вроде быстрее, чем с достованием из бд.
        @Override
        protected List<Problem> doInBackground(Void... voids) {
            Log.d(TAG, "async load problems doInBackground() start");
            try {
                final CommonDao commonDao = OrmConnect.INSTANCE.getDBConnect(getActivity()).getDaoByClass(Problem.class);
                if (commonDao != null) {
                    return commonDao.queryForEq(FAVOURITE, 1);
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error! " + e.getMessage());
            }
            Log.d(TAG, "async load problems doInBackground() done");
            return null;
        }

        @Override
        protected void onPostExecute(List<Problem> problems) {
            super.onPostExecute(problems);
            // добавление данных в адаптер и RecyclerView
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            favouriteProblemsAdapter = new FavouriteProblemsAdapter(problems);
            fragmentFavoritesProblems.setLayoutManager(layoutManager);
            fragmentFavoritesProblems.setAdapter(favouriteProblemsAdapter);
        }
    }
}
