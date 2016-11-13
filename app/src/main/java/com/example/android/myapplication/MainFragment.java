package com.example.android.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements DataReadyInterface {
    private ProgressBar spinner;
    private GridView mGridView;
    private String unitType;
    private ImageAdapter img;
    private List<Movies> newmoviesList;
    private Realm realm;
    private RealmConfiguration config;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        newmoviesList = new ArrayList<>();
        spinner = (ProgressBar) rootView.findViewById(R.id.ProgressBar);
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies item = newmoviesList.get(position);
                Log.d("SIZE OF ARRAY MOVIES", String.valueOf(newmoviesList.size()));
                int movieId =item.getId();              Log.d("unitType", String.valueOf(movieId));
                String title = item.getTitle();                     Log.d("unitType", title);
                String relaseDate = item.getReleaseDate();          Log.d("unitType", relaseDate);
                String poster = item.getPoster();                   Log.d("unitType", poster);
                String overView = item.getOverView();               Log.d("unitType", overView);
                String voteAverage = item.getVoteAverage();         Log.d("unitType", voteAverage);
//                Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                Intent details = new Intent(getActivity(), DetailsActivity.class)
                        .putExtra("title", title).putExtra("relaseDate", relaseDate)
                        .putExtra("poster", poster).putExtra("overView", overView)
                        .putExtra("id",String.valueOf(movieId)).putExtra("voteAverage",voteAverage);
                startActivity(details);

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
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        unitType = prefs.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_popular_value));
        //FetchMovieData fetchMovieData = new FetchMovieData();
        MyAsyncTask moviesTask = new MyAsyncTask();
        moviesTask.setOnListener(this);
        moviesTask.execute(unitType,"movies");
        if (unitType.equals("top_rated")){
            getActivity().setTitle("Top Rated Movies");
        }
        if (unitType.equals("popular")){
            getActivity().setTitle("Popular Movies");
        }
        if (unitType.equals("favorite")){
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
        newmoviesList = result;
        Log.d("Movies json is :--->","nnn" );
        if (result != null) {
            //saveToDatabase(result);
            ImageAdapter img = new ImageAdapter(getActivity(), result);
            spinner.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            mGridView.setAdapter(img);
        }
        if (result == null) {
            RealmResults<TopMovies> TopMoviesRealmResults = realm.where(TopMovies.class).findAll();
            RealmResults<PopMovies> PopMoviesRealmResults = realm.where(PopMovies.class).findAll();
            RealmResults<FavMovies> FavMoviesRealmResults = realm.where(FavMovies.class).findAll();
            // List<Movies> moviesList = new ArrayList<>();
            if (unitType.equals("top_rated")) {
                    newmoviesList = new ArrayList<>();
                for (TopMovies movies : TopMoviesRealmResults) {
                    Movies movies1 = new Movies(movies.getId(), movies.getPoster(), movies.getOverView(), movies.getReleaseDate()
                            , movies.getTitle(), movies.getVoteAverage());
                   newmoviesList.add(movies1);
                }

                Log.d("SIZE OF ARRAY TOPMOVIES", String.valueOf(TopMoviesRealmResults.size()));
                Log.d("SIZE OF ARRAY MOVIES", String.valueOf(newmoviesList.size()));
                Log.d("unitType", unitType);
                img = new ImageAdapter(getActivity(), newmoviesList);
                spinner.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                mGridView.setAdapter(img);
            }
            if (unitType.equals("popular")) {
                newmoviesList = new ArrayList<>();
                for (PopMovies movies : PopMoviesRealmResults) {
                    Movies movies1 = new Movies(movies.getId(), movies.getPoster(), movies.getOverView(), movies.getReleaseDate()
                            , movies.getTitle(), movies.getVoteAverage());
                    newmoviesList.add(movies1);
                }
                Log.d("SIZE OF ARRAY POPMOVIES", String.valueOf(PopMoviesRealmResults.size()));
                Log.d("SIZE OF ARRAY MOVIES", String.valueOf(newmoviesList.size()));
                Log.d("unitType", unitType);
                img = new ImageAdapter(getActivity(), newmoviesList);
                spinner.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                mGridView.setAdapter(img);
            }

            if (unitType.equals("favorite")) {
                newmoviesList = new ArrayList<>();
                if (FavMoviesRealmResults.size()==0){
                    Toast.makeText(getActivity(), "No Favorite Movies Saved", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                    mGridView.setVisibility(View.GONE);
                    return;
                }

                for (FavMovies movies : FavMoviesRealmResults) {
                    Movies movies1 = new Movies(movies.getId(), movies.getPoster(), movies.getOverView(), movies.getReleaseDate()
                            , movies.getTitle(), movies.getVoteAverage());
                    newmoviesList.add(movies1);
                }
                Log.d("SIZE OF ARRAY POPMOVIES", String.valueOf(FavMoviesRealmResults.size()));
                Log.d("SIZE OF ARRAY MOVIES", String.valueOf(newmoviesList.size()));
                Log.d("unitType", unitType);
                img = new ImageAdapter(getActivity(), newmoviesList);
                spinner.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                mGridView.setAdapter(img);
            }


            // New data is back from the server.  Hooray!
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

