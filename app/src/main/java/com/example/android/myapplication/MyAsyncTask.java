package com.example.android.myapplication;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.myapplication.Data.Movies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Hazem on 11/9/2016.
 */

public class MyAsyncTask extends AsyncTask<String, Void, List<Movies>> {
    Parser parser = new Parser();
    DataReadyInterface listener;
    String Data;
    String sortingBy;
    @Override
    protected List doInBackground(String... params) {
        //Saving first param send
        sortingBy = params[0];
        // set the compare string to see if movie or video or reviews
        Data = params[1];
        //if no param send no point for execution
        if (params.length == 0) {
                return null;
            }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JsonString = null;
        try {
            // if user select favorite option from setting it will exit from doInBackground
            if (params[0].equals("favorite")){return null;}
            final String MovieDB_BASE_URL = ("http://api.themoviedb.org/3/movie/" + params[0]);
            final String API_Key_PARAM = "api_key";
            final String My_Key = BuildConfig.MOVIE_DATA_BASE_API_KEY;
            Uri builtUri = Uri.parse(MovieDB_BASE_URL).buildUpon()
                    .appendQueryParameter(API_Key_PARAM,My_Key)
                    .build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setUseCaches(true);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            JsonString = buffer.toString();
        } catch (IOException e) {
            Log.e("Network Connection ", "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Error", " closing stream", e);
                }
            }}
        //Start parsing by first check what data is
        if (Data.equals("movies")){
            try {
                return parser.movieJsonStrParsing(JsonString,sortingBy);
            } catch (Exception e) {
                Log.e("Parsing Erorr", e.getMessage(), e);
                e.printStackTrace();
            }
        }
        if (Data.equals("videos")){
            try {
                return parser.videoJsonStrParsing(JsonString,sortingBy);
            } catch (Exception e) {
                Log.e("Parsing Erorr", e.getMessage(), e);
                e.printStackTrace();
            }
        }
        if (Data.equals("reviews")){
            try {
                return parser.reviewsJsonStrParsing(JsonString,sortingBy);
            } catch (Exception e) {
                Log.e("Parsing Erorr", e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onPreExecute() {
        //for displaying spinner while data is fetching
        listener.onFetchProgress();
    }
    @Override
    protected void onPostExecute(List<Movies> result) {
        listener.onFetchFinish();
        if (Data.equals("movies")){
            listener.onMovieDataReady(result);
        }
        if (Data.equals("videos")){
            listener.onVideoDataReady(result);
        }
        if (Data.equals("reviews")){
            listener.onReviewDataReady(result);
        }

    }
    public void setOnListener(DataReadyInterface listener) {
        this.listener = listener;

    }


}
