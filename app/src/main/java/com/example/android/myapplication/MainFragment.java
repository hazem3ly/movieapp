package com.example.android.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.myapplication.Data.FavMovies;
import com.example.android.myapplication.Data.Movies;
import com.example.android.myapplication.Data.PopMovies;
import com.example.android.myapplication.Data.TopMovies;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainFragment extends Fragment implements DataReadyInterface {
    private ProgressBar spinner;
    private GridView mGridView;
    private String sortingBy;
    private ImageAdapter img;
    private List<Movies> newmoviesList;
    private Realm realm;
    private RealmConfiguration config;
    private static int lastPosition;
    private static String lastSortTybe = "";

    public MainFragment() {
    }
    private MovieSelectInterface movieSelectInterface;
    void setMovieSelectInterface (MovieSelectInterface movieSelectInterface){
        this.movieSelectInterface = movieSelectInterface;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        newmoviesList = new ArrayList<>();
        spinner = (ProgressBar) rootView.findViewById(R.id.ProgressBar);
        mGridView = (GridView) rootView.findViewById(R.id.gridView);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movies item = newmoviesList.get(position);
                Bundle movie = new Bundle();
                movie.putInt("id", item.getId());movie.putString("title", item.getTitle());movie.putString("back_drop",item.getBackdrop());
                movie.putString("relaseDate", item.getReleaseDate());movie.putString("poster", item.getPoster());
                movie.putString("overView", item.getOverView());movie.putString("voteAverage", item.getVoteAverage());
                movieSelectInterface.setSelectedMovie(movie);
            }
        });
        //initializing realm database
        Realm.init(getActivity());
        config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        return rootView;
    }
    @Override
    public void onPause() {
        super.onPause();
        lastPosition = mGridView.getFirstVisiblePosition();
        lastSortTybe = sortingBy;
    }
    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
             sortingBy = prefs.getString(getString(R.string.pref_sorting_key),
                                      getString(R.string.pref_popular_value));
                MyAsyncTask moviesTask = new MyAsyncTask();
                moviesTask.setOnListener(this);
                moviesTask.execute(sortingBy, "movies");
                    if (sortingBy.equals("top_rated")) {
                        getActivity().setTitle("Top Rated Movies");
                    }
                    if (sortingBy.equals("popular")) {
                        getActivity().setTitle("Popular Movies");
                    }
                    if (sortingBy.equals("favorite")) {
                        getActivity().setTitle("Favorite Movies");
                    }
    }

    @Override
    public void onFetchProgress() {
        mGridView.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFetchFinish() {
        mGridView.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onMovieDataReady(List<Movies> result) {
       //setting list for onclick listener to get a movie from position it have
        newmoviesList = result;
        if (result != null) {
            ImageAdapter img = new ImageAdapter(getActivity(), result);
                spinner.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                mGridView.setAdapter(img);
            if (lastSortTybe.equals(sortingBy)){
                mGridView.setSelection(lastPosition);
            }

        }
        if (result == null) {
            Toast.makeText(getActivity(), "You Are Offline", Toast.LENGTH_SHORT).show();
            newmoviesList = new ArrayList<>();
            if (sortingBy.equals("top_rated")) {
                RealmResults<TopMovies> TopMoviesRealmResults = realm.where(TopMovies.class).findAllSorted("index", Sort.ASCENDING);
                    for (TopMovies movies : TopMoviesRealmResults) {
                        Movies movies1 = new Movies(movies.getId(), movies.getPoster(), movies.getOverView(), movies.getReleaseDate()
                                , movies.getTitle(), movies.getVoteAverage(),movies.getBackdrop_path());
                        newmoviesList.add(movies1);
                    }
            }
            if (sortingBy.equals("popular")) {
                RealmResults<PopMovies> PopMoviesRealmResults = realm.where(PopMovies.class).findAllSorted("index", Sort.ASCENDING);
                for (PopMovies movies : PopMoviesRealmResults) {
                    Movies movies1 = new Movies(movies.getId(), movies.getPoster(), movies.getOverView(), movies.getReleaseDate()
                            , movies.getTitle(), movies.getVoteAverage(),movies.getBackdrop_path());
                    newmoviesList.add(movies1);
                }
            }

            if (sortingBy.equals("favorite")) {
                RealmResults<FavMovies> FavMoviesRealmResults = realm.where(FavMovies.class).findAll();
                if (FavMoviesRealmResults.size() == 0) {
                    Toast.makeText(getActivity(), "No Favorite Movies Saved", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                    mGridView.setVisibility(View.GONE);
                    return;
                }
                for (FavMovies movies : FavMoviesRealmResults) {
                    Movies movies1 = new Movies(movies.getId(), movies.getPoster(), movies.getOverView(), movies.getReleaseDate()
                            , movies.getTitle(), movies.getVoteAverage(),movies.getBack_Drop());
                    newmoviesList.add(movies1);
                }
            }
                img = new ImageAdapter(getActivity(), newmoviesList);
                spinner.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                mGridView.setAdapter(img);
            if (lastSortTybe.equals(sortingBy)){
                mGridView.setSelection(lastPosition);
            }


        }
    }


    @Override
    public void onVideoDataReady(List<Movies> result) {
    }

    @Override
    public void onReviewDataReady(List<Movies> jsonStr) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

