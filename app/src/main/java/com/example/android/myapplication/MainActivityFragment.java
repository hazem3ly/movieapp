package com.example.android.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ProgressBar spinner;
    private GridView mGridView;
    private String unitType;
    private ImageAdapter img;
    private List<Movies> moviesList;
    private Realm realm;
    private RealmConfiguration config;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        moviesList = new ArrayList<>();
        spinner = (ProgressBar) rootView.findViewById(R.id.ProgressBar);
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Movies item = moviesList.get(position);
                Log.d("SIZE OF ARRAY MOVIES", String.valueOf(moviesList.size()));
                int movieId =item.getId();              Log.d("unitType", String.valueOf(movieId));
                String title = item.getTitle();                     Log.d("unitType", title);
                String relaseDate = item.getReleaseDate();          Log.d("unitType", relaseDate);
                String poster = item.getPoster();                   Log.d("unitType", poster);
                String overView = item.getOverView();               Log.d("unitType", overView);
                String voteAverage = item.getVoteAverage();         Log.d("unitType", voteAverage);
                Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                Intent details = new Intent(getActivity(), MovieDetails.class)
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
        FetchMovieData fetchMovieData = new FetchMovieData();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        unitType = prefs.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_top_rated_value));
        fetchMovieData.execute(unitType,"secon prams here");
    }

    public class FetchMovieData extends AsyncTask<String, Void, List<Movies>> {
        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        private List<Movies> parsingJsonData(String movieJsonString) throws Exception {
            JSONObject response = new JSONObject(movieJsonString);
            response.getInt("page");
            Log.d("id off page movie is?", String.valueOf(response.getInt("page")));
            JSONArray result = response.getJSONArray("results");
            // To prevent Data duplication
            moviesList.clear();
            for (int i = 0; i < result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                Movies movies = new Movies(obj.getInt("id"), obj.getString("poster_path"), obj.getString("overview"),
                        obj.getString("release_date"), obj.getString("title"), obj.getString("vote_average"));
                moviesList.add(movies);
                int mMovieId = obj.getInt("id");
                Log.d("id off avorit movie is?", String.valueOf(mMovieId));
                //Log.d("SIZE OF ARRAY MOVIES", String.valueOf(moviesList.size()));
                saveToDatabase(obj.getInt("id"), obj.getString("poster_path"), obj.getString("overview"),
                        obj.getString("release_date"), obj.getString("title"), obj.getString("vote_average"));
            }
            return moviesList;
        }

        @Override
        protected List<Movies> doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieStringJson = null;
            String format = "json";
            //           String units = "top_rated";
//            int numDays = 7;

            try {
                if (params[0].equals("favorite")){return null;}
                final String FORECAST_BASE_URL = (
                        "http://api.themoviedb.org/3/movie/" + params[0]);
                //               final String QUERY_PARAM = "";
//                final String FORMAT_PARAM = "mode";
//                final String UNITS_PARAM = "";
//                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "api_key";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, "39dbc8225484ac7c6ceca6ff3701b74b")
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "second param " + params[1]);
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieStringJson = buffer.toString();
                Log.v(LOG_TAG, "Movies String Json= " + movieStringJson);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

            }
            try {
                return parsingJsonData(movieStringJson);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPreExecute() {
            mGridView.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<Movies> result) {
            if (result != null) {
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
                    moviesList.clear();
                    for (TopMovies movies : TopMoviesRealmResults) {
                        Movies movies1 = new Movies(movies.getId(), movies.getPoster(), movies.getOverView(), movies.getReleaseDate()
                                , movies.getTitle(), movies.getVoteAverage());
                        moviesList.add(movies1);
                    }

                    Log.d("SIZE OF ARRAY TOPMOVIES", String.valueOf(TopMoviesRealmResults.size()));
                    Log.d("SIZE OF ARRAY MOVIES", String.valueOf(moviesList.size()));
                    Log.d("unitType", unitType);
                    img = new ImageAdapter(getActivity(), moviesList);
                    spinner.setVisibility(View.GONE);
                    mGridView.setVisibility(View.VISIBLE);
                    mGridView.setAdapter(img);
                }
                if (unitType.equals("popular")) {
                    moviesList.clear();
                    for (PopMovies movies : PopMoviesRealmResults) {
                        Movies movies1 = new Movies(movies.getId(), movies.getPoster(), movies.getOverView(), movies.getReleaseDate()
                                , movies.getTitle(), movies.getVoteAverage());
                        moviesList.add(movies1);
                    }
                    Log.d("SIZE OF ARRAY POPMOVIES", String.valueOf(PopMoviesRealmResults.size()));
                    Log.d("SIZE OF ARRAY MOVIES", String.valueOf(moviesList.size()));
                    Log.d("unitType", unitType);
                    img = new ImageAdapter(getActivity(), moviesList);
                    spinner.setVisibility(View.GONE);
                    mGridView.setVisibility(View.VISIBLE);
                    mGridView.setAdapter(img);
                }
                if (unitType.equals("favorite")) {
                    if (FavMoviesRealmResults.size()==0){
                        Toast.makeText(getActivity(), "No Favorite Movies Saved", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                        return;
                    }
                    moviesList.clear();
                    for (FavMovies movies : FavMoviesRealmResults) {
                        Movies movies1 = new Movies(movies.getId(), movies.getPoster(), movies.getOverView(), movies.getReleaseDate()
                                , movies.getTitle(), movies.getVoteAverage());
                        moviesList.add(movies1);
                    }
                    Log.d("SIZE OF ARRAY POPMOVIES", String.valueOf(FavMoviesRealmResults.size()));
                    Log.d("SIZE OF ARRAY MOVIES", String.valueOf(moviesList.size()));
                    Log.d("unitType", unitType);
                    img = new ImageAdapter(getActivity(), moviesList);
                    spinner.setVisibility(View.GONE);
                    mGridView.setVisibility(View.VISIBLE);
                    mGridView.setAdapter(img);
                }


                // New data is back from the server.  Hooray!
            }
        }

    }    public void saveToDatabase(final int id, final String poster_path, final String overview,
                                    final String release_date, final String title,
                                    final String vote_average) {
        if (unitType.equals("popular")) {
            final PopMovies movies = new PopMovies();
            movies.setId(id);
            movies.setTitle(title);
            movies.setVoteAverage(vote_average);
            movies.setReleaseDate(release_date);
            movies.setOverView(overview);
            movies.setPoster(poster_path);
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // This will create a new object in Realm or throw an exception if the
                    // object already exists (same primary key)
                    // realm.copyToRealm(obj);

                    // This will update an existing object with the same primary key
                    // or create a new object if an object with no primary key
                    realm.copyToRealmOrUpdate(movies);
                }
            });
        }
        if (unitType.equals("top_rated")) {
            final TopMovies movies = new TopMovies();
            movies.setId(id);
            movies.setTitle(title);
            movies.setVoteAverage(vote_average);
            movies.setReleaseDate(release_date);
            movies.setOverView(overview);
            movies.setPoster(poster_path);
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // This will create a new object in Realm or throw an exception if the
                    // object already exists (same primary key)
                    // realm.copyToRealm(obj);

                    // This will update an existing object with the same primary key
                    // or create a new object if an object with no primary key
                    realm.copyToRealmOrUpdate(movies);
                }
            });
        }
    }

        @Override
        public void onDestroy() {
            super.onDestroy();
            realm.close();
        }
    }


