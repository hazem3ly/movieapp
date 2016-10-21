package com.example.android.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private GridView mGridView;
 //   ImageAdapter imageAdapte = new ImageAdapter(getActivity());
    public Integer[] mThumbIds = {
            R.drawable.tst, R.drawable.tst,
            R.drawable.tst, R.drawable.tst,
            R.drawable.tst, R.drawable.tst,
            R.drawable.tst, R.drawable.tst,
            R.drawable.tst, R.drawable.tst,
            R.drawable.tst, R.drawable.tst,
            R.drawable.tst, R.drawable.tst,
            R.drawable.tst, R.drawable.tst,
            R.drawable.tst, R.drawable.tst,
            R.drawable.tst, R.drawable.tst,
    };
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
         mGridView = (GridView) rootView.findViewById(R.id.gridview);
    //    gridView.setAdapter(new ImageAdapter(getActivity()));
        FetchMovieData fetchMovieData = new FetchMovieData();
        fetchMovieData.execute("top_rated");

        return rootView;
    }

    public class ImageAdapter extends BaseAdapter {
        // Keep all Images in array
        List<Movie> mMovies;
        private Context mContext;

        // Constructor
        public ImageAdapter(Context c,List<Movie> movies) {
            mContext = c;
            mMovies = movies;

        }

        public int getCount() {
            return mMovies.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater)
                        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.image_list_items, null);
            }
                ImageView img = (ImageView) view.findViewById(R.id.image_list_item_imageView);
                TextView title = (TextView) view.findViewById(R.id.text_item);
                Movie movie = mMovies.get(position);
                Log.d("ss",movie.title);
                Log.d("aa",movie.relaseDate);
                Log.d("ss",movie.poster);
                Log.d("CHECK", movie.overView);
            title.setText(movie.title);
            String url = "http://image.tmdb.org/t/p/W185/" + movie.poster;
            Log.d("aaaaaaaaaa",url);
            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+movie.poster).into(img);
//                img.setImageResource(mThumbIds[position]);

            return view;
        }

    }

    public class FetchMovieData extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMovieData.class.getSimpleName();
        private List<Movie> parsingJsonData (String movieJsonString)throws Exception {
            List<Movie> movieList = new ArrayList<>();
            JSONObject response = new JSONObject(movieJsonString);
            response.getInt("page");
            JSONArray result = response.getJSONArray("results");
            for (int i = 0; i < result.length(); i++) {
                Movie movie = new Movie();
                JSONObject obj = result.getJSONObject(i);
                movie.overView = obj.getString("overview");
                movie.poster= obj.getString("poster_path");
                movie.relaseDate = obj.getString("release_date");
                movie.title = obj.getString("title");
                movieList.add(movie);
            }
            return movieList;
        }
        @Override
        protected List<Movie> doInBackground(String... params) {
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
                final String FORECAST_BASE_URL =(
                        "http://api.themoviedb.org/3/movie/" + params[0]) ;
 //               final String QUERY_PARAM = "";
//                final String FORMAT_PARAM = "mode";
//                final String UNITS_PARAM = "";
//                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "api_key";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, "39dbc8225484ac7c6ceca6ff3701b74b")
                        .build();
                URL url = new URL(builtUri.toString());
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
                Log.v(LOG_TAG, "Movie String Json= " + movieStringJson);
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
        protected void onPostExecute(List<Movie> result) {
            if (result != null) {
                ImageAdapter img = new ImageAdapter(getActivity(),result);
                mGridView.setAdapter(img);

                }
                // New data is back from the server.  Hooray!
            }
        }
   }


